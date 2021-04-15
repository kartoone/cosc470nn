# -*- coding: utf-8 -*-
"""
Created on Thu Mar 18 12:00:12 2021

@author: karto
"""

import network
import network2
import random
import numpy as np

"""
generates a single string of random 0s and 1s
"""
def generate_string(length):
    digits = [str(random.randint(0,1)) for i in range(length)]
    return "".join(digits)

def convert_string(string):
    return np.array([float(digit) for digit in string],dtype=float).reshape(len(string),1)

def check_pattern(string, pattern):
    return pattern in string

def count_classes(training_data, useargmax):
    counts = [0, 0]
    for _ , label in training_data:
        lbl = np.argmax(label) if useargmax else label
        counts[lbl] = counts[lbl] + 1        
    return counts

strlength = 50
datasize = 100000
pattern = "000000"

data = []
for _ in range(datasize):
    string = generate_string(strlength)
    label = [0.,1.] if check_pattern(string, pattern) else [1.,0.]
    data.append([string, np.array(label,dtype=float).reshape(2,1)])

training_data = data[:90000]
training_data = [[convert_string(string), label] for string, label in training_data]

test_data = data[90000:]
test_data = [[convert_string(string), np.argmax(label)] for string, label in test_data]

print(training_data[0])

print("training_data class count", count_classes(training_data,True))
print("test_data class count",count_classes(test_data, False))

net = network.Network([50,50,2])
net.SGD(training_data, 30, 10, 3.0, test_data=test_data)#

#net = network2.Network([50, 50, 2], cost=network2.CrossEntropyCost)
#net.large_weight_initializer()
#net.SGD(training_data, 30, 10, 0.5, evaluation_data=test_data,monitor_evaluation_accuracy=True)




