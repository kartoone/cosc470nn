# -*- coding: utf-8 -*-
"""
Created on Tue Feb  9 12:42:52 2021

@author: csAdmin
"""

from graphics import *
import numpy as np

# converts the 10 element output vector to the
# corresponding digit 0-9
def convertLabel(label):
    for i,v in enumerate(label):
        if v[0]==1:
            return i

def drawDigit(digit, label):
    #label = str(convertLabel(label))
    win = GraphWin(label, 28, 28)
    for y in range(28):
        for x in range(28):
            grayscale = int(digit[y*28+x][0]*255)
            color = color_rgb(255-grayscale, 255-grayscale, 255-grayscale)
            win.plot(x, y, color)                
    win.getMouse() # Pause to view result
    win.close()    # Close window when done
    
# note about the data ... training data "labels" are in the vector-10 format
# whereas test_data labels have already been converted to "labels" (i.e., 0-9)
# returns the overall accuracy but prints the per class accuracy
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
    return [sum(correct)/sum(count), [correct[i]/count[i] for i in range(10)]]

# returns a pair of lists
#   first list is the confidence averages for correct classifcations and mis-classifications
#   second list is the images associated with the extremes (most confident, least confident for both cases)
def evaluateConfidence(net, data):
    confidences = [0, 0] # position 0 is the ones that are correct, position 1 is the ones that were wrong
    counts = [0, 0]      # count of how many correct (position 0), how many wrong (position 1)
    confidencevals = [1, 1, 0, 0]   # position meanings
                                    #  position 0 is least confident value when correct, 
                                    #  position 1 is least confident value when wrong, 
                                    #  position 2 is most confident value when correct, 
                                    #  position 3 is most confident value when wrong
    confidenceimgs = [None, None, None, None]   # same position meanings for these except it's indices instead of confidence values
    confidencepreds = [0, 0, 0, 0]
    for img, label in data:
        a = net.feedforward(img)
        prediction = np.argmax(a)
        if prediction==label: # yay, it's correct! increment correct count and our confidences total
             counts[0] = counts[0] + 1
             confidences[0] = confidences[0] + a[prediction]
             if a[prediction] < confidencevals[0]:
                 confidencevals[0] = a[prediction]
                 confidenceimgs[0] = img
                 confidencepreds[0] = (prediction,label,a)
             if a[prediction] > confidencevals[2]:
                 confidencevals[2] = a[prediction]
                 confidenceimgs[2] = img
                 confidencepreds[2] = (prediction,label,a)
        else:
             confidences[1] = confidences[1] + a[prediction]
             counts[1] = counts[1] + 1
             if a[prediction] < confidencevals[1]:
                 confidencevals[1] = a[prediction]
                 confidenceimgs[1] = img
                 confidencepreds[1] = (prediction,label,a)
             if a[prediction] > confidencevals[3]:
                 confidencevals[3] = a[prediction]
                 confidenceimgs[3] = img
                 confidencepreds[3] = (prediction,label,a)
    return ([confidences[0]/counts[0],confidences[1]/counts[1]],(confidencevals,confidenceimgs,confidencepreds))
