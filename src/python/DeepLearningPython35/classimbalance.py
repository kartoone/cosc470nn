# -*- coding: utf-8 -*-
"""
Created on Tue Mar 16 12:26:45 2021

@author: karto
"""

import copy
import mnist_loader
import network
import network2
import random
import matplotlib.pyplot as plt
import numpy as np
from util import convertLabel, evaluateAccuracy

# removes a certain percentage one class of the training_data
# for example, removes 75% of the 3s in an MNIST 10 output classifier 
def remove_data(training_data, target, percent):
    new_training_data = []
    for row in training_data:
        label = convertLabel(row[1])
        if (label==target and random.random()>percent) or label!=target:
            new_training_data.append(row)            
    return new_training_data

def count_classes(training_data):
    counts = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    for row in training_data:
        label = convertLabel(row[1])
        counts[label] = counts[label] + 1        
    return counts

def predictDigitNeuralNetwork(img):
    a = net.feedforward(img)
    return np.argmax(a)

# load all the data here and save a copy of the original training data since we are going to be messing with it
training_data, validation_data, test_data = mnist_loader.load_data_wrapper()
training_data = list(training_data)
validation_data = list(validation_data)
test_data = list(test_data)
origtraining_data = copy.deepcopy(training_data)

xvals = range(0,102,2)
yvalsoverall = []
yvalsdigits = [[] for _ in range(10)]
for strip in xvals:
    training_data = copy.deepcopy(origtraining_data)
    before = count_classes(training_data)
    print(before)
    print("stripping out {}% of 3's".format(strip))
    training_data = remove_data(training_data, 3, strip/100)
    after = count_classes(training_data)
    print("stripped out {}% of 3s".format(100*(before[3]-after[3])/before[3]))
    net = network2.Network([784, 30, 10], cost=network2.CrossEntropyCost)
    net.SGD(training_data, 30, 10, 0.5, evaluation_data=test_data,monitor_evaluation_accuracy=True)
#    net = network.Network([784,30,10])
#    net.SGD(training_data, 30, 10, 3.0, test_data=test_data)
    accuracy = evaluateAccuracy(predictDigitNeuralNetwork,test_data)  
    yvalsoverall.append(accuracy[0])
    for i, digitacc in enumerate(accuracy[1]):
        yvalsdigits[i].append(digitacc)
      
fig, ax = plt.subplots()
ax.plot(xvals, yvalsoverall)
ax.set_xlabel("percentage of 3s removed")
ax.set_ylabel("accuracy")

fig, ax = plt.subplots()
for i, yvalsdigit in enumerate(yvalsdigits):
    ax.plot(xvals, yvalsdigit)
    if i==3:
        npyvals = np.asarray(yvalsdigit)
        ax.fill_between(xvals, npyvals-0.01, npyvals+ 0.01, alpha=0.3, color='y')
        #this if statement highlights the digit 3 and distinguishes it from the others

ax.legend(range(10))
ax.set_xlabel("percentage of 3s removed")
ax.set_ylabel("accuracy")

# now looking at the per digit classification without showing the 3s
yvalsdigitsno3s = yvalsdigits[0:3] + yvalsdigits[4:]
overallacc = [0 for _ in range(9)] # only 9 b/c we are excluding 3

fig, ax = plt.subplots()
for i, yvalsdigit in enumerate(yvalsdigitsno3s):
    ax.plot(xvals, yvalsdigit)

ax.legend([0, 1, 2, 4, 5, 6, 7, 8, 9],loc=3)
ax.set_xlabel("percentage of 3s removed - omitting 3s to see other digits better")
ax.set_ylabel("accuracy")

# now looking at the per digit classification showing 0s, 1s, 5s, and 8s only
yvalsdigitsinteresting = yvalsdigits[0:2] + [yvalsdigits[5]] + [yvalsdigits[8]]
fig, ax = plt.subplots()
for i, yvalsdigit in enumerate(yvalsdigitsinteresting):
    ax.plot(xvals, yvalsdigit)

ax.legend([0, 1, 5, 8],loc=3)
ax.set_xlabel("percentage of 3s removed - only showing interesting digits")
ax.set_ylabel("accuracy")

# determine overall accuracy at each strip stage for all digits excluding the 3s
overallacc = [0 for _ in xvals]
for digitaccs in yvalsdigitsno3s:
    for i, digitstripacc in enumerate(digitaccs):
        overallacc[i] = overallacc[i] + digitstripacc
for i in range(len(xvals)):
    overallacc[i] = overallacc[i] / len(yvalsdigitsno3s) # should be dividing by 9 in the normal setup
        
# now print the overall accuracy but excluding the drop b/c of all the missed 3s
fig, ax = plt.subplots()
ax.plot(xvals,overallacc)
ax.set_xlabel("percentage of 3s removed")
ax.set_ylabel("overall accuracy excluding 3s")

