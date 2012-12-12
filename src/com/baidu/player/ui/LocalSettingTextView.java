
package com.baidu.player.ui;


import com.baidu.player.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @ClassName: LocalSettingTextView 
 * @Description: ��������sugҳ��ı�������item�е�TextView
 * @author LEIKANG 
 * @date 2012-12-12 ����2:03:39
 */
public class LocalSettingTextView extends TextView {
 
    public LocalSettingTextView(Context context) {
        super(context);
    }
 
    public LocalSettingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public LocalSettingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {  
        if (this.isPressed()) {
            this.setTextColor(getResources().getColor(R.color.sug_local_setting_text_color_press));
        } else{
            this.setTextColor(getResources().getColor(R.color.sug_local_setting_text_color_normal));
        }
        super.onDraw(canvas);
    }

}


