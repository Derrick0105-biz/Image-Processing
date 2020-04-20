import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    boolean readyToPaint;

    public void paintComponent(Graphics g, int[][][] data, int xOffset, int yOffset) {
        paintComponent(g);
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < (data[y]).length; x++) {
                int r = data[y][x][0];
                int green = data[y][x][1];
                int b = data[y][x][2];
                g.setColor(new Color(r, green, b));
                double[][] matrix = { { 1.0D, 0.0D, xOffset }, { 0.0D, 1.0D, yOffset }, { 0.0D, 0.0D, 1.0D } };
                double[] oldPostion = { x, y, 1.0D };
                double[] newPosition = Util.affine(matrix, oldPostion);
                g.drawLine((int)newPosition[0],
                        (int)newPosition[1],
                        (int)newPosition[0],
                        (int)newPosition[1]);
            }
        }
    }

    public void paintComponent(Graphics g, int[][][] data) {
        paintComponent(g, data, 0, 0);
    }

    public void paintComponent(Graphics g, int[][][] data, Point[] originalPoints, Point[] mappedPoints) {
        paintComponent(g);
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < (data[y]).length; x++) {
                int r = data[y][x][0];
                int green = data[y][x][1];
                int b = data[y][x][2];
                g.setColor(new Color(r, green, b));
                double[][] matrix = { { 1.0D, 0.0D, 0.0D }, { 0.0D, 1.0D, 0.0D }, { 0.0D, 0.0D, 1.0D } };
                double[] oldPostion = { x, y, 1.0D };
                double[] newPosition = Util.affine(matrix, oldPostion);
                g.drawLine((int)newPosition[0],
                        (int)newPosition[1],
                        (int)newPosition[0],
                        (int)newPosition[1]);
            }
        }
        g.setColor(new Color(0, 255, 0));
        g.drawLine((mappedPoints[0]).x, (mappedPoints[0]).y, (mappedPoints[1]).x, (mappedPoints[1]).y);
        g.drawLine((mappedPoints[1]).x, (mappedPoints[1]).y, (mappedPoints[2]).x, (mappedPoints[2]).y);
        g.drawLine((mappedPoints[2]).x, (mappedPoints[2]).y, (mappedPoints[3]).x, (mappedPoints[3]).y);
        g.drawLine((mappedPoints[3]).x, (mappedPoints[3]).y, (mappedPoints[0]).x, (mappedPoints[0]).y);
        g.setColor(new Color(0, 255, 255));
        g.drawLine((originalPoints[0]).x, (originalPoints[0]).y, (originalPoints[1]).x, (originalPoints[1]).y);
        g.drawLine((originalPoints[1]).x, (originalPoints[1]).y, (originalPoints[2]).x, (originalPoints[2]).y);
        g.drawLine((originalPoints[2]).x, (originalPoints[2]).y, (originalPoints[3]).x, (originalPoints[3]).y);
        g.drawLine((originalPoints[3]).x, (originalPoints[3]).y, (originalPoints[0]).x, (originalPoints[0]).y);
    }
}
