# -*- coding: utf-8 -*-
"""
Created on Thu Apr  1 11:45:47 2021

@author: karto
"""

from network3 import Network, ConvPoolLayer, FullyConnectedLayer, ReLU, SoftmaxLayer, load_data_shared
import numpy as np
import matplotlib.pyplot as plt
import time 

# uncomment these lines if you are using a mac ... some windows system may need to comment these out
import theano
theano.config.gcc.cxxflags = "-Wno-c++11-narrowing"

mini_batch_size = 10
epochs = 2    # CAUTION THIS WILL TAKE 7-10 HOURS TO RUN!!!

##########################################################################################
# RESULTS DICTIONARY LIST                                                                #
#   list of dictionary objects summarizing the results of each run                       #
#     { "configuration": CONFIGNAME, "accuracy":0.9463, "bestepoch":17, "elapsed":850 }  #
# NOTE YOU WILL ONLY APPEND ONE RESULT HERE AS IT WILL TAKE 7-10 HOURS TO RUN!!!         #
##########################################################################################
deep3results = []
def getResults():
    # note that by the time you call this method in your main.py script that it should
    # be fully populated
    return deep3results

#####################################################################
# THIRD NEURAL NETWORK - advanced deep convolutional neural network #
#    we will use the expanded training data for this network        #
#    our goal for this one is to simply see if anyone in the class  #
#    is able to hit the 99.67% accuracy reported by the book        #
#####################################################################
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
# bug fix - forgot to assign to the accuracy vars
evaluation_accuracy, training_accuracy, test_accuracy, bestepoch = net.SGD(expanded_training_data, epochs, mini_batch_size, 0.03, validation_data, test_data, lmbda=0.1)
finish = time.time()
elapsed = finish - start
print("Finished @{0}, elapsed {1}, best test_data accuracy {2} at epoch {3}".format(finish,elapsed,test_accuracy[bestepoch],bestepoch))
deep3results.append({"config":"best deep nn", 
                     "accuracy":test_accuracy[bestepoch],
                     "bestepoch":bestepoch,
                     "elapsed":elapsed})

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



