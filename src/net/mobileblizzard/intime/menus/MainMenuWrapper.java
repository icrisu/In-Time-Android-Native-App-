package net.mobileblizzard.intime.menus;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import net.mobileblizzard.intime.InfoActivity;
import net.mobileblizzard.intime.MainActivity;
import net.mobileblizzard.intime.MoviesActivity;
import net.mobileblizzard.intime.R;
import net.mobileblizzard.intime.modals.events.ConfirmListener;

public class MainMenuWrapper {
	
	public static final String ACTION_NONE = "ACTION_NONE";
	public static final String ACTION_SETTINGS = "ACTION_SETTINGS";
	public static final String ACTION_OPEN_START_DELAY = "ACTION_OPEN_START_DELAY";
	
	private String currentMenuActionAfter = ACTION_NONE;
	
	private MainActivity mainContext;
	private MainMenu mainMenuUI;
	private MainMenuPlus plusButton;
	private ImageView plusIco;
	private FrameLayout blackScreenUI;
	
	//menu items
	private FrameLayout mainMenuItems;
	//private MainMenuItem menuItemsButtons[] = {(MainMenuItem) mainMenuUI.findViewById(R.id.playBTN), (MainMenuItem) mainMenuUI.findViewById(R.id.settingsBTN), (MainMenuItem) mainMenuUI.findViewById(R.id.imagesBTN), (MainMenuItem) mainMenuUI.findViewById(R.id.moviesBTN), (MainMenuItem) mainMenuUI.findViewById(R.id.infoBTN)};
	private ArrayList<MainMenuItem> menuItemsButtons;
	
	private ArrayList<MenuItemPosition> itemsPosition;
	
	private MainMenuItem settingsBTN;
	private MainMenuItem playBTN;
	private MainMenuItem deleteBTN;
	private MainMenuItem moviesBTN;
	private MainMenuItem infoBTN;
		 
	
	public MainMenuWrapper(MainActivity c, MainMenu mainMenuUI, FrameLayout blackScreenUI) {
		super();
		this.mainContext = c;
		this.mainMenuUI = mainMenuUI;
		this.blackScreenUI = blackScreenUI;
		plusButton = (MainMenuPlus) mainMenuUI.findViewById(R.id.plusButton);
		plusIco = (ImageView) plusButton.findViewById(R.id.plusIco);
		
		
		plusButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {								
				if(isMenuOpen){
					closeMenu();
				}else{
					openMenu();
				}
			}
		});
		
		settingsBTN = (MainMenuItem) mainMenuUI.findViewById(R.id.settingsBTN);
		playBTN = (MainMenuItem) mainMenuUI.findViewById(R.id.playBTN);
		moviesBTN = (MainMenuItem) mainMenuUI.findViewById(R.id.moviesBTN);
		deleteBTN = (MainMenuItem) mainMenuUI.findViewById(R.id.deleteBTN);
		infoBTN = (MainMenuItem) mainMenuUI.findViewById(R.id.infoBTN);		
		
		//menu items
		menuItemsButtons = new ArrayList<MainMenuItem>();
		menuItemsButtons.add(playBTN);
		menuItemsButtons.add(settingsBTN);
		menuItemsButtons.add(moviesBTN);
		menuItemsButtons.add(deleteBTN);
		menuItemsButtons.add(infoBTN);
		mainMenuItems = (FrameLayout) mainMenuUI.findViewById(R.id.mainMenuItems);
		
		itemsPosition = new ArrayList<MenuItemPosition>();
		int temp = 70;
        float xcenter = 180-temp/2;
        float ycenter = 180-temp/2;
        double angle = -90;        
        int radiusHide = 70;
        int radiusShow = 120;
        //x = xcenter + radius * cos(theta)
        //y = ycenter + radius * sin(theta)         
        //double theta = (2*Math.PI)/4;
		for (int i = 0; i < menuItemsButtons.size(); i++) {
			MainMenuItem btn = (MainMenuItem)menuItemsButtons.get(i);
			btn.setAlpha(0f);
			double theta = Math.toRadians(angle);								
			float x1 = Math.round(xcenter + radiusHide * Math.cos(theta));
			float y1 = Math.round(ycenter + radiusHide * Math.sin(theta));	
			float x2 = Math.round(xcenter + radiusShow * Math.cos(theta));
			float y2 = Math.round(ycenter + radiusShow * Math.sin(theta));				
			itemsPosition.add(new MenuItemPosition(x1, x2, y1, y2));
			 
	        angle += 72.0;			
		}
		handleClicks();		
	}
	
	private boolean isMenuOpen = false;
	//open menu
	public void openMenu(){
		if(isMenuOpen)
			return;		
    	plusButton.setVisibility(View.VISIBLE);
    	mainMenuUI.setVisibility(View.VISIBLE);		
		blackScreenUI.setAlpha(0f);
		toggleBlackScreen(true);
		ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(plusButton, View.SCALE_X, 1f, 0.6f);
		ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(plusButton, View.SCALE_Y, 1f, 0.6f);
		ObjectAnimator rotation = ObjectAnimator.ofFloat(plusIco, View.ROTATION, 0f, 135f);
		ObjectAnimator blackAlpha = ObjectAnimator.ofFloat(blackScreenUI, View.ALPHA, 0f, 0.65f);
		AnimatorSet firstWaveAnimationSet = new AnimatorSet();
		
		
		firstWaveAnimationSet.setInterpolator(new DecelerateInterpolator());		
		firstWaveAnimationSet.setDuration(190);
		firstWaveAnimationSet.playTogether(scaleDownX, scaleDownY, rotation, blackAlpha);
		firstWaveAnimationSet.start();
		openMenuItems();
	}
	
	public void openMenuItems(){
		//menu items
		int itemsAnimDuration = 200;
		mainMenuItems.setVisibility(View.VISIBLE);		
		ObjectAnimator rotationMain = ObjectAnimator.ofFloat(mainMenuItems, View.ROTATION, -30f, 0f);
		rotationMain.setDuration(350);
		rotationMain.start();
		
		for (int i = 0; i < itemsPosition.size(); i++) {
			MainMenuItem btn = (MainMenuItem)menuItemsButtons.get(i);
			btn.setAlpha(0f);
			btn.setScaleX(.5f);
			btn.setScaleY(.5f);
			
			MenuItemPosition pos = (MenuItemPosition) itemsPosition.get(i);
			btn.setX(pos.getX1());
			btn.setY(pos.getY1());
			ObjectAnimator tmpAlpha = ObjectAnimator.ofFloat(btn, View.ALPHA, 1f);
			ObjectAnimator scaleX = ObjectAnimator.ofFloat(btn, View.SCALE_X, .5f, 1f);
			ObjectAnimator scaleY = ObjectAnimator.ofFloat(btn, View.SCALE_Y, .5f, 1f);
			
			ObjectAnimator X = ObjectAnimator.ofFloat(btn, View.X, pos.getX1(), pos.getX2());
			ObjectAnimator Y = ObjectAnimator.ofFloat(btn, View.Y, pos.getY1(), pos.getY2());
		
			AnimatorSet menuItemsOpenAS = new AnimatorSet();			
			menuItemsOpenAS.addListener(new Animator.AnimatorListener(){
				@Override
				public void onAnimationStart(Animator animation) {}
				
				@Override
				public void onAnimationRepeat(Animator animation) {}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					isMenuOpen = true;
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {}				
			});			
			menuItemsOpenAS.setInterpolator(new DecelerateInterpolator());		
			menuItemsOpenAS.setDuration(itemsAnimDuration);
			menuItemsOpenAS.setStartDelay(100);
			menuItemsOpenAS.playTogether(tmpAlpha, scaleX, scaleY, X, Y);
			menuItemsOpenAS.start();			
		}		
	}
	
	//close menu
	public void closeMenu(){		
		if(!isMenuOpen)
			return;		
		//mainContext.toggleBlackScreen(false);
		ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(plusButton, View.SCALE_X, 0.6f, 1f);
		ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(plusButton, View.SCALE_Y, 0.6f, 1f);
		ObjectAnimator rotation = ObjectAnimator.ofFloat(plusIco, View.ROTATION, 135f, 0f);
		ObjectAnimator blackAlpha = ObjectAnimator.ofFloat(blackScreenUI, View.ALPHA, 0.65f, 0f);
		AnimatorSet firstWaveAnimationSet = new AnimatorSet();
		
		firstWaveAnimationSet.setInterpolator(new DecelerateInterpolator());		
		firstWaveAnimationSet.setDuration(190);
		firstWaveAnimationSet.playTogether(scaleUpX, scaleUpY, rotation, blackAlpha);
		firstWaveAnimationSet.start();
		
		closeMenuItems();
	}
	
	public void closeMenuItems(){
		//menu items		
		int itemsAnimDuration = 200;
		mainMenuItems.setVisibility(View.VISIBLE);
		ObjectAnimator rotationMain = ObjectAnimator.ofFloat(mainMenuItems, View.ROTATION, 0f, -30f);
		rotationMain.setDuration(350);
		rotationMain.start();		
		
		for (int i = 0; i < itemsPosition.size(); i++) {
			MainMenuItem btn = (MainMenuItem)menuItemsButtons.get(i);
			btn.setAlpha(1f);
			btn.setScaleX(1f);
			btn.setScaleY(1f);
			
			MenuItemPosition pos = (MenuItemPosition) itemsPosition.get(i);
			btn.setX(pos.getX2());
			btn.setY(pos.getY2());
			ObjectAnimator tmpAlpha = ObjectAnimator.ofFloat(btn, View.ALPHA, 0f);
			ObjectAnimator scaleX = ObjectAnimator.ofFloat(btn, View.SCALE_X, 1f, .5f);
			ObjectAnimator scaleY = ObjectAnimator.ofFloat(btn, View.SCALE_Y, 1f, .5f);
			
			ObjectAnimator X = ObjectAnimator.ofFloat(btn, View.X, pos.getX2(), pos.getX1());
			ObjectAnimator Y = ObjectAnimator.ofFloat(btn, View.Y, pos.getY2(), pos.getY1());
		
			AnimatorSet menuItemsOpenAS = new AnimatorSet();					
			menuItemsOpenAS.setInterpolator(new AccelerateInterpolator());	
			//new AccelerateInterpolator()
			menuItemsOpenAS.setDuration(itemsAnimDuration);
			//menuItemsOpenAS.setStartDelay(30);
			menuItemsOpenAS.playTogether(tmpAlpha, scaleX, scaleY, X, Y);
			menuItemsOpenAS.addListener(new Animator.AnimatorListener(){
				@Override
				public void onAnimationStart(Animator animation) {}
				
				@Override
				public void onAnimationRepeat(Animator animation) {}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					isMenuOpen = false;
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {}				
			});			
			menuItemsOpenAS.start();			
		}		
	}
	
	//instant close menu
	public void closeMenuNow(){
    	plusButton.setVisibility(View.GONE);
    	mainMenuUI.setVisibility(View.GONE);		
	}
	
	//close plus button all
	public void closePlus(String action){
		currentMenuActionAfter = action;
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(plusButton, View.SCALE_X, 0.6f, 0);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(plusButton, View.SCALE_Y, 0.6f, 0);		
		ObjectAnimator alpha = ObjectAnimator.ofFloat(plusButton, View.ALPHA, 1f, 0f);
		AnimatorSet aSet = new AnimatorSet();				
		aSet.setInterpolator(new DecelerateInterpolator());		
		aSet.setDuration(190);
		aSet.playTogether(scaleX, scaleY, alpha);
		aSet.addListener(new Animator.AnimatorListener(){
			public void onAnimationStart(Animator animation) {}			
			public void onAnimationRepeat(Animator animation) {}
			public void onAnimationEnd(Animator animation) {
				if(currentMenuActionAfter.equals(ACTION_OPEN_START_DELAY)){
					openStartDelayUI();
				}
				currentMenuActionAfter = ACTION_NONE;
			}
			public void onAnimationCancel(Animator animation) {}				
		});		
		aSet.start();		
	}
	//close plus button all
	public void openPlus(){		
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(plusButton, View.SCALE_X, 0f, 0.6f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(plusButton, View.SCALE_Y, 0f, 0.6f);		
		ObjectAnimator alpha = ObjectAnimator.ofFloat(plusButton, View.ALPHA, 0f, 1f);
		AnimatorSet aSet = new AnimatorSet();				
		aSet.setInterpolator(new DecelerateInterpolator());		
		aSet.setDuration(190);
		aSet.playTogether(scaleX, scaleY, alpha);	
		aSet.start();		
	}	
	
	
		
    //toggle black screen
    public void toggleBlackScreen(boolean isOn){
    	if(isOn){
    		//hide
    		blackScreenUI.setVisibility(View.VISIBLE);
    	}else{
    		//show
    		blackScreenUI.setVisibility(View.GONE);
    	}
    }
    
    //open start delay menu 
    public void openStartDelayUI(){
    	plusButton.setVisibility(View.GONE);
    	mainMenuUI.setVisibility(View.GONE);
    	toggleBlackScreen(false);
    	mainContext.startDelayMenu();    	
    }
    
    
    //handle menu items click
    private void handleClicks(){
    	settingsBTN.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				closeMenu();			
				mainContext.getSettingsWrapper().open();
			}
		});
    	playBTN.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				closeMenuItems();
				closePlus(ACTION_OPEN_START_DELAY);
			}
		});
    	deleteBTN.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				closeMenu();
				mainContext.getConfirmWrapper().setConfirmText(mainContext.getString(R.string.confirm_remove_all));
				mainContext.getConfirmWrapper().open();
				mainContext.getConfirmWrapper().setConfirmListener(new ConfirmListener(){
					@Override
					public void onConfirmChange(boolean val) {
						//openMenu();
						if(val){
							//start remove all videos
							mainContext.removeAllVideos();
						}							
					}
				});
			}
		});

    	moviesBTN.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				closeMenu();
				mainContext.startActivity(new Intent(mainContext.getBaseContext(), MoviesActivity.class));
			}
		});
    	
    	infoBTN.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				closeMenu();
				mainContext.startActivity(new Intent(mainContext.getBaseContext(), InfoActivity.class));
			}
		});    	
    	    	
    	    	
    }
	

}
