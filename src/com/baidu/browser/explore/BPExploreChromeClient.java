 
package com.baidu.browser.explore;

import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;

import com.baidu.browser.webpool.BPWebPoolChromeClient;
import com.baidu.browser.webpool.BPWebPoolView;
import com.baidu.webkit.sdk.BWebChromeClient.BCustomViewCallback;
import com.baidu.webkit.sdk.BWebView;

/**
 * @ClassName: BPExploreChromeClient 
 * @Description: ����WebView����Javascript�ĶԻ�����վͼ�꣬��վtitle�����ؽ��ȵ�
 * @author LEIKANG 
 * @date 2012-12-6 ����2:17:29
 */
public class BPExploreChromeClient extends BPWebPoolChromeClient {

	@Override
	public void onProgressChanged(BPWebPoolView view, int newProgress) {
	}

	@Override
	public void onReceivedTitle(BPWebPoolView view, String title) {
	}

	@Override
	public void onReceivedIcon(BPWebPoolView view, Bitmap icon) {
	}

	@Override
	public void onReceivedTouchIconUrl(BPWebPoolView view, String url, boolean precomposed) {
	}

	@Override
	public void onShowCustomView(View view, BCustomViewCallback callback) {
	}

	@Override
	public void onHideCustomView() {
	}

	@Override
	public boolean onCreateWindow(BPWebPoolView view, boolean dialog, boolean userGesture, Message resultMsg,
			BWebView.BWebViewTransport bdTransport) {
		return false;
	}

	@Override
	public void onRequestFocus(BPWebPoolView view) {
	}

	@Override
	public void onCloseWindow(BPWebPoolView window) {
	}

}
