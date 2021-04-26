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
deep1results = []
def getResults():
    # note that by the time you call this method in your main.py script that it should
    # be fully populated
    return deep1results

##########################################################################################################
# FIRST NEURAL NETWORK - simple (not deep) neural network with one hidden layer                          #
#   you will be investigating the impact of a different number of hidden neurons (100 vs 50 vs 30 vs 15) #
#   plot the test_data accuracy for all four configurations on top of each other                         #
#   also store the following dictionary info into the "results" list                                     #
#     { "configuration": CONFIGNAME, "accuracy":0.9463, "bestepoch":17, "elapsed":850 }                  #
##########################################################################################################
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
print("Finished @{0}, elapsed {1}, best test_data accuracy {2} at epoch {3}".format(finish,elapsed,test_accuracy[bestepoch],bestepoch))
xvals = range(len(evaluation_accuracy))
yvals_trainingdata = training_accuracy            # you can delete this line
yvals_validationdata = evaluation_accuracy        # you can delete this line
yvals30_testdata = test_accuracy
deep1results.append({"config":"simple 30neurons", 
                     "accuracy":test_accuracy[bestepoch],
                     "bestepoch":bestepoch,
                     "elapsed":elapsed})

fig, ax = plt.subplots()
ax.plot(xvals,yvals_trainingdata)                 # don't plot anything yet!
ax.plot(xvals,yvals_validationdata)               # wait until you have yvals100_testdata, yvals50_testdata, yvals30_testdata, and yvals15_testdata 
ax.plot(xvals,yvals30_testdata)                   # KEEP THIS LINE!
ax.legend(["training_data accuracy", "validation_data accuracy", "test_data accuracy"])    # UPDATE THIS LEGEND AFTER YOU RUN THE OTHER CONFIGS
ax.set_title("SIMPLE NETWORK\n{0:.0f}s, {1:.3f}% accuracy @ epoch {2}".format(elapsed,test_accuracy[bestepoch]*100,bestepoch))
ax.set_xlabel("epoch")
ax.set_ylabel("accuracy")

