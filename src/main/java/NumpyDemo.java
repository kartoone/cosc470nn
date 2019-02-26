import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class NumpyDemo {
    public static void main(String[] args) {
        INDArray myArray = Nd4j.zeros(10, 10);
        System.out.println(myArray);
    }
}
