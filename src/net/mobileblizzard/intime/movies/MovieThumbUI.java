package net.mobileblizzard.intime.movies;

import net.mobileblizzard.intime.MainActivity;
import net.mobileblizzard.intime.MovieSingle;
import net.mobileblizzard.intime.MoviesActivity;
import net.mobileblizzard.intime.R;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MovieThumbUI extends LinearLayout {
	
	private MoviesActivity parentContext;
	private MovieBean movieBean;
	public MovieThumbUI(MoviesActivity context, MovieBean bm) {
		super(context);
		this.parentContext = context;
		this.movieBean = bm;		
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		llp.setMargins(0, 0, 0, 0);		
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(llp);
		this.setClickable(true);
		//setPadding(15, 15, 15, 15);
		setBackgroundResource(R.drawable.movie_list_item_pressed);
		//setBackgroundColor(Color.parseColor("#f7f7f7"));
				
		
		//content UI
		LinearLayout thumbContent = new LinearLayout(context);
		LinearLayout.LayoutParams thumbContentParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		thumbContent.setPadding(20, 20, 20, 20);
		thumbContent.setOrientation(LinearLayout.HORIZONTAL);
		this.addView(thumbContent);
		
		//image ui
		ImageView iv = new ImageView(context);
		iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		iv.setImageBitmap(bm.getBitmap());	
		iv.setBackgroundResource(R.drawable.movie_item_background);
		thumbContent.addView(iv);
		
		//layout right content
		LinearLayout rightContent = new LinearLayout(context);
		LinearLayout.LayoutParams rightContentParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rightContent.setLayoutParams(rightContentParams);
		rightContent.setOrientation(LinearLayout.HORIZONTAL);
		thumbContent.addView(rightContent);
		
		//layout right content left
		LinearLayout rightContentLeft = new LinearLayout(context);
		LinearLayout.LayoutParams rightContentLeftParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
		rightContentLeft.setLayoutParams(rightContentLeftParams);
		rightContentLeft.setOrientation(LinearLayout.VERTICAL);
		rightContentLeft.setPadding(15, 0, 0, 0);
		rightContent.addView(rightContentLeft);		
		
		//RIGHT CONTENT LEFT TITLE
		TextView title = new TextView(context);
		TableLayout.LayoutParams titleParam = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
		title.setLayoutParams(titleParam);
		title.setTextColor(Color.parseColor("#10a6a0"));
		title.setTextSize(15);
		title.setText(bm.getTimeLap().getName());
		rightContentLeft.addView(title);
		
		TextView info = new TextView(context);
		TableLayout.LayoutParams infoParam = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
		info.setLayoutParams(infoParam);
		info.setTextColor(Color.parseColor("#acacac"));		
		info.setText("About"+" "+bm.getTimeLap().getFrames()+" "+"frames");
		infoParam.setMargins(0, 6, 0, 0);
		rightContentLeft.addView(info);		
		
		//arrow right content
		LinearLayout arrowRightUI = new LinearLayout(context);
		arrowRightUI.setGravity(Gravity.BOTTOM);
		LinearLayout.LayoutParams arrowRightUIParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		arrowRightUI.setLayoutParams(arrowRightUIParams);
		arrowRightUIParams.setMargins(0, 8, 0, 0);
		arrowRightUI.setOrientation(LinearLayout.VERTICAL);
		rightContent.addView(arrowRightUI);
		
		//arrow image
		ImageView ai = new ImageView(context);
		LayoutParams aiParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ai.setLayoutParams(aiParams);		
		ai.setImageResource(R.drawable.move13);
		arrowRightUI.addView(ai);		
		
		
		//add bottom line
		LinearLayout bottomLine = new LinearLayout(context);
		LinearLayout.LayoutParams bottomLineParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
		bottomLineParams.setMargins(0, 0, 0, 0);
		bottomLine.setLayoutParams(bottomLineParams);
		bottomLine.setBackgroundColor(Color.parseColor("#e6e6e6"));
		this.addView(bottomLine);
		
		
		ObjectAnimator alpha = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f);
		ObjectAnimator scale = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0f, 1f);
		AnimatorSet aSet = new AnimatorSet();				
		aSet.setInterpolator(new DecelerateInterpolator());		
		aSet.setDuration(100);
		aSet.playTogether(alpha, scale);	
		aSet.start();		
		
		//10a6a0
		
		this.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(parentContext.getBaseContext(), MovieSingle.class);
				i.putExtra("TIME_LAP_ID", movieBean.getTimeLap().getID());
				parentContext.startActivityForResult(i, MoviesActivity.REQUEST_CODE);
			}
		});	
		
	}
	public MovieBean getMovieBean() {
		return movieBean;
	}
	public void setMovieBean(MovieBean movieBean) {
		this.movieBean = movieBean;
	}
	
	
	public void remove(){
		ObjectAnimator alpha = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f);
		ObjectAnimator scale = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, 0f);
		AnimatorSet aSet = new AnimatorSet();				
		aSet.setInterpolator(new DecelerateInterpolator());		
		aSet.setDuration(100);
		aSet.playTogether(alpha, scale);	
		aSet.start();		
	}
	
	


}
