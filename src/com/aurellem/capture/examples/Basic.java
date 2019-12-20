package com.aurellem.capture.examples;

import java.io.File;
import java.io.IOException;


import com.aurellem.capture.Capture;
import com.aurellem.capture.IsoTimer;
import com.jme3.app.SimpleApplication;

import core.Main;


/**
 * Demonstrates how to use basic Audio/Video capture with a
 * jMonkeyEngine application. You can use these techniques to make
 * high quality cutscenes or demo videos, even on very slow laptops.
 * 
 * @author Robert McIntyre
 */

public class Basic {
	
    public static void main(String[] ignore) throws IOException{
	File video = File.createTempFile("JME-water-video", ".avi");
	File audio = File.createTempFile("JME-water-audio", ".wav");
		
	SimpleApplication app = new Main();
	app.setTimer(new IsoTimer(60));
	app.setShowSettings(false);
		
	Capture.captureVideo(app, video);
	Capture.captureAudio(app, audio);
		
	app.start();
		
	System.out.println(video.getCanonicalPath());
	System.out.println(audio.getCanonicalPath());
    }
}
