# -*- coding: utf-8 -*-
"""
Created on Thu Mar  4 11:29:17 2021

Goal: take all 70,000 MNIST images 60,000 training data + 10,000 test data
and then run all that data through a clustering algorithm and see how accurate
the clusters are...

1) Are there 10 clusters?
2) If we tell the algorithm to find 5 clusters, what gets grouped together?
3) How many images get put into the WRONG cluster?

@author: karto
"""

import sklearn.metrics as metrics
from matplotlib import pyplot as plt
from sklearn.cluster import KMeans
from keras.datasets import mnist
import numpy as np

# ys (labels)
# ypredict (the predictions)
def acc(ys, ypredict):
    total = len(ys)
    correct = 0
    for y, prediction in zip(ys, ypredict):
        if y==prediction:
            correct = correct + 1
    return correct/total

(x_train, y_train), (x_test, y_test) = mnist.load_data()
x = np.concatenate((x_train, x_test))
y = np.concatenate((y_train, y_test))

x = x.reshape((x.shape[0], -1))
x = np.divide(x, 255.)
# 10 clusters
n_clusters = len(np.unique(y))*2
# Runs in parallel 4 CPUs
kmeans = KMeans(n_clusters=n_clusters, n_init=20, n_jobs=4)
# Train K-Means.
y_pred_kmeans = kmeans.fit_predict(x)

# display five images from each cluster side-by-side with all clusters stacked
# display all images together in single plot (should show up in "Plots" tab in Spyder)
pos = 0
plti = 1
cols = ['Example {}'.format(col) for col in range(1, 6)]
rows = ['Cluster {}'.format(row) for row in range(10)]
fig, axes = plt.subplots(nrows=20, ncols=10, figsize=(24,30))
for ax, col in zip(axes[0], cols):    
    ax.set_title(col)
for ax, row in zip(axes[:,0], rows):
    ax.set_yticklabels([])
    ax.set_ylabel(row, rotation=0, size='large')
for cluster in range(20):
    for cnt in range(10):
        while y_pred_kmeans[pos] != cluster:
            pos = pos + 1
        pixels = x[pos].reshape((28,28))
        fig.add_subplot(20,10,plti)
        plti = plti + 1
        plt.imshow(pixels, cmap='gray')
        pos = pos + 1
plt.show()

# Evaluate the K-Means clustering accuracy.
# MACHINE: award the cluster to whichever image occurred the most
# so prompt the user for EACH image even within the cluster
mclusters = []
for cluster in range(20):
    mcnt = [0,0,0,0,0,0,0,0,0,0]
    for i in range(10):
        hy = int(input(f"what is img{i} in cluster{cluster}"))
        mcnt[hy] = mcnt[hy]+1
    mclusters.append(np.argmax(mcnt))
  

# HUMAN: at the image to see what clusters the K-Means clustering found
clusters = []
for cluster in range(20):
    clusters.append(int(input("what image is cluster " + str(cluster) + "? ")))

# update the cluster "predictions" with the user input of what each cluster is
my_pred_kmeans = [mclusters[oldpredict] for oldpredict in y_pred_kmeans]
y_pred_kmeans = [clusters[oldpredict] for oldpredict in y_pred_kmeans]
    
ouracc = acc(y, y_pred_kmeans)
print(ouracc)

macc = acc(y, my_pred_kmeans)
print(macc)
