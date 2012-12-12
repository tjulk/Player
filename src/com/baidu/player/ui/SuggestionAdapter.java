package com.baidu.player.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.android.ext.widget.SwipeAdapter;
import com.baidu.browser.db.HistoryConfig;
import com.baidu.browser.db.Suggestion;
import com.baidu.player.R;
import com.baidu.player.util.Utility;

/**
 * @ClassName: SuggestionAdapter 
 * @Description: 搜索建议adapter
 * @author LEIKANG 
 * @date 2012-12-12 下午12:20:12
 */
public class SuggestionAdapter extends SwipeAdapter{
	
	/** 搜索建议view type */
	private static final int VIEW_TYPE_SUG = 0;
	
	/** 隐私模式开关view type */
	private static final int VIEW_TYPE_SWITCH_PRIVATE_MODE = 1;
	
	/** 隐私模式提示view type */
	private static final int VIEW_TYPE_PRIVATE_MODE_TIP = 2;
	
	/** view type count */
	private static final int VIEW_TYPE_COUNT = 3;
	
	/** VIEW_TAG_KEY */
	private static final int VIEW_TAG_KEY = ((0x2f << 24) | android.R.id.summary);
	
	private Context mAdapterContext;

	private LayoutInflater mInflater;
	
	private List<Suggestion> mSuggestions;
	
	private List<Suggestion> mHistorys;
	
	private boolean mIsStartFromSearchBox;
	
    /** 是否需要显示清除历史 */
    private boolean mNeedShowSwitchPrivateModeItem;
    
    /** 当前搜索词。 */
    private String mQuery;
    
    /** 清空历史记录 item onClickListener. */
    private OnClickListener mClearHistoryClickListener;
    
    /** 打开隐私模式的onClickListener */
    private OnClickListener mSwitchPrivateModeClickListener;
    
    /** 建议列表点击. */
    private SuggestionClickListener mSuggestionClickListener;
	
    public SuggestionAdapter(Context mContext, LayoutInflater inflater, boolean isStartFromSearchBox) {
        mAdapterContext = mContext;
        mInflater = inflater;
        mSuggestions = new ArrayList<Suggestion>();
        mHistorys = new ArrayList<Suggestion>();
        mIsStartFromSearchBox = isStartFromSearchBox;
        Utility.setScreenDensity(mContext);
    }

	@Override
	public int getCount() {
        int count = mSuggestions.size();
        if (TextUtils.isEmpty(mQuery) && HistoryConfig.isPrivateMode(mAdapterContext)) 
            count = 1;
        
        setNeedShowSwitchPrivateModeItem();
        
        if (mNeedShowSwitchPrivateModeItem) 
            count += 1;
        
        return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		
        if (convertView != null) {
            if (((Integer) convertView.getTag(VIEW_TAG_KEY)).intValue() != viewType) {
                convertView = null;
            }
        }
        
        if (convertView == null) {
            int resource = R.layout.suggestion_item;
            if (viewType == VIEW_TYPE_SWITCH_PRIVATE_MODE) {
                resource = R.layout.suggestion_clear_history_item;
            } else if(viewType == VIEW_TYPE_PRIVATE_MODE_TIP) {
                resource = R.layout.suggestion_private_mode_tip_item;
            } 
            else {
            }
            convertView = mInflater.inflate(resource, parent, false);
            // 标记此view的类型
            convertView.setTag(VIEW_TAG_KEY, Integer.valueOf(viewType));
        }
        
        if (viewType == VIEW_TYPE_SWITCH_PRIVATE_MODE) {
            buildSwitchPrivateModeView(convertView);
        } else if(viewType == VIEW_TYPE_PRIVATE_MODE_TIP) {
            // 隐藏模式下的提示条
        } else {
            buildSugView(position, convertView, parent);
        }
        
		return convertView;
	}
	
	/**
	 * @Title: buildSwitchPrivateModeView 
	 * @Description: 构造建议提示 包括 清空历史，添加百度影音关键字， 搜索关键字‘xxx’，隐私模式等等
	 * @param convertView   
	 */
    private void buildSwitchPrivateModeView(View convertView) {
        View itemClearHistoryLayout = convertView.findViewById(R.id.suggestion_clear_history_layout);     
        TextView itemClearHistory = (TextView) convertView.findViewById(R.id.suggestion_clear_history);
        TextView itemOpenPrivateMode = (TextView) convertView.findViewById(R.id.suggestion_open_private_mode);
        itemClearHistory.setOnClickListener(mClearHistoryClickListener);
        itemOpenPrivateMode.setOnClickListener(mSwitchPrivateModeClickListener);
        
        if (TextUtils.isEmpty(mQuery) && mSuggestions.size() == 0 && mHistorys.size() == 0) {
            itemClearHistoryLayout.setVisibility(View.VISIBLE);
            itemClearHistory.setEnabled(false);
            itemClearHistory.setText(mIsStartFromSearchBox ? R.string.no_search_history : R.string.no_web_history);
        } 
        
        if (mHistorys.size() > 0){
            itemClearHistoryLayout.setVisibility(View.VISIBLE);
            itemClearHistory.setEnabled(true);
           	itemClearHistory.setText(mIsStartFromSearchBox ? R.string.search_clear_history : R.string.web_clear_history);
        }
        
        if (!TextUtils.isEmpty(mQuery) && !Utility.isUrl(mQuery) && mHistorys.size() == 0) {
            itemClearHistoryLayout.setVisibility(View.VISIBLE);
            itemClearHistory.setEnabled(true);
            if (mQuery.length()>10)
            	mQuery = mQuery.substring(0, 6) + "...";
        	String format = mAdapterContext.getResources().getString(R.string.brow_address_go_search_text);
        	String text = String.format(format, mQuery);
        	itemClearHistory.setText(text);
        	
        	if(mSuggestions.size() == 0 && mIsStartFromSearchBox) {
        		itemClearHistory.setEnabled(false);
        		itemClearHistory.setText(R.string.brow_search_no_suggestion);	
        	}
        }
        
        if (HistoryConfig.isPrivateMode(mAdapterContext)) {
            itemOpenPrivateMode.setText(R.string.search_close_private_mode);
        } else {
            itemOpenPrivateMode.setText(R.string.suggestion_open_private_mode);
        }
    }
    
    /**
     * @Title: buildSugView 
     * @Description: 构造搜索建议 
     * @param position
     * @param convertView
     * @param parent   
     */
    private void buildSugView(int position, View convertView, ViewGroup parent) { 
    	
    	
    }

	@Override
	protected int removeAndInsert(int removePos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSwipeAction(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
    
    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mQuery) && HistoryConfig.isPrivateMode(mAdapterContext)) {
            if(position == 0) 
                return VIEW_TYPE_PRIVATE_MODE_TIP;
             else 
                return VIEW_TYPE_SWITCH_PRIVATE_MODE;
        }
        if (position >= mSuggestions.size()) 
            return VIEW_TYPE_SWITCH_PRIVATE_MODE;
        return VIEW_TYPE_SUG;
    }
	
    /**
     * 设置当前搜索词。
     * @param query 搜索词
     */
    public void setQuery(String query) {
        mQuery = query;
        // 由于清除历史记录显示状态有变化会改变getCount(清除历史按钮在“隐私模式”item上)，需要notify
        boolean oldNeedShowSwitchPrivateModeItem = mNeedShowSwitchPrivateModeItem;
        setNeedShowSwitchPrivateModeItem();
        if (oldNeedShowSwitchPrivateModeItem != mNeedShowSwitchPrivateModeItem) {
            notifyDataSetChanged();
        }
    }
    
    /**
     * 设置是否需要显示清除历史
     */
    private void setNeedShowSwitchPrivateModeItem() {
        boolean result = false;
        if (TextUtils.isEmpty(mQuery)) {
            result = true;
        } else if (!Utility.isUrl(mQuery)) {
            result = true;
        }
        mNeedShowSwitchPrivateModeItem = result;
    }
    
    /**
     * set suggestion/history item click listener.
     * @param listener  the listener
     */
    public void setSuggestionClickListener(SuggestionClickListener listener) {
        mSuggestionClickListener = listener;
    }
    
    /**
     * 设置清空历史记录 click listener.
     * @param listener listener
     */
    public void setClearHistoryClickListener(OnClickListener listener) {
        mClearHistoryClickListener = listener;
    }
    
    /**
     * 打开隐私模式的 click listener.
     * @param listener listener
     */
    public void setSwitchPrivateModeClickListener(OnClickListener listener) {
        mSwitchPrivateModeClickListener = listener;
    }

}
