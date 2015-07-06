package net.mobileblizzard.intime.events;

import java.io.Serializable;

import net.mobileblizzard.intime.camera.CameraPreview;
import net.mobileblizzard.intime.camera.CameraServicePreview;

import android.hardware.Camera;

public class CameraTransport implements Serializable{

	public CameraTransport(Camera camera, CameraServicePreview cPreview) {
		super();
		this.camera = camera;
		this.cPreview = cPreview;
	}

	private Camera camera;
	private CameraServicePreview cPreview;
	
	public Camera getCamera() {
		return camera;
	}	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public CameraServicePreview getcPreview() {
		return cPreview;
	}

	public void setcPreview(CameraServicePreview cPreview) {
		this.cPreview = cPreview;
	}
	
	

}
