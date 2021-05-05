package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import javax.swing.JButton;

import pack.Scene;

public class Btn extends JButton {
	private static final long serialVersionUID = 1L;

	private static LinkedList<Btn> btnList = new LinkedList<Btn>();
	private int x, y;
	private int width, height;
	private int transparent = 0;
	private int arc = 20;
	private String text;
	private Color color;
	private Color textcolor;
	private int textSize = 30;
	private boolean fill;
	private Scene scene;
	private int ID;

//	private static MouseListener;

	public Btn(int x, int y, int width, int height, String text, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.color = color;
		this.textcolor = color;
		this.fill = false;
	}

	public Btn(int x, int y, int width, int height, String text, Color color, Scene scene) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.color = color;
		this.textcolor = color;
		this.fill = false;
		this.scene = scene;
//		if (btnList == null)
//			btnList = new LinkedList<Btn>();
		btnList.add(this);

	}

//	public Btn(int x, int y, int width, int height, String text, Color color, Color textcolor) {
//		this.x = x;
//		this.y = y;
//		this.width = width;
//		this.height = height;
//		this.text = text;
//		this.color = color;
//		this.textcolor = textcolor;
//		this.fill = false;
//	}

//	public Btn(int x, int y, int width, int height, String text, Color color, Color textcolor, boolean fill) {
//		this.x = x;
//		this.y = y;
//		this.width = width;
//		this.height = height;
//		this.text = text;
//		this.color = color;
//		this.textcolor = textcolor;
//		this.fill = fill;
//	}
//
	public Btn(int x, int y, int width, int height, String text, Color color, Color textcolor, boolean fill,
			int transparent, Scene scene, int ID) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.color = color;
		this.textcolor = textcolor;
		this.fill = fill;
		this.transparent = transparent;
		this.scene = scene;
		this.ID = ID;
		btnList.add(this);

	}

//	public Btn(int x, int y, int width, int height, String text, Color color, Color textcolor, boolean fill,
//			int transparent, int arc) {
//		this.x = x;
//		this.y = y;
//		this.width = width;
//		this.height = height;
//		this.text = text;
//		this.color = color;
//		this.textcolor = textcolor;
//		this.fill = fill;
//		this.transparent = transparent;
//		this.arc = arc;
//	}

//	public static void renderBtns(Graphics g) {
//		for (Btn tempBtn : btnList)
//			if (Game.activeScene == tempBtn.getScene()) {
//				tempBtn.render(g);
//			}
//	}

	public void render(Graphics g) {
		g.setColor(textcolor);
		g.setFont(new Font("Roboto", Font.BOLD, textSize));
		g.setColor(color);
		if (transparent != 0) {
			Color.BLACK.getRGB();
			g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), transparent));
		}
		if (fill == true)
			g.fillRoundRect(getX(), getY(), getWidth(), getHeight(), arc, arc);
		g.setColor(textcolor);
		if (transparent != 0) {
			Color.BLACK.getRGB();
			g.setColor(new Color(textcolor.getRed(), textcolor.getGreen(), textcolor.getBlue(), transparent));
		}
		g.drawRoundRect(getX(), getY(), getWidth(), getHeight(), arc, arc);
//		g.setColor(Color.RED);
//		g.drawRoundRect(getX(), getY(), getWidth(), getHeight(), 10, 10);
		FontMetrics fm = g.getFontMetrics();
		int stringx = (getWidth() - fm.stringWidth(text)) / 2;
		int stringy = (fm.getAscent() + (getHeight() - (fm.getAscent() + fm.getDescent())) / 2);
		g.drawString(text, getX() + stringx, getY() + stringy);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public static LinkedList<Btn> getBtnList() {
		return btnList;
	}

	public boolean checkHitbox(Point p) {
		if (p.x > x && p.x < x + width && p.y > y && p.y < y + height)
			return true;
		return false;
	}
}
