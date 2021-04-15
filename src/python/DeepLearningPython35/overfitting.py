# -*- coding: utf-8 -*-
"""
Created on Thu Apr  1 11:45:47 2021

@author: karto
"""

from util import evaluateConfidence, evaluateAccuracy
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

def convertAccuracies(accuracies,datalength):
    return [epochacc/datalength for epochacc in accuracies]

mini_batch_size = 10

# solution 1 to the "overfitting problem" - use validation_data to "see" when accuracy has saturated on the validation_data
training_data, validation_data, test_data = mnist_loader.load_data_wrapper()
training_data = list(training_data)
validation_data = list(validation_data)
test_data = list(test_data)
net = network2.Network([784, 30, 10], cost=network2.CrossEntropyCost)
evaluation_cost, evaluation_accuracy, training_cost, training_accuracy, test_cost, test_accuracy = net.SGD(training_data, 50, 10, 0.5, evaluation_data=validation_data,monitor_training_accuracy=True,monitor_evaluation_accuracy=True,test_data=test_data)
fig, ax = plt.subplots()

xvals = range(len(evaluation_accuracy))
yvals_trainingdata = convertAccuracies(training_accuracy, len(training_data))
yvals_validationdata = convertAccuracies(evaluation_accuracy, len(validation_data))
yvals_testdata = convertAccuracies(test_accuracy, len(test_data))
ax.plot(xvals,yvals_trainingdata)
ax.plot(xvals,yvals_validationdata)
ax.plot(xvals,yvals_testdata)
ax.legend(["training_data accuracy", "validation_data accuracy", "test_data accuracy"])
ax.set_xlabel("epoch")
ax.set_ylabel("validation_data accuracy")


# solution 2 to the "overfitting problem" - expand the training dataset
expanded_training_data, validation_data, test_data = mnist_loader.load_data_wrapper("mnist_expanded.pkl.gz")
expanded_training_data = list(expanded_training_data)
validation_data = list(validation_data)
test_data = list(test_data)
net = network2.Network([784, 30, 10], cost=network2.CrossEntropyCost)
evaluation_cost, evaluation_accuracy, training_cost, training_accuracy, test_cost, test_accuracy = net.SGD(expanded_training_data, 50, 10, 0.5, evaluation_data=validation_data,monitor_evaluation_accuracy=True,test_data=test_data)
xvals = range(len(evaluation_accuracy))
yvals_trainingdata = convertAccuracies(training_accuracy, len(expanded_training_data))
yvals_validationdata = convertAccuracies(evaluation_accuracy, len(validation_data))
yvals_testdata = convertAccuracies(test_accuracy, len(test_data))
ax.plot(xvals,yvals_trainingdata)
ax.plot(xvals,yvals_validationdata)
ax.plot(xvals,yvals_testdata)
ax.legend(["training_data accuracy", "validation_data accuracy", "test_data accuracy"])
ax.set_xlabel("epoch")
ax.set_ylabel("validation_data accuracy")
exit

# solution 3 - regularization
net.SGD(training_data, 1, mini_batch_size, 0.5, 
            validation_data, test_data, lmbda=0.1)


# all three solutions applied together (really, it's just the second two since we aren't stopping early)

