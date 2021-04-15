#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Feb 16 11:32:45 2021

@author: briantoone
"""

sizes = [784, 15, 10]
print(len(sizes))
print(sizes)
print(sizes[1])     # displays 15
print(sizes[1:])    # displays [15, 10]
print(sizes[:2])    # displays [784, 15]
print(sizes[1:3])   # displays [15, 10]

print(sizes[:-1])
even = [x*2 for x in range(20)]
print(even)
print([2]*10)

m = [pow(x, y) for x,y in zip(range(10),[2]*10)]
betterm = [pow(x,2) for x in range(10)]
print(m)
print(betterm)

import numpy as np
print(np.random.randn(15, 1))
print(np.random.randn(10, 1))
biases = [np.random.randn(y, 1) for y in sizes[1:]] 
print(biases)

weights = [np.random.randn(y, x) for x, y in zip([784,15], [15,10])]
print(weights)

# this is java
# even = new int[20];
# for (int i=0; i<20; i++) {
#    even[i] = 2*i;
# }