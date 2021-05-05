package pack;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Jet {

	int jx = 1650, jy = -80;
	int bx, by = 30;
	BufferedImage jetImage, bombImage, tempbombImage;

	public Jet(BufferedImage jetImage, BufferedImage bombImage) {
		this.jetImage = jetImage;
		this.bombImage = bombImage;
		tempbombImage = bombImage;
	}

	public void render(Graphics g) {
//		g.drawImage(jetImage, x, -80, 64 * 4, 64 * 4, null); 
		if (jx < 1900)
			g.drawImage(jetImage, (int) (Game.getFrameWidth() / 1936f * jx), (int) (Game.getFrameHeight() / 1056f * jy),
					(int) (Game.getFrameWidth() / 1936f * 64 * 4), (int) (Game.getFrameHeight() / 1056f * 64 * 4),
					null);
		if (by > 30)
			g.drawImage(bombImage, (int) (Game.getFrameWidth() / 1936f * bx),
					(int) (Game.getFrameHeight() / 1056f * by), Game.mapPlayerOne.kachelSize,
					Game.mapPlayerOne.kachelSize, null);
//			g.drawImage(bombImage, (int) (Game.getFrameWidth() / 1936f * bx),
//					(int) (Game.getFrameHeight() / 1056f * by), (int) (Game.getFrameWidth() / 1936f * 64),
//					(int) (Game.getFrameHeight() / 1056f * 64), null);

//		System.out.print("x: " + (int) (Game.getFrameWidth() / 1936f));
//		System.out.println("  y: " + (int) (Game.getFrameHeight() / 1056f));

//		g.setColor(Color.ORANGE);
//		g.drawRect(Game.getFrameWidth() / 1936 * x, Game.getFrameHeight() / 1056 * y, Game.getFrameWidth() / 1936 * 100,
//				Game.getFrameHeight() / 1056 * 100);
	}

//	Rakete: Map x + kachelgröße*feldx +1
//	Jet y + i while yRakete <map y+kachelgröße * zielfeld

	public void startFlying(int zielx, int ziely) {
//		Thread thraed = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
		System.out.println("zielx: " + zielx);
		while (jx > -(64 * 4)) {
			jx -= 5;
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (jx >= zielx - 2 && jx < zielx + 2) {// TODO
//				System.out.println("Bombe abwerfen!");
				bx = zielx;
				dropBomb(ziely);
			}
		}
//		jx = 1650;
		jx = 1900;
//			}
//		});
//		thraed.start();
	}

	public void dropBomb(int ziely) {
		System.out.println("ziely: " + ziely);
		Thread thraed = new Thread(new Runnable() {
			int r = 0;

			@Override
			public void run() {
				while (by < ziely) {
					by += 3; // 5
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (r < 90) {
						r += 1;
						bombImage = rotateImageByDegrees(tempbombImage, -r);
					}
				}
//				System.out.println("Bombe abgeworfen!");
				bombImage = tempbombImage;
				r = 0;
				by = 30;// TODO
			}
		});
		thraed.start();
	}

	public BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

		double rads = Math.toRadians(angle);
		double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
		int w = img.getWidth();
		int h = img.getHeight();
		int newWidth = (int) Math.floor(w * cos + h * sin);
		int newHeight = (int) Math.floor(h * cos + w * sin);

		BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = rotated.createGraphics();
		AffineTransform at = new AffineTransform();
		at.translate((newWidth - w) / 2, (newHeight - h) / 2);

		int x = w / 2;
		int y = h / 2;

		at.rotate(rads, x, y);
		g2d.setTransform(at);
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();

		return rotated;
	}

}
