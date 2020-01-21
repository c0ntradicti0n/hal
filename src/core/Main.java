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

import com.jme3.app.StatsAppState;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
import com.jme3.cinematic.events.AbstractCinematicEvent;
import com.jme3.cinematic.events.CinematicEvent;
import com.jme3.cinematic.events.CinematicEventListener;
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
import com.jme3.system.JmeContext;
import com.opencsv.bean.CsvToBeanBuilder;

import dao.stellarObjectCSVDAO;
import dao.stellarObjectDAO;
import dao.stellarObjectNeo4jDAO;
import jMonkeyGod.BigBall;
import jMonkeyGod.StellarPoint;
import model.CSVPoint;
import model.ClusterCenter;

import model.stellarObject;
import record.AbstractVideoRecorder;
import record.FileVideoRecorder;
import record.IsoTimer;
import record.XuggleVideoRecorder;
import util.CsvReader;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;

public class Main extends SimpleApplication {
	private static Logger jlog = Logger.getLogger("Main");
	static String kind = "tsne";
	static volatile int MAX;
	static stellarObjectDAO cosmic_dao;
	private static Main app;
	private BulletAppState physicsState = new BulletAppState();
	boolean fly_or_move = true;

	ArrayList<Geometry> stars = new ArrayList<>();
	ArrayList<Material> materials = new ArrayList<>();
	ArrayList<BigBall> balls = new ArrayList<>();
	private List<ClusterCenter> pcaCl;
	private List<ClusterCenter> tsneCl;
	private List<ClusterCenter> k2Cl;
	private static List<ClusterCenter> knCl;
	private ArrayList<ColorRGBA> clusterColors = new ArrayList<ColorRGBA>();
	private static CommandLine cmd;
	private static boolean useCsv;
	private static List<ClusterCenter> colors_by;
	private static List<ClusterCenter> path_by;
	private static Float velocity;
	public static int UNIVERSE_ZOOM = 350;
	private Cinematic cinematic;
	private MotionPath path;
	private CameraNode camNode;
	private MotionEvent cameraMotionControl;


	public static void main(String[] args) {
		Options options = new Options();
		Option colors_opt = new Option("c", "colors", true, "colors");
		Option path_opt = new Option("p", "path", true, "autopilot path");
		Option velocity_opt = new Option("v", "velocity", true, "mean time per path point");
		Option headless_opt = new Option("h", "headless", true, "mean time per path point");
		Option input = new Option("a", "all", true, "universe coordinates csv path");
		Option max_opt = new Option("m", "max", true, "max number of entities to show");
		Option density_opt = new Option("d", "density", true, "density of entities to show, human size normalisation of your coordinates");

		options.addOption(input);
		options.addOption(colors_opt);
		options.addOption(path_opt);
		options.addOption(velocity_opt);
		options.addOption(headless_opt);
		options.addOption(max_opt);
		options.addOption(density_opt);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);
			System.exit(1);
		}

//        if (!cmd.hasOption("h")) {
//			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//			int w = gd.getDisplayMode().getWidth();
//			int h = gd.getDisplayMode().getHeight();
//
//			System.out.println("w,h: " + w + "," + h);
//		}

		AppSettings settings = new AppSettings(true);
		settings.put("Width", 2500);
		settings.put("Height", 1800);
		settings.put("Title", "hal");
		settings.put("VSync", true);
		settings.put("Samples", 1);

		Main app = new Main();
		app.setSettings(settings);
		try {
			Capture.captureVideo(app, new File("record.mp4"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		app.setTimer(new IsoTimer(30));


		String colors_by_path =  "data/kn_clusters_mean_points.csv";
		if (cmd.hasOption("colors")) {
		 jlog.info("Colors loading: " + cmd.getOptionValue("colors"));
			colors_by_path = cmd.getOptionValue("colors");
		}
		colors_by = CsvReader.readClusterCenters(colors_by_path);

		String path_by_path = "data/kn_clusters_mean_points.csv";
		if (cmd.hasOption("path")) {
			jlog.info("Path loading " + cmd.getOptionValue("path"));
			path_by_path =  cmd.getOptionValue("path");
		}
		path_by = CsvReader.readClusterCenters(path_by_path);

		velocity = 1f;
		if (cmd.hasOption("velocity")) {
			velocity = Float.valueOf(cmd.getOptionValue("velocity"));
		}
		MAX = 10000;
		if (cmd.hasOption("max")) {
			MAX = Integer.valueOf(cmd.getOptionValue("max"));
		}
		if (cmd.hasOption("density")) {
			UNIVERSE_ZOOM = Integer.valueOf(cmd.getOptionValue("density"));
		}

		if (cmd.getOptionValue("all") != null) {
			cosmic_dao = new stellarObjectCSVDAO(kind, MAX, cmd.getOptionValue("all"));
			useCsv = true;
		} else {
			cosmic_dao = new stellarObjectNeo4jDAO(kind, MAX);
		}

		if (cmd.hasOption("h")) {

     			settings.setRenderer(AppSettings.JOGL_OPENGL_BACKWARD_COMPATIBLE);

				app.setSettings(settings);
				app.setShowSettings(false);
				app.start(JmeContext.Type.OffscreenSurface);

		} else {
			app.start(); // start the game
		}
	}

	static AbstractVideoRecorder videoRecorder = null;

	public static void captureVideo(final Application app, final File file) throws IOException {
		AbstractVideoRecorder videoRecorder = new XuggleVideoRecorder(file);
		// AbstractVideoRecorder videoRecorder = new FileVideoRecorder(file);
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
		/*if (!useCsv) {
			addWordGroup("anti", ColorRGBA.Orange);
			addWordGroup("contra", ColorRGBA.Magenta);
			addWordGroup("non", ColorRGBA.Red);
			addWordGroup("real", ColorRGBA.Blue);
			addWordRelation("antonym", ColorRGBA.DarkGray);
			List<stellarObject> ElementsInHorizon = cosmic_dao.findByName("dr");
		}*/

		// pcaCl = readClusterCenters("data/pca_clusters_mean_points.csv");
		// tsneCl = readClusterCenters("data/tsne_clusters_mean_points.csv");
		// k2Cl = readClusterCenters("data/k2_clusters_mean_points.csv");


		for (int i = 0; i < path_by.size() + 5; i++) {
			// cl==-1 is for outliers
			clusterColors.add(ColorRGBA.randomColor());
		}
		clusterColors.add(0, ColorRGBA.Black);

		// robot camera man
		cinematic = new Cinematic(rootNode, 4f);
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

	private final ActionListener actionListener = new ActionListener() {
		@Override
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("moremax") && !keyPressed) {
				MAX *= 1.3;
				jlog.info("maxmimum number of loaded elements is now " + MAX);
				cosmic_dao.setLimit(MAX);
			}
			if (name.equals("lessmax") && !keyPressed) {
				MAX /= 1.5;
				jlog.info("maxmimum number of loaded elements is now " + MAX);
				cosmic_dao.setLimit(MAX);
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
				shutdown();
			}
		}

	};

	private void shutdown() {
		Capture.videoRecorder.finish();
		super.stop();
		this.stop();

		try {
		app.stop();
		}
		catch (NullPointerException e ) {
			System.out.println("shut down succesfull... :)");
		}
		finally {
			System.exit(0);
		}
	}

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

	private void createCameraMotion() {
		cam.setLocation(new Vector3f(0f, 0f, 0f));
		camNode = new CameraNode("Motion cam", cam);
		camNode.setControlDir(ControlDirection.SpatialToCamera);
		camNode.setEnabled(false);
		path = new MotionPath();
		path.setCycle(true);
		for (ClusterCenter cl : colors_by) {
			Vector3f sP = (new StellarPoint(cl.getPointKind(kind)).getVector());
			System.out.println(sP);

			path.addWayPoint(sP);
		}

		path.setCurveTension(0.5f);
		// path.enableDebugShape(assetManager, rootNode);

		cameraMotionControl = new MotionEvent(camNode, path);
		cameraMotionControl.setSpeed(1);

		cameraMotionControl.setLoopMode(LoopMode.DontLoop);
		System.out.print ( path_by.size());
		System.out.print ( velocity);

		cameraMotionControl.setInitialDuration(velocity * path_by.size());
		// cameraMotionControl.setLookAt(rootNode.getWorldTranslation(),
		// Vector3f.UNIT_Y);
		cameraMotionControl.setDirectionType(MotionEvent.Direction.Path);

		rootNode.attachChild(camNode);

		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		final BitmapText wayPointsText = new BitmapText(guiFont, false);
		wayPointsText.setSize(guiFont.getCharSet().getRenderedSize());

		guiNode.attachChild(wayPointsText);


		path.addListener(new MotionPathListener() {

			public void onWayPointReach(MotionEvent control, int wayPointIndex) {
				if (wayPointIndex >= path_by.size()/2) {
					System.out.println("stopping " +  wayPointIndex + "/" +( path_by.size()));
					shutdown();

				}
				System.out.println("travelling " +  wayPointIndex + "/" +( path_by.size()-1));
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