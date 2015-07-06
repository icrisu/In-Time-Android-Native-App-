package net.mobileblizzard.intime;

import java.io.File;

import net.mobileblizzard.intime.modals.BlackPopupWrapper;
import net.mobileblizzard.intime.modals.SingleMovieModal;
import net.mobileblizzard.intime.modals.events.ConfirmListener;
import net.mobileblizzard.intime.models.storage.SettingsData;
import net.mobileblizzard.intime.models.storage.TimeLap;
import net.mobileblizzard.intime.storage.InTimeStorage;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class MovieSingle extends Activity {
	
	private FrameLayout movieBackBTN;
	private TextView actionBarTitle;
	private TimeLap currentTimelap;
	private VideoView videoViewUI;
	private SingleMovieModal confirmWrapper;
	private FrameLayout movieContainerUI;
	private FrameLayout movieDeleteBTN;
	private FrameLayout shareVideoBTN;
	private String timelapID;
	public static final String REMOVE_ID = "REMOVE_ID";
	private RemoveFileTask removeTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_single);
		movieBackBTN = (FrameLayout) findViewById(R.id.movieBackBTN);		
		movieBackBTN.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				closeActivity();
			}
		});	
		
		
		//Log.d("intime", "EXTRA"+getIntent().getExtras().get("TIME_LAP_ID"));
		timelapID = (String) getIntent().getExtras().getString("TIME_LAP_ID");		
		currentTimelap = InTimeStorage.getInstance().getSettingsData().getTimeLap(getIntent().getExtras().getString("TIME_LAP_ID"));
		
		movieDeleteBTN = (FrameLayout) findViewById(R.id.movieDeleteBTN);
		
		shareVideoBTN = (FrameLayout) findViewById(R.id.shareVideoBTN);				
		
		actionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
		actionBarTitle.setText(currentTimelap.getName());
		
		//video view 
		
		videoViewUI = (VideoView) findViewById(R.id.videoViewUI);
		videoViewUI.setVideoURI(Uri.fromFile(new File(currentTimelap.getPath())));
		videoViewUI.start();
		
		movieContainerUI = (FrameLayout) findViewById(R.id.movieContainerUI);		
		
		
		
		MediaController vidControl = new MediaController(this);
		vidControl.setAnchorView(videoViewUI);
		videoViewUI.setMediaController(vidControl);
	
		
		//confirm wrapper
		confirmWrapper = new SingleMovieModal();		
		confirmWrapper.initChooser(this, (FrameLayout) findViewById(R.id.confirmChooserUI), (FrameLayout) findViewById(R.id.confirmChooserUIBlack), (LinearLayout) findViewById(R.id.confirmChooserUIContent), (FrameLayout) findViewById(R.id.closeConfirmChooserUI));
		confirmWrapper.initConfirmControls();
		confirmWrapper.setConfirmText(getString(R.string.confirm_remove_video));
		confirmWrapper.setConfirmListener(new ConfirmListener(){
			@Override
			public void onConfirmChange(boolean val) {
				//openMenu();
				if(val){
					removeMovie();
				}							
			}
		});	
		
		movieDeleteBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				confirmWrapper.open();
			}
		});
		
		shareVideoBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				shareVideo();
			}
		});						
		
	}
	
	private void removeMovie(){
		try {
			videoViewUI.stopPlayback();
		} catch (Exception e) {
			// TODO: handle exception
		}		
		movieContainerUI.removeView(videoViewUI);
		removeTask = new RemoveFileTask();
		removeTask.execute(new File(currentTimelap.getPath()));	
	}
	
	private void closeWithResponse(){
		Intent out = new Intent();
		out.putExtra(REMOVE_ID, timelapID);
		setResult(17283, out);
		closeActivity();
	}
	
	//close activity
	private void closeActivity(){
		super.onBackPressed();		
	}	
		
	
	public class RemoveFileTask extends AsyncTask<File, Void, Boolean>{
		@Override
		protected Boolean doInBackground(File... params) {
			File f = (File) params[0];
	    	boolean isRemoved = false;
	    	if(f.exists()){
	    		try {
	    			isRemoved = f.delete();		    			
				} catch (Exception e) {
					// TODO: handle exception
				}
	    		isRemoved = true;
	    	}			
			return isRemoved;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			SettingsData dta = InTimeStorage.getInstance().getSettingsData();
			dta.removeTimelap(timelapID);
			InTimeStorage.getInstance().updateSettings(dta);
			closeWithResponse();			
		}
	}
	
	//SHARE VIDEO
	private void shareVideo(){
        String outputFile = currentTimelap.getPath();     
       
        ContentValues content = new ContentValues(4);
        content.put(Video.VideoColumns.TITLE, currentTimelap.getName());
        content.put(Video.VideoColumns.DATE_ADDED,
        System.currentTimeMillis() / 1000);
        content.put(Video.Media.MIME_TYPE, "video/mp4");
        content.put(MediaStore.Video.Media.DATA, outputFile);
        ContentResolver resolver = getContentResolver();
        Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        content);
       
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share using"));		
	}
}
