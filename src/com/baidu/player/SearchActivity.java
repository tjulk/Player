package com.baidu.player;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.android.ext.widget.SwipeListView;
import com.baidu.browser.BPBrowser;
import com.baidu.browser.SearchManager;
import com.baidu.player.ui.FloatPlayerSearchLayout;
import com.baidu.player.ui.FloatPlayerSearchLayout.FloatSearchboxMode;
import com.baidu.player.ui.FloatPlayerSearchLayout.SearchBoxCommand;
import com.baidu.player.ui.FloatPlayerSearchLayout.SearchBoxCommandListener;
import com.baidu.player.ui.SuggestionAdapter;
import com.baidu.player.util.Utility;

/**
 * @ClassName: SearchActivity 
 * @Description: 
 * @author LEIKANG 
 * @date 2012-12-11 下午9:52:10
 */
@SuppressLint("NewApi")
public class SearchActivity extends BaseActivity{
	
	/** 标记 从何处进入搜索页面 点击地址栏 或 搜索栏 */
	public final static String TAG_IS_START_FROM_SEARCH = "TAG_IS_START_FROM_SEARCH";
	
	private boolean isStartFromSearchBtn;
	
    /** 浮动的searchBox,用来提供搜索类型选择. */
    public FloatPlayerSearchLayout mFloatPlayerSearchLayout = null;
    
    /** 输入框. */
    private EditText mQueryTextView;
    
    /** handler. */
    private Handler mHandler = new Handler();
    
    /** 搜索建议adapter. */
    private SuggestionAdapter mAdapter;
    
    /** 搜索建议/历史 listview. */
    private SwipeListView mSuggestionsListView;
    
    /** 当前关键词. */
    private String mUserQuery;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.search);
		
		isStartFromSearchBtn = getIntent().getBooleanExtra(TAG_IS_START_FROM_SEARCH, false);
		mFloatPlayerSearchLayout = (FloatPlayerSearchLayout) findViewById(R.id.float_MainRoot);
		mFloatPlayerSearchLayout.setEnableStartSearch(true);
		mFloatPlayerSearchLayout.updateMode();
		mFloatPlayerSearchLayout.setIsStartFromSearchButton(isStartFromSearchBtn);
		mFloatPlayerSearchLayout.setSearchBoxCommandListener(mSearchBoxCommandConcrete);
		mFloatPlayerSearchLayout.setOnEditorActionListener(mOnEditorActionListener);
		
        mQueryTextView = (EditText) mFloatPlayerSearchLayout.findViewById(R.id.SearchTextInput);
        mQueryTextView.addTextChangedListener(new SearchTextWatcher());
        mQueryTextView.requestFocus();
        if (isStartFromSearchBtn)
        	mQueryTextView.setHint("");
        
        mSuggestionsListView = (SwipeListView) findViewById(R.id.search_suggestion_list);
        mAdapter = new SuggestionAdapter(this, getLayoutInflater(), isStartFromSearchBtn);
        
        View emptyView = findViewById(R.id.empty_view);
        
        mSuggestionsListView.setEmptyView(emptyView);
        mSuggestionsListView.setAdapter(mAdapter);
        mSuggestionsListView.setItemsCanFocus(true);
        mSuggestionsListView.setDivider(null);
        mSuggestionsListView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 滑动搜索建议列表时，隐藏输入法
                Utility.hideInputMethod(SearchActivity.this, mQueryTextView);
            }
            public void onScroll(AbsListView view, int firstVisibleItem, 
                    int visibleItemCount, int totalItemCount) {
            }
        });
        
		initUI();
	} 
	
	/**
	 * @ClassName: SearchTextWatcher 
	 * @Description: 输入框监听
	 * @author LEIKANG 
	 * @date 2012-12-12 上午11:06:11
	 */
    private class SearchTextWatcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            String query = s.toString();
            
            setUserQuery(query);
            
            mFloatPlayerSearchLayout.updateMode();
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        	
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        	
        }
    }
    
    /**
     * @Title: setUserQuery 
     * @Description:设置查询关键字
     * @param userQuery   
     */
    protected void setUserQuery(String userQuery) {
        if (userQuery == null) {
            userQuery = "";
        }
        mUserQuery = userQuery;
        if (mAdapter != null) {
            mAdapter.setQuery(userQuery);
        }
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//输入法弹出时，点击menu键收起输入法
    	if (keyCode == KeyEvent.KEYCODE_MENU && Utility.isInputMethodActive(this, mQueryTextView)) {
			Utility.hideInputMethod(this, mQueryTextView);
			return true;
    	} 
		return super.onKeyDown(keyCode, event);
	}
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // activity 启动后第一次显示输入法软键盘
            // Launch the IME after a bit
            Runnable showInputMethodTask = new Runnable() {
                public void run() {
                    Utility.showInputMethod(SearchActivity.this, mQueryTextView);
                }
            };
            mHandler.postDelayed(showInputMethodTask, 0);
        }
    }
    
    /**
     * @Title: initUI 
     * @Description: 点击网址输入框时隐藏 输入框UI显示控制
     */
    private void initUI() {
		findViewById(R.id.brow_top_mark).setVisibility(View.GONE);
		findViewById(R.id.devise).setVisibility(View.GONE);
		findViewById(R.id.brow_top_refresh_stop).setVisibility(View.GONE);
    }

    /**
     * 设置发起搜索按钮的点击事件，当内容为空时，是返回操作，当内容不为空时，发起搜索。
     */
    private final SearchBoxCommandListener mSearchBoxCommandConcrete = new SearchBoxCommandListener() {
        @Override
        public void executeSearchBoxCommand(SearchBoxCommand command) {
            if (command == null) {
                return;
            }
            Log.d("SearchActivity", "searchbox mode: " + command.currentMode + ", query: " + command.query );
            
            FloatSearchboxMode mode = command.currentMode;
            switch (mode) {
            case SEARCH_CANCEL:
                finish();
                break;
            case SEARCH_GO:
                executeSearchCommand(command);
                finish();
                break;
            case SEARCH_VISIT:
                executeVisitCommand(command);
                finish();
                break;
            case SEARCH_BDHD:
            	executeBdhdCommand(command);
            	finish();
            	break;
            default:
                break;
            }
        }
    };
    
    /**
     * 添加键盘动作监听器，如果是搜索事件，则发起搜索。
     */
    private final OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        	if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) { 
        		boolean empty = (mUserQuery!=null)&&(!mUserQuery.equals(""));
        		if (empty)
        			mFloatPlayerSearchLayout.startSearchToplayer(mUserQuery);
        		return empty;
            } 
        	return false;
        }
    };
    
    /**
     * @Title: executeSearchCommand 
     * @Description: 执行搜索框发起的搜索命令 
     * @param command   
     */
    private void executeSearchCommand(SearchBoxCommand command) { 
        if (TextUtils.isEmpty(mUserQuery))  // 搜索词为空 直接返回
            return; 
        SearchManager.launchSearch(this, mUserQuery, false);
        finish();
    }
    
    
    /**
     * @Title: executeVisitCommand 
     * @Description: 将query被识别为url，直接发起网络访问
     * @param command   
     */
    private void executeVisitCommand(SearchBoxCommand command) {
        String url = mUserQuery;
        url = Utility.fixUrl(url).trim();
        url = Utility.addSchemeIfNeed(url);
        
        SearchManager.launchURL(this,url);
        finish();
    }
    
    /**
     * @Title: executeBdhdCommand 
     * @Description: query 被识别为bdhd地址，直接调起播放器进行播放
     * @param command   
     */
    private void executeBdhdCommand(SearchBoxCommand command) {
    	
    }
	

}
