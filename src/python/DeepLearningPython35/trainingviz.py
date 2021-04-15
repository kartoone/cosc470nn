# -*- coding: utf-8 -*-
"""
Created on Tue Feb  9 11:52:49 2021

@author: csAdmin
"""

# ----------------------
# - read the input data:
import mnist_loader
training_data, validation_data, test_data = mnist_loader.load_data_wrapper()
training_data = list(training_data)
print(len(training_data))
print(training_data[0])
print(len(training_data[0]))
print(len(training_data[0][0]))
#print(training_data[0][0])

from util import convertLabel, drawDigit

print(convertLabel(training_data[0][1]))
drawDigit(training_data[0][0], training_data[0][1])










