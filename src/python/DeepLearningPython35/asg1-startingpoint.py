##!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
IMAGE CLASSIFIER COMPARING FIVE DIFFERENT TECHNIQUES AS OUTLINED BELOW:
    1. Random guessing
    2. Overall digit darkness
    3. Quad darkness
    4. network.py

@author: briantoone
"""

# random prediction
import random

def predictDigitRandom(img):
    return random.randrange(10)

# overall darknessesses
darks = [0,0,0,0,0,0,0,0,0,0]
def predictDigitDark(img):
    return 1

# quadrant darknessesses
qdarks = [[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0],[0,0,0,0]]
def predictDigitQuadDark(img):
    return 1

# note about the data ... training data "labels" are in the vector-10 format
# whereas test_data labels have already been converted to "labels" (i.e., 0-9)
def evaluateAccuracy(func, data):
    count = [0,0,0,0,0,0,0,0,0,0]
    correct = [0,0,0,0,0,0,0,0,0,0]
    for img, label in data:
        count[label] = count[label] + 1 # unconditionally increment the count of the number of times we have seen this label
        if func(img) == label:
            correct[label] = correct[label]+1 # only increment the number of correct predictions if the prediction function returned an answer equal to the label
    
    # now display the accuracies
    for i in range(10):
        print(f"{i}: {correct[i]} out of {count[i]}")
    
    print(f"overall: {sum(correct)} out of {sum(count)}")
    
import mnist_loader
training_data, validation_data, test_data = mnist_loader.load_data_wrapper()
training_data = list(training_data)
test_data = list(test_data)

# part 1: no need to do any "training" since predictions are entirely random
evaluateAccuracy(predictDigitRandom, test_data)

# part 2: "train" up your network by calculating overall darkness for the images in the training_data
# part 2: after "training", call evaluateAccuracy and pass in predictDigitDark which will need to access your global darknesses list

# part 3: "train" up your network by calculating overall darknesses for each quadrant for all the images in the training_data
# part 3: after "training", call evaluateAccuracy and pass in predictDigitQuadDark which will need to access your global darknesses list

# part 4: finally, do real training by using the network.py per our in-class example from Tuesday class
# part 4: slightly tricky part is creating a function that uses the network to make prediction AFTER it has already trained, yes you can see the accuracies during training, but I also want to see the accuracies after it has gone through all 30 epochs and in our "per-digit" format
from network import Network
net = Network([784,30,10])
net.SGD(training_data, 30, 10, 3.0, test_data=test_data)

import numpy as np
# use our evaluateAccuracy function by creating a new predict function that somehow uses your net object. (note it will need to be global)
def predictDigitNet(img):
    return np.argmax(net.feedforward(img))

#test_data = list(test_data)
evaluateAccuracy(predictDigitNet, test_data)
 

