# -*- coding: utf-8 -*-
"""
Created on Thu Apr  1 11:45:47 2021

@author: karto
"""

from util import evaluateConfidence, evaluateAccuracy
from network3 import Network, ConvPoolLayer, FullyConnectedLayer, ReLU, SoftmaxLayer, load_data_shared
import numpy as np
import matplotlib.pyplot as plt

# uncomment these lines if you are using a mac
import theano
theano.config.gcc.cxxflags = "-Wno-c++11-narrowing"

def predictDigitNeuralNetwork(img):
    a = net.feedforward(img)
    return np.argmax(a)

def convertAccuracies(accuracies,datalength):
    return [epochacc/datalength for epochacc in accuracies]

mini_batch_size = 10
epochs = 50
#expanded_training_data, validation_data, test_data = load_data_shared("mnist_expanded.pkl.gz")
expanded_training_data, validation_data, test_data = load_data_shared("mnist.pkl.gz")
expanded_training_data = list(expanded_training_data)
validation_data = list(validation_data)
test_data = list(test_data)

net = Network([
        FullyConnectedLayer(
            n_in=784, n_out=100, activation_fn=ReLU, p_dropout=0.5),
        SoftmaxLayer(n_in=100, n_out=10, p_dropout=0.5)], 
        mini_batch_size)
evaluation_accuracy, training_accuracy, test_accuracy = net.SGD(expanded_training_data, epochs, mini_batch_size, 0.5, validation_data, test_data, lmbda=0.1)
xvals = range(len(evaluation_accuracy))
yvals_trainingdata = convertAccuracies(training_accuracy, len(expanded_training_data))
yvals_validationdata = convertAccuracies(evaluation_accuracy, len(validation_data))
yvals_testdata = convertAccuracies(test_accuracy, len(test_data))

fig, ax = plt.subplots()
ax.plot(xvals,yvals_trainingdata)
ax.plot(xvals,yvals_validationdata)
ax.plot(xvals,yvals_testdata)
ax.legend(["training_data accuracy", "validation_data accuracy", "test_data accuracy"])
ax.set_title("DEEP1: 5x5, 2x2")
ax.set_xlabel("epoch")
ax.set_ylabel("accuracy")

input("Press any key to continue")
net = Network([
        ConvPoolLayer(image_shape=(mini_batch_size, 1, 28, 28), 
                      filter_shape=(20, 1, 5, 5), 
                      poolsize=(2, 2), 
                      activation_fn=ReLU),
        FullyConnectedLayer(
            n_in=20*12*12, n_out=100, activation_fn=ReLU, p_dropout=0.5),
        SoftmaxLayer(n_in=100, n_out=10, p_dropout=0.5)], 
        mini_batch_size)
evaluation_accuracy, training_accuracy, test_accuracy = net.SGD(expanded_training_data, epochs, mini_batch_size, 0.5, validation_data, test_data, lmbda=0.1)
xvals = range(len(evaluation_accuracy))
yvals_trainingdata = convertAccuracies(training_accuracy, len(expanded_training_data))
yvals_validationdata = convertAccuracies(evaluation_accuracy, len(validation_data))
yvals_testdata = convertAccuracies(test_accuracy, len(test_data))

fig, ax = plt.subplots()
ax.plot(xvals,yvals_trainingdata)
ax.plot(xvals,yvals_validationdata)
ax.plot(xvals,yvals_testdata)
ax.legend(["training_data accuracy", "validation_data accuracy", "test_data accuracy"])
ax.set_title("DEEP1: 5x5, 2x2")
ax.set_xlabel("epoch")
ax.set_ylabel("accuracy")

input("Press any key to continue")


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



