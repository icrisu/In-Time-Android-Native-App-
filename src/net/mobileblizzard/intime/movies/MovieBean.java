package net.mobileblizzard.intime.movies;

import android.graphics.Bitmap;
import net.mobileblizzard.intime.models.storage.TimeLap;

public class MovieBean {

	public MovieBean(TimeLap timeLap, Bitmap bitmap) {
		super();
		this.timeLap = timeLap;
		this.bitmap = bitmap;
	}
	
	
	private TimeLap timeLap;
	private Bitmap bitmap;
	public TimeLap getTimeLap() {
		return timeLap;
	}
	public void setTimeLap(TimeLap timeLap) {
		this.timeLap = timeLap;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	

}
