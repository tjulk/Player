package com.baidu.browser.core.ui;

import com.baidu.browser.core.IElement;
import com.baidu.browser.core.IEventSource;
/**
 * @ClassName: IUIElement 
 * @Description: UIԪ�ؽӿ�
 * @author LEIKANG 
 * @date 2012-12-11 ����5:20:01
 */
public interface IUIElement extends IElement, IEventSource {

	/** Enable State */
	int STATE_ENABLED = 0;
	/** Disabled State */
	int STATE_DISABLED = 1;
	/** Focused State */
	int STATE_FOCUSED = 2;
	/** Selected State */
	int STATE_SELECTED = 3;
	/** Checked State */
	int STATE_CHECKED = 4;
	/** Activated State */
	int STATE_ACTIVATED = 5;
	/** Max State Count */
	int STATE_MAX_COUNT = 6;

	/**
	 * ���õ�ǰ״̬
	 * 
	 * @param aState
	 *            �����õ�״̬
	 */
	void setState(int aState);

	/**
	 * ��ȡ��ǰ״̬
	 * 
	 * @return ��ǰ״̬
	 */
	int getState();

	/**
	 * ��ǰ״̬�ı�֪ͨ
	 * 
	 * @param aState
	 *            �ı���״̬
	 */
	void onStateChanged(int aState);

	/**
	 * ���ð���״̬
	 * 
	 * @param aPressed
	 *            ���������Ϊtrue������Ϊfalse
	 */
	void setPressed(boolean aPressed);

	/**
	 * �Ƿ���
	 * 
	 * @return ��������򷵻�true�����򷵻�false
	 */
	boolean isPressed();

	/**
	 * �����ı��ص���Ŀǰ���а���״̬֪ͨ
	 */
	void onActionChanged();

	/**
	 * �����ʧ����ʱ�ص�
	 * @return �����ture�����أ����򷵻�false
	 */
	boolean onCaptureLoseFocus();

}
