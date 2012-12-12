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
 * @Description: ��������adapter
 * @author LEIKANG 
 * @date 2012-12-12 ����12:20:12
 */
public class SuggestionAdapter extends SwipeAdapter{
	
	/** ��������view type */
	private static final int VIEW_TYPE_SUG = 0;
	
	/** ��˽ģʽ����view type */
	private static final int VIEW_TYPE_SWITCH_PRIVATE_MODE = 1;
	
	/** ��˽ģʽ��ʾview type */
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
	
    /** �Ƿ���Ҫ��ʾ�����ʷ */
    private boolean mNeedShowSwitchPrivateModeItem;
    
    /** ��ǰ�����ʡ� */
    private String mQuery;
    
    /** �����ʷ��¼ item onClickListener. */
    private OnClickListener mClearHistoryClickListener;
    
    /** ����˽ģʽ��onClickListener */
    private OnClickListener mSwitchPrivateModeClickListener;
    
    /** �����б���. */
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
            // ��Ǵ�view������
            convertView.setTag(VIEW_TAG_KEY, Integer.valueOf(viewType));
        }
        
        if (viewType == VIEW_TYPE_SWITCH_PRIVATE_MODE) {
            buildSwitchPrivateModeView(convertView);
        } else if(viewType == VIEW_TYPE_PRIVATE_MODE_TIP) {
            // ����ģʽ�µ���ʾ��
        } else {
            buildSugView(position, convertView, parent);
        }
        
		return convertView;
	}
	
	/**
	 * @Title: buildSwitchPrivateModeView 
	 * @Description: ���콨����ʾ ���� �����ʷ����Ӱٶ�Ӱ���ؼ��֣� �����ؼ��֡�xxx������˽ģʽ�ȵ�
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
     * @Description: ������������ 
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
     * ���õ�ǰ�����ʡ�
     * @param query ������
     */
    public void setQuery(String query) {
        mQuery = query;
        // ���������ʷ��¼��ʾ״̬�б仯��ı�getCount(�����ʷ��ť�ڡ���˽ģʽ��item��)����Ҫnotify
        boolean oldNeedShowSwitchPrivateModeItem = mNeedShowSwitchPrivateModeItem;
        setNeedShowSwitchPrivateModeItem();
        if (oldNeedShowSwitchPrivateModeItem != mNeedShowSwitchPrivateModeItem) {
            notifyDataSetChanged();
        }
    }
    
    /**
     * �����Ƿ���Ҫ��ʾ�����ʷ
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
     * ���������ʷ��¼ click listener.
     * @param listener listener
     */
    public void setClearHistoryClickListener(OnClickListener listener) {
        mClearHistoryClickListener = listener;
    }
    
    /**
     * ����˽ģʽ�� click listener.
     * @param listener listener
     */
    public void setSwitchPrivateModeClickListener(OnClickListener listener) {
        mSwitchPrivateModeClickListener = listener;
    }

}
