package com.baidu.browser.core.ui;

/**
 * �����˵�������
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