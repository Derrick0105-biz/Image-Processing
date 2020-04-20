package dip;
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

import dip.Util;

public class AffineFrame extends JFrame {
    JPanel cotrolPanelMain = new JPanel();
    JPanel cotrolPanelShow = new JPanel();;
    JPanel cotrolPanelBackColor = new JPanel();;
    JPanel cotrolPanelTranslate = new JPanel();;
    JPanel cotrolPanelScale = new JPanel();
    JPanel cotrolPanelShear = new JPanel();;
    ImagePanel imagePanel;
    JButton btnShow;
    JButton btnTranslate;
    JButton btnScale;
    JButton btnShear;
    JTextField tfRed = new JTextField(5);
    JTextField tfGreen = new JTextField(5);
    JTextField tfBlue = new JTextField(5);
    JTextField tfDeltaX = new JTextField(5);
    JTextField tfDeltaY = new JTextField(5);
    JTextField tfAmpX = new JTextField(5);
    JTextField tfAmpY = new JTextField(5);
    JTextField  tfShearX= new JTextField(5);
    JTextField  tfShearY= new JTextField(5);
    JLabel lbRed = new JLabel("BG (R)");
    JLabel lbGreen = new JLabel("BG (G)");
    JLabel lbBlue = new JLabel("BG (B)");
    JLabel lbDeltaX = new JLabel("x-delta");
    JLabel lbDeltaY = new JLabel("y-delta");
    JLabel lbAmpX = new JLabel("x-ratio");
    JLabel lbAmpY = new JLabel("y-artio");
    JLabel lbShearY = new JLabel("x-ratio");
    JLabel lbShearX = new JLabel("y-ratio ");

    final int[][][] data;
    int height;
    int width;
    //BufferedImage img = null;

    ActionListener buttonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnTranslate) processTrans();
            else if (e.getSource() == btnScale) processScale();
            else if (e.getSource() == btnShear) processShear();
            else imagePanel.showImage(width, height, data);
        }
    };

    AffineFrame(){
        setBounds(0, 0, 1500, 1500);
        getContentPane().setLayout(null);
        tfRed.setText("0");
        tfGreen.setText("0");
        tfBlue.setText("0");
        tfDeltaX.setText("0");
        tfDeltaY.setText("0");
        tfAmpX.setText("1.0");
        tfAmpY.setText("1.0");
        tfShearX.setText("0.0");
        tfShearY.setText("0.0");

        setTitle("Image Processing Homework 1: Affine Transform (Scaling Only)");
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("file/abc123.jpg"));
        } catch (IOException e) {
            System.out.println("IO exception");
        }

        height = img.getHeight();
        width = img.getWidth();
        data = new int[height][width][3];

        for (int y=0; y<height; y++){
            for (int x=0; x<width; x++){
                int rgb = img.getRGB(x, y);
                data[y][x][0] = Util.getR(rgb);
                data[y][x][1] =  Util.getG(rgb);
                data[y][x][2] = Util.getB(rgb);
            }
        }

        btnShow = new JButton("Show");
        btnTranslate = new JButton("Translation");
        btnScale = new JButton("Scaling");
        btnShear = new JButton("Shearing");

        btnShow.addActionListener(buttonActionListener);
        btnTranslate.addActionListener(buttonActionListener);
        btnScale.addActionListener(buttonActionListener);
        btnShear.addActionListener(buttonActionListener);



        cotrolPanelMain = new JPanel();
        cotrolPanelMain.setLayout(new GridLayout(1,7));

        cotrolPanelShow.add(btnShow);
        cotrolPanelMain.add(cotrolPanelShow);

        cotrolPanelBackColor.add(lbRed);
        cotrolPanelBackColor.add(tfRed);
        cotrolPanelBackColor.add(lbGreen);
        cotrolPanelBackColor.add(tfGreen);
        cotrolPanelBackColor.add(lbBlue);
        cotrolPanelBackColor.add(tfBlue);
        cotrolPanelMain.add(cotrolPanelBackColor);

        cotrolPanelTranslate.add(lbDeltaX);
        cotrolPanelTranslate.add(tfDeltaX);
        cotrolPanelTranslate.add(lbDeltaY);
        cotrolPanelTranslate.add(tfDeltaY);
        cotrolPanelTranslate.add(btnTranslate);
        cotrolPanelMain.add(cotrolPanelTranslate);

        cotrolPanelScale.add(lbAmpX);
        cotrolPanelScale.add(tfAmpX);
        cotrolPanelScale.add(lbAmpY);
        cotrolPanelScale.add(tfAmpY);
        cotrolPanelScale.add(btnScale);
        cotrolPanelMain.add(cotrolPanelScale);

        cotrolPanelShear.add(lbShearY);
        cotrolPanelShear.add(tfShearY);
        cotrolPanelShear.add(lbShearX);
        cotrolPanelShear.add(tfShearX);
        cotrolPanelShear.add(btnShear);
        cotrolPanelMain.add(cotrolPanelShear);
        cotrolPanelMain.add(new JPanel());
        cotrolPanelMain.add(new JPanel());
        cotrolPanelMain.add(new JPanel());
        cotrolPanelMain.add(new JPanel());

        cotrolPanelMain.setBounds(0, 0,1200,150);
        getContentPane().add(cotrolPanelMain);
        imagePanel = new ImagePanel();
        imagePanel.setBounds(0,180, 1500,1500);
        getContentPane().add(imagePanel);
    }

    private void refillBgColor(int [][][] data) {
        int newR = Util.checkPixelBound(Integer.parseInt(tfRed.getText().length() == 0?"0":tfRed.getText()));
        int newG = Util.checkPixelBound(Integer.parseInt(tfGreen.getText().length() == 0?"0":tfGreen.getText()));
        int newB = Util.checkPixelBound(Integer.parseInt(tfBlue.getText().length() == 0?"0":tfBlue.getText()));

        if (newR == 0 && newG == 0 && newB == 0) return;

        for (int [][] eachLine : data)
            for (int [] pixel : eachLine) {
                pixel[0] = newR;
                pixel[1] = newG;
                pixel[2] = newB;
            }
    }

    private void processTrans() {
        int dX = Integer.parseInt(tfDeltaX.getText().length() == 0?"0":tfDeltaX.getText());
        int dY = Integer.parseInt(tfDeltaY.getText().length() == 0?"0":tfDeltaY.getText());

        if (dY < -data.length) dY = -data.length;
        if (dX < -data[0].length) dX = -data[0].length;

        int [][][] nImage = new int [data.length + dY][data[0].length + dX][3];
        refillBgColor(nImage);

        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[0].length; x++) {
                int [][] pos = {{x}, {y}, {1}};
                int [][] newPos = Util.multiply(new int [][]
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
        double zX = Double.parseDouble(tfAmpX.getText().length() == 0?"0.0":tfAmpX.getText());
        double zY = Double.parseDouble(tfAmpY.getText().length() == 0?"0.0":tfAmpY.getText());

        if (zX < 0.0) zX = 0.0;
        if (zY < 0.0) zY = 0.0;

        int [][][] nImage = new int [(int)(data.length * zY)][(int)(data[0].length * zX)][3];

        for (int y = 0 ; y < nImage.length ; y++) {
            for (int x = 0 ; x < nImage[0].length ; x++) {

                double [][] pos = {{x}, {y}, {1.0}};
                double [][] newPos = Util.multiply(new double [][]
                                {{1/zX, 0.0, 0.0},
                                        {0.0, 1/zY, 0.0},
                                        {0.0, 0.0, 1.0}},
                        pos);

                double mapX = newPos[0][0];
                double mapY = newPos[1][0];

                nImage[y][x] = Util.bilinearColor(data, mapX, mapY);
            }
        }
        imagePanel.showImage(nImage.length==0?0:nImage[0].length, nImage.length, nImage);
    }

    private void processShear() {
        double sY = Double.parseDouble(tfShearX.getText().length() == 0?"0.0":tfShearX.getText());
        double sX = Double.parseDouble(tfShearY.getText().length() == 0?"0.0":tfShearY.getText());

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
                nImage[y][x] = Util.bilinearColor(data, p[0], p[1]);
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
        double [][] newPos = Util.multiply(new double [][]
                        {{1.0, sY, 0.0},
                                {sX, 1.0, 0.0},
                                {0.0, 0.0, 1.0}},
                pos);

        double ny = newPos[1][0], nx = newPos[0][0];
        return new int[] {(int)nx, (int) ny};
    }

    private double [] getShearePosRev(int x, int y, double sX, double sY) {
        double [][] pos = {{x}, {y}, {1.0}};
        double [][] newPos = Util.multiply(new double [][]
                        {{-1.0/(sX*sY-1.0), sY/(sX*sY-1.0), 0.0},
                                {sX/(sX*sY-1.0), -1.0/(sX*sY-1.0), 0.0},
                                {0.0, 0.0, 1.0}},
                pos);

        double ny = newPos[1][0], nx = newPos[0][0];
        return new double[] {nx, ny};
    }
}
