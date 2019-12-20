package com.aurellem.capture.video;

import java.awt.image.BufferedImage;

public interface VideoRecorder{

	/**	
	 * Write this image to video, disk, etc.
	 * @param image the image to write
	 */
	void record(BufferedImage image);
	
	/**
	 * Stop recording temporarily.  The recording can be started again
	 * with start()
	 */
	void pause();
	
	/**
	 * Start the recording.
	 */
	void start();
	
	/**
	 * Closes the video file, writing appropriate headers, trailers, etc.
	 * After this is called, no more recording can be done.
	 */
	void finish();	
}