package pack;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import Image.BufferedImageLoader;
import pack.TcpServer.Modus;

public class Schiff {
	int laenge;
	Point pos;
	boolean versenkt;
	boolean gesetzt;
	private Rectangle box;
	boolean orientationH = true;
	private int treffer;
//	private BufferedImage img;

	public Schiff(int laenge) {
		this.laenge = laenge;
		this.treffer = laenge;
//		img = loadSchiffImg(laenge);
	}

	@SuppressWarnings("unused")
	private BufferedImage loadSchiffImg(int laenge) {
		try {
			switch (laenge) {// TODO SpriteSheed
			case 1:
				return new BufferedImageLoader().loadImage("schiffl1.png");
			case 2:
				return new BufferedImageLoader().loadImage("schiffl1.png");
			case 3:
				return new BufferedImageLoader().loadImage("schiffl1.png");
			case 4:
				return new BufferedImageLoader().loadImage("schiffl1.png");
			case 5:
				return new BufferedImageLoader().loadImage("schiffl1.png");
			case 6:
				return new BufferedImageLoader().loadImage("schiffl1.png");
			default:
				return null;
			}
		} catch (Exception e) {
			System.out.println("Image not found!");
		}
		return null;
	}

	public void rotate() {
		orientationH = !orientationH;
	}

	public int getLaenge() {
		return laenge;
	}

	public void setzePos(Point pos) {
		this.pos = pos;
		gesetzt = true;
	}

	public boolean addTreffer() {
		treffer--;
		if (treffer == 0 && Game.tcpserver.modus != Modus.singleplayer) {
			Game.tcpserver.sendeString("#schiffversenkt");
			return true;
		}
		System.out.println("T: " + treffer + "L: " + laenge);
		return false;
	}

	public void render(Graphics g, int x, int y, int s) {

		if (orientationH) {

			g.setColor(Color.DARK_GRAY.brighter());
			g.fillRect(x + pos.x * s, y + pos.y * s, s * laenge, s);
			g.setColor(Color.LIGHT_GRAY);
			for (int l = 0; l < laenge; l++) {
				g.drawRect(x + l * s + pos.x * s + 1, y + pos.y * s + 1, s - 2, s - 2);
				g.drawRect(x + l * s + pos.x * s + 2, y + pos.y * s + 2, s - 4, s - 4);

			}
			g.setColor(Color.BLACK);
			g.drawRect(x + pos.x * s, y + pos.y * s, s * laenge, s);
			g.drawRect(x + pos.x * s + 1, y + pos.y * s + 1, s * laenge - 2, s - 2);

		} else {
			g.setColor(Color.DARK_GRAY.brighter());
			g.fillRect(x + pos.x * s, y + pos.y * s, s, s * laenge);
			g.setColor(Color.LIGHT_GRAY);
			for (int l = 0; l < laenge; l++) {
				g.drawRect(x + pos.x * s + 1, y + pos.y * s + l * s + 1, s - 2, s - 2);
				g.drawRect(x + pos.x * s + 2, y + pos.y * s + l * s + 2, s - 4, s - 4);

			}
			g.setColor(Color.BLACK);
			g.drawRect(x + pos.x * s, y + pos.y * s, s, s * laenge);

			g.drawRect(x + pos.x * s + 1, y + pos.y * s + 1, s - 2, s * laenge - 2);

		}
// Image
//		if (orientationH)
//			g.drawImage(img, x + pos.x * s + 1, y + pos.y * s + 1, s * laenge - 2, s - 2, null);
//		else {
//			Graphics2D g2d = (Graphics2D) g;
////			g.drawImage(rotateImageByDegrees(img, 90), y + pos.y * s + 1, x + pos.x * s + 1, s - 2, s * laenge - 2,
//					null);
//		}

//			g.drawImage(img, x + pos.x * s + 1, y + pos.y * s + 1,64,64, null);

	}

	public void renderBox(Graphics g, int x, int y, int w, int h) {
		g.setColor(Color.BLUE);
		g.drawRect(x, y, w, h);
		box = new Rectangle(x, y, w, h);
		g.setFont(new java.awt.Font("Dialog", 1, 16));
		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(laenge), x + 10, y + 20);
		g.setColor(Color.BLACK);
		g.fillRect(x + (w / 2 - laenge / 2 * h / 8), y + ((h - h / 8) / 2), laenge * h / 8, h / 8);
		g.setColor(Color.LIGHT_GRAY);
		for (int l = 0; l < laenge; l++) {
			g.drawRect(x + l * h / 8 + w / 2 - laenge / 2 * h / 8, y + (h - h / 8) / 2, h / 8, h / 8);

		}
	}

	public void renderMouse(Graphics g, int x, int y, int kachelSize) {
		if (orientationH) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(x, y, kachelSize * laenge, kachelSize);
			g.setColor(Color.LIGHT_GRAY);
			for (int l = 0; l < laenge; l++) {
				g.drawRect(x + l * kachelSize, y, kachelSize, kachelSize);
			}
		} else {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(x, y, kachelSize, kachelSize * laenge);
			g.setColor(Color.LIGHT_GRAY);
			for (int l = 0; l < laenge; l++) {
				g.drawRect(x, y + l * kachelSize, kachelSize, kachelSize);
			}
		}
	}

	public boolean boxIsTouched(int x, int y) {
		if (x > box.x && x < box.x + box.width && y > box.y && y < box.y + box.height)
			return true;
		return false;
	}

	public boolean checkArea(Rectangle rect) {
		Rectangle schiffRect = null;
		if (orientationH) {
			schiffRect = new Rectangle(pos.x, pos.y, laenge, 1);
		} else if (!orientationH) {
			schiffRect = new Rectangle(pos.x, pos.y, 1, laenge);
		}
		if (schiffRect.intersects(rect))
			return true;
		return false;
	}

	public boolean istGetroffen(int x, int y) {
		if (orientationH) {
			if (x >= pos.x && x < pos.x + laenge && pos.y == y) {
				System.out.print("treffer at: " + x + "|" + y + "  ");
				System.out.println("schiff: " + pos.x + "|" + pos.y + "-L:" + laenge + orientationH);
				return true;
			}
		} else if (!orientationH) {
			if (y >= pos.y && y < pos.y + laenge && pos.x == x)
				return true;
		}
		return false;
	}

	public Rectangle getAreaRectAt(Point point) {
		Rectangle rect = null;
		if (orientationH) {
			rect = new Rectangle(point.x, point.y, laenge, 1);
		} else if (!orientationH) {
			rect = new Rectangle(point.x, point.y, 1, laenge);
		}
		return rect;
	}

//	public BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
//
//		double rads = Math.toRadians(angle);
//		double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
//		int w = img.getWidth();
//		int h = img.getHeight();
//		int newWidth = (int) Math.floor(w * cos + h * sin);
//		int newHeight = (int) Math.floor(h * cos + w * sin);
//
//		BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g2d = rotated.createGraphics();
//		AffineTransform at = new AffineTransform();
//		at.translate((newWidth - w) / 2, (newHeight - h) / 2);
//
//		int x = w / 2;
//		int y = h / 2;
//
//		at.rotate(rads, x, y);
//		g2d.setTransform(at);
//		g2d.drawImage(img, 0, 0, null);
//		g2d.dispose();
//
//		return rotated;
//	}

}
