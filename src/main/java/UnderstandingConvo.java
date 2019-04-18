import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class UnderstandingConvo {

    public static void main(String[] args) {
        INDArray w = Nd4j.rand(5,5);
        INDArray a = Nd4j.rand(24,24);
        for (int j=0; j<a.rows()-4; j++) {
            for(int k=0; k<a.columns()-4; k++) {
                for (int l = 0; l < w.rows(); l++) {
                    for (int m = 0; m < w.columns(); m++) {
                        System.out.println("j:"+j+",k:"+k+",l:"+l+",m:"+m);
                        System.out.println(w.getDouble(l,m));
                        System.out.println(a.getDouble(j+l,k+m));
                    }
                }
            }
        }
    }

}
