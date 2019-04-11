import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

// Adapted from http://neuralnetworksanddeeplearning.com/chap1.html
// Michael A. Nielsen, "Neural Networks and Deep Learning", Determination Press, 2015
// This work is licensed under a Creative Commons Attribution-NonCommercial 3.0 Unported License.

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
        weights = new INDArray[sizes.length-1]; // minus 1 b/c output layer doesn't have weights
        num_layers = sizes.length;
        // init the individual layer biases
        for (int i=1; i<sizes.length; i++) {
            biases[i-1] = Nd4j.randn(sizes[i],1); // column vector of randomly generated biases for each neuron (sizes[i] neurons) in layer i
        }
//        for (int i=0; i<biases.length; i++) {
//            System.out.println("Layer " + (i+1) + " biases:");
//            System.out.println(biases[i]);
//        }

        // init the individual layer weights (layer i rows x layer i-1 cols ... simplifies math)
        for (int i=1; i<sizes.length; i++) {
            weights[i-1] = Nd4j.randn(sizes[i],sizes[i-1]);
        }
//        for (int i=0; i<weights.length; i++) {
//            System.out.println("Layer " + (i+1) + " weights: " + java.util.Arrays.toString(weights[i].shape()));
//            System.out.println(weights[i]);
//        }
    }

    // once this method is called, the SGD algorithm will be broken because the expected outputs will be the wrong shape
    // assuming that the new layer is a different shape than the old outputlayer ...
    // only thing that we can do is call "feedforward" and that should work correctly
    public void addUntrainedLayer(INDArray w, INDArray b) {
        biases = java.util.Arrays.copyOf(biases, biases.length+1);     // make room for the new layer
        weights = java.util.Arrays.copyOf(weights, weights.length+1);
        biases[biases.length-1] = b;    // add the new layer biases
        weights[weights.length-1] = w;  // add the new layer weights
    }

    // a is the input column vector (e.g., 784x1 for our image data)
    // think of "a" as starting out as the input layer activations
    // then it is being simultaneously fed as input to the next layer and then being overwritten as that new layer's activations
    // the final "a" that we return is the output layer activations
    public INDArray feedforward(INDArray a) {
        for (int i=0; i<biases.length; i++) { // whether we use biases or weights doesn't matter ... they have same length
            INDArray b = biases[i];  // layer biases
            INDArray w = weights[i]; // weights from previous layer to current layer
            a = sigmoid(w.mmul(a).add(b)); //
        }
        return a;
    }

    // same as feedforward put print activations in last two layers ... helpful for debugging networks with untrained layer
    public INDArray feedforward2(INDArray a) {
        for (int i=0; i<biases.length; i++) { // whether we use biases or weights doesn't matter ... they have same length
            INDArray b = biases[i];  // layer biases
            INDArray w = weights[i]; // weights from previous layer to current layer
            a = sigmoid(w.mmul(a).add(b)); //
            if (i>=biases.length-2) {
                System.out.println(a);
            }
        }
        return a;
    }

    // returns the number of correct predictions on test_data
    public int evaluate(INDArray test_data[][]) {
        // extract out the tuples
        // each row has two columns ... row[0] is "x", i.e., INDArray column vector with all the input values for one test
        // row[1] is "y", ie., the INDArray desired output activations
        int correct = 0;
        for (int i=0; i<test_data[0].length; i++) {
            INDArray x = test_data[0][i];
            INDArray y = test_data[1][i];
            INDArray out = predict(feedforward(x));
            if (out.equals(y)) {
                correct++;
            }
        }
        return correct;
    }

    // converts the array to have a 1 in the highest position and 0 in all other positions
    public INDArray predict(INDArray out) {
        Number max = out.maxNumber();
        for(int i=0; i<out.rows(); i++) {
            if (out.getDouble(i)==(max.doubleValue())) {
                out.putScalar(i, 0, 1);
            } else {
                out.putScalar(i, 0, 0);
            }
        }
        return out;
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
        SGD(training_data, epochs, batchsize, eta, test_data, true); // default to displaying epoch accuracy
    }

    public void SGD(INDArray training_data[][], int epochs, int batchsize, double eta, INDArray test_data[][], boolean displayepochaccuracy) {
        int maxcorrect = -1; // init to negative value so we can appropriately update
        int maxcorrecti = -1;
        for (int i=0; i<epochs; i++) {
            // first shuffle the training data so that all our batches will be random order
            training_data = shuffle(training_data);
            for (int j=0; j<(training_data[0].length-1)/batchsize; j++) {
                INDArray batchx[] = java.util.Arrays.copyOfRange(training_data[0],batchsize*j, batchsize*(j+1));
                INDArray batchy[] = java.util.Arrays.copyOfRange(training_data[1],batchsize*j, batchsize*(j+1));
                INDArray batch[][] = new INDArray[][] {batchx, batchy};
                update_mini_batch(batch, eta);
            }
            int correct = evaluate(test_data);
            if (displayepochaccuracy) {
                System.out.println(correct + " correct, epoch " + i);
            }
            // see if this is max correct epoch
            if (correct>maxcorrect) {
                maxcorrect = correct;
                maxcorrecti = i;
            }
        }
        // no matter what, let's display the maximum test accuracy and on which epoch it occurred
        System.out.println("Max correct: " + maxcorrect + ", epoch " + maxcorrecti);
    }

    public void update_mini_batch(INDArray batch[][], double eta) {
        // nabla_b and nabla_w should have same type as this.biases and this.weights
        INDArray nabla_b[] = new INDArray[biases.length];
        INDArray nabla_w[] = new INDArray[weights.length];
        for (int i=0; i<nabla_b.length; i++) {
            nabla_b[i] = Nd4j.zeros(biases[i].shape());
            nabla_w[i] = Nd4j.zeros(weights[i].shape());
        }
        for (int i=0; i<batch[0].length; i++) {
            INDArray x = batch[0][i];
            INDArray y = batch[1][i];
            INDArray delta_nabla[][] = backprop(x,y);
            for (int j=0; j<nabla_b.length; j++) {
                nabla_b[j] = nabla_b[j].add(delta_nabla[0][j]); // add the delta to each nabla
                nabla_w[j] = nabla_w[j].add(delta_nabla[1][j]);
            }
        }
        for (int i=0; i<weights.length; i++) {
            weights[i] = weights[i].sub(nabla_w[i].mul(eta/batch[0].length));
            biases[i] = biases[i].sub(nabla_b[i].mul(eta/batch[0].length));
        }
    }

    private void debugl(long o[]) {
        System.out.println(java.util.Arrays.toString(o));
    }

    public INDArray[][] backprop(INDArray x, INDArray y) {
        // nabla_b and nabla_w should have same type as this.biases and this.weights
        INDArray nabla_b[] = new INDArray[biases.length];
        INDArray nabla_w[] = new INDArray[weights.length];
        for (int i=0; i<nabla_b.length; i++) {
            nabla_b[i] = Nd4j.zeros(biases[i].shape());
            nabla_w[i] = Nd4j.zeros(weights[i].shape());
        }
        // feedforward
        INDArray activation = x;
        INDArray activations[] = new INDArray[biases.length+1]; // total number of layers including input layer
        INDArray zs[] = new INDArray[biases.length];
        activations[0] = activation;
        for (int i=1; i<activations.length; i++) {
            INDArray b = biases[i-1];
            INDArray w = weights[i-1];
            INDArray z = w.mmul(activation).add(b);
            zs[i-1] = z; // append z
            activation = sigmoid(z);
            activations[i] = activation;
        }
        // activation will be referencing the last activation ... equiv to activations[-1]
//        INDArray delta = cost_derivative(activation, y).mul(sigmoid_prime(zs[zs.length-1]));
        INDArray delta = cost_derivative(activation, y);
        nabla_b[nabla_b.length-1] = delta;
        nabla_w[nabla_w.length-1] = delta.mmul(activations[activations.length-2].transpose());

        // reverse pass
        for (int l=2; l<num_layers; l++) {
            INDArray z = zs[zs.length-l];
            INDArray sp = sigmoid_prime(z);
            delta = weights[weights.length-l+1].transpose().mmul(delta).mul(sp);
            nabla_b[nabla_b.length-l] = delta;
            nabla_w[nabla_w.length-l] = delta.mmul(activations[activations.length-l-1].transpose());
        }
        return new INDArray[][] { nabla_b, nabla_w };
    }

    public INDArray cost_derivative(INDArray a, INDArray y) {
        return a.sub(y);
    }

    // sigmoidifies "z" already calculated in feedforward
    // z must be z column vector
    public INDArray sigmoid(INDArray z) {
        INDArray sigmoidified = Nd4j.zeros(z.shape());
        for (int r=0; r<z.rows(); r++) {
            double sig = 1.0/(1+Math.exp(-z.getDouble(r,0)));
            sigmoidified.putScalar(new int[]{r,0}, sig); // update a with its sigmoid function output
        }
        return sigmoidified;
    }

    // called from backprop algorithm
    public INDArray sigmoid_prime(INDArray z) {
        INDArray sigz = sigmoid(z);
        INDArray oneminussigz = sigz.mul(-1).add(1);
        return sigz.mul(oneminussigz);
    }

    // Other utility functions ... shuffle for shuffling training data
    private INDArray[][] shuffle(INDArray[][] input) {
        // create an indexlist of all the indices, shuffle that, and then put the training data into two new INDArray[]
        // in the order in the shuffled indexlist
        java.util.List<Integer> indexArray = new java.util.ArrayList<>();
        for (int i=0; i<input[0].length; i++) {
            indexArray.add(i);
        }
        java.util.Collections.shuffle(indexArray);
        INDArray[] xs = new INDArray[input[0].length];
        INDArray[] ys = new INDArray[input[1].length]; // should be same length
        for (int i=0; i<indexArray.size(); i++) {
            xs[i] = input[0][indexArray.get(i)];
            ys[i] = input[1][indexArray.get(i)];
        }
        return new INDArray[][] {xs, ys};
    }
}
