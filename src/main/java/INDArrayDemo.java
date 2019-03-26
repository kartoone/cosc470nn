import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class INDArrayDemo {
    public static void main(String[] args) {
        INDArray a = Nd4j.rand(15,1);
        System.out.println(a);

        INDArray biases = Nd4j.randn(10, 1);
        System.out.println(biases);
        System.out.println(biases.isColumnVector());

        INDArray weights = Nd4j.randn(10, 15);
        System.out.println(weights);

        // weight * activation
        INDArray wa = weights.mmul(a);
        System.out.println(wa);

        // weight * activation + biases
        INDArray z = wa.add(biases);
        System.out.println(z);

        System.out.println(sigmoidify(z));


    }

    private static INDArray sigmoidify(INDArray z) {
        INDArray sigmoidified = Nd4j.zeros(z.shape());
        for (int r=0; r<z.rows(); r++) {
            sigmoidified.putScalar(r,0,1/(1+Math.exp(-z.getDouble(r,0))));
        }
        return sigmoidified;
    }
}
