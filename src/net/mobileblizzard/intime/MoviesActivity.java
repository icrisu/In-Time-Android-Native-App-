package net.mobileblizzard.intime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import net.mobileblizzard.intime.models.storage.TimeLap;
import net.mobileblizzard.intime.movies.MovieBean;
import net.mobileblizzard.intime.movies.MovieThumbUI;
import net.mobileblizzard.intime.storage.InTimeStorage;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MoviesActivity extends Activity {
	
	public static final String LOG_TAG = "movies";
	private FrameLayout moviesBackBTN;
	private LinearLayout moviesContentUI;
	private GetVideoThumbsTask thumbsTask;
	private boolean isTaskRunning = false;	
	public static final int REQUEST_CODE = 73284;
	private ArrayList<MovieThumbUI> thumbsUI;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movies);
		
		thumbsUI = new ArrayList<MovieThumbUI>();
		
		moviesBackBTN = (FrameLayout) findViewById(R.id.moviesBackBTN);		
		moviesBackBTN.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				closeActivity();
			}
		});
		
		moviesContentUI = (LinearLayout) findViewById(R.id.moviesContentUI);
		
		ArrayList<TimeLap> laps = InTimeStorage.getInstance().getSettingsData().getTimelaps();
		if(laps!=null){
			if(laps.size()==0){
				//inform no movies
				noMoviesInfo();
			}else{
				
				isTaskRunning = true;
				thumbsTask = new GetVideoThumbsTask();
				Collections.reverse(laps);
				thumbsTask.execute(laps);
			}
		}else{
			noMoviesInfo();
		}
	}
	
	@Override
	protected void onDestroy() {
		if(thumbsTask!=null){
			if(isTaskRunning){
				thumbsTask.cancel(true);
			}
		}
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("intime", "RESULT CODE"+requestCode);
		if(requestCode==REQUEST_CODE){
			if(data!=null){
				//String id = data.getStringExtra(MovieSingle.REMOVE_ID);
				//remove from list
				removeUI(data.getStringExtra(MovieSingle.REMOVE_ID));
			}else{
				//do nothing
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void removeUI(String uiID){
		for (int i = 0; i < thumbsUI.size(); i++) {
			MovieThumbUI t = thumbsUI.get(i);
			if(t.getMovieBean().getTimeLap().getID().equals(uiID)){
				//remove UI
				moviesContentUI.removeView(t);
				thumbsUI.remove(i);
				Toast.makeText(this, getString(R.string.movie_removed), Toast.LENGTH_SHORT).show();
				if(thumbsUI.size()==0){
					noMoviesInfo();
				}
				break;
			}
		}
	}
	
	private void noMoviesInfo(){
		TextView tvInfo = new TextView(this);
		TableLayout.LayoutParams titleParam = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
		tvInfo.setLayoutParams(titleParam);
		tvInfo.setTextColor(Color.parseColor("#7a7a7a"));
		tvInfo.setTextSize(15);
		tvInfo.setPadding(10, 10, 10, 10);
		tvInfo.setText(getString(R.string.no_timelaps));
		moviesContentUI.addView(tvInfo);				
	}
	
	
	public class GetVideoThumbsTask extends AsyncTask<ArrayList<TimeLap>, MovieBean, Void>{
		
		
		@Override
		protected Void doInBackground(ArrayList<TimeLap>... params) {
			ArrayList<TimeLap> tLaps = params[0];
			for (int i = 0; i < tLaps.size(); i++) {				
				TimeLap t = tLaps.get(i);
				Bitmap thumb = ThumbnailUtils.createVideoThumbnail(t.getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
				if(thumb!=null){
					//512 x 384					
					publishProgress(new MovieBean(t, thumb));
				}								
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(MovieBean... values) {
			addThumbUI(values[0]);
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			isTaskRunning = false;
			super.onPostExecute(result);
		}
	}
	
	//add UI 
	public void addThumbUI(MovieBean bean){
		MovieThumbUI tUI = new MovieThumbUI(this, bean);	
		moviesContentUI.addView(tUI);	
		thumbsUI.add(tUI);
	}
	
	
	
	//close activity
	private void closeActivity(){
		super.onBackPressed();
	}

	
}
