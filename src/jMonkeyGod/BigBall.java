package jMonkeyGod;


import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

public class BigBall{
	public static List<Integer> created = new ArrayList<>();	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "BigBall [loc=" + loc + ", name=" + name + ", rad=" + rad + "]";
	}

	Geometry sphere; // create cube shape
    Material mat;
    BitmapFont font;
    public StellarPoint loc;
    RigidBodyControl sphereController = new RigidBodyControl (10.0f);
    String name;
    int rad;
    
    Node conceptThing;   
	public BigBall(Point3D _loc,
			       int _rad,
			       Material _mat,
			       BitmapFont _font,
			       String _name,
			       Camera cam, 
			       Node rootNode) {
		loc = new StellarPoint(_loc);
		mat = _mat;
		name = _name;
		rad = _rad;
		font = _font;
		if (!BigBall.created.contains(this.hashCode()))  {
	    	BigBall.created.add(this.hashCode());
			//System.out.println(this.hashCode());
		}
		else {
			//System.out.println("Not added");

			return;
		}


		Sphere ballMesh = new Sphere(40,10,3);
        sphere = new Geometry (name, ballMesh);
        
        sphere.setMaterial(mat);   
        
        LabelControl lc = new LabelControl(cam);
        conceptThing = new Node();
        conceptThing.attachChild(sphere);
        conceptThing.attachChild(addLabel(lc));
        conceptThing.setLocalTranslation(loc.getVector());
        rootNode.attachChild(conceptThing);
	}
	
	public BitmapText addLabel(LabelControl lc)  {
		BitmapText label = new BitmapText(font, false);
		label.setSize(5);
		label.setText(sphere.getName()); 
		label.setColor(new ColorRGBA(0f,0f,0f,1f));
		label.setQueueBucket(Bucket.Transparent);
		label.addControl(lc);
		return label;
	}
	
}
