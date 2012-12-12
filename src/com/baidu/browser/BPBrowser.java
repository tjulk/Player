package com.baidu.browser;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.browser.framework.BPFrameView;
import com.baidu.browser.framework.BPWindow;
import com.baidu.browser.webpool.BPWebPoolView;
import com.baidu.player.R;
import com.baidu.webkit.sdk.BValueCallback;

/**
 * @ClassName: BPBrowser 
 * @Description: ���ģ�� ���ฺ���Ž� MainActivity �� BPFrameview ,UI�߳���Ӹú���ʵ������� BPFrameview  
 * @author LEIKANG 
 * @date 2012-12-5 ����3:10:14
 */
public class BPBrowser extends Fragment{
	
	/**Fragment tag.*/
    public static final String FRAGMENT_TAG = "BPBrowser";
    
    /** ��ҳURL.*/
    public static final String HOME_PAGE = "http://m.iqiyi.com";
    
	/** ��������Сֵ�� */
	public static final int PROGRESS_MIN = 10;

	/** ���������ֵ�� */
	public static final int PROGRESS_MAX = 100;
	
	/** ҳ����ؿ�ʼ **/
	public static final int STATE_PAGE_STARTED = 0x01;

	/** ҳ�������� **/
	public static final int STATE_PAGE_FINISHED = 0x02;

	/** ҳ����ؽ����� **/
	public static final int STATE_PROGRESS_CHANGED = 0x03;
	
	/** ����ͼ **/
	private BPFrameView mFrameView;
	
	/** ������ **/
	private BrowserListener mListener;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initInflate();
		mFrameView.restoreFromBundle(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    mFrameView.saveStateToBundle(outState);
	    super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        
		initInflate();
		if (mFrameView != null) {
			mFrameView.onResume();
		}
	}
	
	@Override
	public void onPause() {
		if (mFrameView != null) {
			mFrameView.closeSelectedMenu();
			mFrameView.onPause();
		}
        super.onPause();
	}
	
	public void onDestroy() {
		freeMemory();
		if (mFrameView != null) {
			mFrameView.release();
			mFrameView = null;
			//TODO browser ����ʱ�¼�����
		}
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (mFrameView != null) {
			mFrameView.freeMemory();
		}
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View view = getView();
	    if (view == null) {
	        view = getRootView();
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null) {
	            parent.removeView(view);
	        }
	    }
	    return view;
	}

	/**
	 * @Title: scrollBy 
	 * @Description: ����ҳ�����
	 * @param x
	 * @param y   
	 */
	public void scrollBy(int x, int y) {
		initInflate();
		mFrameView.webviewScrollBy(x, y);
	}
	
	/**
	 * @Title: scrollTo 
	 * @Description: ����ҳ����� 
	 * @param x
	 * @param y   
	 */
	public void scrollTo(int x, int y) {
		initInflate();
		mFrameView.webviewScrollTo(x, y);
	}
	
	/**
	 * @Title: addWebViewTitle 
	 * @Description: ����webview headView 
	 * @param aView   
	 */
	public void addWebViewTitle(View aView) {
		initInflate();
		mFrameView.addWebViewTitle(aView);
	}
	
	/**
	 * @Title: setmListener 
	 * @Description: ע�ᵱǰ����
	 * @param aListener   
	 */
	public void setmListener(BrowserListener aListener) {
		this.mListener = aListener;
	}
	
	/**
	 * @Title: initInflate 
	 * @Description: ���ֽ��� 
	 * @param     �趨�ļ� 
	 * @return void    �������� 
	 * @throws
	 */
	public void initInflate() {
		if (mFrameView == null) {
		    Activity activity = getActivity();
		    mFrameView = (BPFrameView) activity.findViewById(R.id.bdframeview_id);
		    if (mFrameView == null) {
		        mFrameView = new BPFrameView(activity);
		        mFrameView.setId(R.id.bdframeview_id);
		        mFrameView.setBrowser(this);
		    }
		}
	}
	
	/**
	 * @Title: freeMemory 
	 * @Description: �ͷ��ڴ�  
	 * @param     �趨�ļ� 
	 * @return void    �������� 
	 * @throws
	 */
	public void freeMemory() {
		if (mFrameView != null) {
			mFrameView.freeMemory();
		}
	}
	
	/**
	 * 
	 * @Title: onKeyDown 
	 * @Description: ����Activity��onKeyDown.
	 * @param  keyCode
	 * @param  event
	 * @param  �趨�ļ� 
	 * @return boolean   �����Ҫ��������Activity����return false������return true.
	 * @throws
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mFrameView != null) {
			if (mFrameView.onKeyDown(keyCode, event)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @Title: onKeyUp 
	 * @Description: ����Activity��onKeyUp.
	 * @param  keyCode
	 * @param  event
	 * @param  �趨�ļ� 
	 * @return boolean �����Ҫ��������Activity����return false������return true.
	 * @throws
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (mFrameView != null) {
            if (mFrameView.onKeyUp(keyCode, event)) {
                return true;
            }
        }
	    return false;
	}
 
	/**
	 * @Title: goBack 
	 * @Description: ��ʷ���� 
	 * @param     �趨�ļ� 
	 * @return void    �������� 
	 * @throws
	 */
	public void goBack() {
		initInflate();
		mFrameView.goBack();
	}
    
	/**
	 * @Title: goForward 
	 * @Description: ��ʷǰ�� 
	 * @param     �趨�ļ� 
	 * @return void    �������� 
	 * @throws
	 */
	public void goForward() {
		initInflate();
		mFrameView.goForward();
	}
	
	/**
	 * @Title: canGoForward 
	 * @Description: �ж��Ƿ����ǰ�� 
	 * @param  �趨�ļ� 
	 * @return boolean    �������� 
	 * @throws
	 */
	public boolean canGoForward() {
		initInflate();
		return mFrameView.canGoForward();
	}
	
	/**
	 * @Title: canGoBack 
	 * @Description: �ж��Ƿ���Ժ��� 
	 * @param  �趨�ļ� 
	 * @return boolean    �������� 
	 * @throws
	 */
	public boolean canGoBack() {
		initInflate();
		return mFrameView.canGoBack();
	}
	
	/**
	 * @Title: loadUrl 
	 * @Description: ������ַ������������ⲿ�����Ϊ����ʹ�øú�������
	 *               ======================================== 
	 * @param  url  �趨�ļ� 
	 * @return void �������� 
	 * @throws
	 */
	public void loadUrl(String url) {
		initInflate();
		mFrameView.loadUrl(url);
	}
	
	/**
	 * @Title: loadUrlFromHome 
	 * @Description:����ҳ��һ��URL
	 * @param  url
	 * @param  isOpenBackground �Ƿ��̨��
	 * @return void    �������� 
	 * @throws
	 */
	public void loadUrlFromHome(String url, boolean isOpenBackground) {
		initInflate();
		
		BPWindow current = mFrameView.getCurrentWindow();
        if (!isOpenBackground) {
            if (current != null) {
                current.loadUrl(url);
            }
        } else {
            mFrameView.createNewWindowOpenUrl(url, current, false, null);
        }
	}
	
	/**
	 * @Title: reload 
	 * @Description: ���¼���ҳ��  
	 * @param     �趨�ļ� 
	 * @return void    �������� 
	 * @throws
	 */
	public void reload() {
		initInflate();
		mFrameView.reload();
	}
	
	/**
	 * @Title: stopLoading 
	 * @Description: ֹͣ��ǰwebview���� 
	 * @param     �趨�ļ� 
	 * @return void    �������� 
	 * @throws
	 */
	public void stopLoading() {
		initInflate();
		mFrameView.stopLoading();
	}
	
	/**
	 * @Title: clearHistory 
	 * @Description: �����ʷ
	 * @param     �趨�ļ� 
	 * @return void    �������� 
	 * @throws
	 */
	public void clearHistory() {
		if (mFrameView != null) {
			mFrameView.clearHistory();
		}
	}
	
	/**
	 * @Title: getWindowList 
	 * @Description: ��ô����б�
	 * @return List<BdWindow>
	 */
	public List<BPWindow> getWindowList() {
	    if (mFrameView != null) {
	        return mFrameView.getWindowList();
	    }
	    return null;
	}
	
	/**
	 * @Title: getRootView 
	 * @Description: ��ȡbrowser��ʾ��ͼ
	 * @return View
	 */
	public View getRootView() {
		initInflate();
		return mFrameView;
	}
	
	/**
	 * @Title: getCurrentWindow 
	 * @Description: ��õ�ǰ���� 
	 * @return BPWindow
	 */
    public BPWindow getCurrentWindow() {
        initInflate();
        return mFrameView.getCurrentWindow();
    }
	
	/**
	 * @Title: setUpSelect 
	 * @Description: ����ѡ��ģʽ/�������� 
	 * @param     �趨�ļ� 
	 * @return void    �������� 
	 * @throws
	 */
	public void setUpSelect() {
		mFrameView.setUpSelect();
		//TODO ��ǰ�汾�ݲ�ʵ�ָù���
	}

	public boolean handleUrl(BPWebPoolView view, String url) {
		return false;
	}
 
	/**
	 * @Title: pageStateChanged 
	 * @Description: ֪ͨ�ⲿҳ��״̬�����ı�
	 * @param statePageStarted ״̬����
	 * @param url ����ֵ
	 */
	public void pageStateChanged(int statePageStarted, String url) {
		if (mListener != null) {
			mListener.onBrowserStateChanged(statePageStarted, url);
		}		
	}
	
	/**
	 * @Title: onGoHome 
	 * @Description: ������ҳ    
	 */
	public void onGoHome() {
		if (mListener != null) {
			mListener.onGoHome();
		}
	}
	
	/**
	 * @Title: onAddAsBookmark 
	 * @Description: �����ǩ
	 * @param title
	 * @param url   
	 */
    public void onAddAsBookmark(String title, String url) {
        if (mListener != null) {
            mListener.onAddAsBookmark(title, url);
        }
    }
    
    /**
     * @Title: onDownloadStart 
     * @Description: ֪ͨ����
     * @param url
     * @param userAgent
     * @param contentDisposition
     * @param mimetype
     * @param contentLength   
     */
	public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
			long contentLength) {
		if (mListener != null) {
			mListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
		}
	}
	
	/**
	 * @Title: onDownloadStartNoStream 
	 * @Description: ����
	 * @param url
	 * @param userAgent
	 * @param contentDisposition
	 * @param mimetype
	 * @param contentLength   
	 */
	public void onDownloadStartNoStream(String url, String userAgent, String contentDisposition,
			String mimetype, long contentLength) {
		if (mListener != null) {
			mListener.onDownloadStartNoStream(url, userAgent, contentDisposition, mimetype, contentLength);
		}
	}
	
	/**
	 * @Title: onSelectionSearch 
	 * @Description: ���ʻص����� ** �Ա��Ժ�汾������Ӱ����
	 * @param aSelection   
	 */
	public void onSelectionSearch(String aSelection) {
		if (mListener != null) {
			mListener.onSelectionSearch(aSelection);
		}
	}

	public void openFileChooser(BValueCallback<Uri> uploadMsg, String acceptType) {
		if (mListener != null) {
			mListener.openFileChooser(uploadMsg, acceptType);
		}
	}
	
	public void openFileChooser(BValueCallback<Uri> uploadMsg) { // SUPPRESS CHECKSTYLE
		if (mListener != null) {
			mListener.openFileChooser(uploadMsg);
		}
	}
	
	/**
	 * @ClassName: BrowserListener 
	 * @Description: �����ص��ӿ�,�������ⷢ���¼�
	 * @author LEIKANG 
	 * @date 2012-12-6 ����5:53:14
	 */
	public interface BrowserListener {

		/** ֪ͨ������ҳ */
		void onGoHome();

		/** ��ӵ�ǰ�����ҳΪ��ǩ */
		void onAddAsBookmark(String title, String url);

		/** ֪ͨ����ര���л� */
		void onSwitchToMultiWinodow();

		/** ֪ͨ���� */
		void onOpenFromBrowser(String aTitle, String aUrl);

		/** �����״̬�ı� */
		void onBrowserStateChanged(int stateMask, Object newValue);

		/** ������������ */
		void onClickVoiceSearch();

		/** ֪ͨ���� */
		void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength);

		/** ֪ͨ���� */
		void onDownloadStartNoStream(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength);

		/** �������� */
		void onSelectionSearch(String aSelection);

		/** ������menu����ǩѡ�ѡ�� */
		void onSelectBookmarkPopMenu(String title, String url);

		/** ����Э�� */
		void onProtocolSearch(String aSelection);

		/** ��õ�ǰ���������� */
		Bundle getSearchboxBundle(boolean withKeyword);

		/** ֪ͨtab�䶯���� */
		Bundle onTabChangeFinished(Bundle aBundle);

		// For Android 3.0+
		void openFileChooser(BValueCallback<Uri> uploadMsg, String acceptType); // SUPPRESS

		// For Android < 3.0
		void openFileChooser(BValueCallback<Uri> uploadMsg); // SUPPRESS

		Message onRequestCopyHref();  

		void onDismissPopMenu();
	}

	public void initFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
    
}
