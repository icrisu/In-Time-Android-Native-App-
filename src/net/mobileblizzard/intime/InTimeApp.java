package net.mobileblizzard.intime;

import android.app.Application;

public class InTimeApp extends Application {
	private static InTimeApp instance;
	
	public static InTimeApp getInstance(){
		return instance;
	}
	
	@Override
	public void onCreate() {
		instance = this;		
		super.onCreate();
	}
}
