package com.baidu.browser.explore;

import com.baidu.webkit.sdk.BWebView.BHitTestResult;

/**
 * @ClassName: BPExploreViewListener 
 * @Description:  
 * @author LEIKANG 
 * @date 2012-12-6 ����2:24:03
 */
public interface BPExploreViewListener {

	/**
	 * ����ҳ��ӿ�
	 * 
	 * @param result
	 *            BdHitTestResult
	 */
	public void onLongPress(BHitTestResult result);

	/**
	 * ѡ������
	 * 
	 * @param aSelection ѡ����ַ���
	 */
	public void onSelectionSearch(String aSelection);

}
