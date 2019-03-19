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
        // display image in window
        displayImage(bufferedImage);

        // now let's investigate the layout of the image by changing up some bytes
        // let's set the top row to red
        BufferedImage bufferedImage2 = ImageIO.read(new File("people.jpg"));
        byte[] pixels2 = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        for (int i=0; i<w; i++) {
            pixels2[(i+1)*3-3] = 0;
            pixels2[(i+1)*3-2] = 0;
            pixels2[(i+1)*3-1] = -1; // this is 255 when treated as unsigned
        }

        // now let's set the right border to green
        for (int i=0; i<h; i++) {
            pixels2[w*3*(i+1)-3] = 0;
            pixels2[w*3*(i+1)-2] = -1;
            pixels2[w*3*(i+1)-1] = 0;
        }

        // now let's set the left border to blue
        for (int i=0; i<h; i++) {
            pixels2[w*3*i] = -1;
            pixels2[w*3*i+1] = 0;
            pixels2[w*3*i+2] = 0;
        }

        // now let's set the bottom border to yellow
        for (int i=0; i<bufferedImage2.getWidth(); i++) {
            pixels2[(h-1)*w*3+(i+1)*3-3] = 0;
            pixels2[(h-1)*w*3+(i+1)*3-2] = -1;
            pixels2[(h-1)*w*3+(i+1)*3-1] = -1; // this is 255 when treated as unsigned
        }

        displayImage(bufferedImage2);
    }

    public static void displayImage(BufferedImage bufferedImage) {
        ImageIcon icon=new ImageIcon(bufferedImage);
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(bufferedImage.getWidth(),bufferedImage.getHeight()+60);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
