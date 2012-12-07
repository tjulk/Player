package com.baidu.browser.framework;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.browser.BPBrowser;
import com.baidu.browser.core.ui.BdPopMenuGroup;
import com.baidu.player.R;
import com.baidu.player.ui.FakeProgressBar;
import com.baidu.webkit.sdk.BValueCallback;

/**
 * @ClassName: BPFrameView 
 * @Description: 浏览器控制窗口 
 * @author LEIKANG 
 * @date 2012-12-5 下午3:27:26
 */
public class BPFrameView extends FrameLayout{
	
	/**
	 * Window切换时的动画类型
	 */
	public enum WindowStwitchAnimation {
	    /** 无动画 */
	    NONE,
	    /** 关闭窗口动画 */
	    CLOSE_WINDOW,
	    /** 新开窗口 */
	    NEW_WINDOW,
	}
	
	/** CurrentWindow等待resume。
	 * 之所以在真正开始resume之前加上等待和准备这两步操作，是为了确保让CurrentWindow在Activity完全resume后的200毫秒 以后，再执行resume，
	 * 从而让App的框架（searchbox、toolbar等）先显示出来，达到快速启动的效果。
	 */
	private static final int MSG_CURRENT_WINDOW_RESUME_WAITING = 1;
	
	/** CurrentWindow准备resume。 */
	private static final int MSG_CURRENT_WINDOW_RESUME_READY = 2;
	
	/** CurrentWindow真正开始resume。 */
	private static final int MSG_CURRENT_WINDOW_RESUME_GO = 3;
	
	/**用来保存window总数的key。*/
	private static final String WINDOW_SIZE = "WINDOW_SIZE";
	
	/**用来保存当前window位置的key。*/
    private static final String CURRENT_WINDOW_POS = "CURRENT_WINDOW_POS";
    
	/** 是否为从保存的状态中修复 */
	private boolean mRestoredFromState;
	
	/** 窗口列表 **/
	private List<BPWindow> mWindowList;
	
	/** 当前窗口 **/
	private BPWindow mCurrentWindow;
	
	/** 搜索框高度 **/
	private int mSearchBoxHeight;

	/** 工具条高度 **/
	private int mToolbarHeight;
	
	/** 进度条高度 **/
	private int mProgressHeight;
	
	/** 进度条 **/
	private FakeProgressBar mProgressBar;
	
	/** 窗口容器，用于控制窗口的切换 */
	private BdWindowWrapper mWindowWrapper;
	
	/** Browser 主类 */
	private BPBrowser mBrowser;
 
	/**
	 * @param context
	 */
	public BPFrameView(Context context) {
		this(context, null);
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public BPFrameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BPFrameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		mWindowList = new ArrayList<BPWindow>();
		mSearchBoxHeight = context.getResources().getDimensionPixelSize(R.dimen.float_searchbox_height);
		mToolbarHeight = context.getResources().getDimensionPixelSize(R.dimen.bottom_toolbar_height);
		mProgressHeight = context.getResources().getDimensionPixelSize(R.dimen.browser_progress_bar_height);
		
		mProgressBar = (FakeProgressBar) ((Activity) context).getLayoutInflater().inflate(R.layout.browser_progress_bar, null);
		addView(mProgressBar, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,mProgressHeight));
		mWindowWrapper = new BdWindowWrapper(context);
        addView(mWindowWrapper, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != GONE) {
				if (childView.equals(mProgressBar)) {
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(childView.getLayoutParams().height,
							MeasureSpec.EXACTLY);
				} else if (childView instanceof BdPopMenuGroup) {
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
				} else {
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(childView.getLayoutParams().height,
							MeasureSpec.EXACTLY);
				}
				childView.measure(widthMeasureSpec, heightMeasureSpec);
			}
		}
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int layoutTop = 0;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != GONE) {
				int height = childView.getHeight();
				if (childView instanceof ImageView) {
				    childView.layout(0, layoutTop, getWidth(), layoutTop + height);
				} else if (childView.equals(mProgressBar)) {
					childView.layout(0, layoutTop - height / 2, getWidth(), layoutTop - height / 2 + height);
				} else if (childView instanceof BdPopMenuGroup) {
					childView.layout(0, 0, getWidth(), getHeight());
				} 
			}
		}
	}

	/**
	 * @Title: saveStateToBundle 
	 * @Description: 保存状态。
	 * @param  savedState   
	 * @return void    
	 * @throws
	 */
	public void saveStateToBundle(Bundle savedState) {
	    if (savedState == null) {
	        return;
	    }
	    
	    for (BPWindow window : mWindowList) {
	        window.saveStateToBundle(savedState);
        }
	    savedState.putInt(WINDOW_SIZE, mWindowList.size());
	    int curPos = 0;
	    if (mCurrentWindow != null) {
	        curPos = mCurrentWindow.getPostition();
	    }
	    savedState.putInt(CURRENT_WINDOW_POS, curPos);
	}

	/**
	 * @Title: restoreFromBundle 
	 * @Description:浏览器页面回复状态 
	 * @param  savedInstanceState   
	 * @return void    
	 * @throws
	 */
	public void restoreFromBundle(Bundle savedInstanceState) {
	    if (savedInstanceState == null) {
	        return;
	    }
	    mRestoredFromState = true;
	    
	    int size = savedInstanceState.getInt(WINDOW_SIZE);
	    for (int i = 0; i < size; i++) {
	        createWindow(false, savedInstanceState);
	    }
	    
	    int currentPos = savedInstanceState.getInt(CURRENT_WINDOW_POS);
	    BPWindow window = mWindowList.get(currentPos);
	    swapWindowToFocus(window);
	}
	
	/**
	 * @Title: createWindow 
	 * @Description: 创建窗口 
	 * @param focus 是否切换
	 * @return BPWindow
	 */
	public BPWindow createWindow(boolean focus) {
		return createWindow(focus, null);
	}
	
	/**
	 * @Title: createWindow 
	 * @Description: 创建窗口 
	 * @param focus 是否切换到该窗口
	 * @param savedState 如果不为null，则从中恢复状态
	 * @param    
	 * @return BdWindow    
	 * @throws
	 */
	public BPWindow createWindow(boolean focus, Bundle savedState) {
	    RelativeLayout.LayoutParams exploreLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        exploreLayout.topMargin = mSearchBoxHeight;
        exploreLayout.bottomMargin = mToolbarHeight;

        BPWindow window = new BPWindow(getContext());
        window.setFrameView(this);
        window.setLayoutParams(exploreLayout);
        window.setPosition(mWindowList.size());
        mWindowList.add(window);
        
        if (savedState == null) {
            window.loadInitailHome();
        } else {
            window.restoreFromBundle(savedState);
        }
        
        if (focus) {
            swapWindowToFocus(window);
        }
        
        return window;
	}
	
	/**
	 * @Title: swapWindowToFocus 
	 * @Description: 直接切换至窗口 
	 * @param  aWindow    
	 */
	public void swapWindowToFocus(BPWindow aWindow) {
	    swapWindowToFocus(aWindow, WindowStwitchAnimation.NONE, null);
	}
	
	/**
	 * @Title: swapWindowToFocus 
	 * @Description: 切换窗口 ，切换动作，searchURL 为搜索url
	 * @param window
	 * @param mWindowStwitchAnimation
	 * @param searchUrl   
	 */
	private void swapWindowToFocus(BPWindow window,WindowStwitchAnimation mWindowStwitchAnimation, String searchUrl) {
        if (window != null && !window.equals(mCurrentWindow)) {
			if (mCurrentWindow != null && mBrowser != null) {
				//Bundle b = mBrowser.getSearchBoxBundle(true);
				//mCurrentWindow.setSearchboxBundle(b);
			}
			mCurrentWindow = window;
			mWindowWrapper.showWindow(window, mWindowStwitchAnimation, searchUrl);
			//mCurrentWindow.setLastViewedTime(SystemClock.uptimeMillis());
			
			if (mCurrentWindow != null && mBrowser != null) {
				//mBrowser.onTabChangeFinished();
				String url = null;
				if (mCurrentWindow.isHomePage()) {
					url = BPBrowser.HOME_PAGE;
				} else {
					url = mCurrentWindow.getCurrentUrl();
				}
				switchBetweenHomeAndBrowser(url);
				mCurrentWindow.onResume();
			}
			updateState(window);
        } else if (window == mCurrentWindow) {
	        window.loadUrl(searchUrl);
	    }
	}

	/**
	 * @Title: onResume 
	 * @Description: 之所以在真正开始resume之前加上等待和准备这两步操作，
	 * 				 是为了确保让CurrentWindow在Activity完全resume后的200毫秒 以后，再执行resume
	 */
	public void onResume() {
		if (!mRestoredFromState) {
		    mHandler.sendEmptyMessage(MSG_CURRENT_WINDOW_RESUME_WAITING);
		} else {
		    mRestoredFromState = false;
		    resumeCurrentWindow();
		}
	}
	
	/**
	 * @Title: onResume 
	 * @Description: 之所以在真正开始resume之前加上等待和准备这两步操作，是为了确保让CurrentWindow在Activity完全resume后的200毫秒 以后，再执行resume
	 */
    private  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_CURRENT_WINDOW_RESUME_WAITING:
                sendEmptyMessage(MSG_CURRENT_WINDOW_RESUME_READY);
                break;

            case MSG_CURRENT_WINDOW_RESUME_READY:
                final long delayTime = 200;
                sendEmptyMessageDelayed(MSG_CURRENT_WINDOW_RESUME_GO, delayTime);
                break;

            case MSG_CURRENT_WINDOW_RESUME_GO:
                if (mCurrentWindow == null) {
                    createWindow(true);
                }
                resumeCurrentWindow();
                break;
                
            default:
                break;
            }
        };
    };
    
    /**
     * @Title: resumeCurrentWindow 
     * @Description: 启动当前window   
     */
	private void resumeCurrentWindow() {
	    if (mCurrentWindow == null) {
            return;
        }
	    mCurrentWindow.onResume();
        updateState(mCurrentWindow);
        
        if (mCurrentWindow.isHomePage()) {
            mCurrentWindow.getHomeView().onResume();
        } else {
        }
	}

	
	/**
	 * @Title: setBrowser 
	 * @Description: 设置父 BPBrowser
	 * @param  bpBrowser   
	 */
	public void setBrowser(BPBrowser bpBrowser) {
		this.mBrowser = bpBrowser;
	}
	
	/**
	 * @Title: closeSelectedMenu 
	 * @Description: 关闭划屏菜单    
	 */
	public void closeSelectedMenu() {
	    if (mCurrentWindow != null) {
	        mCurrentWindow.closeSelectedMenu();
	    }		
	}
	/**
	 * @Title: onPause 
	 * @Description: BPFrameView 暂停    
	 */
	public void onPause() {
	    if (mCurrentWindow != null) {
	        mCurrentWindow.getHomeView().onPause();
	    }
		for (int i = 0; i < mWindowList.size(); i++) {
			mWindowList.get(i).onPause();
		}		
	}
	
	/**
	 * @Title: freeMemory 
	 * @Description: 释放内存   
	 */
	public void freeMemory() {
		for (int i = 0; i < mWindowList.size(); i++) {
			mWindowList.get(i).freeMemory();
		}		
	}
	
	/**
	 * @Title: release 
	 * @Description: 释放内存  
	 */
	public void release() {
		for (BPWindow w:mWindowList) {
			w.release();
		}
		mWindowList.clear();
		if (mCurrentWindow != null) {
		    mCurrentWindow.getHomeView().onDestroy();
		}		
	}

	
	/**
	 * @Title: goBack 
	 * @Description: 历史后退   
	 */
	public void goBack() {
	    if (mCurrentWindow == null) {
	        return;
	    }
	    if (mCurrentWindow.canGoBack()) {
	        mCurrentWindow.goBack();
	    }  
		updateState(mCurrentWindow);
	}
	
	/**
	 * @Title: goForward 
	 * @Description: 历史前进    
	 */
	public void goForward() {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.goForward();
		updateState(mCurrentWindow);		
	}

	/**
	 * @Title: canGoForward 
	 * @Description: 判断是否前进
	 * @return boolean
	 */
	public boolean canGoForward() {
	    if (mCurrentWindow != null) {
            return mCurrentWindow.canGoForward();
        }
        return false;
	}

	/**
	 * @Title: canGoBack 
	 * @Description: 判断是否后退 
	 * @return boolean
	 */
	public boolean canGoBack() {
	    if (mCurrentWindow != null) {
	        return mCurrentWindow.canGoBack();
	    }
	    return false;
	}

	/**
	 * @Title: loadUrl 
	 * @Description: 加载URL 
	 * @param url   
	 */
	public void loadUrl(String url) {
	    if (mCurrentWindow != null) {
	        mCurrentWindow.loadUrl(url);
	    } else {
	        createNewWindowOpenUrl(url, null, true, null);
	    }
	}

	/**
	 * @Title: getCurrentWindow 
	 * @Description: 获取当前窗口 
	 * @return  BPWindow
	 */
	public BPWindow getCurrentWindow() {
		return mCurrentWindow;
	}

	/**
	 * @Title: createNewWindowOpenUrl 
	 * @Description: 创建窗口 
	 * @param url 需打开的url
	 * @param current 请求新建窗口的窗口
	 * @param loadImmediately 是否立即load
	 * @param bundle 搜索框状态   
	 */
	public void createNewWindowOpenUrl(String url, BPWindow current, boolean loadImmediately,
			Bundle bundle) {
		// TODO Auto-generated method stub
	}

	/**
	 * @Title: reload 
	 * @Description: 重新载入页面   
	 */
	public void reload() {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.reload();		
	}

	/**
	 * @Title: stopLoading 
	 * @Description: 停止加载   
	 */
	public void stopLoading() {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.stopLoading();
		mProgressBar.reset();
		updateState(mCurrentWindow);		
	}

	/**
	 * @Title: clearHistory 
	 * @Description: 清除历史    
	 */
	public void clearHistory() {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.clearHistory();		
	}

	/**
	 * @Title: setUpSelect 
	 * @Description: 进入选自模式    
	 */
	public void setUpSelect() {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.setUpSelect();		
	}

	/**
	 * @Title: getBrowser 
	 * @Description: 返回BPBrowser 
	 * @return  BPBrowser
	 */
	public BPBrowser getBrowser() {
		return mBrowser;
	}

	/**
	 * @Title: updateState 
	 * @Description: 刷新窗口状态 底部工具栏等 
	 * @param bpWindow   
	 */
	public void updateState(BPWindow bpWindow) {
		//TODO 
	}

	/**
	 * @Title: onSelectionSearch 
	 * @Description: 划词搜索
	 * @param aSelection   
	 */
	public void onSelectionSearch(String aSelection) {
		mBrowser.onSelectionSearch(aSelection);		
	}

	/**
	 * @Title: openFileChooser 
	 * @Description: 上传文件 时打开文件夹
	 * @param uploadMsg
	 * @param acceptType   
	 */
	public void openFileChooser(BValueCallback<Uri> uploadMsg,
			String acceptType) {
		mBrowser.openFileChooser(uploadMsg, acceptType);
	}

	/**
	 * @Title: switchBetweenHomeAndBrowser 
	 * @Description: 根据当前窗口状态，在home和SearchBrowser之间切换
	 * @param url   
	 */
	public void switchBetweenHomeAndBrowser(String url) {
        if (mCurrentWindow != null) {
            boolean isHome = TextUtils.equals(url, BPBrowser.HOME_PAGE);
            if (!isHome) {
                mCurrentWindow.hideHomeView();
            } else if (isHome) {
                mCurrentWindow.showHomeView();
            }
        }
	}

	/**
	 * @Title: onInnerCreateNewWindow 
	 * @Description: 内部调用新窗口，这里指 ^^
	 * @param bpWindow
	 * @return   
	 * BPWindow
	 */
	public BPWindow onInnerCreateNewWindow(BPWindow bpWindow) {
		//TODO
		return null;
	}

	/**
	 * @Title: getWindowList 
	 * @Description: 获取窗口列表
	 * @return List<BPWindow>
	 */
	public List<BPWindow> getWindowList() {
		return mWindowList;
	}

	/**
	 * @Title: webviewScrollBy 
	 * @Description: 控制webview滚动 
	 * @param x
	 * @param y   
	 */
	public void webviewScrollBy(int x, int y) {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.webviewScrollBy(x, y);
	}

	/**
	 * @Title: webviewScrollTo 
	 * @Description: 控制webview滚动 
	 * @param x
	 * @param y   
	 */
	public void webviewScrollTo(int x, int y) {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.webviewScrollTo(x, y);		
	}

	/**
	 * @Title: addWebViewTitle 
	 * @Description: 加入webview headView
	 * @param aView   
	 */
	public void addWebViewTitle(View aView) {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.setEmbeddedTitleBar(aView);		
	}

}
