package com.baidu.browser.core;

import android.os.Message;

/**
 * @ClassName: IEventSource 
 * @Description:�¼�Դ�ӿ��� 
 * @author LEIKANG 
 * @date 2012-12-11 ����5:18:08
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
