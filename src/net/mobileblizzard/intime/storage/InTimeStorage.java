package net.mobileblizzard.intime.storage;

import com.google.gson.Gson;

import net.mobileblizzard.intime.InTimeApp;
import net.mobileblizzard.intime.models.storage.SettingsData;
import android.content.SharedPreferences;

public class InTimeStorage {
	public static final String PREFERENCE_KEY = "net.mobileblizzard.InTime.DESTINY_PREFERences";
	public static final String SETTINGS_DATA = "SETTINGS_DATA";
	
	private InTimeApp app;
	
	public InTimeStorage() {
		app = InTimeApp.getInstance();
	}	
	private static InTimeStorage instance;
	public static InTimeStorage getInstance(){
		if(instance==null){
			instance = new InTimeStorage();
		}
		return instance;
	}
	
	//get settings
	public SettingsData getSettingsData(){
		SharedPreferences appPref = app.getSharedPreferences(PREFERENCE_KEY, InTimeApp.MODE_PRIVATE);
		String settingsRaw = appPref.getString(SETTINGS_DATA, "");
		SettingsData dta;
		if(settingsRaw.equals("")){
			//create settings first time and update
			dta = new SettingsData();
			dta.init();
			updateSettings(dta);
		}else{
			Gson g = new Gson();
			dta = g.fromJson(settingsRaw, SettingsData.class);
		}
		return dta;
	}
	//update settings
	public void updateSettings(SettingsData dta){
		SharedPreferences appPref = app.getSharedPreferences(PREFERENCE_KEY, InTimeApp.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = appPref.edit();
		Gson g = new Gson();
		prefEditor.putString(SETTINGS_DATA, g.toJson(dta));
		prefEditor.commit();		
	}

}
