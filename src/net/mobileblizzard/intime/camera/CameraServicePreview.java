package net.mobileblizzard.intime.camera;

import java.io.IOException;
import java.util.List;

import net.mobileblizzard.intime.MainActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class CameraServicePreview extends SurfaceView implements SurfaceHolder.Callback {
	
	private SurfaceHolder mHolder;	
	private Camera mCamera;
	private static final boolean DEBUGGING = true;
	private static final String LOG_TAG = "i-camera";
	private MainActivity mActivity;

	
	public CameraServicePreview(Context context, Camera camera) {
		super(context);					
		FrameLayout.LayoutParams lpp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setLayoutParams(lpp);
		
		if(camera==null)
			return;
		mCamera = camera;		
	}
	
	//init here
	public void init(MainActivity ac){
		this.mActivity = ac;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);		
	}	
	//clean
	public void clean(){
		mActivity = null;
		if(mHolder!=null){
			mHolder.removeCallback(this);
			mHolder = null;
		}		
	}
	public void destroy(){
		if(mHolder!=null){
			mHolder.removeCallback(this);
			mHolder = null;
		}		
	}
	

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("intime", "DESTROYED");
		mHolder.removeCallback(this);
		/*
        if (null == mCamera) {
            return;
        }
        mCamera.stopPreview();
        mCamera.release();        
        mCamera = null;
        */	                
	}
	
	protected List<Camera.Size> mPreviewSizeList;
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
			
			//If your preview can change or rotate, take care of those events here. 
			// Make sure to stop the preview before resizing or reformatting it. 
			if (mHolder.getSurface() == null){ 
				// preview surface does not exist 
				return; 
			} 
			// stop preview before making changes 
			try { 
				mCamera.stopPreview(); 
			} catch (Exception e){ 
				// ignore: tried to stop a non-existent preview 
			} 
			// set preview size and make any resize, rotate or 
			// reformatting changes here 
			// start preview with new settings 			
			 try { 
				 mCamera.setPreviewDisplay(mHolder);				 
				 
				 
				 boolean isPortrait = isPortrait();
				 Parameters cameraParams = mCamera.getParameters();
				 mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
				 Camera.Size previewSize = determinePreviewSize(isPortrait, width, height);
				 Log.d(LOG_TAG, "preview size: "+previewSize.width+" : "+previewSize.height);
				 cameraParams.setPreviewSize(previewSize.width, previewSize.height);

		            int angle;
		            Display display = mActivity.getWindowManager().getDefaultDisplay();
		            
		            
		            switch (display.getRotation()) {
		                case Surface.ROTATION_0: // This is display orientation
		                    angle = 90; // This is camera orientation
		                    break;
		                case Surface.ROTATION_90:
		                    angle = 0;
		                    break;
		                case Surface.ROTATION_180:
		                    angle = 270;
		                    break;
		                case Surface.ROTATION_270:
		                    angle = 180;
		                    break;
		                default:
		                    angle = 90;
		                    break;
		            }
		            Log.v(LOG_TAG, "angle: " + angle);
		            mCamera.setDisplayOrientation(angle);
		    		
		            cameraParams.setRotation(angle);
		            cameraParams.setRecordingHint(true);
		            //cameraParams.set( "cam_mode", 1 ); 
		    		
				 				 
				 mCamera.setParameters(cameraParams);
				
				 
				 mCamera.startPreview(); 			
				 
			 }catch(Exception e){ 
				 e.printStackTrace(); 
			 }			 			
	}
	
	
    /**
     * @param cameraParams
     * @param portrait
     * @param reqWidth must be the value of the parameter passed in surfaceChanged
     * @param reqHeight must be the value of the parameter passed in surfaceChanged
     * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPreviewSizes.
     */
    protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth, int reqHeight) {
        // Meaning of width and height is switched for preview when portrait,
        // while it is the same as user's view for surface and metrics.
        // That is, width must always be larger than height for setPreviewSize.
        int reqPreviewWidth; // requested width in terms of camera hardware
        int reqPreviewHeight; // requested height in terms of camera hardware
        if (portrait) {
            reqPreviewWidth = reqHeight;
            reqPreviewHeight = reqWidth;
        } else {
            reqPreviewWidth = reqWidth;
            reqPreviewHeight = reqHeight;
        }
        
        //reqPreviewWidth = reqWidth;
        //reqPreviewHeight = reqHeight;        

        if (DEBUGGING) {
            Log.v(LOG_TAG, "Listing all supported preview sizes");
            for (Camera.Size size : mPreviewSizeList) {
                Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
            }
            /*
            Log.v(LOG_TAG, "Listing all supported picture sizes");
            for (Camera.Size size : mPictureSizeList) {
                Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
            }
            */
        }

        // Adjust surface size with the closest aspect-ratio
        float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : mPreviewSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }
	
	
	
	//check if is portrait
    public boolean isPortrait() {
        return (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }	
}
