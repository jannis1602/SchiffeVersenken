package pack;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JFrame;

import Image.BufferedImageLoader;
import Sound.SoundPlayer;
import input.KeyInput;
import input.MouseInput;
import pack.TcpServer.Modus;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final int version = 201212;
	static Properties props;

	public static Game game;
	public static TcpServer tcpserver;

	public static String userName = null;
	public static JFrame frame;
	public static boolean running = false;
	private int newframes = 0;
	private Thread thread;
	public static Console console = null;

	public static boolean playerOneReady = false, playerTwoReady = false;
	public static State state;

	private static int size = 10;// TODO ???
	public static int[] anzahlSchiffe;
	public static Map mapPlayerOne;
	public static Map mapPlayerTwo;
	public static Singleplayer singleplayer;
	public static VorbereitungsMenue vorbereitungsMenue;
	private MouseInput mouseInput;
	private KeyInput keyInput;

	private LinkedList<Schiff> startSchiffe;
	public static boolean ende = false;

	SoundPlayer backgroundMusic;

	// images
//	private BufferedImage jetImage = null;
	public static Jet jet;

	public Game(int kacheln, int[] anzahlschiffe) {
		size = kacheln;
		mapPlayerOne = new Map(size, size);// startSchiffe
		mapPlayerTwo = new Map(size, size);
		if (tcpserver.modus == Modus.singleplayer) {
			Game.mapPlayerTwo.showSchiffe = false;
			Game.mapPlayerTwo.schiffe = singleplayer.schiffe;
		}
		anzahlSchiffe = new int[6]; // wird nicht gebraucht?
		anzahlSchiffe[0] = 0;
		anzahlSchiffe[1] = 0;
		anzahlSchiffe[2] = 1;
		anzahlSchiffe[3] = 3;
		anzahlSchiffe[4] = 2;
		anzahlSchiffe[5] = 1;
		if (anzahlschiffe != null)
			anzahlSchiffe = anzahlschiffe;
	}

	private void init() {

		BufferedImageLoader loader = new BufferedImageLoader();
		jet = new Jet(loader.loadImage("jet.png"), loader.loadImage("atombombe.png"));
//		backgroundMusic = new SoundPlayer("DrunkenSailor-Spongebob.wav", true); // TODO AUSWAHL!!!
		state = State.vorbereitung;
		mouseInput = new MouseInput(this);
		addMouseListener(mouseInput);
		keyInput = new KeyInput(this);
		addKeyListener(keyInput);
		startSchiffe = new LinkedList<Schiff>();
		for (int i = 1; i < anzahlSchiffe.length; i++) {
			for (int ii = 0; ii < anzahlSchiffe[i]; ii++)
				startSchiffe.add(new Schiff(i));
		}
		vorbereitungsMenue = new VorbereitungsMenue(this, startSchiffe);
		frame.setVisible(true);
	}

	private synchronized void start() {
		if (running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		if (!running)
			return;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		init();
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 30.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				render();
				frames++;
				delta--;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer = System.currentTimeMillis();
				newframes = frames;
				frames = 0;
			}
		}
		stop();
	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
//		frame.getInsets().top;			//  Fenster Rand!  -------------------------------------------
		Graphics g = bs.getDrawGraphics();
//		Graphics2D g2d = (Graphics2D) g;

//		if (tcpserver.modus == Modus.singleplayer)
//			mapPlayerTwo.schiffe = singleplayer.schiffe;

		// hintergrund
		g.setColor(Color.gray);
		g.setColor(new Color(0, 105, 153));
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

		mapPlayerOne.render(g, 10, 20, frame.getHeight() / 4 * 3 - 20, frame.getHeight() / 4 * 3 - 20, size,
				State.player1);
		if (tcpserver.modus == Modus.singleplayer)
			mapPlayerTwo.schiffe = singleplayer.schiffe;
		mapPlayerTwo.render(g, frame.getHeight() / 4 * 3 + 20, 20, frame.getHeight() / 4 * 3 - 20,
				frame.getHeight() / 4 * 3 - 20, size, State.player2);

// Feld unten
		if (state == State.vorbereitung) {
			vorbereitungsMenue.render(g, 10, frame.getHeight() / 4 * 3, frame.getWidth() - 38,
					frame.getHeight() - frame.getHeight() / 4 * 3 - 45);

			Font newFont = new java.awt.Font("Dialog", 1, 16);
			g.setFont(newFont);
			g.setColor(Color.WHITE);
			g.drawString("FPS: " + newframes, 2, 16);
			g.drawString("ready:ENTER  -  drehen:R  -  nächstes Schiff:Tab", frame.getWidth() / 8, 16);
		}

//		if (showVersenktText) {
//			Font newFont = new java.awt.Font("Dialog", 1, 18);
//			g.setFont(newFont);
//			g.setColor(Color.ORANGE);
//			g.drawString("Schiff Versenkt!", frame.getWidth() / 7 * 3, 18);
//		}

		Font newFont = new java.awt.Font("Dialog", 1, 16);
		g.setFont(newFont);
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + newframes, 2, 16);
		if (!playerOneReady && playerTwoReady)
			g.drawString("Ready 2", 100, 16);
		if (playerOneReady && !playerTwoReady)
			g.drawString("Ready 1", 100, 16);

		if (state == State.gewonnen) {
			g.setColor(Color.ORANGE);
			g.setFont(new java.awt.Font("Dialog", 1, 50));
			Rectangle2D offs = g.getFontMetrics().getStringBounds("GEWONNEN!", 0, "GEWONNEN!".length(), getGraphics());
			g.drawString("GEWONNEN!", 10, (int) (frame.getHeight() - offs.getHeight()) / 2);
		}
		if (state == State.verloren) {
			g.setColor(Color.ORANGE);
			g.setFont(new java.awt.Font("Dialog", 1, 50));
			Rectangle2D offs = g.getFontMetrics().getStringBounds("VERLOREN!", 0, "VERLOREN!".length(), getGraphics());
			g.drawString("VERLOREN!", 10, (int) (frame.getHeight() - offs.getHeight()) / 2);
		}
		if (state == State.error) {
			g.setColor(Color.ORANGE);
			g.setFont(new java.awt.Font("Dialog", 1, 50));
			Rectangle2D offs = g.getFontMetrics().getStringBounds("error!", 0, "error!".length(), getGraphics());
			g.drawString("error!", 10, (int) (frame.getHeight() - offs.getHeight()) / 2);
		}
// BTNs rendern
//		Btn.renderBtns(g);

		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
//		try {	// TODO 
//			System.out.println(new BufferedTextReader().readTestFile("test.txt"));
//		} catch (Exception e) {
//		}
		// parameter für sp/mp/cl/se
		tcpserver = new TcpServer();
		// if (System.getProperty("os.name").toLowerCase().contains("windows")) {
		// System.out.println("Windows-System");
		// }

		// System.out.println(getPropety("time"));
		// setPropety("time", String.valueOf(System.currentTimeMillis()));
	}

	/**
	 * get propeties by name
	 */
	public static String getPropety(String propetyName) {
		if (props == null)
			props = new Properties();
		if (props.isEmpty())
			try {
				props.load(new FileInputStream(getJarExecutionDirectory() + "game.properties"));// game.props ?
			} catch (IOException e) {
				e.printStackTrace();
			}
		return props.getProperty(propetyName);
	}

	/**
	 * set propeties by name and store the string
	 */
	public static void setPropety(String propetyName, String propetyText) {
		if (props == null)
			props = new Properties();
		try {
			props.setProperty(propetyName, propetyText);
			props.store(new FileWriter(getJarExecutionDirectory() + "\\game.properties"), "properties file");
			// game.props ?
		} catch (Exception e) {
		}
	}

	public static String getUsername() {
		if (userName == null) {
			userName = ladeDatei(getJarExecutionDirectory() + "user.txt");
			System.out.println(userName);
		}
		try {
			if (userName == null || userName.length() < 2)
				return System.getProperty("user.name"); // länge
			else
				return userName;
		} catch (Exception e) {
			return "player";
		}
	}

	private static String ladeDatei(String datName) {
		File file = new File(datName);
		System.out.println("DateiName: " + datName);
		if (!file.canRead() || !file.isFile())
			return null;
		FileReader fr = null;
		int c;
		StringBuffer buff = new StringBuffer();
		try {
			fr = new FileReader(file);
			while ((c = fr.read()) != -1)
				buff.append((char) c);
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (buff.length() > 0)
			System.out.println("buffer: " + buff.toString());
		return buff.toString();
	}

	public static String getJarExecutionDirectory() {
		String jarFile = null;
		String jarDirectory = null;
		int cutFileSeperator = 0;
		int cutSemicolon = -1;

		jarFile = System.getProperty("java.class.path");
		System.out.println(jarFile);
		cutFileSeperator = jarFile.lastIndexOf(System.getProperty("file.separator"));
		jarDirectory = jarFile.substring(0, cutFileSeperator);
		cutSemicolon = jarDirectory.lastIndexOf(';');
		jarDirectory = jarDirectory.substring(cutSemicolon + 1, jarDirectory.length());
		return jarDirectory + System.getProperty("file.separator");
	}

	public static int getFrameWidth() {
		return frame.getWidth();
	}

	public static int getFrameHeight() {
		return frame.getHeight();
	}

	public static void startGame(int kacheln, int[] anzahlschiffe) {
		// System.out.println(Toolkit.getDefaultToolkit().getScreenSize().width + " x "
		// + Toolkit.getDefaultToolkit().getScreenSize().height);
		// width = Toolkit.getDefaultToolkit().getScreenSize().width;
		// height = Toolkit.getDefaultToolkit().getScreenSize().height;
		game = new Game(kacheln, anzahlschiffe);
		frame = new JFrame("Schiffe Versenken - " + getUsername());
		// BufferedImageLoader iconload = new BufferedImageLoader();
		// Image frameicon = iconload.loadImage("GameMap10Level1.png");
		// frame.setIconImage(frameicon);
		frame.add(game);
		// frame.setCursor(Cursor.MOVE_CURSOR);
//		frame.setCursor(Cursor.WAIT_CURSOR);
//		frame.setCursor(Txtoptions.optionCursor);
//		frame.setCursor(Txtoptions.optionCursor);
		// Image customImage = iconload.loadImage("cursor2.png");
		// Cursor customCursor =
		// Toolkit.getDefaultToolkit().createCustomCursor(customImage, new Point(0, 0),
		// "customCursor");
		// frame.setCursor(customCursor);
		frame.setSize(1600 / 2, 900 / 2);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		frame.setResizable(true);
//		frame.setUndecorated(true);
		frame.setVisible(true);
		game.setFocusTraversalKeysEnabled(false);
		game.start();
	}

}
