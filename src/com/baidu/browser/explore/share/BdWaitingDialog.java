package com.baidu.browser.explore.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.player.R;

/**
 * �ȴ���ʾ��
 */
public class BdWaitingDialog extends Dialog {
	/**
	 * ���ִ�С
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * ����PADDING
	 */
	private static final int MESSAGE_PADDING = 12;

	/**
	 * ��ʾ����
	 */
	private String mMessage;
	/**
	 * Context
	 */
	private Context mContext;

	/**
	 * @param aContext
	 *            Context
	 */
	public BdWaitingDialog(Context aContext) {
		super(aContext, R.style.BdWaitingDialog);

		mContext = aContext;
	}

	/**
	 * @param context
	 *            Context
	 * @param message
	 *            ��ʾ����
	 * @return BdWaitingDialogʵ��
	 */
	public static BdWaitingDialog show(Context context, CharSequence message) {
		return show(context, message, false);
	}

	/**
	 * @param context
	 *            Context
	 * @param message
	 *            ��ʾ����
	 * @param cancelable �Ƿ��ȡ��
	 * @return BdWaitingDialogʵ��
	 */
	public static BdWaitingDialog show(Context context, CharSequence message, boolean cancelable) {
		return show(context, message, cancelable, null);
	}

	/**
	 * @param context Context
	 * @param message ��ʾ����
	 * @param cancelable �Ƿ��ȡ��
	 * @param cancelListener ȡ��������
	 * @return BdWaitingDialogʵ��
	 */
	public static BdWaitingDialog show(Context context, CharSequence message, boolean cancelable,
			OnCancelListener cancelListener) {
		BdWaitingDialog dialog = new BdWaitingDialog(context);
		dialog.setMessage(message);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.show();
		return dialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new BdWaitingView(mContext));
	}

	/**
	 * @param aMessage ��ʾ����
	 */
	public void setMessage(String aMessage) {
		mMessage = aMessage;
	}

	/**
	 * @param aMessage ��ʾ����
	 */
	public void setMessage(CharSequence aMessage) {
		mMessage = aMessage.toString();
	}

	/**
	 * @param aResId ��ʾ������Դid
	 */
	public void setMessage(int aResId) {
		mMessage = mContext.getResources().getString(aResId);
	}

	/**
	 * �Զ���ȴ���ʾ�ؼ�
	 */
	public class BdWaitingView extends LinearLayout {

		/**
		 * ���ִ�С
		 */
		private int mTextSize;
		/**
		 * ����PADDING
		 */
		private int mMessagePadding;
		/**
		 * ��Ļ���
		 */
		private int mScreenWidth;

		/**
		 * Context
		 */
		private Context mContext;

		/**
		 * @param aContext Context
		 */
		public BdWaitingView(Context aContext) {
			super(aContext);

			mContext = aContext;

			loadConfiguration();

			layoutDesign();
		}

		/**
		 * ��ʼ������
		 */
		private void loadConfiguration() {
			DisplayMetrics dm = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
			float density = dm.density;
			mScreenWidth = dm.widthPixels;

			mTextSize = (int) (TEXT_SIZE * Math.sqrt(density));
			mMessagePadding = (int) (MESSAGE_PADDING * density);
		}

		/**
		 * ��ʼ������
		 */
		private void layoutDesign() {
			setOrientation(LinearLayout.VERTICAL);
			FrameLayout.LayoutParams lParams0 = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			setLayoutParams(lParams0);
			setBackgroundColor(Color.TRANSPARENT);

			ProgressBar progressBar = new ProgressBar(mContext);
			progressBar.setBackgroundColor(Color.TRANSPARENT);
			LinearLayout.LayoutParams lParams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			lParams1.gravity = Gravity.CENTER;
			progressBar.setLayoutParams(lParams1);
			addView(progressBar);
			TextView textView = new TextView(mContext);
			textView.setText(mMessage);
			LinearLayout.LayoutParams lParams2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			lParams2.gravity = Gravity.CENTER;
			lParams2.width = mScreenWidth;
			textView.setGravity(Gravity.CENTER);
			textView.setLayoutParams(lParams2);
			textView.setBackgroundColor(Color.TRANSPARENT);
			textView.setPadding(mMessagePadding, mMessagePadding, mMessagePadding, mMessagePadding);
			textView.setTextSize(mTextSize);
			textView.setTextColor(0xffe5e5e5); // SUPPRESS CHECKSTYLE
			addView(textView);
		}
	}

}
