package dip;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import dip.Util;


public class ImagePanel extends JPanel {

	
	
	
	BufferedImage image;
	
	public void showImage(int width, int height, int [][][] data) {
		System.out.print("有被呼到");
		
		
		if (width == 0 || height == 0) {
			image = null;
			this.repaint();
			return;
		}
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int [] rgb = data[j][i];
				image.setRGB(i, j, Util.getRGB(rgb[0], rgb[1], rgb[2]));
			}
		}
		this.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) 
			g.drawImage(image, 0, 0, this);
	}
}

