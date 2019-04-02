// Demonstrates how a single neuron calculates its output, plus
// how the same neuron can be "trained" to learn a weight and bias that will give it desired output.
// In this case, the input is always a 1, and the desired output is a 0.
// Some of the equations have been optimized for this specific input and target output
// Adapted from http://neuralnetworksanddeeplearning.com/chap3.html
// Michael A. Nielsen, "Neural Networks and Deep Learning", Determination Press, 2015
// This work is licensed under a Creative Commons Attribution-NonCommercial 3.0 Unported License.

public class NeuronDemo {

    public static void main(String[] args) {
        KeyboardInputClass keyin = new KeyboardInputClass();
        double startingWeight = keyin.getDouble(false, 0.6, 0, 0, "Starting weight [default=0.6]? ");
        double startingBias =  keyin.getDouble(false, 0.9, 0, 0, "Starting bias [default=0.9]? ");
        double eta =  keyin.getDouble(false, 0.15, 0, 0, "Learning rate [default=0.15]? ");
        int epochs =  keyin.getInteger(false, 300, 0, 0, "# Epochs [default=300]? ");
        train(startingWeight, startingBias, eta, epochs);
    }

    // stochastic gradient descent when batch size is 1 input and total number of inputs is also 1
    // in other words update the weight and bias immediately after calculating output for current weight and bias
    private static void train(double startingWeight, double startingBias, double eta, int epochs) {
        double w = startingWeight;
        double b = startingBias;
        for (int i=0; i<epochs; i++) {
            double a = calculateOutput(w, b);
            double delta = costDerivative2(a);
            // go ahead and immediately update the weight and bias by the result of the gradient descent
            // algorithm which has been heavily optimized for this specific problem as just the derivative of the cost function
            w += -eta*delta;
            b += -eta*delta;
            System.out.println(a); // print the "result" (i.e., activation of the neuron)
        }

    }

    // quadratic cost function derivative
    private static double costDerivative(double a) {
        return a*a*(1-a);
    }

    // cross-entropy cost function derivative
    private static double costDerivative2(double a) {
        return 1.0/(1-a);
    }

    // calculates the output activation as sigmoid(weight * input + bias).
    // recall that for this problem, the training input is always 1 so
    // this simplifies to just sigmoid(weight+bias)
    private static double calculateOutput(double w, double b) {
        double z = w + b;
        return sigmoid(z);
    }

    private static double sigmoid(double z) {
        return 1.0/(1+Math.exp(-z));
    }

}
