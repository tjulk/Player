package com.baidu.browser.core;

import android.content.Context;
import android.os.Bundle;

/**
 * Element Interface
 */
public interface IElement {

	/**
	 * onInit
	 * 
	 * @param aBundle
	 *            Bundle
	 */
	void onInit(Bundle aBundle);

	/**
	 * onSave
	 * 
	 * @param aContext
	 *            Context
	 */
	void onSave(Context aContext);

	/**
	 * onLoad
	 * 
	 * @param aContext
	 *            Context
	 */
	void onLoad(Context aContext);

	/**
	 * onDestroy
	 */
	void onDestroy();
}
