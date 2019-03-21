import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

// look into using mugshot database - https://www.nist.gov/srd/nist-special-database-18
public class FacialRecognition {
    public static void main(String[] args) throws IOException {
        File imagefile = new File("croppedpeople.bin");
        File dimagefile = new File("train-images-idx3-ubyte");
        File testimagefile = new File("t10k-images-idx3-ubyte");
        INDArray ptrain_data[][] = new INDArray[][] { parsePeople(imagefile), allPeople(48, true) };
        INDArray dtrain_data[][] = new INDArray[][] { DigitRecognizer.parseImageFile(dimagefile, 60000), allPeople(60000,false) };
        INDArray training_data[][] = new INDArray[2][96];
        INDArray test_data[][] = new INDArray[2][8];
        // all add the ptrain data since there is so little of it
        int j=0;
        for (; j<48; j++) {
            training_data[0][j] = ptrain_data[0][j];
            training_data[1][j] = ptrain_data[1][j];
        }

        // now randomly fill out the remaining 48 spots from the handwriting digits images
        for(; j<96; j++) {
            int r = new java.util.Random().nextInt(60000);
            training_data[0][j] = dtrain_data[0][r];
            training_data[1][j] = dtrain_data[1][r];
        }

        // now randomly select test data from handwriting digits
        for (j=0; j<8; j++) {
            int r = new java.util.Random().nextInt(60000);
            test_data[0][j] = dtrain_data[0][r];
            test_data[1][j] = dtrain_data[1][r];
        }

        Network net = new Network(new int[]{784, 5, 1});

        // train the network
        net.SGD(training_data, 6, 8, 3.0, test_data);

        // let's run our network on randomly generated image
        INDArray input = Nd4j.rand(784,1);
        INDArray a = net.feedforward(input);
        System.out.println(a);

    }

    // create labels designating everyone as people
    // size is the number of labels to create
    private static INDArray[] allPeople(int size, boolean ispeople) {
        INDArray labels[] = new INDArray[size];
        for (int i=0; i<labels.length; i++) {
            labels[i] = ispeople ? Nd4j.ones(1,1) : Nd4j.zeros(1,1); // our output answer only has one layer
        }
        return labels;
    }

    public static INDArray[] parsePeople(File f) throws IOException {
        InputStream filein = new FileInputStream(f);
        byte buffer[] = new byte[4];
        filein.read(buffer);
        int width = new BigInteger(buffer).intValue();
        filein.read(buffer);
        int height = new BigInteger(buffer).intValue();
        filein.read(buffer);
        int numpeople = new BigInteger(buffer).intValue();
        INDArray peoplebytes[] = new INDArray[numpeople];
        for (int i=0; i<numpeople; i++) {
            peoplebytes[i] = Nd4j.zeros(width*height,1);
            int neuroni = 0;
            for (int x=0; x<width; x++) {
                for (int y=0; y<height; y++) {
                    filein.read(buffer);
                    double pixel = convertGrayscale(new BigInteger(buffer).intValue());
                    peoplebytes[i].putScalar(neuroni++,0,pixel);
                }
            }
        }
        return peoplebytes;
    }

    // converts from byte value 0-255 to intensity value
    private static double convertGrayscale(int p) {
        int a = (p >> 24) & 255;
        int r = (p >> 16) & 255;
        int g = (p >> 8) & 255;
        int b = p & 255;
        double avg = (r+g+b)/3.0;
        return avg/256.0;
    }
}
