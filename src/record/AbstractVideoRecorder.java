package record;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.BufferUtils;
import com.jme3.util.Screenshots;

/**
 * <code>VideoRecorder</code> copies the frames it receives to video. 
 * To ensure smooth video at a constant framerate, you should set your 
 * application's timer to a new <code>IsoTimer</code>.  This class will 
 * auto-determine the framerate of the video based on the time difference 
 * between the first two frames it receives, although you can manually set 
 * the framerate by calling <code>setFps(newFramerate)</code>.  Be sure to 
 * place this processor *after* any other processors whose effects you want 
 * to be included in the output video. You can attach multiple 
 * <code>VideoProcessor</code>s to the same <code>ViewPort</code>.
 * 
 * For example,
 * <code>
 * someViewPort.addProcessor(new VideoProcessor(file1));
 * someViewPort.addProcessor(someShadowRenderer);
 * someViewPort.addProcessor(new VideoProcessor(file2));
 * </code>
 * 
 * will output a video without shadows to <code>file1</code> and a video 
 * with shadows to <code>file2</code>
 * 
 * @author Robert McIntyre
 */

public abstract class AbstractVideoRecorder 
        implements SceneProcessor, VideoRecorder, AppState{

        final File output;
        Camera camera;
        int width;
        int height;
        String targetFileName;
        FrameBuffer frameBuffer;
        Double fps = null;
        RenderManager renderManager;
        ByteBuffer byteBuffer;
        BufferedImage rawFrame;
        boolean isInitilized = false;
        boolean paused = false;
        
        public AbstractVideoRecorder(File output) throws IOException {
                this.output = output;
                this.targetFileName = this.output.getCanonicalPath();   
        }
        
                
        public double getFps() {return this.fps;}
        
        public AbstractVideoRecorder setFps(double fps) {
                this.fps = fps;
                return this;
        }
        
        public void initialize(RenderManager rm, ViewPort viewPort) {
                Camera camera = viewPort.getCamera();
                this.width = camera.getWidth();
                this.height = camera.getHeight();
                                
                rawFrame = new BufferedImage(width, height, 
                                BufferedImage.TYPE_4BYTE_ABGR);         
                byteBuffer = BufferUtils.createByteBuffer(width * height * 4 );
                this.renderManager = rm;
                this.isInitilized = true;
        }

        public void reshape(ViewPort vp, int w, int h) {}
        
        public boolean isInitialized() {return this.isInitilized;}

        public void preFrame(float tpf) {
                if (null == this.fps){
                        this.setFps(1.0 / tpf);}
        }       
        
        public void postQueue(RenderQueue rq) {}

        public void postFrame(FrameBuffer out) {
                if (!this.paused){
                        byteBuffer.clear();
                        renderManager.getRenderer().readFrameBuffer(out, byteBuffer);
                        Screenshots.convertScreenShot(byteBuffer, rawFrame);
                        record(rawFrame);
                }
        }
                        
        public void cleanup(){
                this.pause();
                this.finish();
        };
        
        public void pause(){
                this.paused = true;
        }
        
        public void start(){
                this.paused = false;
        }

        // methods from AppState
        public void initialize(AppStateManager stateManager, Application app) {}

        public void setEnabled(boolean active) {
                if (active) {this.start();}
                else {this.pause();}
        }

        public boolean isEnabled() {
                return this.paused;
        }

        public void stateAttached(AppStateManager stateManager) {}


        public void stateDetached(AppStateManager stateManager) {
                this.pause();
                this.finish();
        }

        public void update(float tpf) {}        
        public void render(RenderManager rm) {}
        public void postRender() {}
        
}