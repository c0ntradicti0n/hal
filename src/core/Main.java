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
import java.util.logging.Logger;

import org.neo4j.driver.internal.shaded.io.netty.util.internal.logging.Log4J2LoggerFactory;

import com.aurellem.capture.Capture;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
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
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.opencsv.bean.CsvToBeanBuilder;

import dao.stellarObjectNeo4jDAO;
import jMonkeyGod.BigBall;
import jMonkeyGod.StellarPoint;
import model.ClusterCenter;

import model.stellarObject;
import record.AbstractVideoRecorder;
import record.FileVideoRecorder;
import record.IsoTimer;
import record.XuggleVideoRecorder;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;

public class Main extends SimpleApplication {
	private  Logger jlog =  Logger.getLogger("Main");
	static String kind = "tsne";
	static int MAX = 600000;
	static stellarObjectNeo4jDAO cosmic_dao = new stellarObjectNeo4jDAO(kind, MAX);
	private static Main app;
	private BulletAppState physicsState = new BulletAppState();
	boolean fly_or_move = true;


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

		app = new Main();

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
		//AbstractVideoRecorder videoRecorder = new FileVideoRecorder(file);
		// if (file.getCanonicalPath().endsWith(".avi")) {
		// videoRecorder = new AVIVideoRecorder(file);
		// } else if (file.isDirectory()) {
		// videoRecorder = new FileVideoRecorder(file);
		// }

		Callable<Object> thunk = new Callable<Object>() {
			public Object call() {
				ViewPort viewPort = app.getRenderManager().createPostView("aurellem record", app.getCamera());
				viewPort.setClearFlags(false, false, false);

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

	public void addRealm(Vector3f rPos, float radius) {
		StellarPoint local_pos = new StellarPoint(rPos);
		List<stellarObject> ElementsInHorizon = cosmic_dao.findByPosition(local_pos, radius);

		// material
		BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");

		int i = 0;
		for (stellarObject dataObi : ElementsInHorizon) {
			Material knowledgeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // create a
																											// simple

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
		stateManager.attach(physicsState);
		
		// screenshot machine, press KeyInput.KEY_SYSRQ, "Druck"
		ScreenshotAppState screenShotState = new ScreenshotAppState();
		stateManager.attach(screenShotState);

		// view
		viewPort.setBackgroundColor(ColorRGBA.White);
		float fov = 45;
		float near = 50.0f;
		float far = 200000f;
		float aspect = (float) cam.getWidth() / cam.getHeight();
		cam.setFrustumPerspective(fov, aspect, near, far);

		// add some things to universe
		addWordGroup("anti", ColorRGBA.Orange);
		addWordGroup("contra", ColorRGBA.Magenta);
		addWordGroup("non", ColorRGBA.Red);
		addWordGroup("real", ColorRGBA.Blue);
		addWordRelation("antonym", ColorRGBA.DarkGray);
		List<stellarObject> ElementsInHorizon = cosmic_dao.findByName("dr");

		// pcaCl = readClusterCenters("data/pca_clusters_mean_points.csv");
		// tsneCl = readClusterCenters("data/tsne_clusters_mean_points.csv");
		// k2Cl = readClusterCenters("data/k2_clusters_mean_points.csv");
		knCl = readClusterCenters("data/kn_clusters_mean_points.csv");

		for (int i = 0; i < knCl.size() + 1; i++) {
			// cl==-1 is for outliers
			clusterColors.add(ColorRGBA.randomColor());
		}
		clusterColors.add(0, ColorRGBA.Black);

		// robot camera man
		cinematic = new Cinematic(rootNode, 1000f);
		stateManager.attach(cinematic);
		createCameraMotion();
		cinematic.activateCamera(0, "topView");

		// hotkeys
		inputManager.addMapping("more", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(actionListener, "more");
		inputManager.addMapping("cam", new KeyTrigger(KeyInput.KEY_C));
		inputManager.addListener(actionListener, "cam");
		inputManager.addMapping("exit", new KeyTrigger(KeyInput.KEY_X));
		inputManager.addListener(actionListener, "exit");
		inputManager.addMapping("moremax", new KeyTrigger(KeyInput.KEY_EQUALS));
		inputManager.addListener(actionListener, "moremax");
		inputManager.addMapping("lessmax", new KeyTrigger(KeyInput.KEY_MINUS));
		inputManager.addListener(actionListener, "lessmax");
		addRealm(cam.getLocation(), 600000.5f);

		cameraMotionControl.stop();
		camNode.setEnabled(false);
		flyCam.setEnabled(true);
		flyCam.setMoveSpeed(200);
		cam.setLocation(new Vector3f(0, 0f, -345f));
		cam.lookAtDirection(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f));
		
		switch_cam();

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
			if (name.equals("moremax") && !keyPressed) {				
				MAX *= 1.3;
				jlog.info("maxmimum number of loaded elements is now " + MAX);
				stellarObjectNeo4jDAO.setLimit(MAX); 
			}
			if (name.equals("lessmax") && !keyPressed) {
				MAX /= 1.5;
				jlog.info("maxmimum number of loaded elements is now " + MAX);
				stellarObjectNeo4jDAO.setLimit(MAX); 
			}
			if (name.equals("more") && !keyPressed) {
				jlog.info("retrieving more data around actual position");
				addRealm(cam.getLocation(), 30000.5f);

			}
			if (name.equals("cam") && !keyPressed) {
				jlog.info("switching cam");
				switch_cam();

			}
			if (name.equals("exit") && !keyPressed) {
				jlog.info("exiting");
				app.stop(false);
		        System.exit(0);
			}
		}

	};

	private void switch_cam() {
		if (fly_or_move) {

			flyCam.setEnabled(false);

			camNode.setEnabled(true);

			cameraMotionControl.setSpeed(1);
			cameraMotionControl.play();
			fly_or_move = false;
		}

		else {

			cameraMotionControl.stop();
			camNode.setEnabled(false);
			flyCam.setEnabled(true);
			flyCam.setMoveSpeed(200);
			cam.setLocation(new Vector3f(0, 0f, 0f));
			cam.lookAtDirection(new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f));

			fly_or_move = true;

		}

	}

	private MotionPath path;
	private CameraNode camNode;
	private MotionEvent cameraMotionControl;

	private void createCameraMotion() {
		cam.setLocation(new Vector3f(0f,0f,0f));
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

		path.setCurveTension(0.5f);
		// path.enableDebugShape(assetManager, rootNode);

		cameraMotionControl = new MotionEvent(camNode, path);
		cameraMotionControl.setSpeed(1);

		cameraMotionControl.setLoopMode(LoopMode.Loop);
		cameraMotionControl.setInitialDuration(1500f);
		//cameraMotionControl.setLookAt(rootNode.getWorldTranslation(), Vector3f.UNIT_Y);
		cameraMotionControl.setDirectionType(MotionEvent.Direction.Path);

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