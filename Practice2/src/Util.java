import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Util {
    static final int checkPixelBounds(int value) {
        if (value > 255)
            return 255;
        if (value < 0)
            return 0;
        return value;
    }

    static final int getR(int rgb) {
        return checkPixelBounds((rgb & 0xFF0000) >>> 16);
    }

    static final int getG(int rgb) {
        return checkPixelBounds((rgb & 0xFF00) >>> 8);
    }

    static final int getB(int rgb) {
        return checkPixelBounds(rgb & 0xFF);
    }

    static final int makeColor(int r, int g, int b) {
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    static final int covertToGray(int r, int g, int b) {
        return checkPixelBounds((int)(0.2126D * r + 0.7152D * g + 0.0722D * b));
    }

    static final double[] affine(double[][] a, double[] b) {
        int aRow = a.length;
        int bRow = b.length;
        double[] result = new double[aRow];
        for (int i = 0; i < aRow; i++) {
            for (int j = 0; j < bRow; j++)
                result[i] = result[i] + a[i][j] * b[j];
        }
        return result;
    }

    static final int bilinear(int leftTop, int rightTop, int leftBottom, int rightBottom, double alpha, double beta) {
        double left = linear(leftTop, leftBottom, alpha);
        double right = linear(rightTop, rightBottom, alpha);
        double value = linear(left, right, beta);
        return checkPixelBounds((int)value);
    }

    static final double linear(double v1, double v2, double weight) {
        return v1 + (v2 - v1) * weight;
    }

    static final int checkImageBounds(int value, int length) {
        if (value > length - 1)
            return length - 1;
        if (value < 0)
            return 0;
        return value;
    }

    static final double getHueFromRGB(int r, int g, int b) {
        double num = 0.5D * (r - g + r - b);
        double den = Math.sqrt(((r - g) * (r - g) + (r - b) * (g - b)));
        if (den == 0.0D)
            return 0.0D;
        double theta = 180.0D * Math.acos(num / den) / Math.PI;
        if (g <= b)
            theta = 360.0D - theta;
        return theta;
    }

    static final double getIntFromRGB(int r, int g, int b) {
        return (r + g + b) / 765.0D;
    }

    static final double getSatFromRGB(int r, int g, int b) {
        double min = r;
        if (g <= b && g <= r)
            min = g;
        if (b <= r && b <= g)
            min = b;
        return 1.0D - 3.0D * min / (r + g + b);
    }

    static final RGB getRGBFromHSI(double h, double s, double i) {
        double r, g, b;
        int theta = (int)h;
        while (theta < 0)
            theta += 360;
        h = (theta % 360);
        if (h >= 0.0D && h < 120.0D) {
            b = (1.0D - s) * i;
            double tmp = s * Math.cos(h * Math.PI / 180.0D) / Math.cos((60.0D - h) * Math.PI / 180.0D);
            r = (1.0D + tmp) * i;
            g = 3.0D * i - r + b;
        } else if (h > 120.0D && h <= 240.0D) {
            r = (1.0D - s) * i;
            double tmp = s * Math.cos((h - 120.0D) * Math.PI / 180.0D) / Math.cos((180.0D - h) * Math.PI / 180.0D);
            g = (1.0D + tmp) * i;
            b = 3.0D * i - r + g;
        } else {
            g = (1.0D - s) * i;
            double tmp = s * Math.cos((h - 240.0D) * Math.PI / 180.0D) / Math.cos((300.0D - h) * Math.PI / 180.0D);
            b = (1.0D + tmp) * i;
            r = 3.0D * i - g + b;
        }
        return new RGB(checkPixelBounds((int)(255.0D * r)),
                checkPixelBounds((int)(255.0D * g)),
                checkPixelBounds((int)(255.0D * b)));
    }

    static final void makeFile(int[][][] writedData, BufferedImage img, String filename) {
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = makeColor(writedData[y][x][0], writedData[y][x][1], writedData[y][x][2]);
                img.setRGB(x, y, rgb);
            }
        }
        try {
            File outputfile = new File(filename);
            ImageIO.write(img, "png", outputfile);
        } catch (IOException iOException) {}
    }

    static HSL RGB2HSL(double r, double g, double b) {
        double h = 0.0D;
        r /= 255.0D;
        g /= 255.0D;
        b /= 255.0D;
        double min = Math.min(Math.min(r, g), b);
        double max = Math.max(Math.max(r, g), b);
        double l = 0.5D * (min + max);
        double s = getS(min, max, l);
        if (s == 0.0D) {
            h = 0.0D;
        } else if (max == r) {
            h = (g - b) / (max - min) % 6.0D;
        } else if (max == g) {
            h = 2.0D + (b - r) / (max - min);
        } else if (max == b) {
            h = 4.0D + (r - g) / (max - min);
        }
        h *= 60.0D;
        if (h < 0.0D)
            h += 360.0D;
        if (h > 360.0D)
            h -= 360.0D;
        if (h < 0.0D || h > 360.0D)
            System.out.println("RGB2HSL: hue =" + h);
        return new HSL(h, s, l);
    }

    static double getS(double min, double max, double l) {
        if (min == max)
            return 0.0D;
        if (l <= 0.5D)
            return (max - min) / (max + min);
        return (max - min) / (2.0D - max - min);
    }

    /*static RGB HSL2RGB(double hue, double sat, double lum) {
        double r, t1;
        if (sat == 0.0D && hue == 0.0D)
            double d2 = lum, d1 = d2;
        if (lum < 0.5D) {
            t1 = lum * (1.0D + sat);
        } else {
            t1 = lum + sat - lum * sat;
        }
        double t2 = 2.0D * lum - t1;
        hue /= 360.0D;
        double tR = checkValue(hue + 0.333D);
        double tG = checkValue(hue);
        double tB = checkValue(hue - 0.333D);
        if (6.0D * tR < 1.0D) {
            r = t2 + (t1 - t2) * 6.0D * tR;
        } else if (2.0D * tR < 1.0D) {
            r = t1;
        } else if (3.0D * tR < 2.0D) {
            r = t2 + (t1 - t2) * (0.666D - tR) * 6.0D;
        } else {
            r = t2;
        }
        double g = t2 + (t1 - t2) * (0.666D - tG) * 6.0D;
        double b = t1;
        return new RGB(checkPixelBounds((int)(255.0D * r)),
                checkPixelBounds((int)(255.0D * g)),
                checkPixelBounds((int)(255.0D * b)));
    }*/

    static double checkValue(double v) {
        if (v < 0.0D)
            return v + 1.0D;
        if (v > 1.0D)
            return v - 1.0D;
        return v;
    }

    static RGB HSL2RGB_v2(double hue, double sat, double lum) {
        double r, g, b;
        if (hue > 360.0D)
            hue -= 360.0D;
        if (hue < 0.0D)
            hue += 360.0D;
        if (hue > 360.0D || hue < 0.0D)
            System.out.println("hue = " + hue);
        if (sat < 0.0D)
            sat = 0.0D;
        if (sat > 1.0D)
            sat = 1.0D;
        if (lum < 0.0D)
            lum = 0.0D;
        if (lum > 1.0D)
            lum = 1.0D;
        if (sat < 0.0D || sat > 1.0D)
            System.out.println("Care sat");
        if (lum < 0.0D || lum > 1.0D)
            System.out.println("Care lum");
        double c = (1.0D - Math.abs(2.0D * lum - 1.0D)) * sat;
        double x = c * (1.0D - Math.abs(hue / 60.0D % 2.0D - 1.0D));
        double m = lum - c / 2.0D;
        if (hue >= 0.0D && hue < 60.0D) {
            r = c;
            g = x;
            b = 0.0D;
        } else if (hue >= 60.0D && hue < 120.0D) {
            r = x;
            g = c;
            b = 0.0D;
        } else if (hue >= 120.0D && hue < 180.0D) {
            r = 0.0D;
            g = c;
            b = x;
        } else if (hue >= 180.0D && hue < 240.0D) {
            r = 0.0D;
            g = x;
            b = c;
        } else if (hue >= 240.0D && hue < 300.0D) {
            r = x;
            g = 0.0D;
            b = c;
        } else {
            r = c;
            g = 0.0D;
            b = x;
        }
        r += m;
        g += m;
        b += m;
        return new RGB(checkPixelBounds((int)(255.0D * r)),
                checkPixelBounds((int)(255.0D * g)),
                checkPixelBounds((int)(255.0D * b)));
    }

    static HSV RGB2HSV(double r, double g, double b) {
        double s, h = 0.0D;
        r /= 255.0D;
        g /= 255.0D;
        b /= 255.0D;
        double min = Math.min(Math.min(r, g), b);
        double max = Math.max(Math.max(r, g), b);
        double delta = max - min;
        if (max == 0.0D) {
            s = 0.0D;
        } else {
            s = delta / max;
        }
        if (s == 0.0D) {
            h = 0.0D;
        } else if (max == r) {
            h = (g - b) / (max - min) % 6.0D;
        } else if (max == g) {
            h = 2.0D + (b - r) / (max - min);
        } else if (max == b) {
            h = 4.0D + (r - g) / (max - min);
        }
        h *= 60.0D;
        if (h < 0.0D)
            h += 360.0D;
        if (h > 360.0D)
            h -= 360.0D;
        if (h < 0.0D || h > 360.0D)
            System.out.println("RGB2HSV: hue =" + h);
        double v = max;
        return new HSV(h, s, v);
    }

    static RGB HSV2RGB(double hue, double sat, double val) {
        double r, g, b;
        if (hue > 360.0D)
            hue -= 360.0D;
        if (hue < 0.0D)
            hue += 360.0D;
        if (hue > 360.0D || hue < 0.0D)
            System.out.println("hue = " + hue);
        if (sat < 0.0D)
            sat = 0.0D;
        if (sat > 1.0D)
            sat = 1.0D;
        if (val < 0.0D)
            val = 0.0D;
        if (val > 1.0D)
            val = 1.0D;
        if (sat < 0.0D || sat > 1.0D)
            System.out.println("Care sat");
        if (val < 0.0D || val > 1.0D)
            System.out.println("Care sat");
        double c = val * sat;
        double x = c * (1.0D - Math.abs(hue / 60.0D % 2.0D - 1.0D));
        double m = val - c;
        if (hue >= 0.0D && hue < 60.0D) {
            r = c;
            g = x;
            b = 0.0D;
        } else if (hue >= 60.0D && hue < 120.0D) {
            r = x;
            g = c;
            b = 0.0D;
        } else if (hue >= 120.0D && hue < 180.0D) {
            r = 0.0D;
            g = c;
            b = x;
        } else if (hue >= 180.0D && hue < 240.0D) {
            r = 0.0D;
            g = x;
            b = c;
        } else if (hue >= 240.0D && hue < 300.0D) {
            r = x;
            g = 0.0D;
            b = c;
        } else {
            r = c;
            g = 0.0D;
            b = x;
        }
        r += m;
        g += m;
        b += m;
        return new RGB(checkPixelBounds((int)(255.0D * r)),
                checkPixelBounds((int)(255.0D * g)),
                checkPixelBounds((int)(255.0D * b)));
    }
}
