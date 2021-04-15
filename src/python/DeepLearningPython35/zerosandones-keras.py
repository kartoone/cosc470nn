# -*- coding: utf-8 -*-
"""
Created on Thu Mar 18 12:00:12 2021
USES KERAS INSTEAD OF THE AUTHOR'S CODE ... MUST BE RUN FROM TFKERAS ENVIRONMENT

@author: karto
"""

import network
import network2
import random
import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers


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
training_dataX = [convert_string(string) for string, _ in training_data]
training_dataY = [label for _, label in training_data]
print(training_dataX[0])


test_data = data[90000:]
test_data = [[convert_string(string), np.argmax(label)] for string, label in test_data]

print(training_data[0])

print("training_data class count", count_classes(training_data,True))
print("test_data class count",count_classes(test_data, False))

#net = network.Network([50,50,2])
#net.SGD(training_data, 30, 10, 3.0, test_data=test_data)#

# now let's build the same keras network
model = keras.models.Sequential()
model.add(layers.Dense(50, input_shape=(90000,), activation='sigmoid'))
model.add(layers.Dense(2, activation='sigmoid'))
model.compile(loss='mean_squared_error', optimizer='sgd', metrics=['accuracy'])
model.fit(training_dataX,training_dataY, epochs=30, batch_size=10)



