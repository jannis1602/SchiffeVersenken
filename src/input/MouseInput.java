package input;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import Sound.SoundPlayer;
import pack.Game;
import pack.State;
import pack.TcpServer.Modus;

public class MouseInput implements MouseListener, MouseMotionListener {
	public Game game;

	public MouseInput(Game game) {
		this.game = game;
	}

	public int x, y;

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();

	}

	@Override
	public void mouseClicked(MouseEvent e) {

//		for (Btn tempBtn : Btn.getBtnList()) {
//			if (tempBtn.getScene() == Game.activeScene) {
//				if (tempBtn.checkHitbox(e.getPoint()))
//					tempBtn.doClick();
//			}
//		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
//		System.out.println(e.getX() + " " + e.getY());

		switch (Game.state) {
		case vorbereitung:
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (Game.vorbereitungsMenue.getBox(e.getX(), e.getY()))
					for (int i = 0; i < Game.vorbereitungsMenue.startSchiffe.size(); i++) {
						if (Game.vorbereitungsMenue.startSchiffe.get(i).boxIsTouched(e.getX(), e.getY())) {
							System.out.println(" selected " + i);
							if (Game.vorbereitungsMenue.selectedSchiff == null) {
								Game.vorbereitungsMenue.selectedSchiff = Game.vorbereitungsMenue.startSchiffe.get(i);
								Game.vorbereitungsMenue.startSchiffe.remove(i);
							}
							return;
						}
					}
				if (Game.vorbereitungsMenue.selectedSchiff != null)
					if (Game.mapPlayerOne.setzeSchiff(new Point(e.getX(), e.getY()),
							Game.vorbereitungsMenue.selectedSchiff)) {
						Game.vorbereitungsMenue.selectedSchiff = null;
					}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				if (Game.vorbereitungsMenue.selectedSchiff != null) {
					Game.vorbereitungsMenue.startSchiffe.add(Game.vorbereitungsMenue.selectedSchiff);
					Game.vorbereitungsMenue.selectedSchiff = null;
				} else if (Game.vorbereitungsMenue.selectedSchiff == null) {
					if (Game.mapPlayerOne.mouseOnMap(e.getX(), e.getY())) {
						for (int i = 0; i < Game.mapPlayerOne.schiffe.size(); i++) {
							if (Game.mapPlayerOne.schiffe.get(i).istGetroffen(
									Game.mapPlayerOne.findFeld(new Point(e.getX(), e.getY())).x,
									Game.mapPlayerOne.findFeld(new Point(e.getX(), e.getY())).y)) {
								Game.vorbereitungsMenue.startSchiffe.add(Game.mapPlayerOne.schiffe.get(i));
								Game.mapPlayerOne.schiffe.remove(i);
							}
						}
						// get schiff at pos
					}

				}
			}
			break;

		case menu:

			break;
		case player1:
			if (Game.tcpserver.modus == Modus.singleplayer && Game.state == State.player1) { // TODO ? Game.state ==
																								// State.player1
				Point p = Game.mapPlayerTwo.findFeld(new Point(e.getX(), e.getY())); // funktion durch p ersetzen!
				if (p != null)
					if (Game.mapPlayerTwo.mouseOnMap(e.getX(), e.getY())) {
						if (Game.mapPlayerTwo.felder[p.x][p.y] == 0) {
							Thread thread = new Thread(new Runnable() {

								@Override
								public void run() {
									Game.state = State.player2;// test
//									System.out.println(Game.singleplayer
//											.checkFeldForShip(Game.mapPlayerTwo.findFeld(new Point(e.getX(), e.getY()))));
									if (Game.singleplayer.checkFeldForShip(
											Game.mapPlayerTwo.findFeld(new Point(e.getX(), e.getY())))) {
										Game.mapPlayerTwo
												.addTreffer(Game.mapPlayerTwo.findFeld(new Point(e.getX(), e.getY())));
										new SoundPlayer("bomb.wav");
										Game.jet.startFlying(
												p.x * Game.mapPlayerOne.kachelSize + Game.mapPlayerTwo.info[0], // TODO
												p.y * Game.mapPlayerOne.kachelSize + Game.mapPlayerTwo.info[1]);

										Game.singleplayer.anDerReihe();// TODO && state != gewonnen || verloren
									} else {
										Game.mapPlayerTwo
												.addDaneben(Game.mapPlayerTwo.findFeld(new Point(e.getX(), e.getY())));
										new SoundPlayer("bombNoHit.wav");

										Game.jet.startFlying(
												p.x * Game.mapPlayerOne.kachelSize + Game.mapPlayerTwo.info[0], // TODO
												p.y * Game.mapPlayerOne.kachelSize + Game.mapPlayerTwo.info[1]);
										Game.singleplayer.anDerReihe();
									}
								}
							});
							thread.start();
//alt
						}
					}
				return;
			}

			Point p = Game.mapPlayerTwo.findFeld(new Point(e.getX(), e.getY())); // funktion durch p ersetzen!// TODO
																					// START Flying hier statt update
																					// msg
			if (p != null)
				if (Game.mapPlayerTwo.mouseOnMap(e.getX(), e.getY())) {
					if (Game.mapPlayerTwo.felder[p.x][p.y] == 0) {
						Game.state = State.player2;// test
						Game.mapPlayerTwo.requestFeld(Game.mapPlayerTwo.findFeld(new Point(e.getX(), e.getY())));
//					System.out.println(Game.mapPlayerTwo.findFeld(new Point(e.getX(), e.getY())));
					}
				}

			break;
		case player2:
			break;
		default:
			break;
		}

//ENDE
//		if (Game.state == State.Win) {
//
//			for (int i = 0; i < Game.win.Winbtns.length; i++) {
//				Btn btns = Game.win.Winbtns[i];
//
//				if (x >= btns.getX() && y >= btns.getY() && x <= btns.getX() + btns.getWidth()
//						&& y <= btns.getY() + btns.getHeight()) {
//					btns.triggerEvent();
//				}
//			}
//		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

}
