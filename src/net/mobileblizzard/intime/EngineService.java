package net.mobileblizzard.intime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import net.mobileblizzard.intime.camera.CameraPreview;
import net.mobileblizzard.intime.camera.CameraServicePreview;
import net.mobileblizzard.intime.events.BroadcastEvents;
import net.mobileblizzard.intime.events.CameraTransport;
import net.mobileblizzard.intime.models.storage.SettingsData;

import net.mobileblizzard.intime.storage.InTimeStorage;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class EngineService extends Service {
	private static final String LOG_TAG = "engine-service";
	public static final String CAMERA_TRANSPORT_OBJECT = "CAMERA_TRANSPORT_OBJECT";
	private Camera camera;
	private CameraServicePreview cPreview;
	private Timer timer;
	private double duration;
	private double photosInterval;		
	private boolean isWorking = false;
	private int photosTaken;
	private ArrayList<String> currentPaths;

	public EngineService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "ENGINE STOPPED");
		//camera.release();
		//mPreview.on
		if(camera!=null){
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		if(cPreview!=null){
			cPreview.clean();			
		}
		LocalBroadcastManager.getInstance(this).unregisterReceiver(startWorkerReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(stopWorkerReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(cameraRequestReceiver);
		isWorking = false;
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "ENGINE STARTED");
		LocalBroadcastManager.getInstance(this).registerReceiver(startWorkerReceiver, new IntentFilter(BroadcastEvents.START_WORKER));
		LocalBroadcastManager.getInstance(this).registerReceiver(stopWorkerReceiver, new IntentFilter(BroadcastEvents.STOP_WORKER));
		LocalBroadcastManager.getInstance(this).registerReceiver(cameraRequestReceiver, new IntentFilter(BroadcastEvents.REQUEST_CAMERA_OBJECT));
		openCamera();
		return START_STICKY;
	}
	
	private void openCamera(){
    	try {    		
    		camera = Camera.open();
		} catch (RuntimeException e) {
			Toast.makeText(this, "Could not connect to the camera!", Toast.LENGTH_SHORT).show();
			return;
		}
    	if(camera==null)
    		return;
		
    	cPreview = new CameraServicePreview(this, camera);    	
    	sendCameraObject();
	}
	
	private void sendCameraObject(){
		//send camera object back
		Intent i = new Intent(BroadcastEvents.CAMERA_OBJECT_RESPONSE);
		CameraTransport ct = new CameraTransport(camera, cPreview);
		i.putExtra(CAMERA_TRANSPORT_OBJECT, ct);			
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);		
	}
	
	
	
	//----------------------BROADCAST RECEIVERS---------------	
	//MESSAGE INCOME - START WORKER
	private BroadcastReceiver startWorkerReceiver = new BroadcastReceiver() {		
		public void onReceive(Context context, Intent intent) {
			startWorker();
		}
	};	
	//MESSAGE INCOME - STOP WORKER
	private BroadcastReceiver stopWorkerReceiver = new BroadcastReceiver() {		
		public void onReceive(Context context, Intent intent) {
			stopWorker();
		}
	};
	//MESSAGE INCOME - CAMERA REQUEST RECEIVER
	private BroadcastReceiver cameraRequestReceiver = new BroadcastReceiver() {		
		public void onReceive(Context context, Intent intent) {
			sendCameraObject();
		}
	};			
	
	
	//----------------------WORKER---------------		
	private void startWorker(){		
		if(prepareMediaRecorder() && !isWorking){
			isWorking = true;
			mediaRecorder.start();
		}
		/*
		if(!isExternalStorageWritable()){
			//could not save photo to external storage - implement error
			return;			
		}		
		
		photosTaken = 0;
		currentPaths = new ArrayList<String>();
		Log.d(LOG_TAG, "WORKER STARTED");
		SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
		duration = dtaTemp.getTimelapDuration();
		photosInterval = dtaTemp.getPhotosInterval();
		dtaTemp.setWorkerStarted(true);
		InTimeStorage.getInstance().updateSettings(dtaTemp);
		
		Camera.Parameters p = camera.getParameters();
		//p.setPictureSize(30, 30); TBD
		
		Calendar now = Calendar.getInstance();
		String name = Integer.toString(now.get(Calendar.DAY_OF_MONTH))+"/"+EngineService.theMonth(now.get(Calendar.MONTH))+"/"+Integer.toString(now.get(Calendar.YEAR));
		album = new TimeLap();
		album.open(UUID.randomUUID().toString(), name);
		
		//init interval		
		startPhotosInterval();
		//takePhoto();	
		 * 	
		 */
	}
	private void stopWorker(){
		Log.d(LOG_TAG, "WORKER STOPPED");
		if(isWorking){
			isWorking = false;
			mediaRecorder.stop();
			releaseMediaRecorder();
			camera.lock();
			MediaScannerConnection.scanFile(InTimeApp.getInstance().getApplicationContext(), new String[] { currentVideoPath }, new String[] { null }, null);			
		}
		/*
		isWorking = false;
		SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
		dtaTemp.setWorkerStarted(false);
		if(album!=null){
			album.close(photosTaken);
			dtaTemp.addNewTimelapAlbum(album);
		}
		InTimeStorage.getInstance().updateSettings(dtaTemp);
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		scanMedia();
		*/
	}
	
	
	//prepare media recorder
	private MediaRecorder mediaRecorder;
	private String currentVideoPath;
	private boolean prepareMediaRecorder(){
		mediaRecorder = new MediaRecorder();
		camera.stopPreview();
		camera.unlock();
		mediaRecorder.setCamera(camera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		
		File videoFile = getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
		currentVideoPath = videoFile.getPath();
		
		
		mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
		mediaRecorder.setOutputFile(videoFile.toString());
		//mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
		mediaRecorder.setOrientationHint(90);
		
		
		mediaRecorder.setPreviewDisplay(cPreview.getHolder().getSurface());
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
	
	
	
	private void scanMedia(){
		if(currentPaths==null)
			return;
		for (int i = 0; i < currentPaths.size(); i++) {
			MediaScannerConnection.scanFile(InTimeApp.getInstance().getApplicationContext(), new String[] { currentPaths.get(i) }, new String[] { "image/jpeg" }, null);
		}				
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
	
	
	
	
	
	
	
	
	//////////////DELETE BELOW
	
	
	
	
	//----------------------TAKE PHOTO CODE -------------------	
	private void startPhotosInterval(){
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		timer = new Timer();		
		timer.scheduleAtFixedRate(new TimerTask() {			
			@Override
			public void run() {				
				if(!isWorking){
					Log.d(LOG_TAG, "INIT TAKE PHOTO");
					takePhoto();
				}
			}
		}, 0, (long) (photosInterval*1000));
	}
	
	private void takePhoto(){
		isWorking = true;
		camera.takePicture(null, null, mPicture);
	}
	
	PictureCallback mPicture = new PictureCallback() {		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFileTemp();
            if (pictureFile == null) {
                return;
            }            
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                photosTaken++;
                isWorking = false;
                
                
                //add photo to album
                /*
                if(album!=null){
                	album.addPhoto(new TimeLapPhoto(pictureFile.getPath(), UUID.randomUUID().toString()));
                }
                */
                
                //add path to be used to scan files
                currentPaths.add(pictureFile.getPath());
                
                //start preview
        		if(camera!=null){
        			//camera.stopPreview();
        			camera.startPreview();
        		}        		                
                        		                                                
                Log.d(LOG_TAG, "DONE WRITING FILE!");
                //removeFile(pictureFile.getPath());                
            } catch (FileNotFoundException e) {
            	Log.d(LOG_TAG, "FileNotFoundException!");
            } catch (IOException e) {
            	Log.d(LOG_TAG, "IOException!");
            }                       
		}
	};
	
	//testing
	private void removeFile(String path){
    	File f = new File(path);
    	if(f.exists()){
    		try {
    			f.delete();
    			Log.d(LOG_TAG, "DONE DELETE!");
			} catch (Exception e) {
				// TODO: handle exception
			}
    	}    			
	}
	
	
    private static File getOutputMediaFileTemp() {
        //File mediaStorageDir = new File(InTimeApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "InTime");
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"InTimePhotos");        
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(LOG_TAG, "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "A_IMG_T" + timeStamp + ".jpg");

        return mediaFile;
    }			

}
