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

        // let's add a yellow border across the bottom
        for (int p=0; p<w; p++) {
            pixels[(h-1)*w*3+p*3+0] = 0; // -1 is 255 when unsigned
            pixels[(h-1)*w*3+p*3+1] = -1;
            pixels[(h-1)*w*3+p*3+2] = -1;
        }

        displayImage(bufferedImage);
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
