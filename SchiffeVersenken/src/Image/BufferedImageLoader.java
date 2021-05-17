package Image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BufferedImageLoader {
	/**
	 * Use to load a image
	 * 
	 */

	public BufferedImage loadImage(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Image Error!");
		}
		return image;
	}
}
