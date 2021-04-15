# -*- coding: utf-8 -*-
"""
Created on Thu Apr  1 11:45:47 2021

@author: karto
"""

from util import evaluateConfidence, evaluateAccuracy
from network3 import Network, ConvPoolLayer, FullyConnectedLayer, ReLU, SoftmaxLayer, load_data_shared
import numpy as np
import network2
import mnist_loader
import matplotlib.pyplot as plt

# uncomment these lines if you are using a mac
import theano
theano.config.gcc.cxxflags = "-Wno-c++11-narrowing"

def predictDigitNeuralNetwork(img):
    a = net.feedforward(img)
    return np.argmax(a)

mini_batch_size = 10

# solution 1 to the "overfitting problem" - use validation_data to "see" when accuracy has saturated on the validation_data
training_data, validation_data, test_data = mnist_loader.load_data_wrapper()
training_data = list(training_data)
validation_data = list(validation_data)
test_data = list(test_data)
#net = network2.Network([784, 30, 10], cost=network2.CrossEntropyCost)
#evaluation_cost, evaluation_accuracy, training_cost, training_accuracy = net.SGD(training_data, 50, 10, 0.5, evaluation_data=validation_data,monitor_evaluation_accuracy=True,early_stopping_n=10)
#fig, ax = plt.subplots()
#ax.plot(range(len(evaluation_accuracy)), [epochacc/len(validation_data) for epochacc in evaluation_accuracy])
#ax.set_xlabel("epoch")
#ax.set_ylabel("validation_data accuracy")


# solution 2 to the "overfitting problem" - expand the training dataset
expanded_training_data, validation_data, test_data = mnist_loader.load_data_wrapper("mnist_expanded.pkl.gz")
expanded_training_data = list(expanded_training_data)
validation_data = list(validation_data)
test_data = list(test_data)
net = network2.Network([784, 30, 10], cost=network2.CrossEntropyCost)
evaluation_cost, evaluation_accuracy, training_cost, training_accuracy = net.SGD(expanded_training_data, 50, 10, 0.5, evaluation_data=validation_data,monitor_evaluation_accuracy=True,early_stopping_n=10)
fig, ax = plt.subplots()
ax.plot(range(len(evaluation_accuracy)), [epochacc/len(validation_data) for epochacc in evaluation_accuracy])
ax.set_xlabel("epoch")
ax.set_ylabel("validation_data accuracy")
asdfasdf

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



