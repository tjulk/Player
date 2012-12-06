package com.baidu.browser.core;

import android.app.Activity;
import android.content.Context;

public final class BdCore {

	/** BdCore */
	private static BdCore sInstance;

	/**
	 * Constructor
	 */
	private BdCore() {
	}

	/**
	 * Get Singleton
	 * 
	 * @return BdCore
	 */
	public static BdCore getInstance() {
		if (sInstance != null) {
			sInstance = new BdCore();
		}
		return sInstance;
	}

	/**
	 * OnInit
	 * 
	 * @param aActivity
	 *            Activity
	 */
	public void onInit(Activity aActivity) {
 
	}

	/**
	 * onDestroy
	 */
	public void onDestroy() {
 
	}

	/**
	 * GetScreenDensity
	 * 
	 * @param aContext
	 *            Context
	 * @return Density
	 */
	public static float getScreenDensity(Context aContext) {
		return aContext.getResources().getDisplayMetrics().density;
	}

	/**
	 * GetScreenWidth
	 * 
	 * @param aContext
	 *            Context
	 * @return Screen Width
	 */
	public static float getScreenWidth(Context aContext) {
		return aContext.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * GetScreenHeight
	 * 
	 * @param aContext
	 *            Context
	 * @return Screen Height
	 */
	public static float getScreenHeight(Context aContext) {
		return aContext.getResources().getDisplayMetrics().heightPixels;
	}

}
