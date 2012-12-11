package com.baidu.android.ext.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * @ClassName: SwipeListView 
 * @Description: ���Ժ��򻬶�ɾ��item��ListView 
 * @author LEIKANG 
 * @date 2012-12-11 ����7:32:36
 */
public class SwipeListView extends ListView implements SwipeCallback {
    /**DEBUG.*/
    public static final boolean DEBUG = false;
    /**����ģ�ͣ�SwipeAdapter.*/
    private SwipeAdapter mAdapter;
    /**����Э��������ɾ���������ࡣ*/
    private SwipeHelper mSwipeHelper;
    /**����������*/
    private SwipeScrollListener mSwipeScrollListener;
    
    /**����ͬSDK�汾�µĺ�������������ࡣ*/
    private VersionedHelper mVersionedHelper;

    /**
     * ���췽����
     * @param context Context
     * @param attrs AttributeSet
     */
    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mVersionedHelper = VersionedHelper.getInstance();
        
        float densityScale = getResources().getDisplayMetrics().density;
        float pagingTouchSlop = mVersionedHelper.getScaledPagingTouchSlop(getContext());
        mSwipeHelper = new SwipeHelper(SwipeHelper.X, this, densityScale, pagingTouchSlop);
    }
    
    @Override
    public View getContentView() {
        return this;
    }
 
    @Override
    public void computeScroll() {
        if (mSwipeScrollListener != null) {
            mSwipeScrollListener.computeScroll();
        }
        super.computeScroll();
    }

    @Override
    public void removeViewInLayout(final View view) {
        mSwipeHelper.dismissChild(view, 0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mSwipeHelper.onInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mSwipeHelper.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public void onChildDismissed(View v) {
        
        //���Alpha����Ϊ��͸����������ListView�޷���ȷ����
        mVersionedHelper.setAlpha(v, 1);
        //���translation���赽ԭ�㣩������ListView�޷���ȷ����
        mVersionedHelper.setTranslationX(v, 0);
        
        int removePos = getSwipeChildIndex(v);
        
        if (removePos < 0) {
            return;
        }
        
        mAdapter.removeAndInsert(removePos);
        
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBeginDrag(View v) {
        // We do this so the underlying ScrollView knows that it won't get
        // the chance to intercept events anymore
        requestDisallowInterceptTouchEvent(true);
        mVersionedHelper.setActivated(v, true);
    }

    @Override
    public void onDragCancelled(View v) {
        mVersionedHelper.setActivated(v, false);
    }

    @Override
    public int getSwipeChildIndex(View v) {
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            if (v == this.getChildAt(i)) {
                return i + getFirstVisiblePosition();
            }
        }
        return -1;
    }
    
    @Override
    public View getSwipeChildAtPosition(int x, int y) {
        final float absoluteX = x + getScrollX();
        final float basoluteY = y + getScrollY();
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View item = this.getChildAt(i);
            if (item.getVisibility() == View.VISIBLE
                    && absoluteX >= item.getLeft() && absoluteX < item.getRight()
                    && basoluteY >= item.getTop() && basoluteY < item.getBottom()) {
                return item;
            }
        }
        return null;
    }
    
    @Override
    public int getSwipeChildCount() {
        return getChildCount();
    }
    
    @Override
    public View getSwipeChildAt(int index) {
        return getChildAt(index);
    }
    
    @Override
    public int getSwipeChildLeftAt(int index) {
        return getChildAt(index).getLeft();
    }
    
    @Override
    public int getSwipeChildTopAt(int index) {
        return getChildAt(index).getTop();
    }
    
    @Override
    public int getSwipeFirstVisiblePosition() {
        return getFirstVisiblePosition();
    }
    
    @Override
    public int getSwipeLastVisiblePosition() {
        return getLastVisiblePosition();
    }

    @Override
    public void setSwipeScrollListener(SwipeScrollListener listener) {
        mSwipeScrollListener = listener;
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mSwipeScrollListener != null) {
            mSwipeScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setScrollbarFadingEnabled(true);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        float densityScale = getResources().getDisplayMetrics().density;
        mSwipeHelper.setDensityScale(densityScale);
        float pagingTouchSlop = mVersionedHelper.getScaledPagingTouchSlop(getContext());
        mSwipeHelper.setPagingTouchSlop(pagingTouchSlop);
    }
 

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Skip this work if a transition is running; it sets the scroll values independently
        // and should not have those animated values clobbered by this logic
        if (mVersionedHelper.isTransitionRunning(this)) {
            return;
        }
    }

    @Override
    public void setAdapter(SwipeAdapter adapter) {
        mAdapter = adapter;
        super.setAdapter(mAdapter);
    }
    
    @Override
    public SwipeAdapter getAdapter() {
        return mAdapter;
    }
    
    /**
     * ����"ɾ��ֻ��һ����ʷ��¼ʱ�ᱨ��ָ���쳣"������
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

}
