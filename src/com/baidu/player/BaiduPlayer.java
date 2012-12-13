package com.baidu.player;

import android.app.Application;

import com.baidu.player.util.Configurations;

public class BaiduPlayer extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		 Configurations.setDIPScal(this);
	}

	
}
