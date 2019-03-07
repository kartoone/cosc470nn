import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.math.BigInteger;

public class DigitRecognizerBinary {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File labelfile = new File("train-labels-idx1-ubyte");
        File imagefile = new File("train-images-idx3-ubyte");
        File testlabelfile = new File("t10k-labels-idx1-ubyte");
        File testimagefile = new File("t10k-images-idx3-ubyte");

        INDArray training_data[][] = new INDArray[][] { parseImageFile(imagefile, 60000), parseLabelFile(labelfile, 60000) };
        INDArray test_data[][] =  new INDArray[][] { parseImageFile(testimagefile, 10000), parseLabelFile(testlabelfile, 10000) };

        // train the network with just three layers
        Network net = new Network(new int[]{784, 30, 10});

        // train the network
        net.SGD(training_data, 2, 10, 3.0, test_data);

        // now add the new layer, which doesn't require any training
        // setup the new weights and biases
        // biases at each bit simply need to be 0.99 based on assumption given in exercise ... i.e., if any output from previous layer is correct, then it should also trip the new output layer
        // weights need to be 1.0 for the bit position(s) that should be a 1 in the output layer
        // [[ 0.0 0.0 0.0 0.0
        //  [ 0.0 0.0 0.0 0.0
        //  [ 0.0 0.0 1.0 1.0
        //  [ 0.0 1.0 0.0 1.0 ...
        INDArray layerweights = Nd4j.zeros(4,10);
        for (int i=0; i<10; i++) {
            System.out.print(i+": ");
            int bit3pos = (i&8) >> 3; // binary for 8 is 1000
            int bit2pos = (i&4) >> 2; // binary for 4 is 0100
            int bit1pos = (i&2) >> 1; // binary for 2 is 0010
            int bit0pos = i&1;      // binary for 1 is 0001
            System.out.println(bit3pos + "" + bit2pos + "" + bit1pos + "" + bit0pos);
            layerweights.putScalar(0, i, bit3pos==0?-10:bit3pos*10);
            layerweights.putScalar(1, i, bit2pos==0?-10:bit2pos*10);
            layerweights.putScalar(2, i, bit1pos==0?-10:bit1pos*10);
            layerweights.putScalar(3, i, bit0pos==0?-10:bit0pos*10);
        }
        System.out.println(layerweights);
        INDArray layerbiases = Nd4j.zeros( 4,1); // column vector of biases
        for (int i=0; i<4; i++) {
            layerbiases.putScalar(i, 0,0.99);
        }
        net.addUntrainedLayer(layerweights, layerbiases);
        for (int i=0; i<10; i++) {
            System.out.println("Label: " + training_data[1][i]);
            net.feedforward2(training_data[0][i]); // first image ... output should be "5" in binary
        }

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
}
