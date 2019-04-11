import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.io.*;

public class DigitRecognizer {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File labelfile = new File("train-labels-idx1-ubyte");
        File imagefile = new File("train-images-idx3-ubyte");
        File testlabelfile = new File("t10k-labels-idx1-ubyte");
        File testimagefile = new File("t10k-images-idx3-ubyte");

        INDArray training_data[][] = new INDArray[][]{parseImageFile(imagefile, 60000), parseLabelFile(labelfile, 60000)};
        INDArray validation_data[][] = new INDArray[2][10000];
        for (int i = 50000; i < 60000; i++) {
            validation_data[0][i - 50000] = training_data[0][i];
            validation_data[1][i - 50000] = training_data[1][i];
        }
        INDArray shortertraining_data[][] = new INDArray[2][50000];
        for (int i = 0; i < 50000; i++) {
            shortertraining_data[0][i] = training_data[0][i];
            shortertraining_data[1][i] = training_data[1][i];
        }
        training_data = shortertraining_data;
        INDArray test_data[][] = new INDArray[][]{parseImageFile(testimagefile, 10000), parseLabelFile(testlabelfile, 10000)};

        Network net = null;

        // train the network
        for (int epochs = 3; epochs <= 3; epochs = epochs + 10) {
            for (int batchsize = 20; batchsize <= 20; batchsize = batchsize + 10) {
                net = new Network(new int[]{784, 30, 10});
                System.out.printf("Epochs: %d, Batchsize: %d\n", epochs, batchsize);
//                net.SGD(training_data, epochs, batchsize, 3.0, validation_data);
                net.SGD(training_data, epochs, batchsize, 0.5, validation_data, false);
            }
        }

        // let's run our network on randomly generated image
        INDArray input = Nd4j.rand(784,1);
        INDArray a = net.feedforward(input);
        System.out.println(a);

        // open our digitpad window to display the randomly generated image
        // showing the network's best guess "a"
        // and allow the user to interact with the digitpad to draw new digits and see the network in action
        // note - important to scale the result down to 28x28 pixel image
    }

    // returns INDArray[] of pixels for each image ... array[0] is INDArray of pixels for Image 0, etc...
    public static INDArray[] parseImageFile(File f, int max) throws FileNotFoundException, IOException {
        InputStream filein = new FileInputStream(f);
        byte buffer[] = new byte[4];
        filein.read(buffer);
        int magicnumber = new BigInteger(buffer).intValue();
        if (magicnumber != 2051) {
            System.err.println("Invalid image file!");
            System.exit(1);
        }
        filein.read(buffer);
        int numimages = new BigInteger(buffer).intValue();
        System.out.println("Reading image file ... " + max + " images.");

        filein.read(buffer);
        int rows = new BigInteger(buffer).intValue();
        filein.read(buffer);
        int cols = new BigInteger(buffer).intValue();

        INDArray alltheimages[] = new INDArray[numimages];
        for(int i=0; i<max; i++) {
            INDArray pixels = Nd4j.zeros(rows*cols, 1); // column vector
            byte imgbytes[][] = new byte[rows][cols];
            for(int row=0; row<rows; row++) {
                byte colbytes[] = new byte[cols];
                filein.read(colbytes);
                imgbytes[row] = colbytes;
            }
//            dumpImage(imgbytes, "digit"+i); // write this out to our thumbnail folder
            int fi = 0; // use this index to flatten the 2d array into the 1d pixels array
            for(int row=0; row<rows; row++) {
                for (int col=0; col<cols; col++) {
                    pixels.putScalar(fi++, (imgbytes[row][col]&0xFF)/256.0); // NN algorithm requires greyscale pixel intensity in range from 0.0 to 1.0
                }
            }
            alltheimages[i] = pixels;
        }
        filein.close();
        if (max>numimages) {
            return alltheimages;
        } else {
            return java.util.Arrays.copyOfRange(alltheimages, 0, max);
        }
    }

    public static INDArray[] parseLabelFile(File f, int max) throws FileNotFoundException, IOException {
        InputStream filein = new FileInputStream(f);
        byte buffer[] = new byte[4];
        filein.read(buffer);
        int magicnumber = new BigInteger(buffer).intValue();
        if (magicnumber != 2049) {
            System.err.println("Invalid label file!");
            System.exit(1);
        }
        filein.read(buffer);
        int numdigits = new BigInteger(buffer).intValue();
        System.out.println("Reading label file ... " + max + " digits.");
        INDArray allthedigits[] = new INDArray[numdigits];
        for(int i=0; i<numdigits; i++) {
            byte b[] = new byte[1];
            filein.read(b);
            allthedigits[i] = vectorize(b[0]);
        }
        filein.close();
        if (max>numdigits) {
            return allthedigits;
        } else {
            return java.util.Arrays.copyOfRange(allthedigits, 0, max);
        }
    }

    // return "y" as 10 element vector with a zero in all positions except desired output which has a 1
    public static INDArray vectorize(byte b) {
        INDArray y = Nd4j.zeros( 10, 1);
        for (int i=0; i<10; i++) {
            if (i==b) {
                y.putScalar(i,1);
            }
        }
        return y;
    }

    public static void dumpImage(byte imgbytes[][], String fn) throws IOException {
        int targetdim = 28;
        BufferedImage img = new BufferedImage(targetdim, targetdim, BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < targetdim; x++) {
            for (int y = 0; y < targetdim; y++) {
                int pixelval = 255 << 24 | imgbytes[x][y] << 16 | imgbytes[x][y] << 8 | imgbytes[x][y];
                img.setRGB(x, y, pixelval);
            }
        }
        if (Math.random()<0.005) {
            displayImage(img);
        }
        ImageIO.write(img, "jpg", new File("digits/" + fn + ".jpg"));
    }

    public static void displayImage(BufferedImage bufferedImage, String title) {
        ImageIcon icon=new ImageIcon(bufferedImage);
        JFrame frame=new JFrame(title);
        frame.setLayout(new FlowLayout());
        frame.setSize(bufferedImage.getWidth()+150,bufferedImage.getHeight()+80);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void displayImage(BufferedImage bufferedImage) {
        displayImage(bufferedImage,"Untitled");
    }
}
