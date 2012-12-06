/** 
 * Filename:    BdExploreView.java
 * Description:  
 * Copyright:   Baidu MIC Copyright(c)2011 
 * @author:     CoCoMo 
 * @version:    1.0
 * Create at:   2012-4-20 ����11:21:26
 * 
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2012-4-20    CoCoMo      1.0         1.0 Version 
 */
package com.baidu.browser.explore;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.baidu.browser.core.util.BdLog;
import com.baidu.browser.core.util.BdUtil;
import com.baidu.browser.explore.BPExplorePopView.BdExplorePopViewListener;
import com.baidu.browser.webpool.BPWebPoolView;
import com.baidu.player.R;
import com.baidu.webkit.sdk.BWebView.BHitTestResult;

/**
 * @ClassName: BPExploreView 
 * @Description: ���ҳ
 * @author LEIKANG 
 * @date 2012-12-6 ����2:22:09
 */
public class BPExploreView extends BPWebPoolView implements BdExplorePopViewListener {

	/**
	 * ����������
	 */
	private BPExplorePopView mPopupWindow;

	/**
	 * �¼�������
	 */
	private BPExploreViewListener mListener;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Context
	 */
	public BPExploreView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Context
	 * @param attrs
	 *            AttributeSet
	 */
	public BPExploreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Context
	 * @param attrs
	 *            AttributeSet
	 * @param defStyle
	 *            DefaultStyle
	 */
	public BPExploreView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	/**
	 * ��ʼ����
	 * @param context Context
	 */
	private void init(Context context) {

        mPopupWindow = (BPExplorePopView) LayoutInflater.from(getContext()).inflate(
                R.layout.browser_copy_search_view, null);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(mPopupWindow, 1, params);
        mPopupWindow.setVisibility(GONE);
        mPopupWindow.setEventListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child instanceof BPExplorePopView) {
				BdLog.d(changed + ", " + left + ", " + top + ", " + right + ", " + bottom);
				final int childWidth = child.getMeasuredWidth();
				final int childHeight = child.getMeasuredHeight();
				BdLog.d(childWidth + ", " + childHeight);
				// ���㵯����λ��
				BPExplorePopView popView = (BPExplorePopView) child;
				calcPopWindowPos(popView);
				if (popView != null) {
					left += popView.getPopX();
					top += popView.getPopY();
				}
				right = left + childWidth;
				bottom = top + childHeight;
				child.layout(left, top, right, bottom);
			} else {
				super.onLayout(changed, left, top, right, bottom);
			}
		}
	}

	/**
	 * ���㵯����λ��
	 * 
	 * @param aPopView
	 *            BdExplorePopView
	 */
	private void calcPopWindowPos(BPExplorePopView aPopView) {
		int mPopLeftX = aPopView.getPopLeftX();
		int mPopRightX = aPopView.getPopRightX();
		int mPopTopY = aPopView.getPopTopY();
		int mPopBottomY = aPopView.getPopBottomY();

		if (mPopLeftX > mPopRightX) {
			int temp = mPopRightX;
			mPopRightX = mPopLeftX;
			mPopLeftX = temp;
		}

		if (mPopTopY > mPopBottomY) {
			int temp = mPopBottomY;
			mPopBottomY = mPopTopY;
			mPopTopY = temp;
		}

		BdLog.d(mPopLeftX + ", " + mPopRightX + ", " + mPopTopY + ", " + mPopBottomY);

		int popupWindowWidth = aPopView.getMeasuredWidth();
		int webViewWidth = getWidth();
		int mPopX = (mPopLeftX + mPopRightX - popupWindowWidth) / 2;
		if ((mPopX + popupWindowWidth) > webViewWidth) {
			mPopX = webViewWidth - popupWindowWidth;
		}
		if (mPopX < 0) {
			mPopX = 0;
		}

		int popupWindowHeight = aPopView.getMeasuredHeight();
		int webViewHeight = getHeight();
		popupWindowHeight += BdUtil.dip2pix(getContext(), BPExplorePopView.SELECTION_PADDING);
		int mPopY = mPopTopY - popupWindowHeight;
		if (mPopY < 0) {
			mPopY = mPopBottomY + BdUtil.dip2pix(getContext(), BPExplorePopView.SELECTION_PADDING); // ��ֹ��ס���
			// ��������ѡ�������·�
			aPopView.setBackgroundResource(R.drawable.browser_select_menu_up_bg);
		} else {
			// ��������ѡ�������Ϸ�
			aPopView.setBackgroundResource(R.drawable.browser_select_menu_down_bg);
		}

		if ((mPopY + popupWindowHeight) > webViewHeight) {
			mPopY = mPopBottomY - mPopTopY - popupWindowHeight;
		}

		aPopView.setPopX(mPopX);
		aPopView.setPopY(mPopY);
		BdLog.d(mPopX + ", " + mPopY);
	}

	@Override
	public void doSelectionCopy(String aSelection) {
		doSelectionCancel();
		if (aSelection.length() > 0) {
			Toast t = Toast.makeText(getContext(), R.string.text_selection_ok_tip,
					BPExplorePopView.SELECTION_TOP_DUR);
			t.show();
		} else {
			Toast t = Toast.makeText(getContext(), R.string.text_selection_fail_tip,
					BPExplorePopView.SELECTION_TOP_DUR);
			t.show();
		}
	}

	@Override
	public void doSelectionSearch(String aSelection) {
		doSelectionCancel();
		if (aSelection != null && aSelection.length() > 0) {
			BdLog.i("start to search.");
			// ȥ�գ�����encode�������
			aSelection = aSelection.trim();
			if (mListener != null) {
				mListener.onSelectionSearch(aSelection);
			}
		} else {
			Toast t = Toast.makeText(getContext(), R.string.text_selection_fail_tip,
					BPExplorePopView.SELECTION_TOP_DUR);
			t.show();
		}
	}

	@Override
	public void doSelectionCancel() {
		if (mPopupWindow != null) {
			mPopupWindow.setVisibility(GONE);
		}
		setExtendSelection(false);
		setTouchSelection(false);
		setShiftIsPressed(false);
		setSelectingText(false);
		boolean extendSelection = getExtendSelection();
		BdLog.d("extendSelection: " + extendSelection);
		boolean touchSelection = getTouchSelection();
		BdLog.d("touchSelection: " + touchSelection);
		boolean shiftIsPressed = getShiftIsPressed();
		BdLog.d("shiftIsPressed: " + shiftIsPressed);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		BdLog.d("action: " + action);
		boolean retValue = mGestureDetector.onTouchEvent(event);
		if (!retValue) {
			// ���˫ָ�������򲻽��л�������
			if (event.getPointerCount() > 1) {
				setExtendSelection(false);
			} else {
				boolean extendSelection = getExtendSelection();
				boolean selectingText = getSelectingText();
				if (action == MotionEvent.ACTION_DOWN) {
					if (extendSelection || selectingText) {
						if (mPopupWindow != null) {
							int touchX = (int) event.getX();
							int touchY = (int) event.getY();
							// �����һ�λ��ʣ���¼���ϱ�λ��
							String oldSelection = mPopupWindow.getSelection();
							if (oldSelection == null || oldSelection.length() == 0) {
								mPopupWindow.setPopLeftX(touchX);
								mPopupWindow.setPopTopY(touchY);
							}
						}
					}
				}
				if (action == MotionEvent.ACTION_UP) {
					if (extendSelection || selectingText) {
						if (mPopupWindow != null) {
							mPopupWindow.requestLayout();
							int touchX = (int) event.getX();
							int touchY = (int) event.getY();
							// �����һ�λ��ʣ���¼���±�λ��
							String oldSelection = mPopupWindow.getSelection();
							if (oldSelection == null || oldSelection.length() == 0) {
								mPopupWindow.setPopRightX(touchX);
								mPopupWindow.setPopBottomY(touchY);
							} // SUPPRESS CHECKSTYLE
								// ������µ�λ�ã����ұ߾�Ͻ����򻬶������ҹ�꣬���򻬶���������
							else {
								int touchMiddle = (mPopupWindow.getPopLeftX() + mPopupWindow.getPopRightX()) / 2;
								if (touchX > touchMiddle) {
									mPopupWindow.setPopRightX(touchX);
									mPopupWindow.setPopBottomY(touchY);
								} else {
									mPopupWindow.setPopLeftX(touchX);
									mPopupWindow.setPopTopY(touchY);
								}
							}
						}
						// ����������
						if (Build.VERSION.SDK_INT < 14) { // SUPPRESS CHECKSTYLE
							return onUp();
						}
					}
				}
				if (action == MotionEvent.ACTION_MOVE) {
					// bugfix#SEARHBOX-795
					if (extendSelection || selectingText) {
						mPopupWindow.setVisibility(GONE);
					}
				}
			}
			return superOnTouchEvent(event);
		} else {
			return retValue;
		}

	}

	@Override
	public boolean onLongClick(View v) {
		BHitTestResult result = getHitTestResult();
		if (result == null) {
			return false;
		}
		if (mListener != null && !checkPopMenuStatus()) {
			mListener.onLongPress(result);
		}
		int type = result.getType();
		BdLog.d("type: " + type);
		return !(type == BHitTestResult.UNKNOWN_TYPE);
	}

	/**
	 * ����up�¼�
	 * 
	 * @return true��ʾ���ѵ�up�¼�����֮��Ȼ
	 */
	private boolean onUp() {
		boolean extendSelection = getExtendSelection();
		boolean touchSelection = getTouchSelection();
		boolean shiftIsPressed = getShiftIsPressed();
		BdLog.d("extendSelection: " + extendSelection);
		BdLog.d("touchSelection: " + touchSelection);
		BdLog.d("shiftIsPressed: " + shiftIsPressed);
		BdLog.d("popLeftX: " + mPopupWindow.getPopLeftX());
		BdLog.d("popRightX: " + mPopupWindow.getPopRightX());
		BdLog.d("popTopY: " + mPopupWindow.getPopTopY());
		BdLog.d("popBottomY: " + mPopupWindow.getPopBottomY());

		boolean selectingText = getSelectingText();
		boolean drawSelectionPointer = getDrawSelectionPointer();
		if (extendSelection || (selectingText && !drawSelectionPointer)) {
			String selection = getSelection();
			BdLog.i(selection);

			BdLog.d("now show popup window.");
			showPopWindow();
			setDrawSelectionPointer(true);
			mPopupWindow.setSelection(selection);
			return true;
		} else {
			return true;
		}
	}

	/**
	 * ����Ƿ񵯳���PopMenu
	 * 
	 * @return boolean
	 */
	public boolean checkPopMenuStatus() {
		boolean extendSelection = getExtendSelection();
		boolean selectingText = getSelectingText();
		return extendSelection || selectingText;
	}

	/**
	 * ����������
	 */
	private void showPopWindow() {
		if (mPopupWindow != null && mPopupWindow.getVisibility() != VISIBLE) {
			mPopupWindow.setVisibility(VISIBLE);
		}
	}

	/**
	 * ����BdExploreView�ļ�����
	 * 
	 * @param aListener
	 *            BdExploreView�ļ�����
	 */
	public void setEventListener(BPExploreViewListener aListener) {
		mListener = aListener;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // <add begin by caohaitao 20120907, �����Ƶ���Ų���ȫ�����⡣
        // �Ȳ鿴�����ܷ���
        boolean result = super.onKeyDown(keyCode, event);
        if (result) {
            return true;
        }
        // add end>

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// �����ѡ��ģʽ����ȡ��
			if (checkPopMenuStatus()) {
				doSelectionCancel();
				return true;
			}
			//����
			if (canGoBack()) {
				goBack();
				return true;
			}
		}
		return false;
	}

	/**
	 * ��ƵҪ�򿪣�������
	 * 
	 * @param url
	 *            ��Ƶ��ַ
	 * @param contentDisposition
	 *            contentDisposition
	 * @param mimetype
	 *            ��ƵMimeType
	 * @return �Ƿ������Ƶ
	 */
	public boolean openVideoOnDownloadStart(String url, String contentDisposition, String mimetype) {
		// ֻ����Ƶ�ļ�����������������
		if (mimetype != null && mimetype.startsWith("video/")) {
			if (contentDisposition == null || !contentDisposition.regionMatches(true, 0, "attachment", 0, 10)) { // SUPPRESS CHECKSTYLE
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(url), mimetype);
				Context context = getContext();
				// ��ѯ�Ƿ�����п��Դ򿪴����͵�Ӧ�ó�������У��ø�Ӧ�ó���򿪴�����
				ResolveInfo info = getContext().getPackageManager().resolveActivity(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
				if (info != null) {
					if (context instanceof Activity) {
						Activity act = (Activity) context;
						ComponentName myName = act.getComponentName();
						if (!myName.getPackageName().equals(info.activityInfo.packageName)
								|| !myName.getClassName().equals(info.activityInfo.name)) {
							try {
								act.startActivity(intent);
								return true;
							} catch (ActivityNotFoundException e) {
								BdLog.e("Activity not found." + mimetype);
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * �������Ʋ���
	 */
	final GestureDetector mGestureDetector = new GestureDetector(new SimpleOnGestureListener() {

		@Override
		public void onLongPress(MotionEvent e) {
			BHitTestResult result = getHitTestResult();
			if (result == null) {
				return;
			}
			int type = result.getType();
			BdLog.d("type: " + type);

			if (mListener != null && !checkPopMenuStatus()) {
				if (type == BHitTestResult.UNKNOWN_TYPE) {
					mListener.onLongPress(result);
				}
			}
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			BdLog.i("");
			// �����ѡ��ģʽ����ȡ��
			if (checkPopMenuStatus()) {
				doSelectionCancel();
			}
			return false;
		}
	});

	@Override
	public void onErrorPageGoBack() {
		goBack();
	}

	@Override
	public void onErrorPageRefresh() {
		reload();
	}

}
