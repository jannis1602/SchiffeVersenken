package pack;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import Image.BufferedImageLoader;
import Sound.SoundPlayer;
import pack.TcpServer.Modus;

public class UpdateMessage {
	TcpServer tcpServer;
	private boolean notifications = true;
	private boolean soundOn = true;

	public UpdateMessage(TcpServer tcpServer) {
		this.tcpServer = tcpServer;
	}

	public void updateMsg(String msg) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				checkmsg(msg);
			}
		});
		thread.start();
	}

	private void checkmsg(String msg) { // split NICHT bei # !!! unsicher
//		System.out.println("checkmsg:" + msg);
		if (msg.contains("#msg#")) {
			String[] temp = msg.split("#");

			if (Game.console == null)
				Game.console = new Console();
			Game.console.addTextToTextArea(">" + temp[2]);
			if (!Game.console.frame.isFocused())
				sendNotification("SchiffeVersenken - msg", temp[2]);

		} else if (msg.contains("#game#check#")) {
			String[] temp = msg.split("#");
			if (Game.mapPlayerOne.checkFeld(new Point(Integer.valueOf(temp[3]), Integer.valueOf(temp[4])))) {
				Game.tcpserver.sendeString("#game#addtreffer#" + temp[3] + "#" + temp[4]);
				if (soundOn)
					new SoundPlayer("bomb.wav");
//				Game.jet.startFlying(Integer.valueOf(temp[3]), Integer.valueOf(temp[4]) * Game.mapPlayerOne.kachelSize);// TODO
				Game.jet.startFlying(
						Integer.valueOf(temp[3]) * Game.mapPlayerOne.kachelSize + Game.mapPlayerOne.info[0], // TODO
						Integer.valueOf(temp[4]) * Game.mapPlayerOne.kachelSize + Game.mapPlayerOne.info[1]);
				// ykoordinate
				// variabel
			} else {
				Game.tcpserver.sendeString("#game#adddaneben#" + temp[3] + "#" + temp[4]);
				if (soundOn)
					new SoundPlayer("bombnohit.wav");
//				Game.jet.startFlying(Integer.valueOf(temp[3]), Integer.valueOf(temp[4]) * Game.mapPlayerOne.kachelSize);
				Game.jet.startFlying(
						Integer.valueOf(temp[3]) * Game.mapPlayerOne.kachelSize + Game.mapPlayerOne.info[0], // TODO
						Integer.valueOf(temp[4]) * Game.mapPlayerOne.kachelSize + Game.mapPlayerOne.info[1]);
			}
//			System.out.println("check msg check point: "
//					+ Game.mapPlayerOne.checkFeld(new Point(Integer.valueOf(temp[3]), Integer.valueOf(temp[4]))));

		} else if (msg.contains("#newplayername#")) {
			String[] temp = msg.split("#");
			Game.userName = temp[2];
			tcpServer.updateUserName(temp[2]);
//			System.out.println("new USER NAME: " + temp[1]);
		} else if (msg.contains("#game#addtreffer#")) {
			String[] temp = msg.split("#");
			Game.mapPlayerTwo.addTreffer(new Point(Integer.valueOf(temp[3]), Integer.valueOf(temp[4])));
			Game.state = State.player2; /////
			Game.tcpserver.sendeString("#game#nextplayer"); // wenn weggelassen versuche bis daneben
			if (soundOn)
				new SoundPlayer("bomb.wav");
//			Game.jet.startFlying(Integer.valueOf(temp[3]), Integer.valueOf(temp[4]) * Game.mapPlayerOne.kachelSize);
			Game.jet.startFlying(Integer.valueOf(temp[3]) * Game.mapPlayerOne.kachelSize + Game.mapPlayerTwo.info[0], // TODO
					Integer.valueOf(temp[4]) * Game.mapPlayerOne.kachelSize + Game.mapPlayerTwo.info[1]);

		} else if (msg.contains("#game#adddaneben#")) {
			String[] temp = msg.split("#");
			Game.mapPlayerTwo.addDaneben(new Point(Integer.valueOf(temp[3]), Integer.valueOf(temp[4])));
			Game.state = State.player2;
			Game.tcpserver.sendeString("#game#nextplayer");
			if (soundOn)
				new SoundPlayer("bombnohit.wav");
//			Game.jet.startFlying(Integer.valueOf(temp[3]), Integer.valueOf(temp[4]) * Game.mapPlayerOne.kachelSize);
			Game.jet.startFlying(Integer.valueOf(temp[3]) * Game.mapPlayerOne.kachelSize + Game.mapPlayerTwo.info[0], // TODO
					Integer.valueOf(temp[4]) * Game.mapPlayerOne.kachelSize + Game.mapPlayerTwo.info[1]);
		} else if (msg.contains("#game#nextplayer") && Game.state != State.gewonnen && Game.state != State.verloren) {
			if (Game.state == State.player1)
				Game.state = State.player2;
			else if (Game.state == State.player2)
				Game.state = State.player1;
			if (!Game.frame.isFocused())
				sendNotification("SchiffeVersenken", "Du bist an der Reihe!");
		} else if (msg.contains("#game#ready")) {
			System.out.println("P2 ready");
			Game.playerTwoReady = true;
			if (Game.vorbereitungsMenue.startSchiffe.size() == 0) {
				if (Game.playerOneReady) {
					if (Game.tcpserver.modus == Modus.server)// servergestartet
						Game.state = State.player2;
					else
						Game.state = State.player1;
				}
			}
		} else if (msg.contains("#schiffversenkt")) {
			Game.mapPlayerTwo.showSchiffVersenktText();
//			Thread thread = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						Thread.sleep(4000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					Game.showVersenktText = false;
//				}
//			});
//			thread.start();
		} else if (msg.contentEquals("#win")) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("GEWONNEN!");
			Game.state = State.gewonnen;
			int optionPane = JOptionPane.showConfirmDialog(null, "Erneut spielen?", "Achtung!",
					JOptionPane.YES_NO_OPTION); // ja
			if (optionPane == JOptionPane.OK_OPTION) {
				System.out.println("neustart!");
				Game.tcpserver.returnToMultiplayerMenu();// TODO oder to main menu
				/////////////////////////////////////////// restart

			} else {
				System.out.println("Ende...");
				System.exit(0);
			}

			// hacks...
		} else if (msg.contains("#game#addhiddnship#")) {
			String[] temp = msg.split("#");
			Schiff tempSchiff = new Schiff(Integer.valueOf(temp[3]));
			tempSchiff.setzePos(new Point(Integer.valueOf(temp[4]), Integer.valueOf(temp[5])));
			tempSchiff.orientationH = Boolean.valueOf(temp[6]);
			Game.mapPlayerTwo.hiddenShips.add(tempSchiff);
			System.out.println(tempSchiff + " " + Game.mapPlayerTwo.hiddenShips.size()); // rotation...
		} else if (msg.contentEquals("#sendhiddnships")) {
			if (tcpServer.hacks) {
				for (int i = 0; i < Game.mapPlayerOne.schiffe.size(); i++) {
					Schiff tempship = Game.mapPlayerOne.schiffe.get(i);
					Game.tcpserver.sendeString("#game#addhiddnship#" + tempship.laenge + "#" + tempship.pos.x + "#"
							+ tempship.pos.y + "#" + tempship.orientationH); // rotation...
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (msg.contentEquals("#openconsole")) {
			System.out.println("console");
			if (Game.console == null)
				Game.console = new Console();
			else
				Game.console.showConsole();
		} else if (msg.contentEquals("#closeconsole")) {
			System.out.println("console close");
			Game.console.frame.setVisible(false);
		}
		// server client
		else if (msg.contains("#addplayer#")) {
			String[] temp = msg.split("#");
			Game.tcpserver.addPlayer(temp[2]);
			System.out.println(temp[2]);
		} else if (msg.contains("#requestgame#")) {
			System.out.println("---GameRequest!---");
			String[] temp = msg.split("#");
			Game.tcpserver.addPlayer(temp[2]);
			System.out.println(temp[2]);
			sendNotification("SchiffeVersenken - GameRequest", temp[2] + " fragt nach einem Spiel");
			int res = JOptionPane.showConfirmDialog((Component) null, temp[2] + " fragt nach einem Spiel",
					"Gamerequest!", JOptionPane.OK_CANCEL_OPTION);
			if (res == JOptionPane.OK_OPTION && tcpServer.inGame == false) {
				tcpServer.sendeString("#server#createnewgame#" + Game.getUsername() + "#" + temp[2] + "#" + temp[3]
						+ "#" + temp[4] + "#" + temp[5] + "#" + temp[6] + "#" + temp[7]);
				tcpServer.inGame = true;
			}
		}
	}

	public void sendNotification(String caption, String msg) {
		if (notifications == true)
			try {
				if (SystemTray.isSupported()) {
					SystemTray tray = SystemTray.getSystemTray();
					TrayIcon trayIcon = new TrayIcon(
							new BufferedImageLoader().loadImage("atombombe.png").getScaledInstance(16, 16, 16),
							"SchiffeVersenken");
					try {
						tray.add(trayIcon);
					} catch (AWTException e) {
						e.printStackTrace();
					}
					if (!msg.contains("fragt nach einem Spiel"))
						trayIcon.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								Game.console.showConsole();
							}
						});
					trayIcon.displayMessage(caption, msg, MessageType.INFO);
				} else
					System.err.println("System tray not supported!");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
