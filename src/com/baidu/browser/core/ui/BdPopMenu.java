package com.baidu.browser.core.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources.NotFoundException;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * �����˵���
 */
public class BdPopMenu extends LinearLayout implements OnClickListener {

	/**��ǰid**/
	private int mid = 0;
	
	/**�ܶ�**/
	private float density = 0;

	/**�˵�������**/
	private List<BdPopMenuItem> menuItems;
	
	/**����**/
	private BdPopMenuListener listener;

	/**���嵥�����**/
	private int width = 0;

	/**���嵥���߶�**/
	private int height = 0;

	/**����**/
	private LinearLayout.LayoutParams layoutParams;

	/**������**/
	private Context context;

	/**
	 * ���췽��
	 * @param aContext Context
	 */
	public BdPopMenu(Context aContext) {
		this(aContext, null);
	}

	/**
	 * ���췽��
	 * @param aContext Context
	 * @param attrs AttributeSet
	 */
	public BdPopMenu(Context aContext, AttributeSet attrs) {
		super(aContext, attrs);
		this.context = aContext;
		density = context.getResources().getDisplayMetrics().density;
		width = (int) (80 * density);   
		height = (int) (60 * density);  
		layoutParams = new LinearLayout.LayoutParams(width, height);
		menuItems = new ArrayList<BdPopMenuItem>();
		layoutParams.setMargins(1, 1, 1, 1);  
		this.setOrientation(VERTICAL);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (listener != null) {
			BdPopMenuItem item = ((BdPopMenuItem) v);
			listener.onPopMenuClick(mid, menuItems.indexOf(item), item.getText());
		}
	}

	public BdPopMenuListener getListener() {  
		return listener;
	}

	public void setListener(BdPopMenuListener listener) {  
		this.listener = listener;
	}

	/**
	 * ����Ƥ��
	 * @param res id
	 */
	public void setSkin(int res) {
		for (BdPopMenuItem m : menuItems) {
			m.setBackgroundResource(res);
		}
	}
	
	/**
	 * 
	 * @param aTextSize float
	 */
	public void setTextSize(float aTextSize) {
		for (BdPopMenuItem m : menuItems) {
			m.setTextSize(aTextSize);
		}
	}

	/**
	 * ����
	 */
	public void layoutMenu() {
		int count = menuItems.size();

		if (count == 4) {  
			LinearLayout layout1 = new LinearLayout(context);
			LinearLayout layout2 = new LinearLayout(context);
			for (int i = 0; i < count; i++) {
				if (i < 2) {
					layout1.addView(menuItems.get(i), layoutParams);
				} else {
					layout2.addView(menuItems.get(i), layoutParams);
				}
			}
			this.addView(layout1);
			this.addView(layout2);
		} else {
			int line = 0;
			if (count % 3 == 0) {   
				line = count / 3;  
			} else { 
				line = (count / 3) + 1;  
			}
			List<LinearLayout> list = new ArrayList<LinearLayout>();
			for (int k = 0; k < line; k++) {
				LinearLayout layout = new LinearLayout(context);
				list.add(layout);
				this.addView(layout);
			}

			LinearLayout.LayoutParams lineLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
			lineLayout.gravity = Gravity.CENTER;
			for (int i = 0; i < count; i++) {
				int row = i / 3;     
				ImageView separator = new ImageView(getContext());
				//separator.setBackgroundResource(R.drawable.browser_select_separator);
				list.get(row).addView(menuItems.get(i), layoutParams);
				if ((i+1) % 3 != 0 && (i+1) != count) {
					list.get(row).addView(separator, lineLayout);
				}
			}
		}
	}
	

	/**
	 * �����
	 * @param key String
	 * @param colorResId colorResId
	 * @param iconResId iconResId
	 */
	public void addItem(String key, int colorResId, int iconResId) {
		try {
			BdPopMenuItem item = new BdPopMenuItem(context);
			item.setOnClickListener(this);
			ColorStateList colorStateList = context.getResources().getColorStateList(colorResId);
			item.setText(key);
			item.setTextColor(colorStateList);
			item.setIcon(iconResId);

			menuItems.add(item);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �����
	 * @param key String
	 * @param colorResId colorResId
	 * @param iconResId iconResId
	 * @param  bgResId int
	 */
	public void addItem(String key, int colorResId, int iconResId, int bgResId) {
		try {
			BdPopMenuItem item = new BdPopMenuItem(context);
			item.setOnClickListener(this);
			ColorStateList colorStateList = context.getResources().getColorStateList(colorResId);
			item.setText(key);
			item.setTextColor(colorStateList);
			item.setIcon(iconResId);

			menuItems.add(item);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����б���
	 * @param list �ı��б�
	 * @param colorResId int
	 * @param iconResIds int
	 */
	public void addItem(String[] list, int colorResId, int[] iconResIds) {
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				addItem(list[i], colorResId, iconResIds[i]);
			}
		}
	}

	/**
	 * ���� ����б���
	 * @param list �ı�
	 * @param colorResId int
	 * @param iconResIds int
	 * @param bgResId int
	 */
	public void addItem(String[] list, int colorResId, int[] iconResIds, int bgResId) {
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				addItem(list[i], colorResId, iconResIds[i], bgResId);
			}
		}
	}

	/**
	 * ��ʾ
	 * @param index int
	 */
	public void showItem(int index) {
		if (index < 0 || index > menuItems.size()) {
			return;
		}
		BdPopMenuItem item = menuItems.get(index);
		item.setEnabled(true);
	}

	public void hideItem(int index) {
		if (index < 0 || index > menuItems.size()) {
			return;
		}
		BdPopMenuItem item = menuItems.get(index);
		item.setEnabled(false);
	}

	public void hideAllItem() {
		for (BdPopMenuItem m : menuItems) {
			m.setEnabled(false);
		}
	}

	public void showAllItem() {
		for (BdPopMenuItem m : menuItems) {
			m.setEnabled(true);
		}
	}

	public void updateItemText(int index, String text){
		if (index < 0 || index > menuItems.size()) {
			return;
		}
		BdPopMenuItem item = menuItems.get(index);
		item.setText(text);
		postInvalidate();
	}
	
	public int getId() {
		return mid;
	}

	public void setId(int id) {
		this.mid = id;
	}
	
	public List<BdPopMenuItem> getMenuItemList() {
		return menuItems;
	}
}
