package com.aurellem.capture.audio;

import com.jme3.audio.Listener;

/**
 * This interface lets you:
 * 1.) Get at rendered 3D-sound data.
 * 2.) Create additional listeners which each hear 
 *     the world from their own perspective.
 * @author Robert McIntyre
 */

public interface MultiListener {

	void addListener(Listener l);
	void registerSoundProcessor(Listener l, SoundProcessor sp);
	void registerSoundProcessor(SoundProcessor sp);
	
}
