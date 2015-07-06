package net.mobileblizzard.intime.components.spiner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomSpinner extends LinearLayout {

	public CustomSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}
	
	private FrameLayout minusBTN;
	private FrameLayout plusBTN;
	private TextView infoTXT;
	private double currentVal;
	private double minVal, maxVal, stepVal;
	private SpinerListener listener;
	//init spinner
	public void init(double initialVal, double min, double max, double step, FrameLayout minusBTN, FrameLayout plusBTN, TextView infoTXT){
		this.minusBTN = minusBTN;
		this.plusBTN = plusBTN;
		this.infoTXT = infoTXT;
		this.currentVal = initialVal;
		this.minVal = min;
		this.maxVal = max;
		this.stepVal = step;
		
		infoTXT.setText(Double.toString(initialVal));
				
		minusBTN.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				if(isSpeicalStep && currentVal==.5){
					currentVal = 0.4;
				}else if (isSpeicalStep && currentVal==.4) {
					currentVal = 0.3;
				}else if (isSpeicalStep && currentVal==.3) {
					currentVal = 0.2;
				}else if (isSpeicalStep && currentVal==.2) {
					currentVal = 0.1;
				}else {
					if(currentVal>minVal){
						currentVal-=stepVal;					
					}else{
						currentVal = maxVal;
					}					
				}								
				showCurrent(currentVal);
			}
		});
		plusBTN.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				if(isSpeicalStep && currentVal==.1){
					currentVal = 0.2;
				}else if (isSpeicalStep && currentVal==.2) {
					currentVal = 0.3;
				}else if (isSpeicalStep && currentVal==.3) {
					currentVal = 0.4;
				}else if (isSpeicalStep && currentVal==maxVal) {
					currentVal = 0.1;
				}else if (isSpeicalStep && currentVal==.4) {
					currentVal = 0.5;
				}else {
					if(currentVal<maxVal){
						currentVal+=stepVal;					
					}else{
						currentVal = minVal;
					}					
				}
				showCurrent(currentVal);
			}
		});		
	}
	
	private void showCurrent(double val){
		infoTXT.setText(Double.toString(val));
		listener.onValueChanged(val);
	}	
	public void setListener(SpinerListener l){
		this.listener = l;
	}	
	
	private boolean isSpeicalStep = false;
	public boolean isSpeicalStep() {
		return isSpeicalStep;
	}

	public void setSpeicalStep(boolean isSpeicalStep) {
		this.isSpeicalStep = isSpeicalStep;
	}
	
}
