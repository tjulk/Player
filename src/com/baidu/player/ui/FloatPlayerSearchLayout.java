package com.baidu.player.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
/**
 * @ClassName: FloatPlayerSearchLayout 
 * @Description: 
 * 加载所有于浮动的搜索框相关的view,并处理动画及各种点击事件. 使用方法:
 * 
 *         LayoutParams mfloatBoxLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
 *         LayoutParams.FILL_PARENT);
 * 		   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 * 		   FloatSearchBoxLayout mfloatSearchBoxLayout = (FloatSearchBoxLayout) inflater.inflate(
 *         R.layout.searchbox, null);
 * 		   YourActivity.this.addContentView(mfloatSearchBoxLayout, mfloatBoxLayoutParams);
 * 
 * @author LEIKANG 
 * @date 2012-12-10 下午8:00:06
 */
public class FloatPlayerSearchLayout extends RelativeLayout{
	
	private Context mContext;
	
	/**
	 * @param context
	 */
	public FloatPlayerSearchLayout(Context context) {
		super(context);
		this.mContext = context;
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
    public FloatPlayerSearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }
    
    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public FloatPlayerSearchLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

	public void setEnableStartSearch(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setStopLoadingOnClickListener(OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		
	}

}
