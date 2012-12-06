package com.baidu.browser.webpool;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @ClassName: BPWebPoolMaskView 
 * @Description: WebpPoolView���ֲ㣬����TouchEvent����ֹ��View���ֵ�ʱ�򣬻��ܵ����ǰҳ������� 
 * @author LEIKANG 
 * @date 2012-12-6 ����2:25:49
 */
public class BPWebPoolMaskView extends View {

	/**
	 * @param aContext
	 *            Context
	 */
	public BPWebPoolMaskView(Context aContext) {
		this(aContext, null);
	}

	/**
	 * @param aContext
	 *            Context
	 * @param aAttrs
	 *            AttributeSet
	 */
	public BPWebPoolMaskView(Context aContext, AttributeSet aAttrs) {
		super(aContext, aAttrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

}
