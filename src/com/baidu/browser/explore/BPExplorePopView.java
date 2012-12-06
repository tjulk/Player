package com.baidu.browser.explore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.baidu.player.R;

/**
 * @ClassName: BPExplorePopView 
 * @Description: WebView������ ���ฺ�������������ѡ�� ����/�����ȹ��� 
 * @author LEIKANG 
 * @date 2012-12-6 ����2:20:26
 */
public class BPExplorePopView extends LinearLayout implements OnClickListener {

	/**
	 * ����������ʾʱ��
	 */
	public static final int SELECTION_TOP_DUR = 3000;

	/**
	 * ��������������padding
	 */
	public static final int SELECTION_PADDING = 15;

	/**
	 * ���ư�ť
	 */
	private LinearLayout mCopyView;

	/**
	 * ������ť
	 */
	private LinearLayout mSearchView;

	/**
	 * ������x����
	 */
	private int mPopX;

	/**
	 * ������y����
	 */
	private int mPopY;

	/**
	 * ѡ��������߽�x����
	 */
	private int mPopLeftX;

	/**
	 * ѡ�������ұ߽�x����
	 */
	private int mPopRightX;

	/**
	 * ѡ�������ϱ߽�y����
	 */
	private int mPopTopY;

	/**
	 * ѡ�������±߽�y����
	 */
	private int mPopBottomY;

	/** ѡ����ı� */
	private String mSelection;

	/** BdExplorePopView������ */
	private BdExplorePopViewListener mListener;

	/**
	 * @param aContext
	 *            Context
	 */
	public BPExplorePopView(Context aContext) {
		this(aContext, null);
	}

	/**
	 * @param aContext
	 *            Context
	 * @param aAttrs
	 *            AttributeSet
	 */
	public BPExplorePopView(Context aContext, AttributeSet aAttrs) {
		super(aContext, aAttrs);
	}

	@Override
	protected void onFinishInflate() {
		mCopyView = (LinearLayout) findViewById(R.id.btn_wv_copy);
		mCopyView.setOnClickListener(this);
		mSearchView = (LinearLayout) findViewById(R.id.btn_wv_search);
		mSearchView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mCopyView)) { // ����
			if (mListener != null) {
				mListener.doSelectionCopy(mSelection);
			}
		} else if (v.equals(mSearchView)) { // ����
			if (mListener != null) {
				mListener.doSelectionSearch(mSelection);
			}
		}
	}

	/**
	 * @return the mPopX
	 */
	public int getPopX() {
		return mPopX;
	}

	/**
	 * @param aPopX
	 *            the mPopX to set
	 */
	public void setPopX(int aPopX) {
		mPopX = aPopX;
	}

	/**
	 * @return the mPopY
	 */
	public int getPopY() {
		return mPopY;
	}

	/**
	 * @param aPopY
	 *            the mPopY to set
	 */
	public void setPopY(int aPopY) {
		mPopY = aPopY;
	}

	/**
	 * @return the mPopLeftX
	 */
	public int getPopLeftX() {
		return mPopLeftX;
	}

	/**
	 * @param aPopLeftX
	 *            the mPopLeftX to set
	 */
	public void setPopLeftX(int aPopLeftX) {
		mPopLeftX = aPopLeftX;
	}

	/**
	 * @return the mPopRightX
	 */
	public int getPopRightX() {
		return mPopRightX;
	}

	/**
	 * @param aPopRightX
	 *            the mPopRightX to set
	 */
	public void setPopRightX(int aPopRightX) {
		mPopRightX = aPopRightX;
	}

	/**
	 * @return the mPopTopY
	 */
	public int getPopTopY() {
		return mPopTopY;
	}

	/**
	 * @param aPopTopY
	 *            the mPopTopY to set
	 */
	public void setPopTopY(int aPopTopY) {
		mPopTopY = aPopTopY;
	}

	/**
	 * @return the mPopBottomY
	 */
	public int getPopBottomY() {
		return mPopBottomY;
	}

	/**
	 * @param aPopBottomY
	 *            the mPopBottomY to set
	 */
	public void setPopBottomY(int aPopBottomY) {
		mPopBottomY = aPopBottomY;
	}

	/**
	 * @param aSelection
	 *            ѡ����ı�
	 */
	protected void setSelection(String aSelection) {
		mSelection = aSelection;
	}

	/**
	 * ��ȡѡ����ı�
	 * 
	 * @return ѡ����ı�
	 */
	protected String getSelection() {
		return mSelection;
	}

	/**
	 * ����BdWebPoolView�ļ�����
	 * 
	 * @param aListener
	 *            BdWebPoolView�ļ�����
	 */
	public void setEventListener(BdExplorePopViewListener aListener) {
		mListener = aListener;
	}

	/**
	 * BdExplorePopView������
	 */
	public interface BdExplorePopViewListener {

		/**
		 * ���ƽӿ�
		 * 
		 * @param aSelection
		 *            ѡ����ı�
		 */
		void doSelectionCopy(String aSelection);

		/**
		 * �����ӿ�
		 * 
		 * @param aSelection
		 *            ѡ����ı�
		 */
		void doSelectionSearch(String aSelection);

		/**
		 * ȡ���ӿ�
		 */
		void doSelectionCancel();
	}

}
