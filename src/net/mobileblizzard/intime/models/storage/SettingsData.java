package net.mobileblizzard.intime.models.storage;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.util.Log;

public class SettingsData {
	
	//inti default values here first time
	public void init(){
		setPhotosInterval(1);
		setTimelapDuration(24);
		setStartDelay(5);
	}


	private double photosInterval;
	private double timelapDuration;	
	private double startDelay;
	private boolean startDelayIsOn = false;
	private boolean workerStarted = false;
	
	public double getPhotosInterval() {
		return photosInterval;
	}
	public void setPhotosInterval(double photosInterval) {
		this.photosInterval = photosInterval;
	}
	
	public double getTimelapDuration() {
		return timelapDuration;
	}
	public void setTimelapDuration(double timelapDuration) {
		this.timelapDuration = timelapDuration;
	}
	
	
	public double getStartDelay() {
		return startDelay;
	}
	public void setStartDelay(double startDelay) {
		this.startDelay = startDelay;
	}
	public boolean isStartDelayIsOn() {
		return startDelayIsOn;
	}
	public void setStartDelayIsOn(boolean startDelayIsOn) {
		this.startDelayIsOn = startDelayIsOn;
	}
	public boolean isWorkerStarted() {
		return workerStarted;
	}
	public void setWorkerStarted(boolean workerStarted) {
		this.workerStarted = workerStarted;
	}
	
	
	private int cameraAngle = 0;
	public int getCameraAngle() {
		return cameraAngle;
	}
	public void setCameraAngle(int cameraAngle) {
		this.cameraAngle = cameraAngle;
	}
	
	
	//RESOLUTION
	String resolution[] = {"Highest", "Lowest", "1080p", "720p", "480p"};
	int resolutionID[] = {CamcorderProfile.QUALITY_TIME_LAPSE_HIGH, CamcorderProfile.QUALITY_TIME_LAPSE_LOW, CamcorderProfile.QUALITY_TIME_LAPSE_1080P, CamcorderProfile.QUALITY_TIME_LAPSE_720P, CamcorderProfile.QUALITY_TIME_LAPSE_480P};
	private int selectedResolution = 0;

	public String[] getResolution() {
		return resolution;
	}
	public void setResolution(String[] resolution) {
		this.resolution = resolution;
	}
	public int[] getResolutionID() {
		return resolutionID;
	}
	public void setResolutionID(int[] resolutionID) {
		this.resolutionID = resolutionID;
	}
	public int getSelectedResolution() {
		return selectedResolution;
	}
	public void setSelectedResolution(int selectedResolution) {
		this.selectedResolution = selectedResolution;
	}
	
	public String getSelectedResolutionName(){
		String name = "";
		for (int i = 0; i < resolution.length; i++) {
			if(i==selectedResolution){
				name = resolution[i];
				break;
			}
		}
		return name;
	}
	public int getSelectedResolutionID(){
		int out = CamcorderProfile.QUALITY_TIME_LAPSE_HIGH;
		for (int i = 0; i < resolutionID.length; i++) {
			if(i==selectedResolution){
				out = resolutionID[i];
				break;
			}
		}
		return out;
	}
	
	
	//WHITE BALANCE
	private List<String> whiteBalanceList = null;
	private int selectedWhiteBalance = 0;
	private String selectedWhiteBalanceName = "auto";
	
	public List<String> getWhiteBalanceList() {
		return whiteBalanceList;
	}
	public void setWhiteBalanceList(List<String> whiteBalanceList) {
		this.whiteBalanceList = whiteBalanceList;
	}
	public int getSelectedWhiteBalance() {
		return selectedWhiteBalance;
	}
	public void setSelectedWhiteBalance(int selectedWhiteBalance) {
		this.selectedWhiteBalance = selectedWhiteBalance;
		if(whiteBalanceList!=null){
			for (int i = 0; i < whiteBalanceList.size(); i++) {
				if(i==selectedWhiteBalance){
					selectedWhiteBalanceName = whiteBalanceList.get(i);
				}
			}
		}
	}
	public String getSelectedWhiteBalanceName(){
		return selectedWhiteBalanceName;
	}
	
	
	//FOCUS MODE
	private List<String> focusModes = null;
	private int selectedFocusModeIndex = 0;
	private String selectedFocusModeName = "auto";

	public List<String> getFocusModes() {
		return focusModes;
	}
	public void setFocusModes(List<String> focusModes) {
		this.focusModes = focusModes;
	}
	public int getSelectedFocusModeIndex() {
		return selectedFocusModeIndex;
	}
	public void setSelectedFocusModeIndex(int selectedFocusModeIndex) {
		this.selectedFocusModeIndex = selectedFocusModeIndex;
		if(focusModes!=null){
			for (int i = 0; i < focusModes.size(); i++) {
				if(i==selectedFocusModeIndex){
					selectedFocusModeName = focusModes.get(i);
					break;
				}
			}
		}
	}
	public String getSelectedFocusModeName() {
		return selectedFocusModeName;
	}
	public void setSelectedFocusModeName(String selectedFocusModeName) {
		this.selectedFocusModeName = selectedFocusModeName;
	}
	
	//COLOR EFFECTS
	
	private List<String> colorEfects = null;
	private int selectedColorEfxIndx = 0;
	private String selectedColorEfxName = "none";

	public List<String> getColorEfects() {
		return colorEfects;
	}
	public void setColorEfects(List<String> colorEfects) {
		this.colorEfects = colorEfects;
	}
	public int getSelectedColorEfxIndx() {
		return selectedColorEfxIndx;
	}
	public void setSelectedColorEfxIndx(int selectedColorEfxIndx) {
		this.selectedColorEfxIndx = selectedColorEfxIndx;
		if(colorEfects!=null){
			for (int i = 0; i < colorEfects.size(); i++) {
				if(i==selectedColorEfxIndx){
					selectedColorEfxName = colorEfects.get(i);
					break;
				}
			}
		}
	}
	public String getSelectedColorEfxName() {
		return selectedColorEfxName;
	}
	public void setSelectedColorEfxName(String selectedColorEfxName) {
		this.selectedColorEfxName = selectedColorEfxName;
	}
	
	//FLASH MODE
	private List<String> flashModels = null;
	private int selectedFlashModeIndx = 0;
	private String selectedFlashModeName = "off";

	public int getSelectedFlashModeIndx() {
		return selectedFlashModeIndx;
	}
	public void setSelectedFlashModeIndx(int selectedFlashModeIndx) {
		this.selectedFlashModeIndx = selectedFlashModeIndx;
		if(flashModels!=null){
			for (int i = 0; i < flashModels.size(); i++) {
				if(i==selectedFlashModeIndx){
					selectedFlashModeName = flashModels.get(i);
					break;
				}
			}
		}
	}
	public String getSelectedFlashModeName() {
		return selectedFlashModeName;
	}
	public void setSelectedFlashModeName(String selectedFlashModeName) {
		this.selectedFlashModeName = selectedFlashModeName;
	}
	public List<String> getFlashModels() {
		return flashModels;
	}
	public void setFlashModels(List<String> flashModels) {
		this.flashModels = flashModels;
	}
	
	
	//KEEP SCREEN ON
	private boolean keepScreenOn = false;

	public boolean isKeepScreenOn() {
		return keepScreenOn;
	}
	public void setKeepScreenOn(boolean keepScreenOn) {
		this.keepScreenOn = keepScreenOn;
	}
	
	
	//TIME LAPS
	private ArrayList<TimeLap> timelaps;

	public ArrayList<TimeLap> getTimelaps() {
		return timelaps;
	}
	public void setTimelaps(ArrayList<TimeLap> timelaps) {
		this.timelaps = timelaps;
	}
	//add new timelap
	public void addTimeLap(TimeLap t){
		if(timelaps==null){
			timelaps = new ArrayList<TimeLap>();
		}
		timelaps.add(t);
	}
	
	//remove timelap
	public void removeTimelap(String ID){
		if(timelaps==null)
			return;
		for (int i = 0; i < timelaps.size(); i++) {
			TimeLap t = timelaps.get(i);
			if(t.getID().equals(ID)){
				timelaps.remove(i);
				break;
			}
		}
		return;
	}
	
	//get timelap
	public TimeLap getTimeLap(String ID){
		TimeLap t = null;
		for (int i = 0; i < timelaps.size(); i++) {
			TimeLap temp = timelaps.get(i);
			if(temp.getID().equals(ID)){
				t = temp;
				break;
			}
		}
		return t;
	}
	
	
}
