package jMonkeyGod;

import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class LabelControl extends AbstractControl { 
    private int index; // can have custom fields -- example    

    public LabelControl(){} // empty serialization constructor   

    /** Optional custom constructor with arguments that can init custom fields.   
      * Note: you cannot modify the spatial here yet!
      */ 
    
    static Camera cam;
    public LabelControl(Camera cam){
    	this.cam = cam;
        // index=i; // example  
    }    

    @Override 
    public void setSpatial(Spatial spatial) {   
        super.setSpatial(spatial);
    }
 
    @Override 
    protected void controlUpdate(float tpf){  
        if(spatial != null) {
        	Quaternion mirror_up =  new Quaternion().fromAxes(
        			cam.getLeft().mult(-1f),        // x is mirror left
        			cam.getUp(),                    // y is up
        			cam.getDirection().mult(-1f));  // z is to you
            spatial.setLocalRotation(mirror_up);
        } 
    }

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {
		// TODO Auto-generated method stub
		
	}   
}