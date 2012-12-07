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
 * @Description: ��������ƴ��� 
 * @author LEIKANG 
 * @date 2012-12-5 ����3:27:26
 */
public class BPFrameView extends FrameLayout{
	
	/**
	 * Window�л�ʱ�Ķ�������
	 */
	public enum WindowStwitchAnimation {
	    /** �޶��� */
	    NONE,
	    /** �رմ��ڶ��� */
	    CLOSE_WINDOW,
	    /** �¿����� */
	    NEW_WINDOW,
	}
	
	/** CurrentWindow�ȴ�resume��
	 * ֮������������ʼresume֮ǰ���ϵȴ���׼����������������Ϊ��ȷ����CurrentWindow��Activity��ȫresume���200���� �Ժ���ִ��resume��
	 * �Ӷ���App�Ŀ�ܣ�searchbox��toolbar�ȣ�����ʾ�������ﵽ����������Ч����
	 */
	private static final int MSG_CURRENT_WINDOW_RESUME_WAITING = 1;
	
	/** CurrentWindow׼��resume�� */
	private static final int MSG_CURRENT_WINDOW_RESUME_READY = 2;
	
	/** CurrentWindow������ʼresume�� */
	private static final int MSG_CURRENT_WINDOW_RESUME_GO = 3;
	
	/**��������window������key��*/
	private static final String WINDOW_SIZE = "WINDOW_SIZE";
	
	/**�������浱ǰwindowλ�õ�key��*/
    private static final String CURRENT_WINDOW_POS = "CURRENT_WINDOW_POS";
    
	/** �Ƿ�Ϊ�ӱ����״̬���޸� */
	private boolean mRestoredFromState;
	
	/** �����б� **/
	private List<BPWindow> mWindowList;
	
	/** ��ǰ���� **/
	private BPWindow mCurrentWindow;
	
	/** ������߶� **/
	private int mSearchBoxHeight;

	/** �������߶� **/
	private int mToolbarHeight;
	
	/** �������߶� **/
	private int mProgressHeight;
	
	/** ������ **/
	private FakeProgressBar mProgressBar;
	
	/** �������������ڿ��ƴ��ڵ��л� */
	private BdWindowWrapper mWindowWrapper;
	
	/** Browser ���� */
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
	 * @Description: ����״̬��
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
	 * @Description:�����ҳ��ظ�״̬ 
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
	 * @Description: �������� 
	 * @param focus �Ƿ��л�
	 * @return BPWindow
	 */
	public BPWindow createWindow(boolean focus) {
		return createWindow(focus, null);
	}
	
	/**
	 * @Title: createWindow 
	 * @Description: �������� 
	 * @param focus �Ƿ��л����ô���
	 * @param savedState �����Ϊnull������лָ�״̬
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
	 * @Description: ֱ���л������� 
	 * @param  aWindow    
	 */
	public void swapWindowToFocus(BPWindow aWindow) {
	    swapWindowToFocus(aWindow, WindowStwitchAnimation.NONE, null);
	}
	
	/**
	 * @Title: swapWindowToFocus 
	 * @Description: �л����� ���л�������searchURL Ϊ����url
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
	 * @Description: ֮������������ʼresume֮ǰ���ϵȴ���׼��������������
	 * 				 ��Ϊ��ȷ����CurrentWindow��Activity��ȫresume���200���� �Ժ���ִ��resume
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
	 * @Description: ֮������������ʼresume֮ǰ���ϵȴ���׼����������������Ϊ��ȷ����CurrentWindow��Activity��ȫresume���200���� �Ժ���ִ��resume
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
     * @Description: ������ǰwindow   
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
	 * @Description: ���ø� BPBrowser
	 * @param  bpBrowser   
	 */
	public void setBrowser(BPBrowser bpBrowser) {
		this.mBrowser = bpBrowser;
	}
	
	/**
	 * @Title: closeSelectedMenu 
	 * @Description: �رջ����˵�    
	 */
	public void closeSelectedMenu() {
	    if (mCurrentWindow != null) {
	        mCurrentWindow.closeSelectedMenu();
	    }		
	}
	/**
	 * @Title: onPause 
	 * @Description: BPFrameView ��ͣ    
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
	 * @Description: �ͷ��ڴ�   
	 */
	public void freeMemory() {
		for (int i = 0; i < mWindowList.size(); i++) {
			mWindowList.get(i).freeMemory();
		}		
	}
	
	/**
	 * @Title: release 
	 * @Description: �ͷ��ڴ�  
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
	 * @Description: ��ʷ����   
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
	 * @Description: ��ʷǰ��    
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
	 * @Description: �ж��Ƿ�ǰ��
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
	 * @Description: �ж��Ƿ���� 
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
	 * @Description: ����URL 
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
	 * @Description: ��ȡ��ǰ���� 
	 * @return  BPWindow
	 */
	public BPWindow getCurrentWindow() {
		return mCurrentWindow;
	}

	/**
	 * @Title: createNewWindowOpenUrl 
	 * @Description: �������� 
	 * @param url ��򿪵�url
	 * @param current �����½����ڵĴ���
	 * @param loadImmediately �Ƿ�����load
	 * @param bundle ������״̬   
	 */
	public void createNewWindowOpenUrl(String url, BPWindow current, boolean loadImmediately,
			Bundle bundle) {
		// TODO Auto-generated method stub
	}

	/**
	 * @Title: reload 
	 * @Description: ��������ҳ��   
	 */
	public void reload() {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.reload();		
	}

	/**
	 * @Title: stopLoading 
	 * @Description: ֹͣ����   
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
	 * @Description: �����ʷ    
	 */
	public void clearHistory() {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.clearHistory();		
	}

	/**
	 * @Title: setUpSelect 
	 * @Description: ����ѡ��ģʽ    
	 */
	public void setUpSelect() {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.setUpSelect();		
	}

	/**
	 * @Title: getBrowser 
	 * @Description: ����BPBrowser 
	 * @return  BPBrowser
	 */
	public BPBrowser getBrowser() {
		return mBrowser;
	}

	/**
	 * @Title: updateState 
	 * @Description: ˢ�´���״̬ �ײ��������� 
	 * @param bpWindow   
	 */
	public void updateState(BPWindow bpWindow) {
		//TODO 
	}

	/**
	 * @Title: onSelectionSearch 
	 * @Description: ��������
	 * @param aSelection   
	 */
	public void onSelectionSearch(String aSelection) {
		mBrowser.onSelectionSearch(aSelection);		
	}

	/**
	 * @Title: openFileChooser 
	 * @Description: �ϴ��ļ� ʱ���ļ���
	 * @param uploadMsg
	 * @param acceptType   
	 */
	public void openFileChooser(BValueCallback<Uri> uploadMsg,
			String acceptType) {
		mBrowser.openFileChooser(uploadMsg, acceptType);
	}

	/**
	 * @Title: switchBetweenHomeAndBrowser 
	 * @Description: ���ݵ�ǰ����״̬����home��SearchBrowser֮���л�
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
	 * @Description: �ڲ������´��ڣ�����ָ ^^
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
	 * @Description: ��ȡ�����б�
	 * @return List<BPWindow>
	 */
	public List<BPWindow> getWindowList() {
		return mWindowList;
	}

	/**
	 * @Title: webviewScrollBy 
	 * @Description: ����webview���� 
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
	 * @Description: ����webview���� 
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
	 * @Description: ����webview headView
	 * @param aView   
	 */
	public void addWebViewTitle(View aView) {
	    if (mCurrentWindow == null) {
            return;
        }
		mCurrentWindow.setEmbeddedTitleBar(aView);		
	}

}
