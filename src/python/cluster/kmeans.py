import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans

# GENERATE THE RANDOM SAMPLE DATA IN TWO CLUSTERS
X= -2 * np.random.rand(100,2)
X1 = 1 + 2 * np.random.rand(50,2)
X[50:100, :] = X1
print("Raw data:")
print(X)

# CLUSTER THE DATA INTO TWO CLUSTERS
Kmean = KMeans(n_clusters=2)
Kmean.fit(X)
centers = Kmean.cluster_centers_

# PLOT THE DATA WITH BLUE DOTS AND GREEN/RED SQUARE MARKERS AT CENTROID LOCATIONS
plt.scatter(X[ : , 0], X[ :, 1], s = 50, c = 'b')
plt.scatter(centers[0][0], centers[0][1], s=200, c='g', marker='s')
plt.scatter(centers[1][0], centers[1][1], s=200, c='r', marker='s')
plt.show()

print("Cluster mappings of raw data:")
print(Kmean.labels_)

sample_test=[[-1.0,-1.0],[2.0,2.0]]  # note these are specifically chosen to close to center of each cluster
print(sample_test)

# find the closest clusters ... note that cluster 0 isn't always going to be the LEFT one, but it will always be the GREEN one
print(Kmean.predict(sample_test))