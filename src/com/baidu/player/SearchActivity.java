package com.baidu.player;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.player.ui.FloatPlayerSearchLayout;

/**
 * @ClassName: SearchActivity 
 * @Description: 
 * @author LEIKANG 
 * @date 2012-12-11 ����9:52:10
 */
public class SearchActivity extends BaseActivity{
	
	public final static String TAG_IS_START_FROM_SEARCH = "TAG_IS_START_FROM_SEARCH";
	
	private boolean isStartFromSearchBtn;
	
    /** ������searchBox,�����ṩ��������ѡ��. */
    public FloatPlayerSearchLayout mFloatPlayerSearchLayout = null;
    
    /** �����. */
    private EditText mQueryTextView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.search);
		
		isStartFromSearchBtn = getIntent().getBooleanExtra(TAG_IS_START_FROM_SEARCH, false);
		mFloatPlayerSearchLayout = (FloatPlayerSearchLayout) findViewById(R.id.float_MainRoot);
		//mFloatPlayerSearchLayout.setEnableStartSearch(false);
		
        mQueryTextView = (EditText) mFloatPlayerSearchLayout.findViewById(R.id.SearchTextInput);
        mQueryTextView.addTextChangedListener(new SearchTextWatcher());
        mQueryTextView.setFocusable(true);
        mQueryTextView.requestFocus();
        mQueryTextView.setSelection(mQueryTextView.getText().length());
        
        //showInputMethod();
	} 
	
	/**
	 * @Title: showInputMethod 
	 * @Description: �������뷨
	 */
//	private void showInputMethod() {
//		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//	}
	
	
    private class SearchTextWatcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            
            String query = s.toString();
            
            Toast.makeText(getApplicationContext(), query, 0).show();
            
            mFloatPlayerSearchLayout.updateMode();
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
	
	

}
