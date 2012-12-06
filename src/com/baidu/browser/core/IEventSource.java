package com.baidu.browser.core;

import android.os.Message;

/**
 * 事件源接口类
 */
public interface IEventSource {

	/**
	 * 注册一个事件
	 * 
	 * @param aEvent
	 *            注册事件名
	 */
	void registEvent(String aEvent);

	/**
	 * 发送一个事件
	 * 
	 * @param aEvent
	 *            事件名
	 * @param aMsg
	 *            事件对应消息
	 */
	void fireEvent(String aEvent, Message aMsg);

	/**
	 * 发送一个遍历事件，该事件会遍历视图树
	 * 
	 * @param aEvent
	 *            事件名
	 * @param aMsg
	 *            事件对应消息
	 */
	void fireUIEvent(String aEvent, Message aMsg);

	/**
	 * 接受事件
	 * 
	 * @param aEvent
	 *            事件名
	 * @param aMsg
	 *            事件对应消息
	 */
	void onReceiveEvent(String aEvent, Message aMsg);

	/**
	 * 设置事件监听
	 * 
	 * @param aListener
	 *            事件监听
	 */
	void setEventListener(IEventListener aListener);

}
