package com.aurellem.capture.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

/**
 * Method of Combination for sound processors.  This SoundProcessor will 
 * run the methods of each of its constituent SoundProcessors in the order 
 * in which it was constructed.
 * 
 * @author Robert McIntyre
 */
public class CompositeSoundProcessor implements SoundProcessor{

	SoundProcessor[] processors;
	
	public CompositeSoundProcessor(SoundProcessor...processors){
		this.processors = processors;
	}
	
	public void process(ByteBuffer audioSamples, int numSamples, AudioFormat format) {
		for (SoundProcessor sp : processors){
			sp.process(audioSamples, numSamples, format);
		}
	}
	
	public void cleanup() {
		for (SoundProcessor sp : processors){
			sp.cleanup();
		}
	}
}
