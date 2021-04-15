# -*- coding: utf-8 -*-
"""
Created on Thu Apr  1 11:45:47 2021

@author: karto
"""

from util import evaluateConfidence, evaluateAccuracy
from network3 import Network, ConvPoolLayer, FullyConnectedLayer, ReLU, SoftmaxLayer, load_data_shared
import numpy as np

# uncomment these lines if you are using a mac
#import theano
#theano.config.gcc.cxxflags = "-Wno-c++11-narrowing"

def predictDigitNeuralNetwork(img):
    a = net.feedforward(img)
    return np.argmax(a)

mini_batch_size = 10
expanded_training_data, validation_data, test_data = load_data_shared("mnist_expanded.pkl.gz")
expanded_training_data = list(expanded_training_data)
validation_data = list(validation_data)
test_data = list(test_data)

net = Network([
        ConvPoolLayer(image_shape=(mini_batch_size, 1, 28, 28), 
                      filter_shape=(20, 1, 5, 5), 
                      poolsize=(2, 2), 
                      activation_fn=ReLU),
        ConvPoolLayer(image_shape=(mini_batch_size, 20, 12, 12), 
                      filter_shape=(40, 20, 5, 5), 
                      poolsize=(2, 2), 
                      activation_fn=ReLU),
        FullyConnectedLayer(
            n_in=40*4*4, n_out=1000, activation_fn=ReLU, p_dropout=0.5),
        FullyConnectedLayer(
            n_in=1000, n_out=1000, activation_fn=ReLU, p_dropout=0.5),
        SoftmaxLayer(n_in=1000, n_out=10, p_dropout=0.5)], 
        mini_batch_size)

 
net.SGD(expanded_training_data, 1, mini_batch_size, 0.03, 
            validation_data, test_data, lmbda=0.1)



