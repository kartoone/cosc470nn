# -*- coding: utf-8 -*-
"""
Created on Thu Apr  1 11:45:47 2021

@author: karto
"""

from network3 import Network, ConvPoolLayer, FullyConnectedLayer, ReLU, SoftmaxLayer, load_data_shared
import numpy as np
import matplotlib.pyplot as plt
import time 

# uncomment these lines if you are using a mac
import theano
theano.config.gcc.cxxflags = "-Wno-c++11-narrowing"

def predictDigitNeuralNetwork(img):
    a = net.feedforward(img)
    return np.argmax(a)

mini_batch_size = 10
epochs = 60
training_data, validation_data, test_data = load_data_shared("mnist.pkl.gz")
training_data = list(training_data)
validation_data = list(validation_data)
test_data = list(test_data)

start = time.time()
print("Initializing and training simple one hidden layer net 784-30-10 @{0}".format(start))
net = Network([
        FullyConnectedLayer(
            n_in=784, n_out=30),
        SoftmaxLayer(n_in=30, n_out=10)], 
        mini_batch_size)
evaluation_accuracy, training_accuracy, test_accuracy, bestepoch = net.SGD(training_data, epochs, mini_batch_size, 3.0, validation_data, test_data)
finish = time.time()
elapsed = finish - start
print("Finished @{0}, elapsed {1}".format(finish,elapsed))

xvals = range(len(evaluation_accuracy))
yvals_trainingdata = training_accuracy
yvals_validationdata = evaluation_accuracy
yvals_testdata = test_accuracy

fig, ax = plt.subplots()
ax.plot(xvals,yvals_trainingdata)
ax.plot(xvals,yvals_validationdata)
ax.plot(xvals,yvals_testdata)
ax.legend(["training_data accuracy", "validation_data accuracy", "test_data accuracy"])
ax.set_title("SIMPLE NETWORK - 30 hidden neurons")
ax.set_xlabel("epoch")
ax.set_ylabel("accuracy")

start = time.time()
print("Initializing and training deep1, 5x5, 2x2 @{0}".format(start))
net = Network([
        ConvPoolLayer(image_shape=(mini_batch_size, 1, 28, 28), 
                      filter_shape=(20, 1, 5, 5), 
                      poolsize=(2, 2), 
                      activation_fn=ReLU),
        FullyConnectedLayer(
            n_in=20*12*12, n_out=100),
        SoftmaxLayer(n_in=100, n_out=10)], 
        mini_batch_size)
evaluation_accuracy, training_accuracy, test_accuracy, bestepoch = net.SGD(training_data, epochs, mini_batch_size, 0.1, validation_data, test_data)
finish = time.time()
elapsed = finish - start
print("Finished @{0}, elapsed {1}".format(finish,elapsed))
baselineepoch = bestepoch
baselineaccuracy = test_accuracy[bestepoch]
baselineelapsed = elapsed

xvals = range(len(evaluation_accuracy))
yvals_trainingdata = training_accuracy
yvals_validationdata = evaluation_accuracy
yvals_testdata = test_accuracy
fig, ax = plt.subplots()
ax.plot(xvals,yvals_trainingdata)
ax.plot(xvals,yvals_validationdata)
ax.plot(xvals,yvals_testdata)
ax.legend(["training_data accuracy", "validation_data accuracy", "test_data accuracy"])
ax.set_title("DEEP1: 5x5, 2x2")
ax.set_xlabel("epoch")
ax.set_ylabel("accuracy")

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

start = time.time()
print("Initializing and training the best known accuracy network, @{0}".format(start))
net.SGD(expanded_training_data, 60, mini_batch_size, 0.03, 
            validation_data, test_data, lmbda=0.1)
finish = time.time()
elapsed = finish - start
print("Finished @{0}, elapsed {1}".format(finish,elapsed))

xvals = range(len(evaluation_accuracy))
yvals_trainingdata = training_accuracy
yvals_validationdata = evaluation_accuracy
yvals_testdata = test_accuracy
fig, ax = plt.subplots()
ax.plot(xvals,yvals_trainingdata)
ax.plot(xvals,yvals_validationdata)
ax.plot(xvals,yvals_testdata)
ax.legend(["training_data accuracy", "validation_data accuracy", "test_data accuracy"])
ax.set_title("BEST KNOWN DEEP NETWORK")
ax.set_xlabel("epoch")
ax.set_ylabel("accuracy")


