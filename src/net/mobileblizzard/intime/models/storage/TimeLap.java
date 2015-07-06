package net.mobileblizzard.intime.models.storage;

import java.util.UUID;

public class TimeLap {

	
	public TimeLap(String path, String name) {
		super();
		this.path = path;
		this.name = name;
		this.ID = UUID.randomUUID().toString();
	}
	
	public void close(int framesNo){
		this.frames = framesNo;
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public int getFrames() {
		return frames;
	}
	public void setFrames(int frames) {
		this.frames = frames;
	}


	private String path;
	private String name;
	private String ID;
	private int frames;
	
	

}
