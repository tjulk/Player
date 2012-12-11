package com.baidu.browser.core.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * @ClassName: BdPopMenuGroup 
 * @Description: ���õ����˵��ؼ�
 * @author LEIKANG 
 * @date 2012-12-11 ����5:18:53
 */
public class BdPopMenuGroup extends FrameLayout {

	/**BdPopMenuGroup**/
	private static BdPopMenuGroup instance;

	/**��ǰ�㼶���в˵�**/ 
	private List<BdPopMenu> menus;

	/**������**/
	private Context context;

	/**��ʾģʽ**/
	private boolean menuShowMode;

	/**����**/
	private FrameLayout.LayoutParams absParames = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 
			FrameLayout.LayoutParams.WRAP_CONTENT);
	
	/** ��ǰ��ʾ�Ĳ˵��ڲ˵�list�е�index  */
	private int mShowMenuId = -1;

	/**
	 * ���췽��
	 * @param aContext Context
	 */
	public BdPopMenuGroup(Context aContext) {
		this(aContext, null);
	}

	/**
	 * ���췽��
	 * @param aContext Context
	 * @param attrs AttributeSet
	 */
	public BdPopMenuGroup(Context aContext, AttributeSet attrs) {
		super(aContext, attrs);
		this.context = aContext;
		instance = this;
		absParames.gravity = Gravity.CENTER;
		menus = new ArrayList<BdPopMenu>();
		setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 
				FrameLayout.LayoutParams.MATCH_PARENT));
	}

	/**
	 * ����
	 * @return BdPopMenuGroup
	 */
	public static BdPopMenuGroup getInstance() {
		return instance;
	}

	/**
	 * ��ָ��id�˵�������
	 * @param mid menuid
	 * @param key �ı�
	 * @param colorResId colorResId
	 * @param iconResId iconResId
	 */
	public void addItem(int mid, String key, int colorResId, int iconResId) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.addItem(key, colorResId, iconResId);
	}

	/**
	 * ��ӷ���
	 * @param mid menuid
	 * @param keys �ı��б�
	 * @param colorResId colorResId
	 * @param iconResIds iconResIds
	 */
	public void addItem(int mid, String[] keys, int colorResId, int[] iconResIds) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.addItem(keys, colorResId, iconResIds);
	}

	/**
	 * ��ӷ���
	 * @param mid menuid
	 * @param keys �ı��б�
	 * @param colorResId colorResId
	 * @param iconResIds iconResIds
	 * @param bgResIds ����
	 */
	public void addItem(int mid, String[] keys, int colorResId, int[] iconResIds, int bgResIds) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.addItem(keys, colorResId, iconResIds, bgResIds);
	}

	/**
	 * ����
	 * @param mid menuid
	 */
	public void layoutMenu(int mid) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.layoutMenu();
	}

	/**
	 *  Ĭ�Ͼ�����ʾ
	 * @param mid menuid
	 */
	public void show(int mid) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		mShowMenuId = mid;
		menuShowMode = true;
		BdPopMenu cmenu = menus.get(mid);
		if (cmenu.getListener() != null) {
			cmenu.getListener().onPopMenuShow(mid);
		}
		cmenu.setDrawingCacheEnabled(true);
		cmenu.setVisibility(VISIBLE);
	}

	/**
	 * ��ʾָ���˵���ָ��id��
	 * @param mid menuid
	 * @param index λ��
	 */
	public void showItem(int mid, int index) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.showItem(index);
	}

	/**
	 * ����ָ���˵���ָ��id��
	 * @param mid menuid
	 * @param index λ��
	 */
	public void hideItem(int mid, int index) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.hideItem(index);
	}

	/**
	 * ����ָ���˵�ȫ����
	 * @param mid menuid
	 */
	public void hideAllItem(int mid) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.hideAllItem();
	}

	/**
	 * ��ʾָ���˵�ȫ����
	 * @param mid menuid
	 */
	public void showAllItem(int mid) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.showAllItem();
	}

	/**
	 * ����
	 * @param mid menuid
	 */
	public void hide(int mid) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		mShowMenuId = -1;
		menuShowMode = false;
		BdPopMenu cmenu = menus.get(mid);
		if (cmenu.getListener() != null) {
			cmenu.getListener().onPopMenuHide(mid);
		}
		cmenu.setVisibility(INVISIBLE);
		cmenu.destroyDrawingCache();
	}

	/**
	 * ����ȫ����ʾ�˵�
	 */
	public void hideAll() {
		for (BdPopMenu m : menus) {
			if (m.getVisibility() == VISIBLE) {
				if (m.getListener() != null) {
					m.getListener().onPopMenuHide(m.getId());
				}
				m.setVisibility(INVISIBLE);
			}
		}
		mShowMenuId = -1;
		menuShowMode = false;
	}

	/**
	 * ���������˵� ���ص�ǰ�˵�id
	 * @param listener BdPopMenuListener
	 * @return �˵�id
	 */
	public int createMenu(BdPopMenuListener listener) {
		BdPopMenu menu = new BdPopMenu(context);
		menu.setListener(listener);
		menu.setVisibility(INVISIBLE);
		menus.add(menu);
		this.addView(menu, absParames);
		int index = menus.indexOf(menu);
		menu.setId(index);
		return index;
	}

	/**
	 * ����itemƤ��
	 * @param mid menuid
	 * @param res ��Դ
	 */
	public void setItemIcon(int mid, int res) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.setSkin(res);
	}

	/**
	 * ����menuƤ��
	 * @param mid menuid
	 * @param res ��Դ
	 */
	public void setMenuSkin(int mid, int res) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.setBackgroundResource(res);
	}

	/**
	 * ���������С
	 * 
	 * @param mid menuid
	 * @param textSize �ı���С
	 */
	public void setMenuTextSize(int mid, float textSize) {
		if (mid < 0 || mid >= menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.setTextSize(textSize);
	}
	
	/**
	 * ���²˵��ı�
	 * @param mid menuid
	 * @param index λ��
	 * @param text �ı�
	 */
	public void updateMenuItemText(int mid, int index, String text) {
		if (index < 0 || index > menus.size()) {
			return;
		}
		BdPopMenu cmenu = menus.get(mid);
		cmenu.updateItemText(index, text);
		postInvalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (menuShowMode) {
					hideAll();
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:

				break;
			case MotionEvent.ACTION_MOVE:

				break;
			default:
				break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * �˵���ʾ
	 * @return boolean
	 */
	public boolean isMenuShow() {
		return menuShowMode;
	}

	/**
	 * ��ȡ�˵�
	 * @return BdPopMenu
	 */
	public BdPopMenu getShowMenu() {
		if (mShowMenuId >= 0 && mShowMenuId < menus.size()) {
			return menus.get(mShowMenuId);
		} else {
			return null;
		}
	}

}
