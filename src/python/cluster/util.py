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