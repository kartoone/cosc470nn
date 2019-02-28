import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Represents the entire network as arrays of weights and biases. Assumes fully connected and feed-forward only.
 * This is a java port of Nielsen's python implementation of neural network for digit recognition.
 */
public class Network {
    int num_layers;
    int sizes[];
    INDArray biases[]; // biases is array of column vectors ... each vector in the array is the biases for that layer of the network (not including input layer, which doesn't have biases)
    INDArray weights[];// weights is array of 2d matrices where in each layer (e.g., layerweight[k][j] is the weight for input j in the previous layer to input k in the current layer ... natural ordering reversed b/c it simplifies the math
    public Network(int sizes[]) {
        System.out.println("Constructing neural network with " + sizes.length + " layers.");
        biases = new INDArray[sizes.length-1]; // minus 1 b/c input layer of neurons doesn't have biases
        weights = new INDArray[sizes.length-1];
        // init the individual layer biases
        for (int i=1; i<sizes.length; i++) {
            biases[i-1] = Nd4j.randn(sizes[i],1); // column vector of randomly generated biases for each neuron (sizes[i] neurons) in layer i
        }
        for (int i=0; i<biases.length; i++) {
            System.out.println("Layer " + (i+1) + " biases:");
            System.out.println(biases[i]);
        }

        // init the individual layer weights (layer i rows x layer i-1 cols ... simplifies math)
        for (int i=1; i<sizes.length; i++) {
            weights[i-1] = Nd4j.randn(sizes[i],sizes[i-1]);
        }
        for (int i=0; i<weights.length; i++) {
            System.out.println("Layer " + (i+1) + " weights: " + java.util.Arrays.toString(weights[i].shape()));
            System.out.println(weights[i]);
        }
    }

    // a is the input column vector (e.g., 784x1 for our image data)
    // think of "a" as starting out as the input layer activations
    // then it is being simultaneously fed as input to the next layer and then being overwritten as that new layer's activations
    // the final "a" that we return is the output layer activations
    public INDArray feedforward(INDArray a) {
        for (int i=0; i<biases.length; i++) { // whether we use biases or weights doesn't matter ... they have same length
            INDArray b = biases[i];  // layer biases
            INDArray w = weights[i]; // weights from previous layer to current layer
            if (i==biases.length-1) {
                System.out.println(a);
            }
            a = sigmoid(w.mmul(a).add(b)); //
        }
        return a;
    }

    // returns the number of correct predictions on test_data
    public int evaluate(INDArray test_data[][]) {
        // extract out the tuples
        // each row has two columns ... row[0] is "x", i.e., INDArray column vector with all the input values for one test
        // row[1] is "y", ie., the INDArray desired output activations
        int correct = 0;
        for (int i=0; i<test_data.length; i++) {
            INDArray x = test_data[i][0];
            INDArray y = test_data[i][1];
            if (feedforward(x).equals(y)) {
                correct++;
            }
        }
        return correct;
    }

    /**
     * Trains the network using stochastic gradient descent. Training_data is
     * tuples (x, y) represented by a 2d array of INDArrays of length 2... i.e., training_data[0] = x, training_data[1] = y.
     * Epochs is the number of epochs
     * Batchsize is the number of images to include in single epoch
     * Eta is the learning rate
     * Test_data is same format as training_data but is evaluated after each epoch to display training improvement
     */
    public void SGD(INDArray training_data[][], int epochs, int batchsize, double eta, INDArray test_data[][]) {
        int testdatasize = test_data.length;
        // first shuffle the training data so that all our batches will be random order
        shuffle(training_data);
        for (int i=0; i<epochs; i++) {
            INDArray batch[][] = java.util.Arrays.copyOfRange(training_data,batchsize*i, batchsize*(i+1));
            update_mini_batch(batch, eta);
        }
    }

    public void update_mini_batch(INDArray batch[][], double eta) {
        // start here nabla_b and nabla_w should have same type as this.biases and this.weights
    }

    // sigmoidifies "z" already calculated in feedforward
    // z must be z column vector
    public INDArray sigmoid(INDArray z) {
        for (int r=0; r<z.rows(); r++) {
            double sig = 1.0/(1+Math.exp(-z.getDouble(r,0)));
            z.putScalar(new int[]{r,0}, sig); // update a with its sigmoid function output
        }
        return z;
    }

    // called from backprop algorithm
    public INDArray sigmoid_prime(INDArray z) {
        INDArray sigz = sigmoid(z);
        INDArray oneminussigz = sigz.mul(-1).add(1);
        return sigz.mul(oneminussigz);
    }

    // Other utility functions ... shuffle for shuffling training data
    private INDArray[][] shuffle(INDArray[][] input) {
        // easiest way is temporarily convert training_data to ArrayList<training_data[]> and then call shuffle
        java.util.ArrayList<INDArray[]> tdata = new java.util.ArrayList<>(input.length);
        for (int i=0; i<input.length; i++) {
            tdata.add(input[i]);
        }
        java.util.Collections.shuffle(tdata);
        // now stuff back the newly shuffled list into a 2d array
        INDArray output[][] = new INDArray[input.length][2]; // 2 because tuples
        for (int i=0; i<input.length; i++) {
            output[i] = tdata.get(i);
        }
        return output;
    }
}
