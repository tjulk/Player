package com.baidu.player.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * @ClassName: HomeScrollView 
 * @Description: ��ҳ�ɹ������֣���������������������
 * @author LEIKANG 
 * @date 2012-12-7 ����2:42:19
 */
public class HomeScrollView extends ScrollView {
    /** ���й���ʱ��ʱ�� */
    private static final int SCROLL_TIME = 300;
    
    /** ��Ҫ��������View */
    private View mViewToScrollTo;
    /** rect */
    private Rect mRect = new Rect();
    /**
     * ��ҳ��������Ӱ
     */
    private ImageView mShadow;
    
    /**
     * ����������scroller.
     */
    private Scroller mScroller = new Scroller(getContext(),
            new android.view.animation.AccelerateDecelerateInterpolator());

    /**
     * @param context
     *            context
     */
    public HomeScrollView(Context context) {
        super(context);
    }

    /**
     * @param context
     *            context
     * @param attrs
     *            attrs
     */
    public HomeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    /**
     * @param context
     *            context
     * @param attrs
     *            attrs
     * @param defStyle
     *            defStyle
     */
    public HomeScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mViewToScrollTo != null
                && mViewToScrollTo.getVisibility() == View.VISIBLE) {
            mRect.left = 0;
            mRect.right = mViewToScrollTo.getRight();
            mRect.bottom = mViewToScrollTo.getHeight();
            mRect.top = 0;
            offsetDescendantRectToMyCoords(mViewToScrollTo, mRect);
            int toScroll = mRect.top - getScrollY();
            mScroller.abortAnimation();
            mScroller.startScroll(0, getScrollY(), 0, toScroll, SCROLL_TIME);
            mViewToScrollTo = null;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int scrollY = mScroller.getCurrY();
            scrollTo(0, scrollY);
            postInvalidate();
        } else {
            super.computeScroll();
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
        }
        return super.onTouchEvent(ev);
    }
    /**
     * ��һ��VisibiltyΪGONE��View��ʾ��������������һ��ָ����View�Ķ�����
     * 
     * @param toSpan
     *            ��Ҫչ����View����ʱ��״̬������GONE��
     * @param toScrollTo
     *            ��Ҫ��������View��������HomeScrollView����View
     */
    public void spanView(View toSpan, View toScrollTo) {
        toSpan.setVisibility(View.VISIBLE);
        mViewToScrollTo = toScrollTo;
    }
    
    /**
     * ʹ���Զ����scroller������ָ��λ��
     * @param scrollToY ��Ҫ��������λ��
     */
    public void customScrollTo(int scrollToY) {
        int toScroll = scrollToY - getScrollY();
        mScroller.abortAnimation();
        mScroller.startScroll(0, getScrollY(), 0, toScroll, SCROLL_TIME);
        postInvalidate();
    }
    
    /**
     * ����������
     * @param shadow searchbox��Ӱ
     */
    public void setSearchBoxShadow(ImageView shadow) {
        mShadow = shadow;
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mShadow != null) {
            if (oldt == 0 && t != 0) {
                mShadow.setVisibility(View.VISIBLE);
            }
            if (oldt != 0 && t == 0) {
                mShadow.setVisibility(View.INVISIBLE);
            }
        }
 
    }
    
    /**
     * ��ͼ��
     */
    private Bitmap mSnapshot;
    
    /**
     * ���ڽ�ͼ
     * @param width ��ͼ���
     * @param height ��ͼ�߶�
     * @param useHomeCache �Ƿ��ڽ�ͼʱ���棬���ڶര��ʱΪtrue
     * @return ���ڽ�ͼ
     */
    public Bitmap captureSnapshot(int width, int height, boolean useHomeCache) {
        if (useHomeCache && mSnapshot != null && !mSnapshot.isRecycled()) {
            return mSnapshot;
        }
        if (getWidth() > 0 && getHeight() > 0) {
            try {
 
                Bitmap capture = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                capture.eraseColor(Color.WHITE);
                Canvas c = new Canvas(capture);
                final int left = getScrollX();
                final int top = getScrollY();
                int state = c.save();
                float scale = capture.getWidth() / (float) getWidth();
                c.scale(scale, scale);
                c.clipRect(0, 0, getWidth(), (int) (height / scale)); //�������뷨����ʱ���߶Ȼ��С����ʹ�ÿ�ȷ���
                c.translate(-left, -top);
                draw(c);
                c.restoreToCount(state);
                
                if (useHomeCache) {
                    mSnapshot = capture;
                }
                return capture;
            } catch (Exception e) { 
                //��Ϊ�ڻ�ͼ�����У����ܲ�����ָ�룬Ҳ���ܳ����ڴ治�㣬���ڴ�ͳһ����
                return null;
            }
        } else {
            return null;
        }

    }
    /**
     * �����ͼ
     */
    public void clearSnapshot() {
        if (mSnapshot != null) {
            mSnapshot = null;
        }
    }
}
