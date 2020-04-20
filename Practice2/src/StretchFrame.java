import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StretchFrame extends JFrame {
    JPanel cotrolPanelMain = new JPanel();

    JPanel cotrolPanelShow = new JPanel();

    ImagePanel imagePanel;

    ImagePanel imagePanel2;

    JButton btnShow = new JButton("Show)");

    JButton btnStretch1 = new JButton("Stretch 1 (min-max)");

    JButton btnStretch2 = new JButton("Stretch 2 (histogram)");

    JLabel labFile = new JLabel("File Name");

    JTextField tfFile = new JTextField(30);

    String filename = "C:/Users/Derrick/Desktop/HW4/Munich_gray_dark.png";

    int[][][] data;

    int[][][] newData;

    int height;

    int width;

    BufferedImage img = null;

    StretchFrame() {
        setBounds(0, 0, 1500, 1500);
        getContentPane().setLayout((LayoutManager)null);
        setTitle("Image Stretching");
        this.tfFile.setText(this.filename);
        this.cotrolPanelMain = new JPanel();
        this.cotrolPanelMain.setLayout(new GridLayout(6, 1));
        this.cotrolPanelShow.add(this.btnShow);
        this.cotrolPanelShow.add(this.btnStretch1);
        this.cotrolPanelShow.add(this.btnStretch2);
        this.cotrolPanelShow.add(this.labFile);
        this.cotrolPanelShow.add(this.tfFile);
        this.cotrolPanelMain.add(this.cotrolPanelShow);
        this.cotrolPanelMain.setBounds(0, 0, 1200, 200);
        getContentPane().add(this.cotrolPanelMain);
        this.imagePanel = new ImagePanel();
        this.imagePanel.setBounds(20, 220, 720, 620);
        getContentPane().add(this.imagePanel);
        this.imagePanel2 = new ImagePanel();
        this.imagePanel2.setBounds(650, 220, 1500, 1500);
        getContentPane().add(this.imagePanel2);
        this.btnShow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                StretchFrame.this.makeData();
                Graphics g = StretchFrame.this.imagePanel.getGraphics();
                StretchFrame.this.imagePanel.paintComponent(g, StretchFrame.this.data);
            }
        });
        this.btnStretch1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int min = StretchFrame.this.data[0][0][0];
                int max = StretchFrame.this.data[0][0][0];
                StretchFrame.this.newData = new int[StretchFrame.this.height][StretchFrame.this.width][3];
                int j;
                for (j = 0; j < StretchFrame.this.height; j++) {
                    for (int i = 0; i < StretchFrame.this.width; i++) {
                        if (StretchFrame.this.data[j][i][0] < min)
                            min = StretchFrame.this.data[j][i][0];
                        if (StretchFrame.this.data[j][i][0] > max)
                            max = StretchFrame.this.data[j][i][0];
                    }
                }
                System.out.println("min = " + min + "  max = " + max);
                for (j = 0; j < StretchFrame.this.height; j++) {
                    for (int i = 0; i < StretchFrame.this.width; i++) {
                        double tmp = 255.0D * (1.0D * StretchFrame.this.data[j][i][0] - min) / (max - min);
                        int newValue = Util.checkPixelBounds((int)Math.round(tmp));
                        for (int c = 0; c < 3; c++)
                            StretchFrame.this.newData[j][i][c] = newValue;
                    }
                }
                Graphics g = StretchFrame.this.imagePanel2.getGraphics();
                StretchFrame.this.imagePanel2.paintComponent(g, StretchFrame.this.newData);
            }
        });
        this.btnStretch2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int min = StretchFrame.this.data[0][0][0];
                int max = StretchFrame.this.data[0][0][0];
                int[] histogram = new int[256];
                StretchFrame.this.newData = new int[StretchFrame.this.height][StretchFrame.this.width][3];
                for (int j = 0; j < StretchFrame.this.height; j++) {
                    for (int m = 0; m < StretchFrame.this.width; m++) {
                        for (int h = 0; h < 256; h++) {
                            if (StretchFrame.this.data[j][m][0] == h)
                                histogram[h] = histogram[h] + 1;
                        }
                    }
                }
                class Mapping {
                    int originalValue;

                    int cumulatedCount;

                    public Mapping(int i, int cumulatedCount) {
                    }
                };
                List<Mapping> list = new ArrayList<>();
                int cumulatedCount = 0;
                for (int i = 0; i < 256; i++) {
                    if (histogram[i] != 0) {
                        cumulatedCount += histogram[i];
                        list.add(new Mapping(i, cumulatedCount));
                    }
                }
                for (int k = 0; k < StretchFrame.this.height; k++) {
                    for (int m = 0; m < StretchFrame.this.width; m++) {
                        for (Mapping mapping : list) {
                            if (StretchFrame.this.data[k][m][0] == mapping.originalValue) {
                                double tmp = 255.0D * mapping.cumulatedCount / 1.0D * StretchFrame.this.width * StretchFrame.this.height;
                                int mappedValue = Util.checkPixelBounds((int)tmp);
                                for (int c = 0; c < 3; c++)
                                    StretchFrame.this.newData[k][m][c] = mappedValue;
                            }
                        }
                    }
                }
                Graphics g = StretchFrame.this.imagePanel2.getGraphics();
                StretchFrame.this.imagePanel2.paintComponent(g, StretchFrame.this.newData);
            }
        });
    }

    private JTextField JTextField() {
        return null;
    }

    void makeData() {
        try {
            this.filename = this.tfFile.getText();
            this.img = ImageIO.read(new File(this.filename));
        } catch (IOException e) {
            System.out.println("IO exception");
        }
        this.height = this.img.getHeight();
        this.width = this.img.getWidth();
        this.data = new int[this.height][this.width][3];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int rgb = this.img.getRGB(x, y);
                this.data[y][x][0] = Util.getR(rgb);
                this.data[y][x][1] = Util.getG(rgb);
                this.data[y][x][2] = Util.getB(rgb);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new StretchFrame();
        frame.setSize(1500, 1500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(3);
    }
}
