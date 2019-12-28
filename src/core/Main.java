package core;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import com.aurellem.capture.Capture;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.cinematic.Cinematic;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.opencsv.bean.CsvToBeanBuilder;

import dao.stellarObjectNeo4jDAO;
import jMonkeyGod.BigBall;
import jMonkeyGod.StellarPoint;
import model.ClusterCenter;

import model.stellarObject;
import record.AVIVideoRecorder;
import record.AbstractVideoRecorder;
import record.FileVideoRecorder;
import record.IsoTimer;
import record.XuggleVideoRecorder;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;

public class Main extends SimpleApplication {
	static String kind = "tsne";
	static stellarObjectNeo4jDAO cosmic_dao = new stellarObjectNeo4jDAO(kind);
	private BulletAppState physicsState = new BulletAppState();

	ArrayList<Geometry> stars = new ArrayList<>();
	ArrayList<Material> materials = new ArrayList<>();
	ArrayList<BigBall> balls = new ArrayList<>();
	private Cinematic cinematic;
	private List<ClusterCenter> pcaCl;
	private List<ClusterCenter> tsneCl;
	private List<ClusterCenter> k2Cl;
	private List<ClusterCenter> knCl;
	private ArrayList<ColorRGBA> clusterColors = new ArrayList<ColorRGBA>();

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

		app.setTimer(new IsoTimer(30));
		try {
			Capture.captureVideo(app, new File("videos/record.mp4"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		app.setSettings(sets);
		app.start(); // start the game
	}

	static AbstractVideoRecorder videoRecorder = null;

	public static void captureVideo(final Application app, final File file) throws IOException {
		AbstractVideoRecorder videoRecorder = new XuggleVideoRecorder(file);
		// if (file.getCanonicalPath().endsWith(".avi")) {
		// videoRecorder = new AVIVideoRecorder(file);
		// } else if (file.isDirectory()) {
		// videoRecorder = new FileVideoRecorder(file);
		// }

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
			new BigBall(dataObi.coords, 2, knowledgeMaterial, font, dataObi.name, cam, rootNode);
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

	public void addRealm(Vector3f rPos, float radius) {
		StellarPoint local_pos = new StellarPoint(rPos);
		List<stellarObject> ElementsInHorizon = cosmic_dao.findByPosition(local_pos, radius);

		Material knowledgeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // create a simple
																										// material
		BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

		int i = 0;
		for (stellarObject dataObi : ElementsInHorizon) {
			knowledgeMaterial.setColor("Color", clusterColors.get(dataObi.cl_kn + 1));
			// System.out.println(clusterColors.get(dataObi.cl_kn +1).toString() + " <- " +
			// (dataObi.cl_kn +1));
			new BigBall(dataObi.coords, 1, knowledgeMaterial, font, dataObi.name, cam, rootNode);
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

		viewPort.setBackgroundColor(ColorRGBA.White);
		float fov = 45;
		float near = 50.0f;
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

		ElementsInHorizon.get(0);

		// pcaCl = readClusterCenters("data/pca_clusters_mean_points.csv");
		// tsneCl = readClusterCenters("data/tsne_clusters_mean_points.csv");
		// k2Cl = readClusterCenters("data/k2_clusters_mean_points.csv");
		knCl = readClusterCenters("data/kn_clusters_mean_points.csv");

		for (int i = 0; i < knCl.size() + 2; i++) {
			// cl==-1 is for outliers
			clusterColors.add(ColorRGBA.randomColor());
		}

		cinematic = new Cinematic(rootNode, 50);
		stateManager.attach(cinematic);
		createCameraMotion();
		cinematic.activateCamera(0, "topView");

		// cam.lookAtDirection(new Vector3f(0f, 0f, 0f), new Vector3f((float)
		// lastObi.coords.getX(),
		// (float) lastObi.coords.getY(), (float) lastObi.coords.getZ()));

		inputManager.addMapping("more", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(actionListener, "more");
		addRealm(cam.getLocation(), 300000.5f);
		
		flyCam.setEnabled(true);
		 flyCam.setMoveSpeed(200);
		 cam.setLocation(new Vector3f(0, 0f, -345f));
		 cam.lookAtDirection(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f));
		

	}

	private List<ClusterCenter> readClusterCenters(String path) {
		new File(path);
		List<ClusterCenter> beans = null;
		try {
			beans = new CsvToBeanBuilder(new FileReader(path)).withSeparator('\t').withType(ClusterCenter.class).build()
					.parse();
		} catch (IllegalStateException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return beans;
	}

	private final ActionListener actionListener = new ActionListener() {
		@Override
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("more") && !keyPressed) {
				addRealm(cam.getLocation(), 30000.5f);

			}
		}
	};
	private MotionPath path;
	private CameraNode camNode;
	private MotionEvent cameraMotionControl;

	private void createCameraMotion() {

		// CameraNode camNode = cinematic.bindCamera("topView", cam);

		cam.setLocation(new Vector3f(8.4399185f, 11.189463f, 14.267577f));
		camNode = new CameraNode("Motion cam", cam);
		camNode.setControlDir(ControlDirection.SpatialToCamera);
		camNode.setEnabled(false);
		path = new MotionPath();
		path.setCycle(true);
		for (ClusterCenter cl : knCl) {
			Vector3f sP = (new StellarPoint(cl.getPointKind(kind)).getVector());
			System.out.println(sP);

			path.addWayPoint(sP);
		}
		System.out.println(knCl);

		path.setCurveTension(1.83f);
		path.enableDebugShape(assetManager, rootNode);

		cameraMotionControl = new MotionEvent(camNode, path);
		cameraMotionControl.setLoopMode(LoopMode.Loop);
		// cameraMotionControl.setDuration(15f);
		cameraMotionControl.setLookAt(rootNode.getWorldTranslation(), Vector3f.UNIT_Y);
		cameraMotionControl.setDirectionType(MotionEvent.Direction.LookAt);

		rootNode.attachChild(camNode);

		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		final BitmapText wayPointsText = new BitmapText(guiFont, false);
		wayPointsText.setSize(guiFont.getCharSet().getRenderedSize());

		guiNode.attachChild(wayPointsText);

		path.addListener(new MotionPathListener() {

			public void onWayPointReach(MotionEvent control, int wayPointIndex) {
				if (path.getNbWayPoints() == wayPointIndex + 1) {
					wayPointsText.setText(control.getSpatial().getName() + " Finish!!! ");
				} else {
					wayPointsText.setText(control.getSpatial().getName() + " Reached way point " + wayPointIndex);
				}
				wayPointsText.setLocalTranslation((cam.getWidth() - wayPointsText.getLineWidth()) / 2, cam.getHeight(),
						0);
			}
		});
		flyCam.setEnabled(false);
		camNode.setEnabled(true);
		cameraMotionControl.play();
	}

}