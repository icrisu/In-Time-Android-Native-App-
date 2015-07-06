package net.mobileblizzard.intime.movies;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;

public class LeftThumbControl extends LinearLayout {

	public LeftThumbControl(Context context, int thumbID) {
		super(context);
		this.setPadding(5, 5, 5, 5);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 0, 5, 0);
		setLayoutParams(params);
		setOrientation(LinearLayout.HORIZONTAL);		
		
		setBackgroundColor(Color.parseColor("#000000"));
		
		ImageView ai = new ImageView(context);
		LayoutParams aiParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ai.setLayoutParams(aiParams);		
		ai.setImageResource(thumbID);
		this.addView(ai);		
		
		this.setGravity(Gravity.CENTER);
	}

}
