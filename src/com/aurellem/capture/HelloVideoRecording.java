package com.aurellem.capture;

import java.io.File;
import java.io.IOException;


import com.aurellem.capture.Capture;
import com.aurellem.capture.IsoTimer;
import com.jme3.app.Application;

import core.Main;

/** Recording Video from your Application is simple.  If all you want
 *  to do is record Video, then follow the following steps.
 * 
 *  1.) Set the Application's timer to an IsoTimer.  The framerate of
 *  the IsoTimer will determine the framerate of the resulting video.
 * 
 *
 * 
 *  2.) Call Capture.captureVideo(yourApplication, target-file) before
 *  calling yourApplication.start()
 *  
 *  That's it!  If you have any comments/problems, please PM me on the
 *  jMonkeyEngine forms.  My username is bortreb.
 * 
 * @author Robert McIntyre
 */
public class HelloVideoRecording {

    public static void main(String[] ignore) throws IOException {
        Application app = new Main();
        File video = File.createTempFile("JME-simple-video", ".avi");
        app.setTimer(new IsoTimer(60));
        Capture.captureVideo(app, video);
        app.start();
        System.out.println(video.getCanonicalPath());
    }
}