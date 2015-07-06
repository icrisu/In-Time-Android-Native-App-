package net.mobileblizzard.intime.menus;

import net.mobileblizzard.intime.MainActivity;
import net.mobileblizzard.intime.R;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class ThreadMenuWrapper {

	FrameLayout startDelayMenu;
	FrameLayout stopStartUI;
	public static final String ACTION_START_ENGINE = "ACTION_START_ENGINE";
	public static final String ACTION_NONE = "ACTION_NONE";
	private String currentAction;
	private MainActivity mainContext;
	//private TextView delayInfoTXT;	

	public ThreadMenuWrapper(FrameLayout startDelayMenu, MainActivity c) {
		this.currentAction = ACTION_NONE;
		this.mainContext = c;
		this.startDelayMenu = startDelayMenu;
		this.stopStartUI = (FrameLayout) startDelayMenu.findViewById(R.id.stopThreadUI);
		//this.delayInfoTXT = (TextView) startDelayMenu.findViewById(R.id.delayInfoTXT);
		stopStartUI.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View arg0) {
				mainContext.stopThreadMenu();
			}
		});
	}
	
	//animate - show 
	public void showIn(long delay){
		startDelayMenu.setAlpha(0f);
		startDelayMenu.setVisibility(View.VISIBLE);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(startDelayMenu, View.SCALE_X, 0.3f, 1);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(startDelayMenu, View.SCALE_Y, 0.3f, 1);		
		ObjectAnimator alpha = ObjectAnimator.ofFloat(startDelayMenu, View.ALPHA, 0f, 1f);
		AnimatorSet aSet = new AnimatorSet();				
		aSet.setInterpolator(new DecelerateInterpolator());		
		aSet.setDuration(190);
		aSet.setStartDelay(delay);
		aSet.playTogether(scaleX, scaleY, alpha);	
		aSet.start();
		
		stopStartUI.setAlpha(0f);
		stopStartUI.setVisibility(View.VISIBLE);
		ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(stopStartUI, View.SCALE_X, 0.3f, 1);
		ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(stopStartUI, View.SCALE_Y, 0.3f, 1);		
		ObjectAnimator alpha2 = ObjectAnimator.ofFloat(stopStartUI, View.ALPHA, 0f, 1f);
		AnimatorSet aSet2 = new AnimatorSet();				
		aSet2.setInterpolator(new DecelerateInterpolator());		
		aSet2.setDuration(190);
		aSet2.setStartDelay(100+delay);
		aSet2.playTogether(scaleX2, scaleY2, alpha2);	
		aSet2.start();		
	}
	
	public void showOut(String action){
		currentAction = action;
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(startDelayMenu, View.SCALE_X, 1f, 0.3f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(startDelayMenu, View.SCALE_Y, 1f, 0.3f);		
		ObjectAnimator alpha = ObjectAnimator.ofFloat(startDelayMenu, View.ALPHA, 1f, 0f);
		AnimatorSet aSet = new AnimatorSet();				
		aSet.setInterpolator(new DecelerateInterpolator());		
		aSet.setDuration(190);
		aSet.setStartDelay(100);
		aSet.playTogether(scaleX, scaleY, alpha);	
		aSet.addListener(new Animator.AnimatorListener(){
			@Override
			public void onAnimationStart(Animator animation) {}
			
			@Override
			public void onAnimationRepeat(Animator animation) {}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				if(currentAction.equals(ACTION_NONE)){
					//open menu
					mainContext.getMainMenuWrapper().openMenu();
					mainContext.getMainMenuWrapper().openPlus();
				}				
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {}				
		});		
		aSet.start();
		
		ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(stopStartUI, View.SCALE_X, 1, 0.3f);
		ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(stopStartUI, View.SCALE_Y, 1, 0.3f);		
		ObjectAnimator alpha2 = ObjectAnimator.ofFloat(stopStartUI, View.ALPHA, 1f, 0f);
		AnimatorSet aSet2 = new AnimatorSet();				
		aSet2.setInterpolator(new DecelerateInterpolator());		
		aSet2.setDuration(190);		
		aSet2.playTogether(scaleX2, scaleY2, alpha2);	
		aSet2.start();		
	}
	
	public void cancel(){		
		showOut(ACTION_NONE);
	}
	

}
