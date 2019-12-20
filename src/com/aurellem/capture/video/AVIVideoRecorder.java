package com.aurellem.capture.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.jme3.profile.AppProfiler;

import ca.randelshofer.AVIOutputStream;


public class AVIVideoRecorder extends AbstractVideoRecorder{

    AVIOutputStream out = null;
    boolean videoReady = false;
    BufferedImage frame;
	
    public AVIVideoRecorder(File output) throws IOException {
	super(output);
	this.out = new 
	    AVIOutputStream(output, AVIOutputStream.VideoFormat.RAW, 24);
	this.out.setFrameRate(60);
    }
	
    public void initVideo (){
	frame = new BufferedImage(
				  width, height,
				  BufferedImage.TYPE_INT_RGB);
	out.setFrameRate((int) Math.round(this.fps));
	out.setTimeScale(1);
	out.setVideoDimension(width, height);
	this.videoReady = true;
    }
	
    public void record(BufferedImage rawFrame) {
	if (!videoReady){initVideo();}
	this.frame.getGraphics().drawImage(rawFrame, 0, 0, null);
	try {out.writeFrame(frame);}
	catch (IOException e){e.printStackTrace();}
    }
	
    public void finish() {
	try {out.close();} 
	catch (IOException e) {e.printStackTrace();}
    }

	@Override
	public void setProfiler(AppProfiler arg0) {
		// TODO Auto-generated method stub
		
	}

	

}
