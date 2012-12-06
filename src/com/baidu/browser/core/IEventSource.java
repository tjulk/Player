package com.baidu.browser.core;

import android.os.Message;

/**
 * �¼�Դ�ӿ���
 */
public interface IEventSource {

	/**
	 * ע��һ���¼�
	 * 
	 * @param aEvent
	 *            ע���¼���
	 */
	void registEvent(String aEvent);

	/**
	 * ����һ���¼�
	 * 
	 * @param aEvent
	 *            �¼���
	 * @param aMsg
	 *            �¼���Ӧ��Ϣ
	 */
	void fireEvent(String aEvent, Message aMsg);

	/**
	 * ����һ�������¼������¼��������ͼ��
	 * 
	 * @param aEvent
	 *            �¼���
	 * @param aMsg
	 *            �¼���Ӧ��Ϣ
	 */
	void fireUIEvent(String aEvent, Message aMsg);

	/**
	 * �����¼�
	 * 
	 * @param aEvent
	 *            �¼���
	 * @param aMsg
	 *            �¼���Ӧ��Ϣ
	 */
	void onReceiveEvent(String aEvent, Message aMsg);

	/**
	 * �����¼�����
	 * 
	 * @param aListener
	 *            �¼�����
	 */
	void setEventListener(IEventListener aListener);

}
