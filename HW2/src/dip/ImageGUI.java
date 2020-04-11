package dip;

import javax.swing.JFrame;

public class ImageGUI {

	public static void main(String[] args) {
		//BasicFrame frame = new BasicFrame();
		AffineFrame frame = new AffineFrame();
		frame.setVisible(true);
        frame.setSize(1500, 1500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
