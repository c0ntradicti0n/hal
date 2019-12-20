package com.aurellem.capture.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

public interface SoundProcessor {

    /**
     * Called when the SoundProcessor is being destroyed, and there
     * are no more samples to process.  This happens at the latest
     * when the Application is shutting down.
     */
    void cleanup();
	
    /**
     * 
     * Called whenever there are new audio samples to process. The
     * audioSamples ByteBuffer contains 3D audio data rendered by
     * OpenAL.
     * 
     * @param audioSamples a ByteBuffer containing processed audio
     * samples
     * @param numSamples the number of samples, in bytes, that are
     * valid
     * @param format the format of the audio samples in audioSamples
     */
    void process(ByteBuffer audioSamples, int numSamples, AudioFormat format);
	
}
