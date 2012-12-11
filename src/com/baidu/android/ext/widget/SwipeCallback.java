package com.baidu.android.ext.widget;

import android.view.View;

/**
 * ��ִ���϶�ɾ����Ŀ��View����ؽӿڡ�
 * @author qumiao
 *
 */
public interface SwipeCallback {
    
    /**
     * ��ȡʵ�ָýӿڵ���View��
     * @return content view.
     */
    View getContentView();
    
    /**
     * ����Adapter��
     * @param adapter SwipeApdater.
     */
    void setAdapter(SwipeAdapter adapter);
    
    /**
     * ��ȡAdapter��
     * @return SwipeAdapter.
     */
    SwipeAdapter getAdapter();
    
    /**
     * ɾ��child�Ļص���
     * @param v child
     */
    void onChildDismissed(View v);
    
    /**
     * �϶�child�Ļص���
     * @param v child
     */
    void onBeginDrag(View v);
    
    /**
     * ȡ���϶��Ļص���
     * @param v child
     */
    void onDragCancelled(View v);
    
    
    /**
     * ��ȡchild��container�е�λ�á�
     * @param v child
     * @return index.
     */
    int getSwipeChildIndex(View v);
    
    /**
     * ��ȡָ�����괦��child��
     * @param x X
     * @param y Y
     * @return child.
     */
    View getSwipeChildAtPosition(int x, int y);
    
    /**
     * ��ȡָ����������child��
     * @param index index
     * @return child.
     */
    View getSwipeChildAt(int index);
    
    /**
     * ��ȡ���϶���child������
     * @return count.
     */
    int getSwipeChildCount();
    
    /**
     * ��ȡindex����child�������ꡣ
     * @param index index
     * @return �����ꡣ
     */
    int getSwipeChildLeftAt(int index);
    
    /**
     * ��ȡindex����child�������ꡣ
     * @param index index
     * @return �����ꡣ
     */
    int getSwipeChildTopAt(int index);
    
    
    /**
     * ��ȡ�ɼ������еĵ�һ��child��������
     * @return child������
     */
    int getSwipeFirstVisiblePosition();
    
    /**
     * ��ȡ�ɼ������е����һ��child��������
     * @return child������
     */
    int getSwipeLastVisiblePosition();
    
    
    /**
     * ���ù���������
     * @param listener ������
     */
    void setSwipeScrollListener(SwipeScrollListener listener);
    
    /**
     * ����������
     * @author qumiao
     *
     */
    interface SwipeScrollListener {
        
        /**
         * �������λ�á�
         */
        void computeScroll();
        
        /**
         * ����λ�ñ仯��
         * @param l left
         * @param t top 
         * @param oldl old left
         * @param oldt old top
         */
        void onScrollChanged(int l, int t, int oldl, int oldt);
        
    }
}
