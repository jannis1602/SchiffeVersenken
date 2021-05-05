package pack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.util.LinkedList;

public class VorbereitungsMenue {

	Game game;
	public Rectangle[][] schiffAuswahl;
	public LinkedList<Schiff> startSchiffe;
	public Schiff selectedSchiff;
	private Rectangle box;

	public VorbereitungsMenue(Game game, LinkedList<Schiff> startSchiffe) {
		this.game = game;
		schiffAuswahl = new Rectangle[2][4];
		this.startSchiffe = startSchiffe;
	}

	public void render(Graphics g, int x, int y, int width, int height) {
		g.setColor(Color.red);
		g.drawRect(x, y, width, height);
		box = new Rectangle(x, y, width, height);
		// 2. Reihe schiffe
		for (int i = 0; i < startSchiffe.size(); i++) {
			startSchiffe.get(i).renderBox(g, x + i * height / 2 + 1, y + 1, height / 2, height / 2);
		}
		if (selectedSchiff != null) {
			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();
			int mx = (int) b.getX();// + Game.frame.getLocationOnScreen().x;
			int my = (int) b.getY();// + Game.frame.getLocationOnScreen().y;

			try {
				selectedSchiff.renderMouse(g, mx, my - 22, Game.mapPlayerOne.kachelSize);
			} catch (Exception e) {
			}
		}
	}

	public void renderL(Graphics g, int x, int y, int w, int h) {

		g.setColor(Color.gray);
		g.fillRect(x, y, w, h);
		g.setColor(Color.DARK_GRAY);
		g.drawRect(x, y, w, h);
		g.fillRect(w, y, 5, h + 10);

		int seitenFeldX = 0;
		int seitenFeldY = 0;
		int feldSize = w / 4;

		for (int i = 0; i < startSchiffe.size(); i++) {
			startSchiffe.get(i).renderBox(g, x + seitenFeldX * feldSize, y + seitenFeldY * feldSize, feldSize,
					feldSize);// render
			schiffAuswahl[seitenFeldY][seitenFeldX] = new Rectangle(x + seitenFeldX * feldSize,
					y + seitenFeldY * feldSize, feldSize, feldSize);
			seitenFeldX++;
			if (seitenFeldX == 4) {
				seitenFeldX = 0;
				seitenFeldY++;
			}
		}
	}

	public void renderR(Graphics g, int x, int y, int w, int h) {
		g.setColor(Color.gray);
		g.fillRect(x, y, w, h);
		g.setColor(Color.DARK_GRAY);
		g.drawRect(x, y, w, h);
		g.fillRect(x - 5, y, 5, h + 10);

		Rectangle[] rectR = new Rectangle[4];
		rectR[0] = new Rectangle(x, y, w, h / 6); // recDelAll
		rectR[1] = new Rectangle(x, y + h / 6 * 1, w, h / 6); // recRotate
		rectR[2] = new Rectangle(x, y + h / 6 * 2, w, h / 6); // recCancel
		rectR[3] = new Rectangle(x, h - h / 6, w, h / 6); // recReady

		g.drawRect(x, y, w, h / 6);
		g.drawRect(x, y + h / 6 * 1, w, h / 6);
		g.drawRect(x, y + h / 6 * 2, w, h / 6);
		g.drawRect(x, h - h / 6, w, h / 6);

	}

	public boolean getBox(int x, int y) {
		if (x > box.x && x < box.x + box.width && y > box.y && y < box.y + box.height)
			return true;
		else
			return false;
	}

}
