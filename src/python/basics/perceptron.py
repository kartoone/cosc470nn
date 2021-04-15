#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  4 08:52:17 2021

@author: briantoone
"""

class Perceptron:
    
    def __init__(self, weights, bias):
        self.weights = weights
        self.bias = bias
    
    def activate(self, inputs):
        if len(inputs) != len(self.weights):
            print(f"Error: input length {len(inputs)} != number of required inputs {len(self.weights)}")
        total = 0
        for x,w in zip(inputs,self.weights):
            total = total + x*w            
        total = total + self.bias
            
        return 0 if total<=0 else 1
    
# demonstrates a Perceptron that implements the NAND logic gate
p = Perceptron([-2,-2],3)
print("p.activate([0,0]) = ", p.activate([0, 0]))
print("p.activate([0,1]) = ", p.activate([0, 1]))
print("p.activate([1,0]) = ", p.activate([1, 0]))
print("p.activate([1,1]) = ", p.activate([1, 1]))
