package test;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class AffineFrame extends JFrame {

	private static final long serialVersionUID = 4390493953307669741L;
	JPanel cotrolPanel = new JPanel();
	ImagePanel imagePanel = new ImagePanel();
	JButton btnShow, btnTrans, btnScale, btnShear;
	
	JPanel bgColorPanel = new JPanel();
	JPanel transPanel = new JPanel();
	JPanel scalePanel = new JPanel();
	JPanel shearPanel = new JPanel();
	
	JTextField 
		bgR = new JTextField("0"), 
		bgG = new JTextField("0"), 
		bgB = new JTextField("0"),
		transX = new JTextField("0"),
		transY = new JTextField("0"),
		scaleX = new JTextField("0"),
		scaleY = new JTextField("0"),
		shearX = new JTextField("0"),
		shearY = new JTextField("0");
	
	JLabel 
		label_bgR = new JLabel("BG (R)　"),
		label_bgG = new JLabel("BG (G)　"),
		label_bgB = new JLabel("BG (B)　"),
		label_transX = new JLabel("x-delta"),
		label_transY = new JLabel("y-delta"),
		label_scaleX = new JLabel("x-ratio"),
		label_scaleY = new JLabel("y-artio"),
		label_shearX = new JLabel("x-ratio"),
		label_shearY = new JLabel("y-ratio ");
	
	final int[][][] data;
	int height, width;
	BufferedImage img = null;
	
	ActionListener buttonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == btnScale) processScale();
			
			else imagePanel.showImage(width, height, data);
		}
	};
	
	protected AffineFrame(){
		setTitle("Image Processing Homework 1: Affine Transform (Scaling Only)");
		
		try {
		    img = ImageIO.read(new File("file/Munich.png"));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		
		height = img.getHeight();
		width = img.getWidth();
		data = new int[height][width][3]; 
		
		this.setSize(width + 15, height + 77);
		
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++) {
				int rgb = img.getRGB(x, y);
				data[y][x][0] = Utils.getR(rgb);
				data[y][x][1] = Utils.getG(rgb);
				data[y][x][2] = Utils.getB(rgb);
			}
		
		btnShow = new JButton("Show");
		btnTrans = new JButton("Translation");
		btnScale = new JButton("Scaling");
		btnShear = new JButton("Shearing");
		
		btnShow.addActionListener(buttonActionListener);
		
		btnScale.addActionListener(buttonActionListener);
		
		
		// 顯示背景色
		bgColorPanel.setLayout(new GridLayout(3,2));
		bgColorPanel.add(label_bgR);
		bgColorPanel.add(bgR);
		bgColorPanel.add(label_bgG);
		bgColorPanel.add(bgG);
		bgColorPanel.add(label_bgB);
		bgColorPanel.add(bgB);
		
		// 平移
		transPanel.setLayout(new GridLayout(3,2));
		transPanel.add(label_transX);
		transPanel.add(transX);
		transPanel.add(label_transY);
		transPanel.add(transY);
		transPanel.add(btnTrans);
		
		// 縮放
		scalePanel.setLayout(new GridLayout(3,2));
		scalePanel.add(label_scaleX);
		scalePanel.add(scaleX);
		scalePanel.add(label_scaleY);
		scalePanel.add(scaleY);
		scalePanel.add(btnScale);
		
		// 斜切
		shearPanel.setLayout(new GridLayout(3,2));
		shearPanel.add(label_shearX);
		shearPanel.add(shearX);
		shearPanel.add(label_shearY);
		shearPanel.add(shearY);
		shearPanel.add(btnShear);
		
		
		cotrolPanel.add(btnShow);
		cotrolPanel.add(new JPanel());
		cotrolPanel.add(bgColorPanel);
		cotrolPanel.add(new JPanel());
		cotrolPanel.add(transPanel);
		cotrolPanel.add(new JPanel());
		cotrolPanel.add(scalePanel);
		cotrolPanel.add(new JPanel());
		cotrolPanel.add(shearPanel);
		
		setLayout(new BorderLayout());	 
	    add(cotrolPanel, BorderLayout.PAGE_START);
	    add(imagePanel, BorderLayout.CENTER);
	}
	
	/**
	 * 用新背景色填充
	 * @param data 要填充的陣列
	 */
	private void refillBgColor(int [][][] data) {
		int newR = Utils.checkPixelBound(Integer.parseInt(bgR.getText().length() == 0?"0":bgR.getText()));
		int newG = Utils.checkPixelBound(Integer.parseInt(bgG.getText().length() == 0?"0":bgG.getText()));
		int newB = Utils.checkPixelBound(Integer.parseInt(bgB.getText().length() == 0?"0":bgB.getText()));
		
		if (newR == 0 && newG == 0 && newB == 0) return;
		
		for (int [][] eachLine : data)
			for (int [] pixel : eachLine) {
				pixel[0] = newR;
				pixel[1] = newG;
				pixel[2] = newB;
			}
	}
	
	private void processTrans() {
		int dX = Integer.parseInt(transX.getText().length() == 0?"0":transX.getText());
		int dY = Integer.parseInt(transY.getText().length() == 0?"0":transY.getText());
		
		if (dY < -data.length) dY = -data.length;
		if (dX < -data[0].length) dX = -data[0].length;
		
		int [][][] nImage = new int [data.length + dY][data[0].length + dX][3];
		refillBgColor(nImage);
		
		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data[0].length; x++) {
				int [][] pos = {{x}, {y}, {1}};
				int [][] newPos = Utils.multiply(new int [][]
						{{1, 0, dX},
						{0, 1, dY},
						{0, 0, 1}}, 
						pos);
				
				int ny = newPos[1][0], nx = newPos[0][0];
				
				// 超出範圍的不用處理 (防呆)
				if (nx < 0 || ny < 0 || nx >= nImage[0].length || ny >= nImage.length) continue;
				
				nImage[ny][nx] = data[y][x];
			}
		}
		imagePanel.showImage(nImage[0].length, nImage.length, nImage);
	}
	
	private void processScale() {
		double zX = Double.parseDouble(scaleX.getText().length() == 0?"0.0":scaleX.getText());
		double zY = Double.parseDouble(scaleY.getText().length() == 0?"0.0":scaleY.getText());
		
		if (zX < 0.0) zX = 0.0;
		if (zY < 0.0) zY = 0.0;
		
		int [][][] nImage = new int [(int)(data.length * zY)][(int)(data[0].length * zX)][3];
		
		for (int y = 0 ; y < nImage.length ; y++) {
			for (int x = 0 ; x < nImage[0].length ; x++) {
				
				double [][] pos = {{x}, {y}, {1.0}};
				double [][] newPos = Utils.multiply(new double [][]
						{{1/zX, 0.0, 0.0},
						{0.0, 1/zY, 0.0},
						{0.0, 0.0, 1.0}}, 
						pos);
				
				double mapX = newPos[0][0];
				double mapY = newPos[1][0];

				nImage[y][x] = Utils.bilinearColor(data, mapX, mapY);
			}
		}
		imagePanel.showImage(nImage.length==0?0:nImage[0].length, nImage.length, nImage);
	}

	private void processShear() {
		double sY = Double.parseDouble(shearX.getText().length() == 0?"0.0":shearX.getText());
		double sX = Double.parseDouble(shearY.getText().length() == 0?"0.0":shearY.getText());

		int offsetX = (int) (sY * height), offsetY = (int) (width * sX);
		int height = data.length, width = data[0].length;
		int [][][] nImage = new int [Math.abs(offsetY) + height][width + Math.abs(offsetX)][3];
		refillBgColor(nImage);
		
		int [][] newPos = getCornerPos(sX, sY);
		int oX = offsetX < 0 ? offsetX : 0, oY = offsetY < 0 ? offsetY : 0;
		Area area = new Area( new Polygon(
				new int [] {newPos[0][0]-oX,newPos[1][0]-oX,newPos[2][0]-oX,newPos[3][0]-oX},
				new int [] {newPos[0][1]-oY,newPos[1][1]-oY,newPos[2][1]-oY,newPos[3][1]-oY}, 4));
		
		for (int y = 0; y < nImage.length; y++) {
			for (int x = 0; x < nImage[0].length; x++) {
				if (!area.contains(x, y)) continue;
				double [] p = getShearePosRev(offsetX < 0 ? x + offsetX : x, offsetY < 0 ? y + offsetY : y, sX, sY);
				if (p[0] < 0.0 || p[1] < 0.0 || p[0] >= data[0].length || p[1] >= data.length) continue;
				nImage[y][x] = Utils.bilinearColor(data, p[0], p[1]);
			}
		}
		imagePanel.showImage(nImage[0].length, nImage.length, nImage);
	}
	
	private int [][] getCornerPos(double sX, double sY) {
		int [] 	posA = getShearePos(0, 0, sX, sY), 
				posB = getShearePos(data[0].length, 0, sX, sY),
				posC = getShearePos(data[0].length, data.length, sX, sY),
				posD = getShearePos(0, data.length, sX, sY);
		return new int [][] {posA, posB, posC, posD};
	}
	
	private int [] getShearePos(int x, int y, double sX, double sY) {
		double [][] pos = {{x}, {y}, {1.0}};
		double [][] newPos = Utils.multiply(new double [][]
				{{1.0, sY, 0.0},
				{sX, 1.0, 0.0},
				{0.0, 0.0, 1.0}}, 
				pos);
		
		double ny = newPos[1][0], nx = newPos[0][0];
		return new int[] {(int)nx, (int) ny};
	}
	
	private double [] getShearePosRev(int x, int y, double sX, double sY) {
		double [][] pos = {{x}, {y}, {1.0}};
		double [][] newPos = Utils.multiply(new double [][]
				{{-1.0/(sX*sY-1.0), sY/(sX*sY-1.0), 0.0},
				{sX/(sX*sY-1.0), -1.0/(sX*sY-1.0), 0.0},
				{0.0, 0.0, 1.0}}, 
				pos);
		
		double ny = newPos[1][0], nx = newPos[0][0];
		return new double[] {nx, ny};
	}
}
