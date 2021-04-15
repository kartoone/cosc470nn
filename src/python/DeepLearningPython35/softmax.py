#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
DEMONSTRATES HOW SOFTMAX WORKS ON THE SAME NEURAL NETWORK THAT WAS USED FOR PART4 of the FIRST ASSIGNMENT
Illustrates difference between confidence and accuracy
Requires an updated version of util.py
Warning - pops up graphic window (usually in background) and you should NEVER close with the X button as it locks up python and potentially spyder
@author: briantoone
"""

from util import evaluateAccuracy, evaluateConfidence, drawDigit
from network import Network
import numpy as np
import time 
import mnist_loader

# use our neural network to make a prediction on a single image
def predictDigitNeuralNetwork(img):
    a = net.feedforward(img)
    return np.argmax(a)

training_data, validation_data, test_data = mnist_loader.load_data_wrapper()
training_data = list(training_data)
test_data = list(test_data)

start = time.time()
net = Network([784,30,10])
net.SGD(training_data, 30, 10, 3.0, test_data=test_data)
finish1 = time.time()

evaluateAccuracy(predictDigitNeuralNetwork, test_data)
print("Confidences:", evaluateConfidence(net, test_data))
finish2 = time.time()
print("training time: " + str(finish1-start))
print("prediction time: " + str(finish2-finish1))
print("elapsed time: " + str(finish2-start))

confidence_avgs, confidence_extremes = evaluateConfidence(net, test_data)
confidence_predictions = confidence_extremes[2]
print("Confidences:", confidence_avgs)
print(f"least confident when correct: {confidence_extremes[0][0]}, predicted {confidence_predictions[0][0]} actual {confidence_predictions[0][1]} full activation {confidence_predictions[0][2]}")
drawDigit(confidence_extremes[1][0], f"least confident when correct: {confidence_extremes[0][0]}")
print(f"least confident when wrong: {confidence_extremes[0][1]}, predicted {confidence_predictions[1][0]} actual {confidence_predictions[1][1]} full activation {confidence_predictions[1][2]}")
drawDigit(confidence_extremes[1][1], f"least confident when wrong: {confidence_extremes[0][1]}")
print(f"most confident when correct: {confidence_extremes[0][2]}, predicted {confidence_predictions[2][0]} actual {confidence_predictions[2][1]} full activation {confidence_predictions[2][2]}")
drawDigit(confidence_extremes[1][2], f"most confident when correct: {confidence_extremes[0][2]}")
print(f"most confident when wrong: {confidence_extremes[0][3]}, predicted {confidence_predictions[3][0]} actual {confidence_predictions[3][1]} full activation {confidence_predictions[3][2]}")
drawDigit(confidence_extremes[1][3], f"most confident when wrong: {confidence_extremes[0][3]}")

