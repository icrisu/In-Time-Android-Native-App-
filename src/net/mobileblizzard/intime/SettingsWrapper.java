package net.mobileblizzard.intime;

import java.util.ArrayList;
import java.util.List;

import net.mobileblizzard.intime.components.spiner.CustomCheckbox;
import net.mobileblizzard.intime.components.spiner.CustomSpinner;
import net.mobileblizzard.intime.components.spiner.SpinerListener;
import net.mobileblizzard.intime.modals.BlackPopupWrapper;
import net.mobileblizzard.intime.models.storage.SettingsData;
import net.mobileblizzard.intime.storage.InTimeStorage;
import net.mobileblizzard.intime.uibeans.ChooserItemChange;
import net.mobileblizzard.intime.uibeans.ResolutionOption;
import net.mobileblizzard.intime.uibeans.ResolutionOptionChange;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsWrapper {
	
	private FrameLayout settingsView;
	private MainActivity mainContext;
	
	private CustomSpinner hoursSpinnerUI;
	private CustomSpinner photoIntervalSpinnerUI;
	private CustomCheckbox screenCB;
	private CustomSpinner delaySpinnerUI;
	
	private BlackPopupWrapper chooserWrapper;
	
	private TextView resolutionInfo;
	private LinearLayout resolutionBTN;
		
	
	private LinearLayout sceneBTN;
	private TextView sceneInfo;
	
	private LinearLayout focusBTN;
	private TextView focusInfo;
	
	private LinearLayout colorBTN;
	private TextView colorInfo;	
	
	private LinearLayout flashBTN;
	private TextView flashInfo;	
	
	public SettingsWrapper(FrameLayout settingsView, MainActivity c) {
		super();
		this.mainContext = c;
		this.settingsView = settingsView;
		
		//back button implementation
		FrameLayout settingsBackBTN = (FrameLayout) settingsView.findViewById(R.id.settingsBackBTN);
		settingsBackBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				close();
			}
		});
		
		SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
		
		photoIntervalSpinnerUI = (CustomSpinner) c.findViewById(R.id.photoIntervalSpinnerUI);
		photoIntervalSpinnerUI.init(dtaTemp.getPhotosInterval(), 0.5, 60, 0.5, (FrameLayout)c.findViewById(R.id.intervalMinus), (FrameLayout)c.findViewById(R.id.intervalPlus), (TextView)c.findViewById(R.id.intervalInfo));
		photoIntervalSpinnerUI.setSpeicalStep(true);
		photoIntervalSpinnerUI.setListener(new SpinerListener(){			
			public void onValueChanged(double val){
				//save interval here
				SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
				dtaTemp.setPhotosInterval(val);
				InTimeStorage.getInstance().updateSettings(dtaTemp);
			}			
		});		
		
		hoursSpinnerUI = (CustomSpinner) c.findViewById(R.id.hoursSpinnerUI);
		hoursSpinnerUI.init(dtaTemp.getTimelapDuration(), 0.5, 24, 0.5, (FrameLayout)c.findViewById(R.id.hoursMinus), (FrameLayout)c.findViewById(R.id.hoursPlus), (TextView)c.findViewById(R.id.hoursInfo));		
		hoursSpinnerUI.setListener(new SpinerListener(){			
			public void onValueChanged(double val){
				//save hours here
				SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
				dtaTemp.setTimelapDuration(val);
				InTimeStorage.getInstance().updateSettings(dtaTemp);				
			}			
		});										
		
		screenCB = (CustomCheckbox) c.findViewById(R.id.screenCB);
		screenCB.init(dtaTemp.isKeepScreenOn());
		screenCB.setListener(new SpinerListener(){
			public void onCBValueChanged(boolean val){
				//save close screen here
				SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
				dtaTemp.setKeepScreenOn(val);
				InTimeStorage.getInstance().updateSettings(dtaTemp);
				mainContext.keepScreenOn();
			}			
		});
		
		delaySpinnerUI = (CustomSpinner) c.findViewById(R.id.delaySpinnerUI);
		delaySpinnerUI.init(dtaTemp.getStartDelay(), 2, 30, 1, (FrameLayout)c.findViewById(R.id.delayMinus), (FrameLayout)c.findViewById(R.id.delayPlus), (TextView)c.findViewById(R.id.delayInfo));		
		delaySpinnerUI.setListener(new SpinerListener(){			
			public void onValueChanged(double val){
				//save start delay here
				SettingsData dtaTemp = InTimeStorage.getInstance().getSettingsData();
				dtaTemp.setStartDelay(val);
				InTimeStorage.getInstance().updateSettings(dtaTemp);				
			}			
		});	
		
		chooserWrapper = new BlackPopupWrapper();
		chooserWrapper.initChooser(c, (FrameLayout) c.findViewById(R.id.settingsChooserUI), (FrameLayout) c.findViewById(R.id.settingsChooserUIBlack), (LinearLayout) c.findViewById(R.id.settingsChooserUIContent), (FrameLayout) c.findViewById(R.id.closeSettingsChooserUI));
		
		
		//quality
		resolutionBTN = (LinearLayout) c.findViewById(R.id.resolutionBTN);
		resolutionBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openResolutionDialog();
			}
		});				
		resolutionInfo = (TextView) c.findViewById(R.id.resolutionInfo);	
		resolutionInfo.setText(dtaTemp.getSelectedResolutionName());
		
		//WHITE BALANCE
		sceneBTN = (LinearLayout) c.findViewById(R.id.sceneBTN);
		sceneBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openWhiteBalanceDialog();
			}
		});	
		sceneInfo = (TextView) c.findViewById(R.id.sceneInfo);			
		sceneInfo.setText(Character.toUpperCase(dtaTemp.getSelectedWhiteBalanceName().charAt(0)) + dtaTemp.getSelectedWhiteBalanceName().substring(1));
		
		//FOCUS MODE
		focusBTN = (LinearLayout) c.findViewById(R.id.focusBTN);
		focusBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openFocusDialog();
			}
		});	
		focusInfo = (TextView) c.findViewById(R.id.focusInfo);			
		focusInfo.setText(Character.toUpperCase(dtaTemp.getSelectedFocusModeName().charAt(0)) + dtaTemp.getSelectedFocusModeName().substring(1));
		
		//COLOR EFFECT
		colorBTN = (LinearLayout) c.findViewById(R.id.colorBTN);
		colorBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openColorEfxDialog();
			}
		});	
		colorInfo = (TextView) c.findViewById(R.id.colorInfo);			
		colorInfo.setText(Character.toUpperCase(dtaTemp.getSelectedColorEfxName().charAt(0)) + dtaTemp.getSelectedColorEfxName().substring(1));
		
		//FLASH MODE
		flashBTN = (LinearLayout) c.findViewById(R.id.flashBTN);
		flashBTN.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openFlashModeDialog();
			}
		});	
		flashInfo = (TextView) c.findViewById(R.id.flashInfo);			
		flashInfo.setText(Character.toUpperCase(dtaTemp.getSelectedFlashModeName().charAt(0)) + dtaTemp.getSelectedFlashModeName().substring(1));						
				
	}
	
	//choose camera resolution
	private void openResolutionDialog(){
		SettingsData dta = InTimeStorage.getInstance().getSettingsData();
		ChooserItemChange listener = new ChooserItemChange(){
			@Override
			public void onChange(int index, boolean val) {
				SettingsData dta = InTimeStorage.getInstance().getSettingsData();
				dta.setSelectedResolution(index);
				InTimeStorage.getInstance().updateSettings(dta);
				resolutionInfo.setText(dta.getSelectedResolutionName());
				chooserWrapper.close();
			}
		};				
		chooserWrapper.setContentVector(dta.getResolution(), dta.getSelectedResolution(), listener);
		chooserWrapper.open();	
	}
	
	//choose white balance
	private void openWhiteBalanceDialog(){
		SettingsData dta = InTimeStorage.getInstance().getSettingsData();
		ChooserItemChange listener = new ChooserItemChange(){
			@Override
			public void onChange(int index, boolean val) {
				SettingsData dta = InTimeStorage.getInstance().getSettingsData();
				dta.setSelectedWhiteBalance(index);
				InTimeStorage.getInstance().updateSettings(dta);
				//update view
				sceneInfo.setText(Character.toUpperCase(dta.getSelectedWhiteBalanceName().charAt(0)) + dta.getSelectedWhiteBalanceName().substring(1));
				chooserWrapper.close();				
				//update camera params
				mainContext.getmPreview().updateCameraParams();
			}
		};				
		chooserWrapper.setContentList(dta.getWhiteBalanceList(), dta.getSelectedWhiteBalance(), listener);
		chooserWrapper.open();	
	}	

	//choose focus mode
	private void openFocusDialog(){
		SettingsData dta = InTimeStorage.getInstance().getSettingsData();
		ChooserItemChange listener = new ChooserItemChange(){
			@Override
			public void onChange(int index, boolean val) {
				SettingsData dta = InTimeStorage.getInstance().getSettingsData();
				dta.setSelectedFocusModeIndex(index);				
				InTimeStorage.getInstance().updateSettings(dta);
				//update view
				focusInfo.setText(Character.toUpperCase(dta.getSelectedFocusModeName().charAt(0)) + dta.getSelectedFocusModeName().substring(1));
				chooserWrapper.close();				
				//update camera params
				mainContext.getmPreview().updateCameraParams();
			}
		};				
		chooserWrapper.setContentList(dta.getFocusModes(), dta.getSelectedFocusModeIndex(), listener);
		chooserWrapper.open();	
	}	
	
	
	//choose color efx
	private void openColorEfxDialog(){
		SettingsData dta = InTimeStorage.getInstance().getSettingsData();
		ChooserItemChange listener = new ChooserItemChange(){
			@Override
			public void onChange(int index, boolean val) {
				SettingsData dta = InTimeStorage.getInstance().getSettingsData();
				dta.setSelectedColorEfxIndx(index);				
				InTimeStorage.getInstance().updateSettings(dta);
				//update view
				colorInfo.setText(Character.toUpperCase(dta.getSelectedColorEfxName().charAt(0)) + dta.getSelectedColorEfxName().substring(1));
				chooserWrapper.close();				
				//update camera params
				mainContext.getmPreview().updateCameraParams();
			}
		};				
		chooserWrapper.setContentList(dta.getColorEfects(), dta.getSelectedColorEfxIndx(), listener);
		chooserWrapper.open();	
	}
	
	
	//choose flash mode
	private void openFlashModeDialog(){
		SettingsData dta = InTimeStorage.getInstance().getSettingsData();
		ChooserItemChange listener = new ChooserItemChange(){
			@Override
			public void onChange(int index, boolean val) {
				SettingsData dta = InTimeStorage.getInstance().getSettingsData();
				dta.setSelectedFlashModeIndx(index);				
				InTimeStorage.getInstance().updateSettings(dta);
				//update view
				flashInfo.setText(Character.toUpperCase(dta.getSelectedFlashModeName().charAt(0)) + dta.getSelectedFlashModeName().substring(1));
				chooserWrapper.close();				
				//update camera params
				mainContext.getmPreview().updateCameraParams();
			}
		};				
		chooserWrapper.setContentList(dta.getFlashModels(), dta.getSelectedFlashModeIndx(), listener);
		chooserWrapper.open();	
	}	
	
	


	//open settings
	public void open(){
		mainContext.setSettingsOpen(true);
		settingsView.setAlpha(0f);
		settingsView.setVisibility(View.VISIBLE);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(settingsView, View.SCALE_X, .8f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(settingsView, View.SCALE_Y, .8f, 1f);
		ObjectAnimator tmpAlpha = ObjectAnimator.ofFloat(settingsView, View.ALPHA, 0f, 1f);
		AnimatorSet aSet = new AnimatorSet();
		aSet.setInterpolator(new AccelerateInterpolator());			
		aSet.setDuration(200);		
		aSet.playTogether(tmpAlpha, scaleX, scaleY);
		aSet.start();		
	}
	
	//close settings
	public void close(){		
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(settingsView, View.SCALE_X, 1f, .8f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(settingsView, View.SCALE_Y, 1f, .8f);
		ObjectAnimator tmpAlpha = ObjectAnimator.ofFloat(settingsView, View.ALPHA, 1f, 0f);
		AnimatorSet aSet = new AnimatorSet();
		
		aSet.addListener(new Animator.AnimatorListener(){
			@Override
			public void onAnimationStart(Animator animation) {}
			
			@Override
			public void onAnimationRepeat(Animator animation) {}
			
			@Override
			public void onAnimationEnd(Animator animation) {				
				settingsView.setVisibility(View.GONE);
				mainContext.getMainMenuWrapper().openMenu();
				mainContext.setSettingsOpen(false);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {}				
		});		
		
		aSet.setInterpolator(new AccelerateInterpolator());			
		aSet.setDuration(200);		
		aSet.playTogether(tmpAlpha, scaleX, scaleY);
		aSet.start();		
	}	

}
