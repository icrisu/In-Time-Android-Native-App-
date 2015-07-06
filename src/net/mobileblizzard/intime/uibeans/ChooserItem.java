package net.mobileblizzard.intime.uibeans;

import net.mobileblizzard.intime.components.spiner.CustomCheckbox;
import net.mobileblizzard.intime.components.spiner.SpinerListener;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ChooserItem extends LinearLayout {
	
	private int currentIndex;
	private CustomCheckbox cb;
	public ChooserItem(Context context, String name, boolean isSelected, int indx) {		
		super(context);
		this.currentIndex = indx;
		
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		llp.setMargins(0, 15, 0, 15);		
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(llp);
		setPadding(8, 8, 8, 8);	
		
		TableLayout.LayoutParams textViewParam = new TableLayout.LayoutParams
			     (TableLayout.LayoutParams.WRAP_CONTENT,
			     TableLayout.LayoutParams.WRAP_CONTENT);
		textViewParam.setMargins(15, 0, 0, 0);
		textViewParam.gravity = Gravity.CENTER_VERTICAL;
		TextView info = new TextView(context);
		info.setTextSize(14);
		info.setTextColor(Color.parseColor("#FFFFFF"));
		info.setLayoutParams(textViewParam);
		info.setText(name);			
		
		cb = new CustomCheckbox(context);
		
		LinearLayout.LayoutParams llpCB = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		llpCB.setMargins(10, 0, 0, 0);
		cb.setLayoutParams(llpCB);
		cb.init(isSelected);
		
		cb.setListener(new SpinerListener(){
			public void onCBValueChanged(boolean val){				
				if(listener!=null){					
					listener.onChange(currentIndex, val);
				}				
			}			
		});	
			
		addView(cb);	
		addView(info);
		this.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cb.setChecked(true);
				if(listener!=null){					
					listener.onChange(currentIndex, true);
				}				
			}
		});
	}	
	
	ChooserItemChange listener;
	public ChooserItemChange getListener() {
		return listener;
	}
	public void setListener(ChooserItemChange listener) {
		this.listener = listener;
	}
	
	
	

}
