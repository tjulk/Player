package com.baidu.browser.explore;

import com.baidu.webkit.sdk.BWebView.BHitTestResult;

/**
 * @ClassName: BPExploreViewListener 
 * @Description:  
 * @author LEIKANG 
 * @date 2012-12-6 下午2:24:03
 */
public interface BPExploreViewListener {

	/**
	 * 长按页面接口
	 * 
	 * @param result
	 *            BdHitTestResult
	 */
	public void onLongPress(BHitTestResult result);

	/**
	 * 选词搜索
	 * 
	 * @param aSelection 选择的字符串
	 */
	public void onSelectionSearch(String aSelection);

}
