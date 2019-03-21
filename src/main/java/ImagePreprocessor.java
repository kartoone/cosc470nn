import java.awt.*;
import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;

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

        // let's add a red border across the top
        for (int p=0; p<w; p++) {
            pixels[p*3+0] = 0; // -1 is 255 when unsigned
            pixels[p*3+1] = 0;
            pixels[p*3+2] = -1;
        }

        // let's add a blue border down the right
        for (int p=1; p<=h; p++) {
            pixels[p*w*3-3] = -1;
            pixels[p*w*3-2] = 0;
            pixels[p*w*3-1] = 0;
        }

        // let's add a green border down the left
        for (int p=0; p<h; p++) {
            pixels[p*w*3+0] = 0;
            pixels[p*w*3+1] = -1;
            pixels[p*w*3+2] = 0;
        }

        // let's add a white border across the bottom
        for (int p=0; p<w; p++) {
            pixels[(h-1)*w*3+p*3+0] = -1; // -1 is 255 when unsigned
            pixels[(h-1)*w*3+p*3+1] = -1;
            pixels[(h-1)*w*3+p*3+2] = -1;
        }

        // now let's try to figure out dimensions of each image
        // drawing red line between images
        for (int i=0; i<h; i++) {
            if (isBorder(pixels, w,i*w*3)) {
                for (int p = 0; p < w; p++) {
                    pixels[i * w * 3 + p * 3 + 0] = 0; // -1 is 255 when unsigned
                    pixels[i * w * 3 + p * 3 + 1] = 0;
                    pixels[i * w * 3 + p * 3 + 2] = -1;
                }
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

    public static void displayImage(BufferedImage bufferedImage) {
        ImageIcon icon=new ImageIcon(bufferedImage);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(bufferedImage.getWidth()+50,bufferedImage.getHeight()+80);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
