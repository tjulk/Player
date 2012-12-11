package com.baidu.player.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.baidu.player.R;
/**
 * @ClassName: FloatPlayerSearchLayout 
 * @Description: 
 * ���������ڸ�������������ص�view,�������������ֵ���¼�. ʹ�÷���:
 * 
 *         LayoutParams mfloatBoxLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
 *         LayoutParams.FILL_PARENT);
 * 		   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 * 		   FloatSearchBoxLayout mfloatSearchBoxLayout = (FloatSearchBoxLayout) inflater.inflate(
 *         R.layout.searchbox, null);
 * 		   YourActivity.this.addContentView(mfloatSearchBoxLayout, mfloatBoxLayoutParams);
 * 
 * @author LEIKANG 
 * @date 2012-12-10 ����8:00:06
 */
public class FloatPlayerSearchLayout extends RelativeLayout{
	
	private Context mContext;
	
    /** ����������. */
    private EditText mSearchTextInput = null;
	
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
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init(mContext);
    }
    
    /**
     * @Title: init 
     * @Description: ��ʼ��
     * @param context   
     */
    private void init(Context context) {
    	mSearchTextInput = (EditText) findViewById(R.id.SearchTextInput);
    }

	public void setEnableStartSearch(boolean b) {
        mSearchTextInput.setFocusable(b);
        mSearchTextInput.setFocusableInTouchMode(b);
	}

	public void setStopLoadingOnClickListener(OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		
	}

	public void showStopLoadingIcon() {
		// TODO Auto-generated method stub
		
	}

	public void hideStopLoadingIcon() {
		// TODO Auto-generated method stub
		
	}

	public void updateMode() {
		// TODO Auto-generated method stub
		
	}

}
