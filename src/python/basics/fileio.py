#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  4 17:41:09 2021

@author: briantoone
"""

file = open('pairs.txt')
lines = file.readlines()
x = []
y = []
for line in lines:
    line = line.strip()
    line = line.split(',')
    x = x + [float(line[0][1:])]
    y = y + [float(line[1][0:-1])]

print(x)
print(y)

