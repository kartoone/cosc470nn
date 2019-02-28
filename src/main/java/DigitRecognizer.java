import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class DigitRecognizer {
    public static void main(String[] args) {
        Network net = new Network(new int[]{784, 15, 10});
        // let's run our network on randomly generated image
        INDArray input = Nd4j.rand(784,1);
        INDArray a = net.feedforward(input);
        System.out.println(a);
    }
}
