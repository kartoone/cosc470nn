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
epochs = 4    # CHANGE THIS TO 60 FOR YOUR "REAL" RUNS AFTER YOU MAKE SURE YOU CODE IS WORKING

################################
# LOADING THE TRAINING, VALIDATION, AND TEST DATA REUSED FOR NETWORKS 1 AND 2
# OUR FIRST TWO NETWORKS WILL NOT USE THE EXPANDED TRAINING DATA ... BECAUSE IT TAKES TOO LONG!
################################
training_data, validation_data, test_data = load_data_shared("mnist.pkl.gz")
training_data = list(training_data)
validation_data = list(validation_data)
test_data = list(test_data)

##########################################################################################
# RESULTS DICTIONARY LIST                                                                #
#   list of dictionary objects summarizing the results of each run                       #
#     { "configuration": CONFIGNAME, "accuracy":0.9463, "bestepoch":17, "elapsed":850 }  #
##########################################################################################
deep2results = []
def getResults():
    # note that by the time you call this method in your main.py script that it should
    # be fully populated
    return deep2results

##################################################################################################
# SECOND NEURAL NETWORK - shallowest deep convolutional neural network                           #
#   you will be investigating the impact of configuration changes                                #
#   relating to the number of feature maps as well as the local receptive field size             #
#   and the pooling region size. This is a LOT of changes so you will be running this one a LOT  #
##################################################################################################
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
print("Finished @{0}, elapsed {1}, best test_data accuracy {2} at epoch {3}".format(finish,elapsed,test_accuracy[bestepoch],bestepoch))
deep2results.append({"config":"deep1, 5x5, 2x2, 20featmaps", 
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
ax.set_title("DEEP1: 5x5, 2x2")
ax.set_xlabel("epoch")
ax.set_ylabel("accuracy")



