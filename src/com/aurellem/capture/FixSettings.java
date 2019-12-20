package com.aurellem.capture;


import com.jme3.app.Application;
import com.jme3.system.AppSettings;

import core.Main;


/**
 * jMonkeyEngine will save the settings used in previous runs.
 * This can be especially frustrating when it saves the "Send" 
 * AudioRenderer setting from running the a program that is 
 * capturing audio.  This resets your settings to fix the problem.
 * 
 * if you get 
 *  "Unrecognizable audio renderer specified: Send"
 * run this program.
 * 
 * @author Robert McIntyre
 *
 */

public class FixSettings {

	public static void main(String[] ignore){
		Application app = new Main();
		AppSettings basic = new AppSettings(true);
		app.setSettings(basic);
		app.start();
	}
}
