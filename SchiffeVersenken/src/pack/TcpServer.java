package pack;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import Image.BufferedImageLoader;

public class TcpServer {
	private ServerSocket server;
	Socket client = null;
	Socket clientServer = null;
	private UpdateMessage updateMsg;
	public boolean hacks = false;
	private Point point = new Point();

	private JFrame frame, frameWait, mpFrame, serverSettingsFrame;
	private JButton btnCoop, btnSingleplayer, btnMulitplayer, btnOptions, btnName;

	private int[] anzahlSchiffe;
	private int kacheln;
	@SuppressWarnings("unused")
	private boolean nachTrefferErneut = false;
	@SuppressWarnings("unused")
	private LinkedList<String> playerList = null;
	private JComboBox<String> auswahl;

	private Color btnColor = new Color(22, 145, 201);
	private Color btnColorFocus = new Color(19, 117, 161);// 20, 131, 181);
	public Modus modus = null;
	public boolean inGame = false;

	private MouseListener maBtnColor;

	public enum Modus {
		server, client, multiplayer, singleplayer
	}

	public TcpServer() {
		frame = new JFrame("Schiffe Versenken");
		frame.setBounds(100, 100, 280, 340); // 344 bei Decoration
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setLayout(new BorderLayout());

		anzahlSchiffe = new int[6];
		anzahlSchiffe[0] = 0;
		anzahlSchiffe[1] = 0;
		anzahlSchiffe[2] = 1;
		anzahlSchiffe[3] = 3;
		anzahlSchiffe[4] = 2;
		anzahlSchiffe[5] = 1;
		kacheln = 10;

		JPanel tbPanel = new JPanel();
		tbPanel.setSize(250, 20);
		tbPanel.setLayout(new BorderLayout());
		tbPanel.setBackground(Color.DARK_GRAY);
		tbPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				point.x = e.getX();
				point.y = e.getY();
			}
		});
		tbPanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Point p = frame.getLocation();
				frame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
			}
		});

		JLabel lblTitle = new JLabel(" Schiffe Versenken ");
		lblTitle.setForeground(Color.BLACK);
//		lblTitle.setBorder(new LineBorder(Color.BLACK));
		tbPanel.add(lblTitle, BorderLayout.WEST);

		JPanel frameMenuPanel = new JPanel();
		frameMenuPanel.setLayout(new GridLayout(1, 2));
		frameMenuPanel.setSize(20, 20);

// MouseListener
		MouseListener maTopBarBtnColor = new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent arg0) {
				JButton tempBtn = (JButton) arg0.getSource();
				tempBtn.setBackground(Color.GRAY.darker());
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				JButton tempBtn = (JButton) arg0.getSource();
				tempBtn.setBackground(Color.DARK_GRAY);
			}
		};

		maBtnColor = new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent arg0) {
				JButton tempBtn = (JButton) arg0.getSource();
				tempBtn.setBackground(btnColorFocus);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				JButton tempBtn = (JButton) arg0.getSource();
				tempBtn.setBackground(btnColor);
			}
		};

// BTN TASKBAR
		JButton btnTaskbar = new JButton();
		ImageIcon ico2 = new ImageIcon(new BufferedImageLoader().loadImage("taskbar.png"));
		ico2.setImage(ico2.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
		btnTaskbar.setIcon(ico2);
		btnTaskbar.setPreferredSize(new Dimension(25, 25));
		btnTaskbar.setFocusPainted(false);
		btnTaskbar.setBackground(Color.DARK_GRAY);
		btnTaskbar.setBorderPainted(false);
		btnTaskbar.addMouseListener(maTopBarBtnColor);
		// TODO 1 actionlistener für alle mit switch case?
		btnTaskbar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.setState(Frame.ICONIFIED);
			}
		});
		frameMenuPanel.add(btnTaskbar);

// BTN EXIT
		JButton btnExit = new JButton();
		ImageIcon ico = new ImageIcon(new BufferedImageLoader().loadImage("exit.png"));
		ico.setImage(ico.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
		btnExit.setIcon(ico);
		btnExit.setPreferredSize(new Dimension(25, 25));
		btnExit.setFocusPainted(false);
		btnExit.setBackground(Color.DARK_GRAY);
		btnExit.setBorderPainted(false);
		btnExit.addMouseListener(maTopBarBtnColor);
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		frameMenuPanel.add(btnExit);

		tbPanel.add(frameMenuPanel, BorderLayout.EAST);
		frame.add(tbPanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(frame.getWidth(), 210);
		buttonPanel.setLayout(new java.awt.GridLayout(5, 1));
		buttonPanel.setBackground(Color.DARK_GRAY);

// SELECT GAMEMODE ----------

// BTN COOP-MODUS
		btnCoop = new JButton("Coop-Modus");
		btnCoop.setFont(new Font("Robot", Font.BOLD, 20));
		btnCoop.setBackground(btnColor);
		btnCoop.setForeground(Color.BLACK);
		btnCoop.setFocusPainted(false);
		btnCoop.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		btnCoop.addMouseListener(maBtnColor);
		btnCoop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Server");
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						Object[] choices = { "Server", "Client", "Cancel" };
						int res = JOptionPane.showOptionDialog(frame, "Server oder Client starten?", "Coop-Modus",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[2]);
						if (res == JOptionPane.CANCEL_OPTION || res == JOptionPane.CLOSED_OPTION)
							frame.setVisible(true);
						else if (res == JOptionPane.YES_OPTION) {
							System.out.println("server");
							modus = Modus.server;
							createSettingsFrame();
							// serverStart(6666);
						} else if (res == JOptionPane.NO_OPTION) {
							System.out.println("client");
							modus = Modus.client;
							createClientFrame();
						}

					}
				});
				thread.start();
				frame.setVisible(false);
			}
		});
		buttonPanel.add(btnCoop);

//BTN MULTIPLAYER
		btnMulitplayer = new JButton("Mulitplayer");
		btnMulitplayer.setFont(new Font("Robot", Font.BOLD, 20));
		btnMulitplayer.setBackground(btnColor);
		btnMulitplayer.setForeground(Color.BLACK);
		btnMulitplayer.setFocusPainted(false);
		btnMulitplayer.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		btnMulitplayer.addMouseListener(maBtnColor);
		btnMulitplayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Mulitplayer");

				String[] temp = getPrivateServerIP("https://superxy.jimdofree.com").split(":");
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						modus = Modus.multiplayer;
						// schiffeversenken-:2.tcp.ngrok.io:13729
						multiplayerClientStart(temp[1], Integer.valueOf(temp[2]));
					}
				});
				thread.start();
				frame.setVisible(false); // =0 null?

			}
		});
		buttonPanel.add(btnMulitplayer);

// BTN SINGLEPLAYER
		btnSingleplayer = new JButton("Singleplayer");
		btnSingleplayer.setFont(new Font("Robot", Font.BOLD, 20));
		btnSingleplayer.setBackground(btnColor);
		btnSingleplayer.setForeground(Color.BLACK);
		btnSingleplayer.setFocusPainted(false);
		btnSingleplayer.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		btnSingleplayer.addMouseListener(maBtnColor);
		btnSingleplayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Singleplayer");
//				JOptionPane.showMessageDialog(frame, "Ist noch in Arbeit...", "Achtung!", JOptionPane.WARNING_MESSAGE);

				int result = JOptionPane.showConfirmDialog(frame, "alpha Version \n" + "Ist noch in Arbeit...",
						"ACHTUNG!", JOptionPane.OK_CANCEL_OPTION);
//				if (result != JOptionPane.OK_OPTION)
//					return;
				modus = Modus.singleplayer;
				createSettingsFrame();
//				Thread thread = new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						modus = Modus.singleplayer;
//						LinkedList<Schiff> startSchiffe = new LinkedList<Schiff>();
//						for (int iLaenge = 0; iLaenge <= 5; iLaenge++)
//							for (int i = 0; i < anzahlSchiffe[iLaenge]; i++)
//								startSchiffe.add(new Schiff(iLaenge));
//						Game.singleplayer = new Singleplayer(kacheln, startSchiffe);
//						Game.startGame(kacheln, anzahlSchiffe);
//					}
//				});
//				thread.start();
				frame.setVisible(false);

			}
		});
		buttonPanel.add(btnSingleplayer);

//BTN OPTIONS
		btnOptions = new JButton("Options");
		btnOptions.setFont(new Font("Robot", Font.BOLD, 20));
		btnOptions.setBackground(btnColor);
		btnOptions.setForeground(Color.BLACK);
		btnOptions.setFocusPainted(false);
		btnOptions.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		btnOptions.addMouseListener(maBtnColor);
		btnOptions.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Options");
				JOptionPane.showMessageDialog(frame, "Ist noch in Arbeit...", "Achtung!", JOptionPane.WARNING_MESSAGE);
//		TODO start optionFrame
			}
		});
		buttonPanel.add(btnOptions);

//BTN SHOP
		JButton btnShop = new JButton("Shop");
		btnShop.setFont(new Font("Robot", Font.BOLD, 20));
		btnShop.setBackground(btnColor);
		btnShop.setForeground(Color.BLACK);
		btnShop.setFocusPainted(false);
		btnShop.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		btnShop.addMouseListener(maBtnColor);
		btnShop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Options");
				JOptionPane.showMessageDialog(frame, "Ist noch in Arbeit...", "Achtung!", JOptionPane.WARNING_MESSAGE);

			}
		});
		buttonPanel.add(btnShop);

		frame.add(buttonPanel, BorderLayout.CENTER);
		frame.setVisible(true);
	}

	private void serverStart(int port) {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		client = null;
		try {
			System.out.println("warte auf client!");
			showWaitFrame("waiting for a client...", "Server Port: " + port);
			client = server.accept();
			System.out.println("Verbunden!");
			frameWait.setVisible(false); // = null?
			updateMsg = new UpdateMessage(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String clientAdresse = client.getInetAddress().getHostAddress() + " - " + client.getInetAddress().getHostName();
		System.out.println(clientAdresse);

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			sendeString("connection confirmed#" + kacheln + "#" + anzahlSchiffe[1] + "#" + anzahlSchiffe[2] + "#"
					+ anzahlSchiffe[3] + "#" + anzahlSchiffe[4] + "#" + anzahlSchiffe[5]); // nachTrefferErneut
																							// boolean... //name
			String readin;
			while ((readin = reader.readLine()) != null) {
				System.out.println("client> " + readin);
				updateMsg.updateMsg(readin);
				if (readin.contains("verbunden")) {

					System.out.println("verbindung ok!");
					Game.startGame(kacheln, anzahlSchiffe);
				}
			}
			System.out.println("DISCONNECTED!");
			Game.state = State.error;
		} catch (Exception e) {
			e.printStackTrace();
			Game.state = State.error;
		}
	}

	private void clientStart(String IP, int port) {
		try {
			showWaitFrame("connecting to server...", null);
			clientServer = new Socket(IP, port); // timeout && bound
			System.out.println("PRIVAT VERBUNDEN");
//			clientServer.connect(new InetSocketAddress("192.168.178.53", 6666), 10000);
			updateMsg = new UpdateMessage(this);
			sendeString("verbunden");
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientServer.getInputStream()));
			String readin;
			while ((readin = reader.readLine()) != null) {
				System.out.println("server> " + readin);
				updateMsg.updateMsg(readin);
				if (readin.contains("connection confirmed#")) {
					String[] temp = readin.split("#");
					System.out.println("conected ok!");

					System.out.println(temp[1] + temp[2] + temp[3] + temp[4] + temp[5] + temp[6]);
					kacheln = Integer.valueOf(temp[1]);
					anzahlSchiffe = new int[6];
					anzahlSchiffe[0] = 0;
					anzahlSchiffe[1] = Integer.valueOf(temp[2]);
					anzahlSchiffe[2] = Integer.valueOf(temp[3]);
					anzahlSchiffe[3] = Integer.valueOf(temp[4]);
					anzahlSchiffe[4] = Integer.valueOf(temp[5]);
					anzahlSchiffe[5] = Integer.valueOf(temp[6]);

					frameWait.setVisible(false);
					Game.startGame(kacheln, anzahlSchiffe);
				}
			}
			System.out.println("DISCONNECTED!");
			Game.state = State.error;
		} catch (Exception e) {
			e.printStackTrace();
			Game.state = State.error;
		}

	}

	private void multiplayerClientStart(String IP, int port) {
		playerList = new LinkedList<String>();
		try {
			showWaitFrame("connecting to server...", null);
//			try {
			clientServer = new Socket(IP, port); // timeout && bound
//			} catch (Exception e) {
//				e.printStackTrace();
//				frameWait = null;
//				servergestartet = false;
//				frame.setVisible(false);
//			}
			System.out.println("Multiplayer VERBUNDEN");
//			clientServer.connect(new InetSocketAddress("192.168.178.53", 6666), 10000);
			updateMsg = new UpdateMessage(this);

//			sendeString("verbunden");
			sendeString("verbunden#" + Game.getUsername() + "#" + Game.version);
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientServer.getInputStream()));
			String readin;
			while ((readin = reader.readLine()) != null) {
				System.out.println("server> " + readin);
				updateMsg.updateMsg(readin);
				if (readin.contains("connection confirmed#")) {
					System.out.println("conected ok!");
					System.out.println("USERNMAME:" + Game.getUsername());
//					sendeString("#addplayer#" + Game.getUsername());
					// player liste
					createMPframe();
					frameWait.setVisible(false);

				}
				if (readin.contains("#startgame#")) {
					System.out.println("start Game!");
					frameWait.setVisible(false);
					mpFrame.setVisible(false);

					String[] temp = readin.split("#");
					System.out.println(temp[3] + temp[4] + temp[5] + temp[6]);
					kacheln = Integer.valueOf(temp[2]);
					anzahlSchiffe = new int[6];
					anzahlSchiffe[0] = 0;
					anzahlSchiffe[1] = 0;
					anzahlSchiffe[2] = Integer.valueOf(temp[3]);
					anzahlSchiffe[3] = Integer.valueOf(temp[4]);
					anzahlSchiffe[4] = Integer.valueOf(temp[5]);
					anzahlSchiffe[5] = Integer.valueOf(temp[6]);

					Game.startGame(Integer.valueOf(temp[2]), anzahlSchiffe);
				}
				if (readin.contains("#startgameok#")) {
					System.out.println("start Game!");
					frameWait.setVisible(false);
					mpFrame.setVisible(false);
//					servergestartet = true; // temp test!!!
					String[] temp = readin.split("#");
					Game.startGame(Integer.valueOf(temp[2]), anzahlSchiffe);
					// nachTrefferErneut--> next player senden true false
				}
				if (readin.contains("#file#")) {
					String[] temp = readin.split("#");
					System.out.println(temp[2]);
					if (temp[2].toLowerCase().contains("schiffeversenken")) {
						JOptionPane.showMessageDialog(mpFrame, "UPDATE!", "Achtung!", JOptionPane.WARNING_MESSAGE); // ja
						ReadFileString(new DataInputStream(clientServer.getInputStream()), temp[2]);
						System.out.println("ausführen: " + Game.getJarExecutionDirectory() + temp[2]);
						Runtime.getRuntime().exec("cmd /c start " + Game.getJarExecutionDirectory() + temp[2]);
						// bei .jar mit java
						// -jar!!!//////////////////////////////////////////////////////////////////////
						System.exit(0); // nein?
					} else {
						ReadFileString(new DataInputStream(clientServer.getInputStream()), temp[2]);
						System.out.println("ausführen: " + Game.getJarExecutionDirectory() + temp[2]);
						Runtime.getRuntime().exec("cmd /c start " + Game.getJarExecutionDirectory() + temp[2]);
					}

				}
			}
			System.out.println("DISCONNECTED!");
			Game.state = State.error;
			btnName.setText("DISCONNECTED!");
		} catch (Exception e) {
			e.printStackTrace();
			Game.state = State.error;
		}

	}

	public static String ReadFileString(DataInputStream is, String fileName) throws IOException {
		String ret = null;
		int len = ReadInt(is);
		if ((len == 0) || (len > 10000000)) {// 10MB
			ret = "";
			System.out.println("tcp " + "len 0");
		} else {
			byte[] buffer = new byte[len];
			is.readFully(buffer, 0, len);
			ret = new String(buffer, Charset.defaultCharset());

			Path path = Paths.get(Game.getJarExecutionDirectory() + "\\" + fileName);

			try {
				Files.write(path, buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (ret);
	}

	public static int ReadInt(DataInputStream is) throws IOException {
		int ret = 0;
		byte[] intAsBytes = new byte[4];
		is.readFully(intAsBytes);
		ret = fromByteArray(intAsBytes);
		return (ret);
	}

	public static int fromByteArray(byte[] bytes) {
		return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8)
				| ((bytes[3] & 0xFF) << 0);
	}

	public void sendeString(String msg) {
		Thread thread = new Thread(new Runnable() {
// TODO nur ein socket
			@Override
			public void run() {
				if (modus == Modus.client || modus == Modus.multiplayer) {
					try {
						PrintWriter senden = new PrintWriter(clientServer.getOutputStream());
						senden.println(msg);
						senden.flush();
						System.out.println("out>" + msg);
					} catch (IOException e) { 
						e.printStackTrace();
					}

				} else {
					try {
						PrintWriter senden = new PrintWriter(client.getOutputStream());
						senden.println(msg);
						senden.flush();
						System.out.println("out>" + msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}

	public void updateUserName(String name) {
//		mpFrame.setTitle("Schiffe Versenken - " + name);
		btnName.setText(" Name: " + name);
	}

	private void showWaitFrame(String text, String text2) {
		frameWait = new JFrame("Schiffe Versenken");
		frameWait.setBounds(100, 100, 350, 200);
		frameWait.setLayout(new GridLayout(2, 1));
		JLabel lbl1 = new JLabel(text, SwingConstants.CENTER);
		lbl1.setFont(new Font("Robot", 1, 30));
		lbl1.setVerticalAlignment(JLabel.CENTER);
		frameWait.add(lbl1);
		JLabel lbl2 = new JLabel(text2, SwingConstants.CENTER);
		lbl2.setFont(new Font("Robot", 1, 30));
		lbl2.setVerticalAlignment(JLabel.CENTER);
		frameWait.add(lbl2);

		frameWait.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameWait.setVisible(true);
	}

//	private void createServerFrame() {
//
//	}

	private void createClientFrame() {
		JFrame frame = new JFrame("Schiffe Versenken");
		GridLayout gLayout = new GridLayout(3, 1);
		frame.setLayout(gLayout);
		// ----IP----
		JPanel panelIP = new JPanel();
		panelIP.setBackground(Color.gray);
		panelIP.setBounds(0, 10, 300, 30);
		FlowLayout fLayout = new FlowLayout();
		fLayout.setAlignment(FlowLayout.LEFT);
		panelIP.setLayout(fLayout);

		JLabel lblIP = new JLabel("IP: ", SwingConstants.CENTER);
		lblIP.setFont(new Font("Dialog", 1, 20));
		lblIP.setVerticalAlignment(JLabel.CENTER);
		panelIP.add(lblIP);

		JTextField tfIP = new JTextField("192.168.178.53         ");
		tfIP.setBounds(100, 10, 100, 22);
		tfIP.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (tfIP.getText().length() >= 20)
					e.consume();
			}
		});
		panelIP.add(tfIP);
		frame.add(panelIP);
		// --------PORT--------

		JPanel panelPort = new JPanel();
		panelPort.setBackground(Color.gray);
		panelPort.setBounds(50, 10, 300, 30);
//		FlowLayout fLayout = new FlowLayout();
		fLayout.setAlignment(FlowLayout.LEFT);
		panelPort.setLayout(fLayout);

		JLabel lblPort = new JLabel("Port: ", SwingConstants.CENTER);
		lblPort.setFont(new Font("Dialog", 1, 20));
		lblPort.setVerticalAlignment(JLabel.CENTER);
		panelPort.add(lblPort);

		JTextField tfPort = new JTextField("6666    ");
		tfPort.setBounds(100, 10, 100, 22);
		tfPort.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (tfPort.getText().length() >= 5)
					e.consume();
			}
		});
		panelPort.add(tfPort);
		frame.add(panelPort);

//		Panel emptyPanel = new Panel();
//		frame.add(emptyPanel);

		// ----btns----

		Panel btnPanel = new Panel();

		Button btnPrivate = new Button("Privater Server");
		btnPrivate.setBounds(5, frame.getHeight() - 65, 150, 22);
		btnPrivate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] temp = getPrivateServerIP("https://superxy.jimdofree.com").split(":");
//				System.out.println(temp[1] + "---" + Integer.valueOf(temp[2]));
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						clientStart(temp[1], Integer.valueOf(temp[2]));
					}
				});
				thread.start();
//				servergestartet = true;
				frame.setVisible(false);
			}
		});
		btnPanel.add(btnPrivate);

		Button btnOK = new Button("        OK        ");
		btnOK.setBounds(frame.getWidth() - 170, frame.getHeight() - 65, 150, 22);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						clientStart(tfIP.getText(), Integer.valueOf(tfPort.getText()));
					}
				});
				if (Integer.valueOf(tfPort.getText()) != null) {
					thread.start();
//					servergestartet = true;
					frame.setVisible(false);
				}
			}
		});
		btnPanel.add(btnOK);
		frame.add(btnPanel);
		frame.setBounds(1, 10, 300, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				tfIP.setText("192.168.178.53");
				tfPort.setText("6666");
			}
		});
		thread.start();

	}

// MULTIPLAYER FRAME -----
	private void createMPframe() {
		mpFrame = new JFrame("Schiffe Versenken - " + Game.getUsername());
		mpFrame.getContentPane().setBackground(Color.DARK_GRAY);
		mpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mpFrame.setBounds(100, 100, 320, 340);
		mpFrame.setLayout(new java.awt.GridLayout(6, 1));

		LinkedList<String> liste = new LinkedList<String>();
		// server request players
		auswahl = new JComboBox<String>();
		auswahl.setFont(new Font("Robot", Font.BOLD, 20));
		auswahl.setBackground(btnColor);
		auswahl.setForeground(Color.BLACK);
		auswahl.setFocusable(false);
		auswahl.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		auswahl.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				auswahl.setBackground(btnColor);
			}

			public void mouseExited(MouseEvent evt) {
				auswahl.setBackground(btnColorFocus);
			}
		});
		auswahl.addItem("select a Player");
		for (String string : liste) {
			auswahl.addItem(string);
		}

// BTN NAME
		btnName = new JButton(" Name: " + Game.getUsername());
		btnName.setFont(new Font("Robot", Font.BOLD, 20));
		btnName.setBackground(new Color(20, 95, 128));
		btnName.setForeground(Color.BLACK);
		btnName.setFocusPainted(false);
		btnName.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		mpFrame.add(btnName);

// BTN REFRESH		
		JButton btnRefresh = new JButton("refresh");
		btnRefresh.setFont(new Font("Robot", Font.BOLD, 20));
		btnRefresh.setBackground(btnColor);
		btnRefresh.setForeground(Color.BLACK);
		btnRefresh.setFocusPainted(false);
		btnRefresh.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		btnRefresh.addMouseListener(maBtnColor);
		btnRefresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				auswahl.removeAllItems();
				sendeString("#server#update#");
				auswahl.addItem("select a Player");

			}
		});
		mpFrame.add(btnRefresh);

// BTN CONNECT		
		JButton btnConnect = new JButton("Play");
		btnConnect.setFont(new Font("Robot", Font.BOLD, 20));
		btnConnect.setBackground(btnColor);
		btnConnect.setForeground(Color.BLACK);
		btnConnect.setFocusPainted(false);
		btnConnect.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		btnConnect.addMouseListener(maBtnColor);
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (serverSettingsFrame != null)
						serverSettingsFrame.setVisible(false);
					System.out.println("Play------------------------------------------------------------------");
					sendeString("#server#requestgame#" + auswahl.getSelectedItem().toString() + "#" + kacheln + "#"
							+ anzahlSchiffe[2] + "#" + anzahlSchiffe[3] + "#" + anzahlSchiffe[4] + "#"
							+ anzahlSchiffe[5]);
				} catch (Exception e) {
					System.out.println("error!!!");
				}
			}
		});
		mpFrame.add(btnConnect);
		mpFrame.add(auswahl);

// BTN ServerSettings
		JButton btnServerSettings = new JButton("Server Settings");
		btnServerSettings.setFont(new Font("Robot", Font.BOLD, 20));
		btnServerSettings.setBackground(btnColor);
		btnServerSettings.setForeground(Color.BLACK);
		btnServerSettings.setFocusPainted(false);
		btnServerSettings.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		btnServerSettings.addMouseListener(maBtnColor);
		btnServerSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("settings...");
				if (serverSettingsFrame == null)
					createSettingsFrame();
				else
					serverSettingsFrame.setVisible(true);
			}
		});
		mpFrame.add(btnServerSettings);
		mpFrame.setVisible(true);
	}

	public void addPlayer(String player) {
		auswahl.addItem(player);
	}

// SETTINGS FRAME	
	private void createSettingsFrame() {
		serverSettingsFrame = new JFrame("Server Settings");
		if (modus == Modus.server)
			serverSettingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// TODO closing operation
		if (modus == Modus.singleplayer)
			serverSettingsFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.out.println("Closed");
					serverSettingsFrame = null;
					frame.setVisible(true);
				}
			});
		serverSettingsFrame.setBounds(300, 100, 300, 300);
		serverSettingsFrame.setLayout(new java.awt.GridLayout(8, 2)); // 2 x 7 ?
		// MapSize
		JLabel lableSize = new JLabel("Map Size:");
		serverSettingsFrame.add(lableSize);
		String[] cbAuswahlSize = { "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" };
		JComboBox<Object> cbMapSize = new JComboBox<Object>(cbAuswahlSize);
		serverSettingsFrame.add(cbMapSize);
		// Ships Menge
		String[] cbAuswahl = { "0", "1", "2", "3", "4" };
		// ShipsL2
		JLabel lableShips2 = new JLabel("Schiffe Länge 2:");
		serverSettingsFrame.add(lableShips2);
		JComboBox<Object> cbShips2 = new JComboBox<Object>(cbAuswahl);
		serverSettingsFrame.add(cbShips2);
		// ShipsL3
		JLabel lableShips3 = new JLabel("Schiffe Länge 3:");
		serverSettingsFrame.add(lableShips3);
		JComboBox<Object> cbShips3 = new JComboBox<Object>(cbAuswahl);
		serverSettingsFrame.add(cbShips3);
		// ShipsL4
		JLabel lableShips4 = new JLabel("Schiffe Länge " + "4:");
		serverSettingsFrame.add(lableShips4);
		JComboBox<Object> cbShips4 = new JComboBox<Object>(cbAuswahl);
		serverSettingsFrame.add(cbShips4);
		// ShipsL5
		JLabel lableShips5 = new JLabel("Schiffe Länge 5:");
		serverSettingsFrame.add(lableShips5);
		JComboBox<Object> cbShips5 = new JComboBox<Object>(cbAuswahl);
		serverSettingsFrame.add(cbShips5);
		// ErneutNachTreffer
		JLabel lableErneutNachTreffer = new JLabel("erneut nach Treffer:");
		serverSettingsFrame.add(lableErneutNachTreffer);
		JButton btnErneutNachTreffer = new JButton("false");
		btnErneutNachTreffer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnErneutNachTreffer.getText() == "false")
					btnErneutNachTreffer.setText("true");
				else
					btnErneutNachTreffer.setText("false");
			}
		});
		serverSettingsFrame.add(btnErneutNachTreffer);

		// apply settings
		JLabel lableEmpty = new JLabel("");
		serverSettingsFrame.add(lableEmpty);
		JLabel lableEmpty2 = new JLabel("");
		serverSettingsFrame.add(lableEmpty2);
		JButton btnResetSettings = new JButton("reset settings");
		btnResetSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("reset settings");
				cbMapSize.setSelectedIndex(5);
				cbShips2.setSelectedIndex(1);
				cbShips3.setSelectedIndex(3);
				cbShips4.setSelectedIndex(2);
				cbShips5.setSelectedIndex(1);
				btnErneutNachTreffer.setText("false");
			}
		});
		serverSettingsFrame.add(btnResetSettings);

		JButton btnApplySettings = new JButton("apply settings");
		if (modus == Modus.server || modus == Modus.singleplayer)
			btnApplySettings.setText("Play");
		btnApplySettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				kacheln = Integer.valueOf(cbMapSize.getSelectedItem().toString());
				anzahlSchiffe[2] = Integer.valueOf(cbShips2.getSelectedItem().toString());
				anzahlSchiffe[3] = Integer.valueOf(cbShips3.getSelectedItem().toString());
				anzahlSchiffe[4] = Integer.valueOf(cbShips4.getSelectedItem().toString());
				anzahlSchiffe[5] = Integer.valueOf(cbShips5.getSelectedItem().toString());
				System.out.println("Kacheln: " + kacheln + "  s2: " + anzahlSchiffe[2] + "   s3: " + anzahlSchiffe[3]
						+ "   s4: " + anzahlSchiffe[4] + "   s5: " + anzahlSchiffe[5]);
				if (btnErneutNachTreffer.getText() == "false")
					nachTrefferErneut = false;
				else
					nachTrefferErneut = true;
// TODO Modus				
				if (modus == Modus.server) {
					serverSettingsFrame.setVisible(false);
					serverSettingsFrame = null;
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							serverStart(6666);
						}
					});
					thread.start();
				} else if (modus == Modus.singleplayer) {
					serverSettingsFrame.setVisible(false);
					serverSettingsFrame = null;
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							LinkedList<Schiff> startSchiffe = new LinkedList<Schiff>();
							for (int iLaenge = 0; iLaenge <= 5; iLaenge++)
								for (int i = 0; i < anzahlSchiffe[iLaenge]; i++)
									startSchiffe.add(new Schiff(iLaenge));
							Game.singleplayer = new Singleplayer(kacheln, startSchiffe);
							Game.startGame(kacheln, anzahlSchiffe);
						}
					});
					thread.start();
				}
			}
		});
		serverSettingsFrame.add(btnApplySettings);

		serverSettingsFrame.setVisible(true);
		cbMapSize.setSelectedIndex(5);
		cbShips2.setSelectedIndex(1);
		cbShips3.setSelectedIndex(3);
		cbShips4.setSelectedIndex(2);
		cbShips5.setSelectedIndex(1);
	}

	private String getPrivateServerIP(String webseite) {
		String serverIP = null;
		try {
			URL url = new URL(webseite);
			Scanner scanner = new Scanner(url.openStream());
			StringBuffer sBuffer = new StringBuffer();
			while (scanner.hasNext())
				sBuffer.append(scanner.next());
			String result = sBuffer.toString();
			result = result.replaceAll("<[^>]*>", "");
			// System.out.println("Contents of the web page: " + result);
			String[] temp = result.split("#");
			System.out.println(temp[1]);
			serverIP = temp[1];
		} catch (IOException e) {
			e.printStackTrace();
		}
		return serverIP;
	}

	public void returnToMultiplayerMenu() { // TODO
//		createMPframe();
//		sendeString("verbunden#" + Game.getUsername() + "#" + Game.version);
	}

	public void returnToMainMenu() {// TODO funktioniert? alt wird gelöscht?
		Game.frame.setVisible(false);
		Game.tcpserver = new TcpServer();
	}

}
