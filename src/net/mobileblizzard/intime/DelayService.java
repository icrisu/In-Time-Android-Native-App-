package net.mobileblizzard.intime;

import java.util.Timer;
import java.util.TimerTask;

import net.mobileblizzard.intime.events.BroadcastEvents;
import net.mobileblizzard.intime.models.storage.SettingsData;
import net.mobileblizzard.intime.storage.InTimeStorage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DelayService extends Service {
	
	static final int UPDATE_INTERVAL = 1000;
	private int currentDelay=0;
	private Timer timer;
	public static final String UPDATE_INFO = "UPDATE_INFO";
	public static final String TYPE = "TYPE";
	public static final String TYPE_UPDATE = "TYPE_UPDATE";
	public static final String TYPE_CLOSE = "TYPE_CLOSE";
	
	public DelayService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//init countdown
		//informProgress();
		Log.d("d-service", "DELAY SERVICE START");
		SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
		dtaTemp.setStartDelayIsOn(true);
		InTimeStorage.getInstance().updateSettings(dtaTemp);
		
		currentDelay = (int) dtaTemp.getStartDelay();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {			
			@Override
			public void run() {
				Log.d("d-service", "On time");
				currentDelay--;				
				if(currentDelay==-1){
					//Log.d("d-service", "Stop service by itself");
					killServiceInside();
				}else{
					informProgress();
				}
			}
		}, 1000, UPDATE_INTERVAL);
		return START_STICKY;
	}
	
	@Override
	public void onCreate() {		
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		Log.d("d-service", "DELAY SERVICE STOP");
		if(timer!=null){
			timer.cancel();
		}
		SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
		dtaTemp.setStartDelayIsOn(false);
		InTimeStorage.getInstance().updateSettings(dtaTemp);		
		super.onDestroy();
	}
	
	public void informProgress(){
		Intent i = new Intent(BroadcastEvents.ON_DELAY_TICK);
		i.putExtra(TYPE, TYPE_UPDATE);
		i.putExtra(UPDATE_INFO, currentDelay);		
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);		
	}
	
	public void killServiceInside(){
		Intent i = new Intent(BroadcastEvents.ON_DELAY_TICK);
		i.putExtra(TYPE, TYPE_CLOSE);			
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);		
		this.stopSelf();
	}

}
