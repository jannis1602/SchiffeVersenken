package Image;

import java.awt.image.BufferedImage;

public class SpriteSheet {

	private BufferedImage image;
	
	public SpriteSheet(BufferedImage image) {
		this.image=image;
		
	}
	
	public BufferedImage grabImage(int col,int row,int width,int hieght) {
		return image.getSubimage((col*32)-32, (row*32)-32, width, hieght);
	}
	public BufferedImage grabImage64(int col,int row,int width,int hieght) {
		return image.getSubimage((col*64)-64, (row*64)-64, width, hieght);
	}
	public BufferedImage grabImage80(int col,int row,int width,int hieght) {
		return image.getSubimage((col*80)-80, (row*80)-80, width, hieght);
	}
	
}
