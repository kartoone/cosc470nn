import java.awt.*;
import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.math.BigInteger;

public class ImagePreprocessor {
    public static void main(String[] args) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(new File("people.jpg"));
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        int[] unsigned = new int[pixels.length];
        for (int i=0; i<pixels.length; i++) {
            unsigned[i] = pixels[i] & 255;
        }
        System.out.println(java.util.Arrays.toString(pixels));
        System.out.println(java.util.Arrays.toString(unsigned));
        int w= bufferedImage.getWidth();
        int h= bufferedImage.getHeight();
        System.out.println(w);
        System.out.println(h);
        System.out.println(unsigned.length);
        System.out.println(w*h*3); // note that this the same as the length of the array
                                   // this means that for each pixel in the image, there are three bytes stored in BLUE, GREEN, RED order
        System.out.println(bufferedImage.getType());
        System.out.println(bufferedImage.TYPE_3BYTE_BGR); // pixel bytes are BLUE, GREEN, RED for each pixel

        // now let's investigate the layout of the image by changing up some bytes
        // let's set the top row to red

        System.out.println(unsigned[0]);
        System.out.println(unsigned[1]);
        System.out.println(unsigned[2]);
        // let's add a red border across the top
        for (int x=0; x<w; x++) {
            int p = bufferedImage.getRGB(x, 0);
            int a = (p >> 24) & 255;
            int r = (p >> 16) & 255;
            int g = (p >> 8) & 255;
            int b = p & 255;
            int newp = a<<24 | 128<<16 | 0<<8 | 0;
            bufferedImage.setRGB(x, 0, newp);
        }

        int peoplepixels[][][] = new int[28][28][48];

        double rightx=32;
        double leftx=4;
        double topy=7;
        double bottomy=30;
        double xinc = 37.5;
        double yinc = 47.5;

        int peoplei = 0;
        for (int peoplex=0; peoplex<8; peoplex++) {
            for (int peopley=0; peopley<6; peopley++) {
                int leftside = (int)Math.round(leftx+peoplex*xinc);
                int topside = (int)Math.round(topy+peopley*yinc);
                for (int x=leftside; x<leftside+28; x++) {
                    for (int y=topside; y<topside+28; y++) {
                        peoplepixels[x-leftside][y-topside][peoplei] = bufferedImage.getRGB(x, y);
                    }
                }
                BufferedImage peopleimg = new BufferedImage(28,28,BufferedImage.TYPE_3BYTE_BGR);
                for (int x=0; x<28; x++) {
                    for (int y=0; y<28; y++) {
                        peopleimg.setRGB(x,y,peoplepixels[x][y][peoplei]);
                    }
                }
                displayImage(peopleimg);
                dumpImage(peopleimg, "people"+peoplei+".jpg");
                peoplei++;
            }
        }

        // write all the cropped people out to a single file
        // binary file ...
        //  first byte - width of each person
        //  second byte - height of each person
        //  third byte - number of people in the file
        //  all the rest of the bytes are all the people pixels in row major order
        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File ("croppedpeople.bin")));
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(28);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(28);
        out.write(0);
        out.write(0);
        out.write(0);
        out.write(48);
        for (int pi=0; pi<48; pi++) {
            for (int x=0; x<28; x++) {
                for (int y=0; y<28; y++) {
                    byte buffer[] = intToByteArray(peoplepixels[x][y][pi]);
                    out.write(buffer);
                }
            }
        }
        out.close();


        // let's "find" the optimal place to crop person on the right
        for (rightx=32; rightx<w; rightx+=37.5) {
            for (int y = 0; y < h; y++) {
                int cyanp = 255 << 24 | 0 << 16 | 255 << 8 | 255;
                bufferedImage.setRGB((int)Math.round(rightx), y, cyanp);
            }
        }

        // let's find the crop spot on the left
        for (leftx=4; leftx<w; leftx+=37.5) {
            for (int y = 0; y < h; y++) {
                int cyanp = 255 << 24 | 0 << 16 | 255 << 8 | 255;
                bufferedImage.setRGB((int)Math.round(leftx), y, cyanp);
            }
        }

        // let's find the crop line for the top of each person
        for (topy=7; topy<h; topy+=47.5) {
            for (int x = 0; x < w; x++) {
                int cyanp = 255 << 24 | 0 << 16 | 255 << 8 | 255;
                bufferedImage.setRGB(x, (int)Math.round(topy), cyanp);
            }
        }

        // let's find the crop line for the top of each person
        for (bottomy=35; bottomy<h; bottomy+=47.5) {
            for (int x = 0; x < w; x++) {
                int cyanp = 255 << 24 | 0 << 16 | 255 << 8 | 255;
                bufferedImage.setRGB(x, (int)Math.round(bottomy), cyanp);
            }
        }

        displayImage(bufferedImage);

//        BufferedImage bufferedImage2 = ImageIO.read(new File("people.jpg"));
//        convertGrayscale(bufferedImage2);
//        displayImage(bufferedImage2);
//        byte[] pixels2 = ((DataBufferByte) bufferedImage2.getRaster().getDataBuffer()).getData();
//        unsigned = new int[pixels2.length/3];
//        for (int i=0; i<unsigned.length; i++) {
//            unsigned[i] = pixels2[i*3]&255;
//        }
//        System.out.println(java.util.Arrays.toString(unsigned));
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static boolean isBorder(byte pixels[], int w, int h) {
        // check for white along the first half of the pixels (skip left border though)
        for (int p=3; p<w/2; p++) {
            if (h+p>pixels.length) {
                return false;
            }
            int u = pixels[h+p]&255;
            if (u<200) {
                return false;
            }
        }
        return true;
    }

    // https://www.johndcook.com/blog/2009/08/24/algorithms-convert-color-grayscale/
    // luminosity method - 0.21 R + 0.72 G + 0.07 B
    public static void convertGrayscale(BufferedImage img) {
        //get image width and height
        int width = img.getWidth();
        int height = img.getHeight();

        //convert to grayscale
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = img.getRGB(x,y);

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                //calculate average
                int avg = (r+g+b)/3;

                //replace RGB value with avg
                p = (a<<24) | (avg<<16) | (avg<<8) | avg;

                img.setRGB(x, y, p);
            }
        }
    }

    public static void dumpImage(BufferedImage img, String filename) throws IOException {
        ImageIO.write(img, "jpg", new File(filename));
    }

    public static void displayImage(BufferedImage bufferedImage) {
        ImageIcon icon=new ImageIcon(bufferedImage);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(bufferedImage.getWidth()+150,bufferedImage.getHeight()+80);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
