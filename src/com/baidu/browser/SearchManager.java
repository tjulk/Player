package com.baidu.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.baidu.browser.db.HistoryConfig;
import com.baidu.browser.db.Suggestion;
import com.baidu.player.MainActivity;
import com.baidu.player.R;
import com.baidu.player.util.StringUtil;
import com.baidu.player.util.SystemUtil;
import com.baidu.player.util.Turple;

/**
 * @ClassName: SearchManager 
 * @Description: ������λ�õ��������������ҳ ���Ժ�� widget ����html�ļ��ȷ�ʽ
 * @author LEIKANG 
 * @date 2012-12-12 ����5:50:41
 */
public final class SearchManager {
	
	/** ������loadurlʱ intent tag */
	public static final String TAG_KEY_URL = "key_url";
	
	private static final String searchUrl = "http://m.baidu.com/s?from=%s&tn=%s&word=%s";
	
    /**
     * ������������������������������������ߵ��ñ���������
     * @param context context
     * @param query Ҫ�����Ĺؼ���
     * @param type �������� 
     * @param voiceSuggestions �������������б�
     * @param isFromWidgetVoiceSearch �Ƿ��Ǵ�widget��������������
     * @param searchSource ������Դ
     */
    public static void launchSearch(Context context, String query, boolean isFromWidgetVoiceSearch) {
        String url = null;
        if (!TextUtils.isEmpty(query)) 
            addWebSearchHistory(query, context);
        url = getSearchUrl(query, context) + context.getResources().getString(R.string.brow_search_insert);

        // �������������������
        if (url != null) {
            Bundle extras = new Bundle();
            extras.putString(TAG_KEY_URL, url);
            startBrowser(context, extras);
        }        
    }
    
    /**
     * @Title: launchURL 
     * @Description: ֱ�Ӽ��dURL 
     * @param context
     * @param url   
     */
    public static void launchURL(Context context, String url) {
        if (url != null) {
            Bundle extras = new Bundle();
            extras.putString(TAG_KEY_URL, url);
            startBrowser(context, extras);
        } 
    }
    
    /**
     * @Title: startBrowser 
     * @Description:��������� 
     * @param context
     * @param extras   
     */
    public static void startBrowser(Context context, Bundle extras) {
        Intent intent = getBrowserStartIntent(context, extras);
        if ((context instanceof MainActivity)) {
            MainActivity activity = (MainActivity) context;
            FragmentManager manager = activity.getSupportFragmentManager();
            BPBrowser searchBrowser = (BPBrowser) manager.findFragmentByTag(BPBrowser.FRAGMENT_TAG);
            if (searchBrowser != null) {
                searchBrowser.initFromIntent(intent);
            }
        } else {
            context.startActivity(intent);
        }
    }
    
    /**
     * ��ȡ�������ҳ��intent.
     * @param extras Extras.
     * @return Intent.
     */
    public static Intent getBrowserStartIntent(Context context, Bundle extras) {
        Intent intent = new Intent(IntentConstants.ACTION_BROWSER);
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setPackage(context.getPackageName());
        return intent;
    }
    
    /**
     * @Title: addWebSearchHistory 
     * @Description: ����һ����ʷ������¼
     * @param query
     * @param ctx   
     */
    public static void addWebSearchHistory(String query, Context ctx) {
        if (TextUtils.isEmpty(query.trim()) || HistoryConfig.isPrivateMode(ctx)) {
            return;
        }
        Suggestion history = new Suggestion();
        //history.setText1(query);
        //history.setUserQuery(query);
        //history.setIntentQuery(query);
        //history.setSourceName(HistoryConfig.SOURCE_WEB);
        //history.setVersionCode("1");
        //HistoryControl.getInstance(ctx).insertHistoryInfo(history);
    }
    
    /**
     * add by LEIKANG �滻������ַΪ�ٶ�Ӱ����ַ
     * @param word
     * @param context
     * @return
     */
	private static String getSearchUrl(String word, Context context) {
		Turple<Integer, Integer> size = SystemUtil.getResolution(context);
		String ua = String.format("bdp_%d_%d_android_%s_a1", size.getX(), size.getY(), Build.VERSION.INCREMENTAL);
		String ut = String.format("%s_%s_%d", Build.MODEL, Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
		String uid = String.format("bdp_%s|%s", "321323123", StringUtil.reverse(SystemUtil.getEmid(context)));
		String pu = String.format("sz@401320_1001,osname@android,uid@%s,ua@%s,ut@%s",  StringUtil.encode(uid), StringUtil.encode(ua), StringUtil.encode(ut));
		String from = "1000324a";
		String tn = "bdplayer";
		String url = String.format(searchUrl, from, tn, StringUtil.encode(word));
		return url;
	}
	
}
