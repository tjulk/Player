package com.baidu.browser.core.ui;
/**
 * @ClassName: BdPopMenuListener 
 * @Description: �����˵�������
 * @author LEIKANG 
 * @date 2012-12-11 ����5:19:28
 */
public interface BdPopMenuListener {

	/**
	 * ���˵����
	 * 
	 * @param mid
	 *            menuid
	 * @param index
	 *            λ��
	 * @param name
	 *            ����
	 */
	void onPopMenuClick(int mid, int index, String name);

	/**
	 * ���˵���ʾ
	 * 
	 * @param mid
	 *            menuid
	 */
	void onPopMenuShow(int mid);

	/**
	 * ���˵�����
	 * 
	 * @param mid
	 *            menuid
	 */
	void onPopMenuHide(int mid);
}
