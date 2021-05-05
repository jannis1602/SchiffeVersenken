package pack;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import pack.TcpServer.Modus;

public class Map {
	// private...
	public boolean showSchiffe = true;
	public int[][] felder; // IDs: 0=unbekannt; 1=daneben; 2=treffer;
	private boolean[][] schiffFelder;
	public LinkedList<Schiff> hiddenShips;// methode zum setzen!
	public boolean showHiddenShips = false;
	public LinkedList<Schiff> schiffe;
	public int kachelSize = 0;
	public int[] info = new int[5]; // TODO einzelne variablen
	private int schiffeVersenkt = 0;
	private boolean showSchiffVersenktText = false;

	public Map(int width, int height, LinkedList<Schiff> schiffe) { // w und h durh kacheln ersetzen
		width += 1;
		height += 1;
		felder = new int[width][height];
		schiffFelder = new boolean[width][height];
		this.schiffe = schiffe;
		hiddenShips = new LinkedList<Schiff>();
	}

	public Map(int width, int height) { // kacheln
		width += 1;
		height += 1;
		felder = new int[width][height];
		schiffFelder = new boolean[width][height];
		this.schiffe = new LinkedList<Schiff>();
		hiddenShips = new LinkedList<Schiff>();
	}

	public void render(Graphics g, int posx, int posy, int width, int height, int kacheln, State activeState) {
		info[0] = posx;
		info[1] = posy;
		info[2] = width;
		info[3] = height;
		info[4] = kacheln;
		kacheln = kacheln + 1;
		kachelSize = height / kacheln;
		if (Game.state == activeState) {
			g.setColor(Color.GREEN);
			g.fillRect(posx + kachelSize / 4, posy + kachelSize / 4, kachelSize / 2, kachelSize / 2);
		}
		for (int y = 0; y < kacheln; y++)
			for (int x = 0; x < kacheln; x++) {
// feld hintergrund blau
				if (x > 0 && y > 0) {
//					g.setColor(new Color(19, 84, 189));
					g.setColor(new Color(22, 145, 201));
					g.fillRect(posx + width / kacheln * x, posy + height / kacheln * y, width / kacheln,
							height / kacheln);
				}
//feldumrandung
				g.setColor(Color.DARK_GRAY);
				g.drawRect(posx + width / kacheln * x, posy + height / kacheln * y, width / kacheln, height / kacheln);
				g.drawRect(posx + width / kacheln * x + 1, posy + height / kacheln * y + 1, width / kacheln - 2,
						height / kacheln - 2);

// feldbeschriftung
				if (y == 0 && x >= 1) {
					Font newFont = new java.awt.Font("Dialog", 1, 16);
					g.setFont(newFont);
					g.setColor(Color.WHITE);
					g.drawString(String.valueOf(x), posx + width / kacheln * x + kachelSize / 6,
							posy + height / kacheln * y + 20);// +20 // kachelsize-kachelsize/6 ???
//					g.setColor(new Color(19, 84, 189));
//					g.fillRect(posx + width / kacheln * x, posy + height / kacheln * y, width / kacheln,
//							height / kacheln);
				}
				if (x == 0 && y >= 1) {
					Font newFont = new java.awt.Font("Dialog", 1, 16);
					g.setFont(newFont);
					g.setColor(Color.WHITE);
					String temp = "abcdefghijklmnopqrstuvwxyz";
					g.drawString(String.valueOf(temp.charAt(y - 1)), posx + width / kacheln * x + kachelSize / 6,
							posy + height / kacheln * y + 20);
				}
			}
		if (showSchiffe)
			if (schiffe != null && schiffe.size() > 0)
				for (int i = 0; i < schiffe.size(); i++) {
					schiffe.get(i).render(g, posx, posy, kachelSize);
				}

		if (showHiddenShips)
			for (int i = 0; i < hiddenShips.size(); i++)
				hiddenShips.get(i).render(g, posx, posy, kachelSize);

		// treffer oder daneben Felder
		for (int x = 0; x < felder.length; x++)
			for (int y = 0; y < felder[x].length; y++) {
				if (felder[x][y] == 1) {
					g.setColor(Color.black);
					g.fillRect(posx + kachelSize / 4 + kachelSize * x, posy + kachelSize / 4 + kachelSize * y,
							kachelSize / 2, kachelSize / 2);
				} else if (felder[x][y] == 2) {
					g.setColor(Color.red);
					g.fillRect(posx + kachelSize / 4 + kachelSize * x, posy + kachelSize / 4 + kachelSize * y,
							kachelSize / 2, kachelSize / 2);
				}
			}
		// test //////////////////////////////////////
		Game.jet.render(g);
		// test ende /////////////

// Schiff versenkt Text
		if (showSchiffVersenktText) {
			Font font = new java.awt.Font("Dialog", 1, 18);
			g.setFont(font);
			g.setColor(Color.ORANGE);
			g.drawString("Schiff Versenkt!", posx + width / 6, posy - 5);
		}
	}

	public boolean checkFeld(Point point) {
		if (schiffFelder[point.x][point.y]) {
			addTreffer(point);
			return true;
		}
		addDaneben(point);
		return false;
	}

	public void requestFeld(Point point) {
		if (felder[point.x][point.y] == 0) {
			Game.tcpserver.sendeString("#game#check#" + point.x + "#" + point.y);
		}
	}

	public Point findFeld(Point point) {
		Point kachel = null;
		for (int x = 1; x < felder.length; x++)
			for (int y = 1; y < felder[x].length; y++)
				if (point.x > info[0] + kachelSize * x && point.x < info[0] + kachelSize * x + kachelSize
						&& point.y > info[1] + kachelSize * y && point.y < info[1] + kachelSize * y + kachelSize)
					kachel = new Point(x, y);
		return kachel;
	}

	public boolean mouseOnMap(int x, int y) {
		if (x > info[0] && x < info[0] + info[2] && y > info[1] && y < info[1] + info[3]) {
			return true;
		}
		return false;
	}

	public boolean setzeSchiff(Point point, Schiff schiff) {
		if (point.x > info[0] && point.x < info[0] + info[2] && point.y > info[1] && point.y < info[1] + info[3]) {
			for (int x = 1; x < felder.length; x++) {
				for (int y = 1; y < felder[x].length; y++) {
					if (point.x > info[0] + kachelSize * x && point.x < info[0] + kachelSize * x + kachelSize
							&& point.y > info[1] + kachelSize * y && point.y < info[1] + kachelSize * y + kachelSize) {
						for (int i = 0; i < Game.mapPlayerOne.schiffe.size(); i++) {
							if (Game.mapPlayerOne.schiffe.get(i).checkArea(schiff.getAreaRectAt(new Point(x, y))))
								return false;
						}
						if (schiff.orientationH) {
							if (x + schiff.laenge > info[4] + 1)
								return false;
						} else if (!schiff.orientationH)
							if (y + schiff.laenge > info[4] + 1)
								return false;
						schiff.setzePos(new Point(x, y));
//						addSchiffFelder(schiff);
						schiffe.add(schiff);
						return true;
					}
				}
			}
		}
		return false;
	}

	public void addTreffer(Point point) {
		felder[point.x][point.y] = 2;
		for (int i = 0; i < schiffe.size(); i++) {
			if (schiffe.get(i).istGetroffen(point.x, point.y))
				if (schiffe.get(i).addTreffer()) {
					schiffeVersenkt++;
					System.out.println("versenkt:" + schiffeVersenkt); // TODO anzeigen
					showSchiffVersenktText();
					if (schiffeVersenkt == schiffe.size() && Game.tcpserver.modus != Modus.singleplayer) {
						Game.ende = true;
						Game.tcpserver.sendeString("#win");
						Game.state = State.verloren;
						int optionPane = JOptionPane.showConfirmDialog(null, "Erneut spielen?", "Achtung!",
								JOptionPane.YES_NO_OPTION); // ja
						if (optionPane == JOptionPane.OK_OPTION) {
							System.out.println("Map neustart!");
//							Game.tcpserver.returnToMainMenu();// TODO oder to mp menu
						} else {
							System.out.println("Ende...");
							System.exit(0);
						}
					} else if (schiffeVersenkt == schiffe.size() && Game.tcpserver.modus == Modus.singleplayer
							&& this == Game.mapPlayerTwo) {
						Game.ende = true;
						Game.state = State.gewonnen;
						int optionPane = JOptionPane.showConfirmDialog(null, "Erneut spielen?", "Achtung!",
								JOptionPane.YES_NO_OPTION); // ja
						if (optionPane == JOptionPane.YES_OPTION) {
							System.out.println("Singleplayer neustart!");
							Game.tcpserver.returnToMainMenu();// TODO oder to mp menu

						} else {
							System.out.println("Ende...");
							System.exit(0);
						}
					}

				}
		}
	}

	public void addDaneben(Point point) {
		felder[point.x][point.y] = 1;
	}

	public void addAllShips() {
		for (int i = 0; i < schiffe.size(); i++)
			addSchiffFelder(schiffe.get(i));
	}

	private void addSchiffFelder(Schiff schiff) {
		if (schiff.orientationH)
			for (int i = 0; i < schiff.laenge; i++) {
				schiffFelder[schiff.pos.x + i][schiff.pos.y] = true;
			}
		else
			for (int i = 0; i < schiff.laenge; i++) {
				schiffFelder[schiff.pos.x][schiff.pos.y + i] = true;
			}
	}

	public void showSchiffVersenktText() {
		showSchiffVersenktText = true;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				showSchiffVersenktText = false;
			}
		});
		thread.start();
	}

}
