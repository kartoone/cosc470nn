import java.awt.*;
import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.BufferOverflowException;

public class ImagePreprocessor {
    public static void main(String[] args) throws Exception {
//        int peoplepixels[][][] = processPeople();
        int peoplepixels[][][] = processMugshots(-1, 28);
        writePeople(peoplepixels);
    }

    public static void writePeople(int peoplepixels[][][]) throws IOException {
        int peoplelength = peoplepixels.length;

        // write all the cropped people out to a single file
        // binary file ...
        //  first byte - width of each person
        //  second byte - height of each person
        //  third byte - number of people in the file
        //  all the rest of the bytes are all the people pixels in row major order
        OutputStream out = new BufferedOutputStream(new FileOutputStream(new File ("croppedpeople.bin")));
        int w = peoplepixels[0].length;
        int h = peoplepixels[0][0].length;
        out.write(intToByteArray(w));
        out.write(intToByteArray(h));
        out.write(intToByteArray(peoplelength));
        for (int pi=0; pi<peoplelength; pi++) {
            for (int x=0; x<w; x++) {
                for (int y=0; y<h; y++) {
                    byte buffer[] = intToByteArray(peoplepixels[pi][x][y]);
                    out.write(buffer);
                }
            }
        }
        out.close();
    }

    // if limit < 0, then count the number of the files in the directory and import them all
    public static int[][][] processMugshots(int limit, int targetdim) throws IOException {
        File f = new File("mugshots/f");
        File mugshotfiles[] = f.listFiles();

        // if limit < 0, then count the number of the files and do them all
        if (limit<0) {
            limit = 0;
            for (File mf : mugshotfiles) {
                if (!mf.isDirectory()) {
                    limit++; // only count the files (not the directories)
                }
            }
        }

        // targetdim is width, height in pixels
        int peoplepixels[][][] = new int[limit][targetdim][targetdim];

        // have to use two different indices here since mugshotfiles[i] could be a directory
        int pi = 0;
        for (int i=0; pi<limit; i++) {
            if (!mugshotfiles[i].isDirectory()) {
                peoplepixels[pi] = processFile(mugshotfiles[i], targetdim);
                BufferedImage peopleimg = new BufferedImage(targetdim, targetdim, BufferedImage.TYPE_3BYTE_BGR);
                for (int x=0; x<targetdim; x++) {
                    for (int y=0; y<targetdim; y++) {
                        peopleimg.setRGB(x,y,peoplepixels[pi][x][y]);
                    }
                }
                if (Math.random()<0.005)
                    displayImage(peopleimg);
                dumpImage(peopleimg, "mugshots/ft"+pi+".jpg");
                pi++;
            }
        }

        return peoplepixels;
    }

    private static int[][] processFile(File mugshotfile, int targetdim) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(mugshotfile.getPath()));
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        int dim = Math.min(w,h);
        System.out.println(w + "x" + h);
        int rawpixels[] = new int[dim*dim];
        int offsetx = 0;
        int offsety = 0;
        if (w==dim) {
            offsety = (h-w)/2;
        } else {
            offsetx = (w-h)/2;
        }
        bufferedImage.getRGB(offsetx, offsety, dim, dim, rawpixels, 0, dim);

        // create a new image with the cropped data
        BufferedImage croppedimg = new BufferedImage(dim,dim,BufferedImage.TYPE_BYTE_GRAY);
        for (int x=0; x<dim; x++) {
            for (int y=0; y<dim; y++) {
                croppedimg.setRGB(y,x,rawpixels[x*dim+y]);
            }
        }

        // now scale the image and get the scaled pixels
        BufferedImage scaledimg = toBufferedImage(croppedimg.getScaledInstance(targetdim, targetdim, BufferedImage.SCALE_AREA_AVERAGING));
        int scaledpixels[] = new int[targetdim*targetdim];
        scaledimg.getRGB(0,0,targetdim, targetdim, scaledpixels, 0, targetdim);

        // finally turn the scaled pixels into 2d array (i.e., unflatten it)
        int pixels[][] = new int[targetdim][targetdim];
        for (int row=0; row<targetdim; row++) {
            for (int col=0; col<targetdim; col++) {
                int pos = row*targetdim + col;
                pixels[col][row] = scaledpixels[pos];
            }
        }

        return pixels;
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static int[][][] processPeople() throws IOException {
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

        int peoplepixels[][][] = new int[48][28][28];
        // determined experimentally by drawing lines using img.setRGB
        // that code has been removed and only the result remains
        double leftx=4;
        double topy=7;
        double xinc = 37.5;
        double yinc = 47.5;

        int peoplei = 0;
        for (int peoplex=0; peoplex<8; peoplex++) {
            for (int peopley=0; peopley<6; peopley++) {
                int leftside = (int)Math.round(leftx+peoplex*xinc);
                int topside = (int)Math.round(topy+peopley*yinc);
                for (int x=leftside; x<leftside+28; x++) {
                    for (int y=topside; y<topside+28; y++) {
                        peoplepixels[peoplei][x-leftside][y-topside] = bufferedImage.getRGB(x, y);
                    }
                }
                BufferedImage peopleimg = new BufferedImage(28,28,BufferedImage.TYPE_3BYTE_BGR);
                for (int x=0; x<28; x++) {
                    for (int y=0; y<28; y++) {
                        peopleimg.setRGB(x,y,peoplepixels[peoplei][x][y]);
                    }
                }
                displayImage(peopleimg);
                dumpImage(peopleimg, "people"+peoplei+".jpg");
                peoplei++;
            }
        }
        return peoplepixels;
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
