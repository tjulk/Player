 
package com.baidu.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.baidu.browser.BPBrowser;
import com.baidu.webkit.sdk.BWebKitFactory;
 
/**
 * @ClassName: MainActivity 
 * @Description: 百度影音主activity
 * @author LEIKANG 
 * @date 2012-12-7 上午10:51:09
 */
public class MainActivity extends BaseActivity {

	/** log tag. */
	public static final String TAG = "MainActivity";
	
	/** debug 开关. */
	public static final boolean DEBUG = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	      boolean initResult = BWebKitFactory.init(this);
	        if (initResult) {
	            BWebKitFactory.destroy();
	            boolean ret = BWebKitFactory.setEngine(BWebKitFactory.ENGINE_ORIGINAL);
	            if (ret)
	            	init(savedInstanceState);
	        }
	}
	
	/**
	 * @Title: init 
	 * @Description: 初始化动作
	 * @param savedInstanceState   
	 */
	private void init(Bundle savedInstanceState) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(R.layout.main);
        
        FragmentManager manager = getSupportFragmentManager();
        
        BPBrowser browser = (BPBrowser) manager.findFragmentByTag(BPBrowser.FRAGMENT_TAG);
        if (browser == null) {
            browser = new BPBrowser();
            browser.setRetainInstance(true);
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(R.id.layout_for_fragment,browser, BPBrowser.FRAGMENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
            
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }
	}
	
	private Handler mHandler = new Handler() {
	    
	    @Override
	    public void handleMessage(Message msg) {
	    	FragmentManager manager = getSupportFragmentManager();
	    	BPBrowser browser = (BPBrowser) manager.findFragmentByTag(BPBrowser.FRAGMENT_TAG);
		    browser.loadUrl("http://www.baidu.com/");
	    }
	};

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		FragmentManager manager = getSupportFragmentManager();
        BPBrowser browser = (BPBrowser) manager.findFragmentByTag(BPBrowser.FRAGMENT_TAG);
        if (browser.onKeyDown(keyCode, event)) {
            return true;
        }
		return super.onKeyDown(keyCode, event);
	}

	
}
