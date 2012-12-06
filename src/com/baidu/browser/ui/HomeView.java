package com.baidu.browser.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.baidu.browser.framework.BPFrameView;
import com.baidu.player.R;

/**
 * @ClassName: HomeView 
 * @Description:
 *  Home页以前是一个Fragment（Home类），经整理后融进浏览框架后，成为BdWindow的一个子孩子（HomeRoot->HomeView）
 *  HomeView在本应用中应该是一个单实例的View，请各位RD不要使用构造方法或者xml生成HomeView实例，应该使用getInstance()获得。
 *  HomeView将展示主页部分的UI内容：
 *  不再包括：搜索框、工具栏
 * @author LEIKANG 
 * @date 2012-12-5 下午3:36:24
 */
public class HomeView extends RelativeLayout{
    
    /** log tag. */
    private static final String TAG = "HomeView";
    /** debug 开关. */
    private static final boolean DEBUG = true;
 
    /** 父BdFrameView **/
    private BPFrameView mFrameView;
    
    /** 静态单实例引用 */
    private static HomeView sInstance;
    
    /** 
     * 获得单实例的引用
     * @param context Context 
     * @return HomeView
     */
    public static HomeView getInstance(Context context) {
        if (null == sInstance) {
            sInstance = (HomeView) ((Activity) context).findViewById(R.id.homeview_id);
            if (sInstance == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                sInstance = (HomeView) inflater.inflate(R.layout.homeview, null);
                sInstance.setId(R.id.homeview_id);
            }
        }
        return sInstance;
    }
    
    /**
     * Constructor
     * @param context context
     */
    public HomeView(Context context) {
        super(context);
        checkInstance(context);
    }
    
    /**
     * Constructor
     * @param context context
     * @param attrs attrs
     */
    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkInstance(context);
    }
    
    /**
     * Constructor
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public HomeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        checkInstance(context);
    }
    
    /**
     * 确保HomeView是单实例
     * 
     * @param context Context
     */
    private void checkInstance(Context context) {
        if (null == sInstance) {
        	
        } else {
            throw new RuntimeException("HomeView should be Single Instance.");
        }
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
 
    }
    
    /**
     * 设置BdFrameView
     * 
     * @param aFrameView
     *            BdFrameView
     */
    public void setFrameView(BPFrameView aFrameView) {
        mFrameView = aFrameView;
    }
    
    /**
     * 获得BdFrameView
     * @return mFrameView
     */
    public BPFrameView getFrameView() {
        return mFrameView;
    }
    
    /**
     * 初始化UI上的元素
     */
    private void init() {
        if (DEBUG) {
            Log.d(TAG, "init()");
        }

    }

	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

}
