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
 *  Homeҳ��ǰ��һ��Fragment��Home�ࣩ����������ڽ������ܺ󣬳�ΪBdWindow��һ���Ӻ��ӣ�HomeRoot->HomeView��
 *  HomeView�ڱ�Ӧ����Ӧ����һ����ʵ����View�����λRD��Ҫʹ�ù��췽������xml����HomeViewʵ����Ӧ��ʹ��getInstance()��á�
 *  HomeView��չʾ��ҳ���ֵ�UI���ݣ�
 *  ���ٰ����������򡢹�����
 * @author LEIKANG 
 * @date 2012-12-5 ����3:36:24
 */
public class HomeView extends RelativeLayout{
    
    /** log tag. */
    private static final String TAG = "HomeView";
    /** debug ����. */
    private static final boolean DEBUG = true;
 
    /** ��BdFrameView **/
    private BPFrameView mFrameView;
    
    /** ��̬��ʵ������ */
    private static HomeView sInstance;
    
    /** 
     * ��õ�ʵ��������
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
     * ȷ��HomeView�ǵ�ʵ��
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
     * ����BdFrameView
     * 
     * @param aFrameView
     *            BdFrameView
     */
    public void setFrameView(BPFrameView aFrameView) {
        mFrameView = aFrameView;
    }
    
    /**
     * ���BdFrameView
     * @return mFrameView
     */
    public BPFrameView getFrameView() {
        return mFrameView;
    }
    
    /**
     * ��ʼ��UI�ϵ�Ԫ��
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
