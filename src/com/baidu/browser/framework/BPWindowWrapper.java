package com.baidu.browser.framework;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.baidu.browser.framework.BPFrameView.WindowStwitchAnimation;

/**
 * @ClassName: BdWindowWrapper 
 * @Description: ����װ��BdWindow��ViewGroup��ͬʱ֧�ֶ�����ʾ�л�Ч����
 * @author LEIKANG 
 * @date 2012-12-7 ����3:33:36
 */
public class BPWindowWrapper extends FrameLayout {
    /** ��ǰ���� */
    private BPWindow mCurrentWindow;
    /**
     *  ��Ҫ�رյĴ��ڣ���resetWrapper()�н��йر� 
     *  ֻ�ڹرմ��ڲ��ҷ�����ҳʱ��ʹ�á�
     * */
    private BPWindow mWindowToClose;

    /**
     * @param context
     *            context
     * @param attrs
     *            attrs
     * @param defStyle
     *            defStyle
     */
    public BPWindowWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 
     * @param context
     *            context
     * @param attrs
     *            attrs
     */
    public BPWindowWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     *            context
     */
    public BPWindowWrapper(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
    }

    /**
     * ��ʾһ��BdWindow��ͬʱ�Ƴ���ǰWindow��
     * 
     * @param window
     *            ��Ҫ��ʾ��window.
     * 
     * @param animation
     *            �л�����Ч������ΪNONE���ʾû�ж���
     * 
     * @param url
     *            ������ɺ���Ҫ���ص�url
     */
    public void showWindow(BPWindow window, WindowStwitchAnimation animation,
            String url) {
        if (window == null || window == mCurrentWindow) {
            return;
        }
        
        resetWrapper();
        removeCallbacks(mResetWrapperAction);
        
        if (animation == WindowStwitchAnimation.CLOSE_WINDOW) {
            
            if (mCurrentWindow != null) {
                mCurrentWindow.setVisibility(View.INVISIBLE);
                removeView(mCurrentWindow);
            }
            
            mCurrentWindow = window;
            window.setVisibility(View.VISIBLE);
            addViewSafely(window, 0);
            window.requestFocus();
            
        } else if (animation == WindowStwitchAnimation.NEW_WINDOW) {
            mCurrentWindow = window;
            window.setVisibility(View.INVISIBLE);
            addViewSafely(window);
            window.requestFocus();
 
        } else {
            addViewSafely(window);
            
            if (mCurrentWindow != null) {
                removeView(mCurrentWindow);
            }
            
            mCurrentWindow = window;
            
            if (!TextUtils.isEmpty(url)) {
                mCurrentWindow.loadUrl(url);
            }
            
            if (mCurrentWindow != null) {
                mCurrentWindow.requestFocus();
            }
            post(mResetWrapperAction);
        }
    }
    
    /**
     * add view safely
     * @param child child
     */
    private void addViewSafely(View child) {
        ViewParent parent = child.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(child);
        }

        addView(child);
    }
    
    /**
     * add view safely
     * @param child child
     * @param index index
     */
    private void addViewSafely(View child, int index) {
        ViewParent parent = child.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(child);
        }

        addView(child, index);
    }
    
    /**
     * 
     * �ڶ�����ʼǰ����BdWindowWrapper���õ�һ��״̬����ֻ������ǰ���ڡ�
     */
    private void resetWrapper() {
        mViewsToRemove.clear();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            
            if (child != null && child == mCurrentWindow) {
                if (child.getVisibility() != View.VISIBLE) {
                    child.setVisibility(View.VISIBLE);
                }
                continue;
            }
            mViewsToRemove.add(child);
        }
        
        for (View child : mViewsToRemove) {
            removeView(child);
            if (child != null && child == mWindowToClose) {
                mWindowToClose.release();
                mWindowToClose = null;
            }
        }
        
        mViewsToRemove.clear();
    }
    
    /**
     * ������Ҫɾ����View���б�
     */
    private ArrayList<View> mViewsToRemove = new ArrayList<View>();
    
    /**
     * ��Animation����ʱ��post��Action,����wrapper�е�view
     */
    private Runnable mResetWrapperAction = new Runnable() {
        
        @Override
        public void run() {
            resetWrapper();
        }
    };
    

    /**
     * ��֤wrapper�д���״̬���� ����Ҫ�ǽ�������������л��������
     */
    public void ensureWindow() {
        resetWrapper();
        removeCallbacks(mResetWrapperAction);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ensureWindow();
    }
}
