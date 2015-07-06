package net.mobileblizzard.intime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class InfoActivity extends Activity {
	
	private FrameLayout infoBackBTN;
	private LinearLayout shareAppBTN;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		infoBackBTN = (FrameLayout) findViewById(R.id.infoBackBTN);		
		infoBackBTN.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				closeActivity();
			}
		});
		
		shareAppBTN = (LinearLayout) findViewById(R.id.shareAppBTN);		
		shareAppBTN.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
		        Intent intent = new Intent(Intent.ACTION_SEND);
		        intent.setType("text/plain");
		        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=net.mobileblizzard.intime");
		        intent.putExtra(Intent.EXTRA_SUBJECT, "InTime - Time Lapse Camera");
		        startActivity(Intent.createChooser(intent, "Share"));				
			}
		});	
				
	}


	public void closeActivity(){
		this.onBackPressed();
	}

}
