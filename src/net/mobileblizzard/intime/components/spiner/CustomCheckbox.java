package net.mobileblizzard.intime.components.spiner;

import net.mobileblizzard.intime.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CustomCheckbox extends FrameLayout {
	
	public CustomCheckbox(Context context) {
		super(context);		
		// TODO Auto-generated constructor stub
	}	
	
	private Context context;
	public CustomCheckbox(Context context, AttributeSet attrs) {
		super(context, attrs);		
		// TODO Auto-generated constructor stub
	}		
	
	private boolean isChecked;
	private ImageView off;
	private ImageView on;
	public void init(boolean val){
		
		this.isChecked = val;
		off = new ImageView(getContext());
		off.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		off.setImageResource(R.drawable.check25_off);		
		
		on = new ImageView(getContext());
		on.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		on.setImageResource(R.drawable.check25);
		
		if(!isChecked)
			on.setVisibility(View.GONE);
		this.addView(off);
		this.addView(on);	
		
		this.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(isChecked){
					isChecked = false;
				}else{
					isChecked = true;
				}
				updateView();
			}
		});
	}
	
	public void updateView(){
		if(isChecked){
			on.setVisibility(View.VISIBLE);
			off.setVisibility(View.GONE);
		}else{
			on.setVisibility(View.GONE);
			off.setVisibility(View.VISIBLE);
		}
		if(listener!=null){
			listener.onCBValueChanged(isChecked);
		}		
	}
	
	
	
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		if(isChecked){
			on.setVisibility(View.VISIBLE);
			off.setVisibility(View.GONE);
		}else{
			on.setVisibility(View.GONE);
			off.setVisibility(View.VISIBLE);
		}
	}

	private SpinerListener listener;
	public void setListener(SpinerListener l){
		this.listener = l;
	}

}
