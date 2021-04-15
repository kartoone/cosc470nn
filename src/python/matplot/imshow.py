# -*- coding: utf-8 -*-
"""
Created on Wed Apr  7 10:51:39 2021

@author: karto
"""

import matplotlib.pyplot as plt
import numpy as np
import mnist_loader

# generate some random noise
pixeldata = []
for row in range(28):
    rowdata = []
    for col in range(28):
        rowdata.append(np.random.random()*255)
    pixeldata.append(rowdata)
    
fig, ax = plt.subplots()
ax.imshow(pixeldata, cmap='gray')

training_data, validation_data, test_data = mnist_loader.load_data()
labeled_images = list(zip(training_data[0], training_data[1]))
print(len(labeled_images))
print(len(labeled_images[0]))
fig, ax = plt.subplots(5,1,figsize=(20,20))
i = 0
for axi in ax:
    axi.imshow(np.reshape((1-labeled_images[i][0])*255,(28,28)), cmap='gray')
    axi.text(27,1,'actual:' + str(labeled_images[i][1]),horizontalalignment='right')
    i = i+1
