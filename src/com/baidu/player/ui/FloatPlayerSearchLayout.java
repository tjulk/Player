package com.baidu.player.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.player.R;
import com.baidu.player.util.Utility;
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
	
	
    public enum FloatSearchboxMode {
        /**������������ģʽ��*/
        SEARCH_GO,
        /**ȡ����������ģʽ��*/
        SEARCH_CANCEL,
        /**���ʰ�ťģʽ. */
        SEARCH_VISIT,  
        /** about settingsģʽ.*/
        SEARCH_BDHD;
    }
	
	
	private Context mContext;
	
    /** ����������. */
    private EditText mSearchTextInput = null;
    
    /** ����������ȡ����ť�� */
    private TextView mSearchOrCancelView = null;
    
    /** ��ǰ������ģʽ.*/
    private FloatSearchboxMode currentMode = FloatSearchboxMode.SEARCH_CANCEL;
    
    /** ���������������.*/
    private SearchBoxCommandListener mSearchBoxCommandListener;
    
    private boolean isStartFromSearchButton = false;
	
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
    	mSearchOrCancelView = (TextView) findViewById(R.id.float_search_or_cancel);
        mSearchOrCancelView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startSearchToplayer(mSearchTextInput.getText().toString());
            }
        });
    }

	public void setEnableStartSearch(boolean enableStartSearch) {
			mSearchTextInput.setFocusable(enableStartSearch);
        	mSearchTextInput.setFocusableInTouchMode(enableStartSearch);
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
	
    public void setIsStartFromSearchButton(boolean is) {
    	isStartFromSearchButton = is;
        mSearchTextInput.setImeOptions(is?EditorInfo.IME_ACTION_SEARCH:EditorInfo.IME_ACTION_DONE);
    }
	
    public void setSearchBoxCommandListener(SearchBoxCommandListener listener) {
        mSearchBoxCommandListener = listener;
    }
	
	
	
    public void startSearchToplayer(String query) {
        if (mSearchBoxCommandListener != null) {
            SearchBoxCommand command = new SearchBoxCommand();
            command.currentMode = currentMode;
            mSearchBoxCommandListener.executeSearchBoxCommand(command);
        }
    }

	/**
	 * @Title: updateMode 
	 * @Description: ˢ������ģʽ   
	 */
	public void updateMode() {
		FloatSearchboxMode mode = FloatSearchboxMode.SEARCH_CANCEL;	
		String query = mSearchTextInput.getText().toString();
        if (TextUtils.isEmpty(query)) {
            mode = FloatSearchboxMode.SEARCH_CANCEL;
        } else {
            String tmp = Utility.fixUrl(query).trim();
            
            if (Utility.isUrl(tmp) && !isStartFromSearchButton) {
                mode = FloatSearchboxMode.SEARCH_VISIT;
            } else if (Utility.isBDHD(tmp)) {
            	mode = FloatSearchboxMode.SEARCH_BDHD;
            } else {
                mode = FloatSearchboxMode.SEARCH_GO;
            }
        }
        setMode(mode);
	}
	
	/**
	 * @Title: setMode 
	 * @Description: ��������ģʽUI
	 * @param mode   
	 */
    private void setMode(FloatSearchboxMode mode) {
    	currentMode = mode;
        switch (mode) {
        case SEARCH_GO:
            mSearchOrCancelView.setVisibility(VISIBLE);
            mSearchOrCancelView.setText(R.string.search_go);
            break;
        case SEARCH_CANCEL:
            mSearchOrCancelView.setVisibility(VISIBLE);
            mSearchOrCancelView.setText(R.string.search_cancel);
            break;
        case SEARCH_VISIT:
            mSearchOrCancelView.setVisibility(VISIBLE);
            mSearchOrCancelView.setText(R.string.search_visit);
            break;
        case SEARCH_BDHD:
        	mSearchOrCancelView.setVisibility(VISIBLE);
        	mSearchOrCancelView.setText(R.string.search_play);
        	break;
        default:
            break;
        }
    }

    /**
     * @ClassName: SearchBoxCommand 
     * @Description: �����װ������ָ��.
     * @author LEIKANG 
     * @date 2012-12-12 ����4:38:53
     */
    public static class SearchBoxCommand {
    	/** ��ǰ������ģʽ.*/
    	public FloatSearchboxMode currentMode;
    	/** ��������query.*/
    	public String query;
    }
    
    /**
     * @ClassName: SearchBoxCommandListener 
     * @Description: �����������������ִ��  ֻ���������������
     * @author LEIKANG 
     * @date 2012-12-12 ����4:47:18
     */
    public interface SearchBoxCommandListener {
        void executeSearchBoxCommand(SearchBoxCommand command);
    }
    

    /**
     * @Title: setOnEditorActionListener 
     * @Description: ���ü��̶����������� 
     * @param mOnEditorActionListener   
     */
	public void setOnEditorActionListener(OnEditorActionListener mOnEditorActionListener) {
		mSearchTextInput.setOnEditorActionListener(mOnEditorActionListener);
	}
}
