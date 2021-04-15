# -*- coding: utf-8 -*-
"""
Created on Tue Apr  6 12:20:01 2021

@author: karto
"""

import matplotlib.pyplot as plt
from math import exp

def generateData(f):
    xvals = range(1, 10)
    yvals = [f(x) for x in xvals]
    return (xvals, yvals)

# single plot
xvals, yvals = generateData(lambda x : exp(x))
fig, ax = plt.subplots()
ax.plot(xvals, yvals)
ax.set_xlabel("x")
ax.set_ylabel("1/exp(x)")

# muliple plots grouped together
fig, ax = plt.subplots(3, 3, figsize=(10,10))
axes = ax.flat

xvals, yvals = generateData(lambda x : x)
axes[0].plot(xvals, yvals)

xvals, yvals = generateData(lambda x : 6)
axes[1].plot(xvals, yvals)

xvals, yvals = generateData(lambda x : 2*x)
axes[2].plot(xvals, yvals)

xvals, yvals = generateData(lambda x : 4*x)
axes[3].plot(xvals, yvals)

xvals, yvals = generateData(lambda x : -x*x)
axes[4].plot(xvals, yvals)

xvals, yvals = generateData(lambda x : -x*x*x)
axes[5].plot(xvals, yvals)




    
