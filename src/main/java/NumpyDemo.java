import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Demonstrates the ND4J equivalent of Numpy arrays.
 */
public class NumpyDemo {
    public static void main(String[] args) {
        // row vector ... i.e, 1 row, 10 columns
        INDArray array1 = Nd4j.ones(1, 10);
        System.out.println(array1);
        System.out.println(array1.isRowVector());

        // column vector ... i.e., 10 rows, 1 column
        INDArray array2 = Nd4j.ones(10, 1).mul(2);
        System.out.println(array2);
        System.out.println(array2.isColumnVector());

        // mmul is equivalent to dot product of two vectors if multiplying a row vector by a column vector
        // this is the mathematical trick that is part of the gradient descent algorithm to quickly sum all the weights time the inputs
        INDArray out = array1.mmul(array2);
        System.out.println(out);
        System.out.println(out.isScalar());

        // here is our representation of the neural network ... biases is an array of column vectors ... i.e., "layer" of the network starting from the second layer has all of its neurons stored in a INDArray column vector
        int sizes[] = new int[] {784, 15, 10};
        INDArray biases[] = new INDArray[sizes.length-1]; // minus 1 b/c input layer of neurons doesn't have biases
        INDArray weights[] = new INDArray[sizes.length-1];
        // init the individual layer biases
        for (int i=1; i<sizes.length; i++) {
            biases[i-1] = Nd4j.randn(sizes[i],1); // column vector of randomly generated biases for each neuron (sizes[i] neurons) in layer i
        }
        for (int i=0; i<biases.length; i++) {
            System.out.println(biases[i]);
        }

        // init the individual layer weights (layer i rows x layer i-1 cols ... simplifies math)
        for (int i=1; i<sizes.length; i++) {
            weights[i-1] = Nd4j.randn(sizes[i],sizes[i-1]);
        }
        for (int i=0; i<weights.length; i++) {
            System.out.println("weights["+i+"] dimensions: " + java.util.Arrays.toString(weights[i].shape()));
            System.out.println(weights[i]);
        }

        // consider input vector randomly generated below
        INDArray input = Nd4j.rand(784,1);
        System.out.println(input);

    }
}