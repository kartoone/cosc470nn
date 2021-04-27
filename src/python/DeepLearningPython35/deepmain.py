#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Apr 24 17:46:39 2021

@author: briantoone
"""

# THESE IMPORTS BELOW SHOULD CAUSE ALL THE TOP LEVEL CODE TO RUN IN EACH FILE!!!
import deep1
import deep2
import matplotlib.pyplot as plt

# COMBINE THE RESUTLS FROM THE SIMPLE NN (DEEP1) AND THE ACTUAL DEEP NN (DEEP2)
results1 = deep1.getResults()
results2 = deep2.getResults()
results = results1 + results2
print(results)

fig, ax = plt.subplots()
xvals = [result["elapsed"] for result in results]
yvals = [result["accuracy"] for result in results]
ax.plot(xvals,yvals)
ax.set_xlabel("elapsed time")
ax.set_ylabel("accuracy")
ax.set_title("time vs accuracy")

# eleven different markers for the eleven results
fig, ax = plt.subplots()
for i, marker in enumerate(['o', '.', ',', 'x', '+', 'v', '^', '<', '>', 's', 'd']):
    if i<len(results):
        xval = results[i]["elapsed"]
        yval = results[i]["accuracy"]
        label = results[i]["config"]
        ax.plot([xval],[yval],marker,label=label)
ax.legend(numpoints=1)
ax.set_xlabel("elapsed time")
ax.set_ylabel("accuracy")
ax.set_title("time vs accuracy")

# eleven different markers for the eleven results
fig, ax = plt.subplots()
for i, marker in enumerate(['o', '.', ',', 'x', '+', 'v', '^', '<', '>', 's', 'd']):
    if i<len(results):
        xval = results[i]["epoch"]
        yval = results[i]["accuracy"]
        label = results[i]["config"]
        ax.plot([xval],[yval],marker,label=label)
ax.legend(numpoints=1)
ax.set_xlabel("best epoch")
ax.set_ylabel("accuracy")
ax.set_title("best epoch vs accuracy")
