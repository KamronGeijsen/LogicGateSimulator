package main.java;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import main.java.Debug.TimeWaypoint;
import main.java.GUI.Debug;
import main.java.GUI.Hotbar;
import main.java.GUI.SmallNotification;
import main.java.GUI.Updatespeed;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;

	static int GridSize = (int) Math.pow(1.75, 7);

	static int WIDTH = 800, HEIGHT = 500;
	static int x = WIDTH / 2, y = HEIGHT / 2;

	static boolean FullScreen;
	static boolean ToggleFullScreen;
	static boolean ExtendedScreen;
	static int sW, sH;
	static int sX, sY;
	static int lW = 800, lH = 500;

	static volatile boolean MainDone;
	static boolean Hand;
	static boolean Type;

	static long Sec;
	static long ExactSecond;

	static long UpdateStart;
	static long UpdateTime;

	static long FullUpdateStart;
	static long FullUpdateTime;

	static int TargetFPS = 30;
	static int FPS;
	static double ExactFPS;
	static long Frames;
	static long startFrame;
	static long FrameTime;
	static long FrameStart;

	static int TargetRPS = 60;
	static int RPS;
	static double ExactRPS;
	static long Refreshes;
	static long startRefresh;
	static long RefreshTime;
	static long RefreshStart;

	static int TargetImgUpdPS = 20;
	static long startImgUpd;

	static long LogicDelay;
	static double ExactUPS;
	static long Updates;
	static int LogicUpdatecount;
	static long maxSpeedDelay;
	static int skipped;

	static long ThreadStartTime;
	static long ThreadRunTime;
	static long AddedThreadRunTime;

	static long time, time2;
	static int addtime, addtime2;
	static boolean ThreadedUpdate = false;

	static TimeWaypoint FullUpdatedistr = new TimeWaypoint();
	static TimeWaypoint Framedistr = new TimeWaypoint();
	static TimeWaypoint LastFramedistr = new TimeWaypoint();

	public Main() {

		addKeyListener(new Controls.KA());
		addMouseListener(new Controls.MA());
		addMouseWheelListener(new Controls.MA());
		addMouseMotionListener(new Controls.MMA());
		addComponentListener(new CA());

		setFocusTraversalKeysEnabled(false);

		setTitle("Logics Gates Simulator");
		setSize(WIDTH, HEIGHT);
		setResizable(true);
		setVisible(true);

		try {
			setIconImage((Image) ImageIO.read(new File("src/img/InvDot.png")));
		} catch (Exception e) {

		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(new Color(100, 100, 100));
		setLocation(1920 / 2 - WIDTH / 2, 1080 / 2 - HEIGHT / 2);

		x = WIDTH / 2 - 8;
		y = HEIGHT / 2 - 19;

		ExactSecond = Sec = System.nanoTime() / 1000000000 * 1000000000;
		LastFramedistr.init();

		MainDone = true;
	}

	class CA extends ComponentAdapter {
		CA() {
		}

		public void componentResized(ComponentEvent e) {
			WIDTH = getWidth() - (FullScreen ? 0 : 16);
			HEIGHT = getHeight() - (FullScreen ? 0 : 38);
			if (WIDTH != lW || HEIGHT != lH) {
				x += (WIDTH - lW + 8) / 2 - 8;
				y += (HEIGHT - lH + 19) / 2 - 19;

				Hotbar.x = Math.max((WIDTH - GUI.Hotbar.IDinslot.length * GUI.Hotbar.slotSize) / 2, 8);
				Hotbar.y = HEIGHT - GUI.Hotbar.slotSize;

				Debug.Updates.Translate(Main.WIDTH - Debug.Updates.dx, Main.HEIGHT - Debug.Updates.dy);
				Debug.MainTimeDistr.Translate(Main.WIDTH - Debug.MainTimeDistr.dx, Debug.Updates.y - Debug.MainTimeDistr.dy);
				Debug.logicTimeDistr.Translate(Main.WIDTH - Debug.logicTimeDistr.dx, Debug.MainTimeDistr.y - Debug.logicTimeDistr.dy);
				Debug.graphicsTimeDistr.Translate(Main.WIDTH - Debug.graphicsTimeDistr.dx, Debug.logicTimeDistr.y - Debug.graphicsTimeDistr.dy);

				SmallNotification.dx = SmallNotification.text.length() * 11 + 30;
				SmallNotification.x = (Main.WIDTH - SmallNotification.dx) / 2;
				Display.UpdateFieldImage = true;
				Display.fieldImg = new BufferedImage(Main.WIDTH, Main.HEIGHT, BufferedImage.TYPE_INT_RGB);

				Display.field = (Graphics2D) Display.fieldImg.getGraphics();
				Display.SCREEN = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				Display.g = (Graphics2D) Display.SCREEN.getGraphics();

				if (!FullScreen)
					GUI.Hotbar.y -= 8;
			}

			lW = WIDTH - 8;
			lH = HEIGHT - 19;

			Dot.DrawCoords();
		}

		public void componentMoved(ComponentEvent e) {

		}
	}

	void ToggleFullScreen() {
		FullScreen = !FullScreen;

		dispose();
		setUndecorated(FullScreen);
		setVisible(true);

		if (FullScreen) {
			ExtendedScreen = getExtendedState() == 6;
			if (!ExtendedScreen) {
				sW = WIDTH + 16;
				sH = HEIGHT + 38;
				sX = getX();
				sY = getY();
				setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
			}
		} else {
			setSize(sW, sH);
			setLocation(sX, sY);
			if (ExtendedScreen)
				setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		}
		Controls.UpdateField = true;
		ToggleFullScreen = false;
	}

	public void paint(Graphics g) {
		long constant = System.nanoTime();
		if (!MainDone) {
			repaint();
			return;
		}

		if (ToggleFullScreen)
			ToggleFullScreen();
		if (Hand)
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else if (Type)
			setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		else
			setCursor(Cursor.getDefaultCursor());

		ThreadedUpdateHandler();

		g.drawImage(Display.SCREEN, FullScreen ? 0 : 8, FullScreen ? 0 : 30, null);

		try {
			Thread.sleep(
					(int) (Math.max(0, Math.min(1000, 1000.0 / TargetFPS - (System.nanoTime() - constant) / 1000000))));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();

	}

	void ThreadedUpdateHandler() {
		long constant = UpdateStart = RefreshStart = System.nanoTime();
		FullUpdateTime = FullUpdateStart - constant;
		FullUpdateStart = constant;
		FullUpdatedistr.init("Sec");

		if (Sec < constant) {
			while (Sec < constant)
				Sec += 1000000000;

			FPS = (int) Frames;
			ExactFPS = 1000000000d / (constant - ExactSecond) * Frames;
			Frames = 0;

			RPS = (int) Refreshes;
			ExactRPS = 1000000000d / (constant - ExactSecond) * Refreshes;
			Refreshes = 0;

			ExactUPS = 1000000000d / (constant - ExactSecond) * Updates;
			Updates = 0;

			ExactSecond = constant;

			Debug.MainTimeDistr.Add(2, 100);
			Debug.MainTimeDistr.Add(1, Math.round((Main.addtime) / 1000000d) / 10d);
			Debug.MainTimeDistr.Add(0, Math.round(Main.addtime2 / 1000000d) / 10d);
			Debug.MainTimeDistr.UpdatedrawGraph();

			Debug.logicTimeDistr.Add(0, Math.round((AddedThreadRunTime) / 1000000d) / 10d);
			Debug.logicTimeDistr.Add(1, 100);
			Debug.logicTimeDistr.UpdatedrawGraph();
			AddedThreadRunTime = 0;

			addtime = 0;
			addtime2 = 0;
		}
		FullUpdatedistr.add("threadStart");

		long temptime2 = constant = System.nanoTime();

		Debug.logicTimeDistr.visible = ThreadedUpdate = Updatespeed.maxSpeed;
		if (Updatespeed.maxSpeed) {
			if (!logic.isAlive())
				logic.start();
		} else if (GUI.Updatespeed.on && LogicDelay < constant) {
			do {
				LogicDelay += Updatespeed.Delay;
				LogicUpdate.safeUpdate();
				Controls.UpdateField = true;
				LogicUpdatecount++;
			} while (LogicDelay < constant);
			LogicUpdate.updateGraphics();
		}

		time2 = System.nanoTime() - temptime2;
		addtime2 += time2;

		constant = System.nanoTime();

		FullUpdatedistr.add("UpdateImg");
		//TODO only update when image changed. Defaulted to always update (also when idle) 
//		if (startImgUpd < constant) {
//			while (startImgUpd < constant)
//				startImgUpd += 1000000000 / TargetImgUpdPS;
//			Dot.UpdateImg();
//		}
		
		FullUpdatedistr.add("startFrame");
		if (startFrame < constant) {
			FrameTime = constant - FrameStart;
			long temptime = FrameStart = constant;

			Framedistr.init("UpdateField");

			skipped = 0;
			for (; startFrame < constant; startFrame += 1000000000 / TargetFPS)
				skipped++;

			// LogicUpdate.UpdateLamps(); TODO
			Display.UpdateField();

			Display.g.drawImage(Display.fieldImg, 0, 0, null);

			Framedistr.add("drawScreen");

			Display.drawScreen();

			Framedistr.terminate();
			LastFramedistr.waypoints = Framedistr.waypoints;

			Updates += LogicUpdatecount;
			LogicUpdatecount = 0;
			Frames++;

			time = System.nanoTime() - temptime;
			addtime += time;

		}

		Refreshes++;
		RefreshTime = System.nanoTime() - RefreshStart;
		UpdateStart = System.nanoTime();
	}

	final static LogicThread logic = new LogicThread();

	static class LogicThread implements Runnable {
		private volatile boolean requestInterrupt = false;
		Thread thread;

		public boolean isAlive() {
			if (thread == null)
				return false;
			return thread != null;
		}

		public void start() {
			thread = new Thread(this);
			thread.start();
		}

		public void terminate() {
			if (thread == null)
				return;
			requestInterrupt = true;
		}

		public void terminateWait() {
			if (thread == null)
				return;
			requestInterrupt = true;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			ThreadStartTime = System.nanoTime();
//			LogicUpdate.createLogic();
//			LogicUpdate.setupQuick();
			int i = 1;
			while (ThreadedUpdate) {
				LogicUpdate.safeUpdate();
//				LogicUpdate.quickUpdate();
				i++;
				if (requestInterrupt)
					break;
			}
//			LogicUpdate.stopQuick();
			LogicUpdate.updateGraphics();

			// LogicUpdate.LogicToField();
			LogicUpdatecount = i;
			Updates += i;
			requestInterrupt = false;
			Controls.UpdateField = true;
			ThreadRunTime = System.nanoTime() - ThreadStartTime;
			AddedThreadRunTime += ThreadRunTime;
			thread = null;
		}

	}

	static int fixCoords(int i) {
		return i - (((i % (int) GridSize) + (int) GridSize) % (int) GridSize);
	}

	static int fixGridCoords(int i) {
		return fixCoords(i) / GridSize;
	}

	static int fixCoords(int i, int c) {
		return i - (((i % c) + c) % c);
	}

	static int xSide(int s) {
		if (s == 1)
			return -1;
		if (s == 3)
			return 1;
		return 0;
	}

	static int ySide(int s) {
		if (s == 0)
			return -1;
		if (s == 2)
			return 1;
		return 0;
	}

	static boolean CloseCoord(int Cx, int Cy, int DistanceFrom) {
		return (Math.sqrt((Cx - Controls.x - x) * (Cx - Controls.x - x)
				+ (Cy - Controls.y - y) * (Cy - Controls.y - y)) < DistanceFrom);
	}

	static double DistanceToCoord(int Cx, int Cy) {
		return Math.sqrt((Cx - Controls.x - x) * (Cx - Controls.x - x) + (Cy - Controls.y - y) * (Cy - Controls.y - y));
	}

	public static void main(String[] args) {
		new Main();

	}
}
