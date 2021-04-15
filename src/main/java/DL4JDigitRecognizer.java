import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.graph.ComputationGraph.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork.*;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.nn.weights.WeightInit.*;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nesterovs.*;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions.*;

import java.io.IOException;

public class DL4JDigitRecognizer {

    public static void main(String[] args) throws IOException {
        int batchSize = 20;
        int rngSeed = 123;

        DataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed);
        DataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);

        while (mnistTest.hasNext()) {
            DataSet ds = mnistTest.next();
            for (int i=0; i<10; i++) {
                INDArray row = ds.getFeatures().getRow(i);
                INDArray imgdata = row.reshape(28,28);
                System.out.println(imgdata);
            }
            System.out.println(ds.getLabels());
            break;
        }

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed).updater(new Sgd(0.1)) //High Level Configuration
                .list() //For configuring MultiLayerNetwork we call the list method
                .layer(0, new DenseLayer.Builder().nIn(784).nOut(100).weightInit(WeightInit.XAVIER).activation(Activation.SIGMOID).build()) //Configuring Layers
                .layer(1, new OutputLayer.Builder().nIn(100).nOut(10).weightInit(WeightInit.XAVIER).activation(Activation.SIGMOID).build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(100));
        System.out.println("Training");
        for (int i=0; i<2; i++) {
            net.fit(mnistTrain);
        }

        DataSet firstbatch = mnistTest.next();
        System.out.println(firstbatch);
        INDArray output = net.output(firstbatch.getFeatures()); //get the networks prediction
        System.out.println(output);




    }

}
