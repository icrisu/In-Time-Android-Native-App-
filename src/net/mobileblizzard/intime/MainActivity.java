package net.mobileblizzard.intime;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import net.mobileblizzard.intime.camera.CameraSPreview;

import net.mobileblizzard.intime.events.BroadcastEvents;

import net.mobileblizzard.intime.menus.DelayMenuWrapper;
import net.mobileblizzard.intime.menus.MainMenu;

import net.mobileblizzard.intime.menus.MainMenuWrapper;
import net.mobileblizzard.intime.menus.ThreadMenuWrapper;
import net.mobileblizzard.intime.modals.BlackPopupWrapper;
import net.mobileblizzard.intime.models.storage.SettingsData;
import net.mobileblizzard.intime.models.storage.TimeLap;
import net.mobileblizzard.intime.storage.InTimeStorage;
import android.R.bool;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	private static final String LOG_TAG = "intime";
	
	private MainMenu mainMenuUI;	
	private MainMenuWrapper mainMenuWrapper;
	
	private FrameLayout blackScreenUI;	
	private FrameLayout cameraPreview;
	
	private SettingsWrapper settingsWrapper;
	private DelayMenuWrapper delayMenuWrapper;
	private ThreadMenuWrapper threadMenuWrapper;
	
	private boolean isWorkerStarted = false;
	private CameraSPreview mPreview;
	private Camera camera;
	
	private BlackPopupWrapper confirmWrapper;
	private TimeLap currentTimeLap;
	
	private Timer informTimer;
	private TextView threadMinTxt;
	private TextView threadFramesTxt;
	private String minutesInfo;
	private String framesInfo;	
	
	

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        blackScreenUI = (FrameLayout) findViewById(R.id.blackScreenUI);
        
        //main menu
        mainMenuUI = (MainMenu) findViewById(R.id.mainMenuUI);
        mainMenuWrapper = new MainMenuWrapper(this, mainMenuUI, blackScreenUI);
        
        //delay menu
        delayMenuWrapper = new DelayMenuWrapper((FrameLayout) findViewById(R.id.startDelayMenu), this);
        
        //thread menu 
        threadMenuWrapper = new ThreadMenuWrapper((FrameLayout) findViewById(R.id.threadMenu), this);
        
        //settings
        settingsWrapper = new SettingsWrapper((FrameLayout) findViewById(R.id.settingsView), this);
        if(isSettingsOpen)
        	settingsWrapper.open();
                       
        
        cameraPreview = (FrameLayout) findViewById(R.id.cameraPreview);         
        
        //REGISTER BROADCAST EVENTS
        LocalBroadcastManager.getInstance(this).registerReceiver(delayStartReceiver, new IntentFilter(BroadcastEvents.ON_DELAY_TICK));
        LocalBroadcastManager.getInstance(this).registerReceiver(informStatusReceiver, new IntentFilter(BroadcastEvents.INFORM_STATUS_RECEIVER));
        //LocalBroadcastManager.getInstance(this).registerReceiver(cameraObjectReceiver, new IntentFilter(BroadcastEvents.CAMERA_OBJECT_RESPONSE));
        
        //show context menu 
		SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
		if(dtaTemp.isStartDelayIsOn()){
			//show delay menu
			this.getDelayMenuWrapper().showIn();
			if(mainMenuWrapper!=null){
				mainMenuWrapper.closeMenuNow();
				mainMenuWrapper.closeMenuItems();
				mainMenuWrapper.closePlus(MainMenuWrapper.ACTION_NONE);
			}
		}
		
		//check
		keepScreenOn();
		
		//confirm wrapper
		confirmWrapper = new BlackPopupWrapper();
		confirmWrapper.initChooser(this, (FrameLayout) findViewById(R.id.confirmChooserUI), (FrameLayout) findViewById(R.id.confirmChooserUIBlack), (LinearLayout) findViewById(R.id.confirmChooserUIContent), (FrameLayout) findViewById(R.id.closeConfirmChooserUI));
		confirmWrapper.initConfirmControls();	
		
		//info
		threadFramesTxt = (TextView) findViewById(R.id.threadFramesTxt);
		threadMinTxt = (TextView) findViewById(R.id.threadMinTxt);
		minutesInfo = getString(R.string.minutes_info)+" ";
		framesInfo = getString(R.string.frames_info)+" ";
    }
        
    //private Camera c;
    @Override
    protected void onResume() { 
		//start engine service if not started yet    	
		if(isWorkerStarted){
			//show thread menu
			threadMenuWrapper.showIn(0);
			if(mainMenuWrapper!=null){
				mainMenuWrapper.closeMenuNow();
				mainMenuWrapper.closeMenuItems();
				mainMenuWrapper.closePlus(MainMenuWrapper.ACTION_NONE);
			}		
		}		
		accessCamera();
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	//UNREGISTER RECEIVERS
        LocalBroadcastManager.getInstance(this).unregisterReceiver(delayStartReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(informStatusReceiver);        
        Log.d(LOG_TAG, "DESTROY HERE");    	    	
        if(isWorkerStarted){
        	mediaRecorder.stop();
        	releaseMediaRecorder();
        	releaseCamera(); 
        	saveCurrentTimelap();
        	MediaScannerConnection.scanFile(InTimeApp.getInstance().getApplicationContext(), new String[] { currentVideoPath }, new String[] { null }, null);
        }else{
        	releaseCamera();
        }
        isWorkerStarted = false;        
    	super.onDestroy();
    }    
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if(!isWorkerStarted){
    		releaseCamera();
    	}
    	Log.d(LOG_TAG, "PAUSE HERE");    	
    }
    
    
    private void accessCamera(){
    	try {    		
    		camera = Camera.open();
		} catch (RuntimeException e) {
			Toast.makeText(this, "Could not connect to the camera! Camera might be used by another application.", Toast.LENGTH_LONG).show();
			return;
		}
    	if(camera==null)
    		return;
    	
    	Camera.Parameters params = camera.getParameters();
    	//if no settings
    	SettingsData stData = InTimeStorage.getInstance().getSettingsData();
    	
    	//WHITE BALANCE
    	if(stData.getWhiteBalanceList()==null){
    		stData.setWhiteBalanceList(params.getSupportedWhiteBalance());    		
    		stData.setSelectedWhiteBalance(0);
    		InTimeStorage.getInstance().updateSettings(stData);
    	}  
    	
    	//FOCUS MODE
    	if(stData.getFocusModes()==null){
    		List<String> focusModes = params.getSupportedFocusModes();
    		stData.setFocusModes(focusModes);
    		for (int i = 0; i < focusModes.size(); i++) {
				if(focusModes.get(i).equals("auto")){
					stData.setSelectedFocusModeIndex(i);
					break;
				}
			}
    		InTimeStorage.getInstance().updateSettings(stData);
    	} 
    	
    	//COLOR EFX
    	if(stData.getColorEfects()==null){
    		List<String> colorEfx = params.getSupportedColorEffects();
    		stData.setColorEfects(colorEfx);
    		for (int i = 0; i < colorEfx.size(); i++) {
				if(colorEfx.get(i).equals("none")){
					stData.setSelectedColorEfxIndx(i);
					break;
				}
			}
    		InTimeStorage.getInstance().updateSettings(stData);
    	}
    	
    	//FLASH MODE
    	if(stData.getFlashModels()==null){
    		List<String> flashModes = params.getSupportedFlashModes();
    		stData.setFlashModels(flashModes);
    		for (int i = 0; i < flashModes.size(); i++) {
				if(flashModes.get(i).equals("off")){
					stData.setSelectedFlashModeIndx(i);
					break;
				}
			}
    		InTimeStorage.getInstance().updateSettings(stData);
    	}
    	

    	mPreview = new CameraSPreview(this, camera);    
    	mPreview.init(this);
    	cameraPreview.addView(mPreview);    		
    }
  
    
    
    
	//----------------------ROUTES-----------------
    //start delay menu
    public void startDelayMenu(){
    	//start delay service
    	startService(new Intent(getBaseContext(), DelayService.class));    	
    	this.getDelayMenuWrapper().showIn();
    }
    public void stopDelayMenu(){
    	//stop delay service
    	stopService(new Intent(getBaseContext(), DelayService.class));
    	this.getDelayMenuWrapper().cancel();
    }
    
    //thread menu
    public void stopThreadMenu(){
    	//stop worker    	
    	getThreadMenuWrapper().cancel();  
    	stopWorkerService();
    }    
    
    //start worker service
    public void startWorkerService(){    	
		if(prepareMediaRecorder() && !isWorkerStarted){
			isWorkerStarted = true;
			mediaRecorder.start();
			double hours = InTimeStorage.getInstance().getSettingsData().getPhotosInterval();
			long val = Math.round(hours*1000);
			currentRate = val;
			initInformTimer(val);				
		}  	
    }
    //stop worker service
    public void stopWorkerService(){
    	isWorkerStarted = false;
		mediaRecorder.stop();
		releaseMediaRecorder();
		camera.lock();
		//Log.d(LOG_TAG, "STOP VIDEO HERE");
		saveCurrentTimelap();
		MediaScannerConnection.scanFile(InTimeApp.getInstance().getApplicationContext(), new String[] { currentVideoPath }, new String[] { null }, null);    	  	
    }    
    
    //save current timelap
    private void saveCurrentTimelap(){
    	destroyInformTimer();
    	if(currentTimeLap==null)
    		return;
    	SettingsData dta = InTimeStorage.getInstance().getSettingsData();
    	Log.d(LOG_TAG, "CURRENT FRAMES"+currentFrames);
    	currentTimeLap.setFrames(currentFrames);
    	dta.addTimeLap(currentTimeLap);
    	InTimeStorage.getInstance().updateSettings(dta);
    	Toast.makeText(this, getString(R.string.video_created_confirm), Toast.LENGTH_LONG).show();
    }
    
    private int currentFrames = 0;
    private double currentMinutes = 0;
    private long currentRate;
    //init inform timer
    private void initInformTimer(long period){
    	threadFramesTxt.setText(framesInfo);
    	threadMinTxt.setText(minutesInfo);
    	currentFrames = 0;
    	currentMinutes = 0;
    	informTimer = new Timer();
    	informTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				currentFrames++;				
				LocalBroadcastManager.getInstance(InTimeApp.getInstance().getApplicationContext()).sendBroadcast(new Intent(BroadcastEvents.INFORM_STATUS_RECEIVER));
			}
		}, 0, period);
    }
    
    //destroy inform timer
    private void destroyInformTimer(){
    	threadFramesTxt.setText(framesInfo);  
    	threadMinTxt.setText(minutesInfo);    	
		if(informTimer!=null){
			informTimer.cancel();
			informTimer = null;
		}    	
    }
    
    
	
    
    
	//----------------------BROADCAST RECEIVERS---------------
	//MESSAGE INCOME UPDATE EVENT
	private BroadcastReceiver delayStartReceiver = new BroadcastReceiver() {		
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			DelayMenuWrapper dmw = getDelayMenuWrapper();
			if(dmw==null)
				return;
			if(bundle.getString(DelayService.TYPE).equals(DelayService.TYPE_UPDATE))
				dmw.updateSeconds(bundle.getInt(DelayService.UPDATE_INFO));
			if(bundle.getString(DelayService.TYPE).equals(DelayService.TYPE_CLOSE)){
				getDelayMenuWrapper().showOut(DelayMenuWrapper.ACTION_START_ENGINE);
				if(threadMenuWrapper!=null){
					threadMenuWrapper.showIn(250);
					//start worker
					startWorkerService();
				}										
			}
			
		}
	};
	//INFORM STATUS RECEIVER
	private BroadcastReceiver informStatusReceiver = new BroadcastReceiver() {		
		public void onReceive(Context context, Intent intent) {	
			threadFramesTxt.setText(framesInfo+Integer.toString(currentFrames));
			long milisecodsDuration = (currentRate*currentFrames);
			long secondsDuration = milisecodsDuration/1000;											
			threadMinTxt.setText(minutesInfo+Long.toString((1*secondsDuration)/60));
			//1sec.......30frames
			//xsec.......currentFrames100
			
			
		}
	};	
		
	
	
	
    //SETTERS AND GETTERS
    public MainMenuWrapper getMainMenuWrapper() {    	
		return mainMenuWrapper;
	}

	public void setMainMenuWrapper(MainMenuWrapper mainMenuWrapper) {
		this.mainMenuWrapper = mainMenuWrapper;
	}

	public SettingsWrapper getSettingsWrapper() {
		return settingsWrapper;
	}

	public void setSettingsWrapper(SettingsWrapper settingsWrapper) {
		this.settingsWrapper = settingsWrapper;
	} 

	
	private boolean isSettingsOpen = false;
	public boolean isSettingsOpen() {
		return isSettingsOpen;
	}
	public void setSettingsOpen(boolean isSettingsOpen) {
		this.isSettingsOpen = isSettingsOpen;
	}
	
	private boolean isMoviesOpen = false;
	public boolean isMoviesOpen() {
		return isMoviesOpen;
	}
	public void setMoviesOpen(boolean isMoviesOpen) {
		this.isMoviesOpen = isMoviesOpen;
	}

	public DelayMenuWrapper getDelayMenuWrapper() {
		return delayMenuWrapper;
	}
	public void setDelayMenuWrapper(DelayMenuWrapper delayMenuWrapper) {
		this.delayMenuWrapper = delayMenuWrapper;
	}

	public ThreadMenuWrapper getThreadMenuWrapper() {
		return threadMenuWrapper;
	}

	public void setThreadMenuWrapper(ThreadMenuWrapper threadMenuWrapper) {
		this.threadMenuWrapper = threadMenuWrapper;
	}	
	public CameraSPreview getmPreview() {
		return mPreview;
	}
	public void setmPreview(CameraSPreview mPreview) {
		this.mPreview = mPreview;
	}
	
	public BlackPopupWrapper getConfirmWrapper() {
		return confirmWrapper;
	}
	public void setConfirmWrapper(BlackPopupWrapper confirmWrapper) {
		this.confirmWrapper = confirmWrapper;
	}



	//prepare media recorder
	private MediaRecorder mediaRecorder;
	private String currentVideoPath;
	private boolean prepareMediaRecorder(){
		SettingsData dta = InTimeStorage.getInstance().getSettingsData();		
		mediaRecorder = new MediaRecorder();
		camera.stopPreview();
		camera.unlock();
		mediaRecorder.setCamera(camera);
		//mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		
		File videoFile = getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
		currentVideoPath = videoFile.getPath();
		
		//init current timelap
		Calendar now = Calendar.getInstance();
		String lapName = (now.get(Calendar.DAY_OF_MONTH))+" "+EngineService.theMonth(now.get(Calendar.MONTH))+" "+Integer.toString(now.get(Calendar.YEAR)).toString();
		currentTimeLap = new TimeLap(currentVideoPath, lapName);
		
		double seconds = 1;		
		//double finalS = 1/seconds;
		//mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
		mediaRecorder.setProfile(CamcorderProfile.get(dta.getSelectedResolutionID()));
				
		
		mediaRecorder.setCaptureRate(1/seconds);
		//mediaRecorder.setCaptureRate(7.0);
		
		mediaRecorder.setOutputFile(videoFile.toString());
		mediaRecorder.setMaxDuration((int)(dta.getTimelapDuration()*3600)*1000);		
		//Log.d(LOG_TAG, "DURATION: "+(dta.getTimelapDuration()*3600)*1000);
		//mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
		mediaRecorder.setOrientationHint(dta.getCameraAngle());
		
		
		mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
	    // Step 6: Prepare configured MediaRecorder
	    try {
	    	mediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(LOG_TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(LOG_TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    
	    return true;		
	}
	
	
    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
        	mediaRecorder.reset();   // clear recorder configuration
        	mediaRecorder.release(); // release the recorder object
        	mediaRecorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (camera != null){
        	camera.release();        // release the camera for other applications
        	camera = null;
        }
    }
	
	
	
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "InTimeVideos");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d(LOG_TAG, "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    
	    if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}	
	
	
	
	//KEEP SCREEN ON
	public void keepScreenOn(){
		cameraPreview.setKeepScreenOn(InTimeStorage.getInstance().getSettingsData().isKeepScreenOn());
	}
	
	
	//REMOVE ALL VIDEOS
	public void removeAllVideos(){
		ArrayList<TimeLap> timeLaps = InTimeStorage.getInstance().getSettingsData().getTimelaps();
		if(timeLaps==null)
			return;
		if(timeLaps.size()==0){
			Toast.makeText(this, getString(R.string.no_timelapse_to_remve), Toast.LENGTH_LONG).show();
			return;
		}
		RemoveVideos rv = new RemoveVideos();
		rv.execute(timeLaps);
	}
	
	private ArrayList<File> deletedFiles;
	private MediaScannerConnection conn;
	public void informRemoveAllVideos(ArrayList<File> result){
		deletedFiles = result;
		Toast.makeText(this, getString(R.string.all_videos_removed), Toast.LENGTH_LONG).show();
		/*
		if(deletedFiles==null)
			return;		
		MediaScannerConnectionClient mediaScannerConnectionClient = new MediaScannerConnectionClient() {				
			@Override
			public void onScanCompleted(String path, Uri uri) {
				//Log.d(LOG_TAG, "SCAN_COMPLETE");
				conn.disconnect(); 
			}	
			
			@Override
			public void onMediaScannerConnected() {
				//Log.d(LOG_TAG, "SCANNER_CONNECTED");
				for (int i = 0; i < deletedFiles.size(); i++) {
					File t = deletedFiles.get(i);					 						 					 
				    try{
				        //conn.scanFile(t.getPath(), "image/*");
				        conn.scanFile(t.getPath(), null);
				       } catch (java.lang.IllegalStateException e){
				    }	
				    				
				}													
				
			}
		};	
		conn = new MediaScannerConnection(this, mediaScannerConnectionClient);
		conn.connect();
		*/
	}
	
	public class RemoveVideos extends AsyncTask<ArrayList<TimeLap>, String, ArrayList<File>>{
		@Override
		protected ArrayList<File> doInBackground(ArrayList<TimeLap>... params) {
			ArrayList<File> outResult = new ArrayList<File>();
			ArrayList<TimeLap> temp = (ArrayList<TimeLap>) params[0];
			//Log.d(LOG_TAG, "Deleting: "+temp.size());
			for (int i = 0; i < temp.size(); i++) {
				TimeLap t = temp.get(i);
				//Log.d(LOG_TAG, "PAth: "+t.getPath());
		    	File f = new File(t.getPath());
		    	boolean isRemoved = false;
		    	if(f.exists()){
		    		try {
		    			isRemoved = f.delete();		    			
					} catch (Exception e) {
						// TODO: handle exception
					}
		    		if(isRemoved){
			    		outResult.add(f);
			    		publishProgress(t.getID());		    			
		    		}
		    	}		    	
			}
			return outResult;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			String ID = values[0];			
			SettingsData dta = InTimeStorage.getInstance().getSettingsData();
			dta.removeTimelap(ID);
			InTimeStorage.getInstance().updateSettings(dta);
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(ArrayList<File> result) {
			//Log.d(LOG_TAG, "ALL DELETED");
			informRemoveAllVideos(result);			
			super.onPostExecute(result);
		}
	}
	
	
	//prevent back button
	@Override
	public void onBackPressed() {
		if(isSettingsOpen){
			settingsWrapper.close();
		}else{
			super.onBackPressed();
		}		
	}
	
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}	
	

	
	//----------------------UTILS-------------------		
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	public static String theMonth(int month){
	    String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	    return monthNames[month];
	}		
    
    

}
