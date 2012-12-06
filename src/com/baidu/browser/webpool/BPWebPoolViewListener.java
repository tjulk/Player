package com.baidu.browser.webpool;

import android.graphics.Bitmap;

/**
 * @ClassName: BPWebPoolViewListener 
 * @Description: BdWebPoolView������
 * @author LEIKANG 
 * @date 2012-12-6 ����2:26:56
 */
public class BPWebPoolViewListener {

	/**
	 * �Ƿ�����ҳ�����
	 * 
	 * @param view
	 *            ��ǰ���ص�BdWebPoolViewʵ��
	 * @param url
	 *            Ҫ���ص�Url
	 * @return true��ʾ����ҳ����أ����д���false��ʾ�����أ����ں˴���
	 */
	public boolean shouldOverrideUrlLoading(BPWebPoolView view, String url) {
		return false;
	}

	/**
	 * ��ʼ����ҳ��
	 * 
	 * @param view
	 *            ��ǰ���ص�BdWebPoolViewʵ��
	 * @param url
	 *            ��ʼ���ص�Url
	 * @param favicon
	 *            ��վfaviconͼ��
	 */
	public void onPageStarted(BPWebPoolView view, String url, Bitmap favicon) {
	}

	/**
	 * ҳ�������
	 * 
	 * @param view
	 *            ��ǰ���ص�BdWebPoolViewʵ��
	 * @param newProgress
	 *            ���ؽ���
	 */
	public void onProgressChanged(BPWebPoolView view, int newProgress) {
	}

	/**
	 * �յ�ҳ�����
	 * 
	 * @param view
	 *            ��ǰ���ص�BdWebPoolViewʵ��
	 * @param title
	 *            �յ��ı���
	 */
	public void onReceivedTitle(BPWebPoolView view, String title) {
	}

	/**
	 * Report an error to the host application. These errors are unrecoverable
	 * (i.e. the main resource is unavailable). The errorCode parameter
	 * corresponds to one of the ERROR_* constants.
	 * 
	 * @param view
	 *            The WebView that is initiating the callback.
	 * @param errorCode
	 *            The error code corresponding to an ERROR_* value.
	 * @param description
	 *            A String describing the error.
	 * @param failingUrl
	 *            The url that failed to load.
	 */
	public void onReceivedError(BPWebPoolView view, int errorCode, String description, String failingUrl) {

	}

	/**
	 * ҳ����ؽ���
	 * 
	 * @param view
	 *            ��ǰ���ص�BdWebPoolViewʵ��
	 * @param url
	 *            ���ؽ�����Url
	 */
	public void onPageFinished(BPWebPoolView view, String url) {
	}

	/**
	 * ��ʼWebView�л�
	 * 
	 * @param view
	 *            ��ǰʹ�õ�BdWebPoolViewʵ��
	 */
	public void onWebViewSwitchStarted(BPWebPoolView view) {
	}

	/**
	 * ���WebView�л�
	 * 
	 * @param view
	 *            ��ǰʹ�õ�BdWebPoolViewʵ��
	 */
	public void onWebViewSwitchFinish(BPWebPoolView view) {
	}

	/**
	 * ����
	 * 
	 * @param aBackItem
	 *            ���˵�BdWebPoolBackForwardListItem����
	 */
	public void onGoBack(BPWebPoolBackForwardListItem aBackItem) {

	}

	/**
	 * ǰ��
	 * 
	 * @param aForwardItem
	 *            ǰ����BdWebPoolBackForwardListItem����
	 */
	public void onGoForward(BPWebPoolBackForwardListItem aForwardItem) {

	}

	/**
	 * �������״̬�����ı�ʱ�������˷�����
	 * 
	 * @param view
	 *            �����ı����View
	 * @param changeStateMask
	 *            ״̬�ı��־λ
	 * @param newValue
	 *            ��ֵ
	 * */
	public void onStateChanged(BPWebPoolView view, int changeStateMask, Object newValue) {

	}
}
