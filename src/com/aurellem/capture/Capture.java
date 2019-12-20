package com.aurellem.capture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import com.aurellem.capture.audio.MultiListener;
import com.aurellem.capture.video.AVIVideoRecorder;
import com.aurellem.capture.video.AbstractVideoRecorder;
import com.aurellem.capture.video.FileVideoRecorder;
//import com.aurellem.capture.video.XuggleVideoRecorder;
import com.jme3.app.Application;
import com.jme3.audio.AudioRenderer;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

/**
 * Use the methods in this class for capturing consistent,
 * high quality video and audio from a jMonkeyEngine3
 * application.  To capture audio or video do the following:
 * 
 * 1.) Set your application's timer to an IsoTimer. Create
 *     the IsoTimer with the desired video
 *     frames-per-second.
 *
 * 2.) Call captureAudio and/or captureVideo on the
 *     Application as desired before starting the
 *     Application.
 * 
 * See the Basic and Advanced demos in the examples section
 * for more information.  If you have any trouble, please PM
 * me on the jMonkeyEngine forums.  My username is bortreb.
 * 
 * @author Robert McIntyre
 */

public class Capture {

    /**
     * Use this function to capture video from your
     * application.  You specify the framerate at which the
     * video will be recording by setting the Application's
     * timer to an IsoTimer created with the desired
     * frames-per-second.
     * 
     * There are three ways to record and they are selected
     * by the properties of the file that you specify.
     * 
     * 1.) (Preferred) If you supply an empty directory as
     *     the file, then the video will be saved as a
     *     sequence of .png files, one file per frame.  The
     *     files start at 0000000.png and increment from
     *     there.  You can then combine the frames into your
     *     preferred container/codec. If the directory is
     *     not empty, then writing video frames to it will
     *     fail, and nothing will be written.
     *     
     * 2.) If the filename ends in ".avi" then the frames
     *     will be encoded as a RAW stream inside an AVI 1.0
     *     container.  The resulting file will be quite
     *     large and you will probably want to re-encode it
     *     to your preferred container/codec format.  Be
     *     advised that some video payers cannot process AVI
     *     with a RAW stream, and that AVI 1.0 files
     *     generated by this method that exceed 2.0GB are
     *     invalid according to the AVI 1.0 spec (but many
     *     programs can still deal with them.)  Thanks to
     *     Werner Randelshofer for his excellent work which
     *     made AVI file writer option possible.
     *  
     * 3.) Any non-directory file ending in anything other
     *     than ".avi" will be processed through Xuggle.
     *     Xuggle provides the option to use many
     *     codecs/containers, but you will have to install
     *     it on your system yourself in order to use this
     *     option. Please visit http://www.xuggle.com/ to
     *     learn how to do this.
     * 
     * @param app The Application from which you wish to record Video.
     * @param file The file to which the video will be captured. 
     * @throws IOException
     */
	
    public static void captureVideo
	(final Application app, final File file) throws IOException{
	final AbstractVideoRecorder videoRecorder;

	if (file.getCanonicalPath().endsWith(".avi")){
	    videoRecorder = new AVIVideoRecorder(file);}
	else {
	    videoRecorder = new FileVideoRecorder(file);}
	//else { 
		//videoRecorder = new XuggleVideoRecorder(file);
	//}

	Callable<Object> thunk = new Callable<Object>(){
	    public Object call(){

		ViewPort viewPort = 
		app.getRenderManager()
		.createPostView("aurellem video record", 
				app.getCamera());

		viewPort.setClearFlags(false, false, false);

		// get GUI node stuff
		for (Spatial s : app.getGuiViewPort().getScenes()){
		    viewPort.attachScene(s);
		}

		app.getStateManager().attach(videoRecorder);
		viewPort.addProcessor(videoRecorder);
		return null;
	    }
	};
	app.enqueue(thunk);
    }


    /**
     * Use this function to capture audio from your
     * application.  Audio data will be saved in linear PCM
     * format at 44,100 hertz in the wav container format to
     * the file that you specify.
     * 
     * Note that you *have* to use an IsoTimer for your
     * Application's timer while recording audio or the
     * recording will fail. Also ensure that your IsoTimer
     * obeys the following constraints:
     * 
     *  1.) The frames-per-second value of the IsoTimer
     *   cannot be lower than 10 frames-per-second.
     *
     *  2.) The frames-per-second value of the IsoTimer must
     *   evenly divide 44,100.
     * 
     * @param app The Application from which you wish to
     * record Audio.
     * @param file the target file to which you want to
     * record audio data.
     * @throws IOException
     */
	
    public static void captureAudio
	(final Application app, final File file) throws IOException{
	AppSettings settings = null;
	if (app.getContext() != null){
	    settings = app.getContext().getSettings();}
	if (settings == null){settings = new AppSettings(true);}
	settings.setAudioRenderer("Send");
	app.setSettings(settings);

	JmeSystem.setSystemDelegate(new AurellemSystemDelegate());
		

	Callable<Object> thunk = new Callable<Object>(){
	    public Object call(){
		AudioRenderer ar = app.getAudioRenderer();
		if (ar instanceof MultiListener){
		    MultiListener ml = (MultiListener)ar;
		}
		return null;
	    }
	};
	app.enqueue(thunk);
    }		
}
