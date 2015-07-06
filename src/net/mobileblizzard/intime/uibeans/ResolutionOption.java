package net.mobileblizzard.intime.uibeans;

import net.mobileblizzard.intime.components.spiner.CustomCheckbox;
import net.mobileblizzard.intime.components.spiner.SpinerListener;
import net.mobileblizzard.intime.models.storage.SettingsData;
import net.mobileblizzard.intime.storage.InTimeStorage;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class ResolutionOption extends LinearLayout {	
	
	//private int isSelected;
	private int currentIndex;	
	private boolean currentSelected;
	private CustomCheckbox cb;
	public ResolutionOption(Context context, Size s, int index, boolean isSelected) {
		super(context);
		this.currentIndex = index;
		this.currentSelected = isSelected;
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		llp.setMargins(0, 15, 0, 15);		
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(llp);
		setPadding(8, 8, 8, 8);
		//setBackgroundColor(Color.parseColor("#FFFFFF"));
		
		TableLayout.LayoutParams textViewParam = new TableLayout.LayoutParams
			     (TableLayout.LayoutParams.WRAP_CONTENT,
			     TableLayout.LayoutParams.WRAP_CONTENT, 1f);		
		TextView info = new TextView(context);
		info.setTextSize(14);
		info.setTextColor(Color.parseColor("#FFFFFF"));
		info.setLayoutParams(textViewParam);
		info.setText(Integer.toString(s.width)+" x "+Integer.toString(s.height));
		
		addView(info);		
		//<TextView  android:layout_width="0dp" android:layout_weight="1" android:layout_height="wrap_content" android:layout_gravity="center_vertical" />
		
		cb = new CustomCheckbox(context);
		LinearLayout.LayoutParams llpCB = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		cb.setLayoutParams(llpCB);
		cb.init(isSelected);
		cb.setListener(new SpinerListener(){
			public void onCBValueChanged(boolean val){
				if(listener!=null){
					setCurrentSelected(val);
					listener.onResolutionChange(currentIndex, val);
				}
			}			
		});		
		addView(cb);
		
	}
	
	
	
	public boolean isCurrentSelected() {
		return currentSelected;
	}
	public void setCurrentSelected(boolean currentSelected) {
		this.currentSelected = currentSelected;
		if(!currentSelected)
			cb.setChecked(currentSelected);
	}


	public ResolutionOptionChange getListener() {
		return listener;
	}
	public void setListener(ResolutionOptionChange listener) {
		this.listener = listener;
	}
	private ResolutionOptionChange listener;	
	

}
