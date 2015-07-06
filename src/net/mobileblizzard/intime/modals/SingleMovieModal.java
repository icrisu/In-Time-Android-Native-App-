package net.mobileblizzard.intime.modals;


import java.util.List;

import net.mobileblizzard.intime.MainActivity;
import net.mobileblizzard.intime.MovieSingle;
import net.mobileblizzard.intime.R;
import net.mobileblizzard.intime.modals.events.ConfirmListener;
import net.mobileblizzard.intime.uibeans.ChooserItem;
import net.mobileblizzard.intime.uibeans.ChooserItemChange;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleMovieModal {
	
	private FrameLayout mainUI;
	private FrameLayout settingsChooserUIBlack;
	private LinearLayout contentUI;
	private FrameLayout closeChooserUI;
	private MovieSingle mainContext;
	public void initChooser(MovieSingle c, FrameLayout mainUI, FrameLayout settingsChooserUIBlack, LinearLayout contentUI, FrameLayout closeChooserUI){
		this.mainContext = c;
		this.mainUI = mainUI;
		this.settingsChooserUIBlack = settingsChooserUIBlack;
		this.contentUI = contentUI;
		this.closeChooserUI = closeChooserUI;
		this.closeChooserUI.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				close();
			}
		});
		this.closeChooserUI.setScaleX(0.7f);
		this.closeChooserUI.setScaleY(0.7f);
	}
	
	public void open(){		
		mainUI.setVisibility(View.VISIBLE);		
		closeChooserUI.setAlpha(0f);
		contentUI.setAlpha(0f);
				
		ObjectAnimator scale = ObjectAnimator.ofFloat(settingsChooserUIBlack, View.SCALE_Y, 0f, 1f);
		AnimatorSet aSet = new AnimatorSet();
		aSet.setInterpolator(new AccelerateInterpolator());			
		aSet.setDuration(200);		
		aSet.playTogether(scale);
		aSet.addListener(new Animator.AnimatorListener(){
			@Override
			public void onAnimationStart(Animator animation) {}			
			@Override
			public void onAnimationRepeat(Animator animation) {}			
			@Override
			public void onAnimationEnd(Animator animation) {
				showContent();
			}			
			@Override
			public void onAnimationCancel(Animator animation) {}				
		});			
		aSet.start();		
	}
	
	private void showContent(){
		ObjectAnimator alpha1 = ObjectAnimator.ofFloat(contentUI, View.ALPHA, 0f, 1f);
		AnimatorSet aSet = new AnimatorSet();
		aSet.setInterpolator(new DecelerateInterpolator());			
		aSet.setDuration(100);		
		aSet.playTogether(alpha1);		
		aSet.start();
		
		ObjectAnimator alpha2 = ObjectAnimator.ofFloat(closeChooserUI, View.ALPHA, 0f, 1f);
		ObjectAnimator rotation = ObjectAnimator.ofFloat(closeChooserUI, View.ROTATION, -45f, 0f);
		AnimatorSet aSet2 = new AnimatorSet();
		aSet2.setInterpolator(new DecelerateInterpolator());			
		aSet2.setDuration(100);		
		aSet2.playTogether(alpha2, rotation);		
		aSet2.start();		
	}
	
	
	public void close(){		
		ObjectAnimator alpha1 = ObjectAnimator.ofFloat(contentUI, View.ALPHA, 1f, 0f);
		AnimatorSet aSet = new AnimatorSet();
		aSet.setInterpolator(new DecelerateInterpolator());			
		aSet.setDuration(100);		
		aSet.playTogether(alpha1);		
		aSet.start();
		
		ObjectAnimator alpha2 = ObjectAnimator.ofFloat(closeChooserUI, View.ALPHA, 1f, 0f);
		ObjectAnimator rotation = ObjectAnimator.ofFloat(closeChooserUI, View.ROTATION, 0f, -45f);
		AnimatorSet aSet2 = new AnimatorSet();
		aSet2.setInterpolator(new DecelerateInterpolator());			
		aSet2.setDuration(100);		
		aSet2.playTogether(alpha2, rotation);
		aSet2.addListener(new Animator.AnimatorListener(){
			@Override
			public void onAnimationStart(Animator animation) {}			
			@Override
			public void onAnimationRepeat(Animator animation) {}			
			@Override
			public void onAnimationEnd(Animator animation) {
				closeSecond();
			}			
			@Override
			public void onAnimationCancel(Animator animation) {}				
		});		
		aSet2.start();		
	}	
	
	
	private void closeSecond(){
		
		ObjectAnimator scale = ObjectAnimator.ofFloat(settingsChooserUIBlack, View.SCALE_Y, 1f, 0f);
		AnimatorSet aSet = new AnimatorSet();
		aSet.setInterpolator(new AccelerateInterpolator()); 			
		aSet.setDuration(200);		
		aSet.playTogether(scale);
		aSet.addListener(new Animator.AnimatorListener(){
			@Override
			public void onAnimationStart(Animator animation) {}			
			@Override
			public void onAnimationRepeat(Animator animation) {}			
			@Override
			public void onAnimationEnd(Animator animation) {
				mainUI.setVisibility(View.GONE);
			}			
			@Override
			public void onAnimationCancel(Animator animation) {}				
		});		
		aSet.start();		
	}	
	
	
	//set content list
	public void setContentVector(String list[], int selected, ChooserItemChange listener){
		contentUI.removeAllViews();
		for (int i = 0; i < list.length; i++) {
			boolean isSelected = false;
			if(i==selected)
				isSelected = true;
			ChooserItem itm = new ChooserItem(mainContext, list[i], isSelected, i);
			itm.setListener(listener);
			contentUI.addView(itm);
		}
	}
	
	//set content list
	public void setContentList(List<String> list, int selected, ChooserItemChange listener){
		contentUI.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			boolean isSelected = false;
			if(i==selected)
				isSelected = true;
			String s = list.get(i);			
			ChooserItem itm = new ChooserItem(mainContext, Character.toUpperCase(s.charAt(0)) + s.substring(1), isSelected, i);
			itm.setListener(listener);
			contentUI.addView(itm);
		}
	}
	
	
	private FrameLayout yesBTN;
	private FrameLayout noBTN;
	private TextView confirmInfo;
	//CONFIRM CODE
	public void initConfirmControls(){
		yesBTN = (FrameLayout) contentUI.findViewById(R.id.yesBTN);
		noBTN = (FrameLayout) contentUI.findViewById(R.id.noBTN);
		confirmInfo = (TextView) contentUI.findViewById(R.id.confirmInfo);
		
		yesBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(confirmListener!=null){
					close();
					confirmListener.onConfirmChange(true);
				}
			}
		});
		noBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(confirmListener!=null){
					close();
					confirmListener.onConfirmChange(false);
				}
			}
		});		
	}
	
	public void setConfirmText(String txt){
		confirmInfo.setText(txt);
	}
	
	private ConfirmListener confirmListener;
	public ConfirmListener getConfirmListener() {
		return confirmListener;
	}
	public void setConfirmListener(ConfirmListener confirmListener) {
		this.confirmListener = confirmListener;
	}
	
	
	
}
