package com.aurellem.capture;

import com.jme3.system.Timer;

/**
 * RatchetTimer is the same as IsoTimer, except that it will ensure
 * that the simulation does not proceed any faster than it would had
 * NanoTimer been used.
 * 
 * @author normenhansen, Robert McIntyre
 */

public class RatchetTimer extends Timer{
    private long framerate;
    private int ticks;
    private long lastTime = 0;

    public RatchetTimer(float framerate) {
        this.framerate = (long) framerate;
        this.ticks = 0;
    }
    
    /**
     * return time in milliseconds
     */
    public long getTime() {
	return ticks;
    }

    public long getResolution() {
	return framerate;
    }

    public float getFrameRate() {
        return framerate;
    }

    public float getTimePerFrame() {
        return (float) (1.0f / framerate);
    }

    public void update() {
        long time = System.currentTimeMillis();
        long difference = time - lastTime;
        lastTime = time;
        if (difference < (1.0f / this.framerate) * 1000.0f) {
            try {
                Thread.sleep(difference);
            } catch (InterruptedException ex) {
            }
        }
        this.ticks++;
    }

    public void reset() {
        this.ticks = 0;
    }
}

