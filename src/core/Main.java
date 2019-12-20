package core;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import com.aurellem.capture.Capture;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;

import dao.stellarObjectNeo4jDAO;
import jMonkeyGod.BigBall;
import jMonkeyGod.StellarPoint;
import model.stellarObject;
import record.AVIVideoRecorder;
import record.AbstractVideoRecorder;
import record.FileVideoRecorder;
import record.IsoTimer;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;

public class Main extends SimpleApplication {
	static stellarObjectNeo4jDAO cosmic_dao = new stellarObjectNeo4jDAO("tsne");
	private BulletAppState physicsState = new BulletAppState();

	ArrayList<Geometry> stars = new ArrayList<>();
	ArrayList<Material> materials = new ArrayList<>();
	ArrayList<BigBall> balls = new ArrayList<>();

	public static void main(String[] args) {

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int w = gd.getDisplayMode().getWidth();
		int h = gd.getDisplayMode().getHeight();

		System.out.println("w,h: " + w + "," + h);

		AppSettings sets = new AppSettings(true);
		sets.setTitle("LightNodeViewer");

		sets.setWidth(w);
		sets.setHeight(h);

		sets.setFullscreen(true);

		Main app = new Main();
		app.setTimer(new IsoTimer(100));
		try {
			Capture.captureVideo(app, new File("record.avi"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		app.setSettings(sets);
		app.start(); // start the game
	}

	public static void captureVideo(final Application app, final File file) throws IOException {
		AbstractVideoRecorder videoRecorder = new FileVideoRecorder(file);
		if (file.getCanonicalPath().endsWith(".avi")) {
			videoRecorder = new AVIVideoRecorder(file);
		} else if (file.isDirectory()) {
			videoRecorder = new FileVideoRecorder(file);
		}

		Callable<Object> thunk = new Callable<Object>() {
			public Object call() {
				ViewPort viewPort = app.getRenderManager().createPostView("aurellem record", app.getCamera());
				viewPort.setClearFlags(false, false, false);
// get GUI node stuff
				for (Spatial s : app.getGuiViewPort().getScenes()) {
					viewPort.attachScene(s);
				}
				app.getStateManager().attach(videoRecorder);
				viewPort.addProcessor(videoRecorder);
				return null;
			}
		};
		app.enqueue(thunk);
	}

	public void addWordGroup(String sub, ColorRGBA color) {
		List<stellarObject> ElementsInHorizon = cosmic_dao.findByName(sub);

		Material knowledgeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

		knowledgeMaterial.setColor("Color", color);
		for (stellarObject dataObi : ElementsInHorizon) {
			BigBall obi = new BigBall(dataObi.coords, 2, knowledgeMaterial, font, dataObi.name, cam, rootNode);
		}
	}

	public void addWordRelation(String relation, ColorRGBA color) {
		HashMap<stellarObject, stellarObject> ElementsInHorizon = cosmic_dao.findByRel(relation);

		ElementsInHorizon.forEach((n1, n2) -> {
			Vector3f v1 = new StellarPoint(n1.coords).getVector();
			Vector3f v2 = new StellarPoint(n2.coords).getVector();

			Line line = new Line(v1, v2);
			line.setLineWidth(1);
			Geometry geometry = new Geometry("Bullet", line);
			Material orange = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			orange.setColor("Color", color);
			geometry.setMaterial(orange);
			rootNode.attachChild(geometry);
		});
	}

	final static int MAX = 30000;

	public void addRealm(Vector3f rPos, float radius, ColorRGBA color) {
		StellarPoint local_pos = new StellarPoint(rPos);
		List<stellarObject> ElementsInHorizon = cosmic_dao.findByPosition(local_pos, radius);

		Material knowledgeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // create a simple
																										// material
		BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

		knowledgeMaterial.setColor("Color", color); // set color of material to blue
		int i = 0;
		for (stellarObject dataObi : ElementsInHorizon) {
			BigBall obi = new BigBall(dataObi.coords, 1, knowledgeMaterial, font, dataObi.name, cam, rootNode);
			i++;
			if (i > MAX)
				break;
		}
	}

	@Override
	public void simpleInitApp() {

		// adds the Physics state to the application, sets the speed and global
		// gravity variable.

		stateManager.attach(physicsState);
		physicsState.setSpeed(5f);

		ScreenshotAppState screenShotState = new ScreenshotAppState();
		stateManager.attach(screenShotState);

		flyCam.setEnabled(true);
		flyCam.setMoveSpeed(200);
		cam.setLocation(new Vector3f(0, 0f, -345f));
		cam.lookAtDirection(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f));
		// viewPort.setBackgroundColor(ColorRGBA.White);
		float fov = 45;
		float near = 250.0f;
		float far = 100000f;
		float aspect = (float) cam.getWidth() / cam.getHeight();
		cam.setFrustumPerspective(fov, aspect, near, far);

		addWordGroup("anti", ColorRGBA.Orange);
		addWordGroup("contra", ColorRGBA.Magenta);
		addWordGroup("non", ColorRGBA.Red);
		addWordGroup("real", ColorRGBA.Blue);

		addWordRelation("antonym", ColorRGBA.White);

		// rootNode.rotateUpTo(cam.getDirection());

		List<stellarObject> ElementsInHorizon = cosmic_dao.findByName("dr");

		stellarObject lastObi = ElementsInHorizon.get(0);

		cam.lookAtDirection(new Vector3f(0f, 0f, 0f), new Vector3f((float) lastObi.coords.getX(),
				(float) lastObi.coords.getY(), (float) lastObi.coords.getZ()));

		inputManager.addMapping("more", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(actionListener, "more");
	}

	private final ActionListener actionListener = new ActionListener() {
		@Override
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("more") && !keyPressed) {
				addRealm(cam.getLocation(), 30000.5f, ColorRGBA.Green);

			}
		}
	};
}