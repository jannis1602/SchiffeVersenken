package pack;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

import Sound.SoundPlayer;

public class Singleplayer {
	private int[][] felder;// IDs: 0=unbekannt; 1=daneben; 2=treffer;
	public LinkedList<Schiff> schiffe;
	private boolean[][] schiffFelder;
	private int kacheln;
	@SuppressWarnings("unused")
	private int schiffeVersenkt = 0;

	public Singleplayer(int kacheln, LinkedList<Schiff> startSchiffe) {
		this.kacheln = kacheln;
		felder = new int[kacheln + 1][kacheln + 1];
		schiffe = new LinkedList<Schiff>();
		schiffFelder = new boolean[kacheln + 1][kacheln + 1];
		setzeSchiffe(startSchiffe);
		schiffe = startSchiffe;

		Game.playerTwoReady = true;
	}

	private void setzeSchiffe(LinkedList<Schiff> schiffe) {
		// gucken ob sie sich überschneiden
		// senkrecht auch
		Random r = new Random();
		for (Schiff schiff : schiffe) {
			Point pos = new Point(r.nextInt(kacheln), r.nextInt(kacheln));
			schiff.orientationH = r.nextBoolean();

			while (!feldIsFree(schiff, pos)) {
				pos = new Point(r.nextInt(kacheln), r.nextInt(kacheln));
				schiff.orientationH = r.nextBoolean();
			}
			System.out.println("posx: " + pos.x + " posy: " + pos.y + " laenge: " + schiff.laenge
					+ "---------------------------------");

			schiff.setzePos(pos);
			System.out.println(schiff.laenge + " " + schiff.pos.x + " " + schiff.pos.y);
			addSchiffFelder(schiff);
		}
	}

	private boolean feldIsFree(Schiff schiff, Point pos) {
		if (pos.y > 0 && pos.x > 0) {
			if (schiff.orientationH) {
				if (pos.x + schiff.laenge < kacheln) {
					for (int i = 0; i < schiff.laenge; i++)
						if (schiffFelder[pos.x + i][pos.y] != false) {
							return false;
						}
					return true;
				}

			} else {
				if (pos.y + schiff.laenge < kacheln) {
					for (int i = 0; i < schiff.laenge; i++)
						if (schiffFelder[pos.x][pos.y + i] != false) {
							return false;
						}
					return true;
				}
			}
		}
		return false;

	}

	public boolean checkFeldForShip(Point point) {
		if (Game.mapPlayerTwo.felder[point.x][point.y] == 0) {
			if (schiffFelder[point.x][point.y]) {

				return true;
			}
			return false;
		}
		return false;
	}

//	public void addAllShips() {// am ende setzen
//		for (int i = 0; i < schiffe.size(); i++)
//			addSchiffFelder(schiffe.get(i));
//	}

	private void addSchiffFelder(Schiff schiff) {
		try { // eigentlich ohne try catch
			if (schiff.orientationH)
				for (int i = 0; i < schiff.laenge; i++) {
					schiffFelder[schiff.pos.x + i][schiff.pos.y] = true;
				}
			else
				for (int i = 0; i < schiff.laenge; i++) {
					schiffFelder[schiff.pos.x][schiff.pos.y + i] = true;
				}
		} catch (Exception e) {
		}

	}

	public void anDerReihe() {
		Random r = new Random();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Point p = new Point(r.nextInt(kacheln) + 1, r.nextInt(kacheln) + 1);
		while (felder[p.x][p.y] != 0) {
			p = new Point(r.nextInt(kacheln) + 1, r.nextInt(kacheln) + 1);
		}
		boolean treffer = Game.mapPlayerOne.checkFeld(p);
		// System.out.println(p);
		if (!treffer) {
			felder[p.x][p.y] = 2;
			new SoundPlayer("bombNoHit.wav");
		} else {
			felder[p.x][p.y] = 1;
			new SoundPlayer("bomb.wav");
		}
		Game.jet.startFlying(p.x * Game.mapPlayerOne.kachelSize + Game.mapPlayerOne.info[0], // TODO für true und false
				p.y * Game.mapPlayerOne.kachelSize + Game.mapPlayerOne.info[1]);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Game.state = State.player1;
	}

}
