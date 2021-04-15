# -*- coding: utf-8 -*-

import math

"""
modeling a Sigmoid Neuron as described
in Chapter 1

one function - activate that
outputs a value between 0 and 1
by applying sigmoid function to 
the weighted sum of inputs and bias

properties -
    weights
    bias

Created on Tue Feb  2 12:06:53 2021

@author: csAdmin
"""
class SigmoidNeuron:
    
    def __init__(self, weights, bias):
        self.weights = weights
        self.bias = bias
        
    """
    inputs should be a list
    returns sigmoid function applied to thhe weighted sum of inputs + bias
    """
    def activate(self, inputs):
        if len(inputs) != len(self.weights):
            raise Exception('input length incorrect, does not match the number of weights for this neuron')
        
        z = 0
        for ipt, w in zip(inputs, self.weights):
           z = z + ipt*w
        
        z = z + self.bias
        return self._sigmoid(z)
        
    def _sigmoid(self, z):
        return 1/(1+math.exp(-z))

# demonstrates SigmoidNeuron version of a NAND gate    
print("sigmoidneuron implementation of nand gate showing the results for all four possible inputs:")
p = SigmoidNeuron([-20,-20],30)
print(f"p.activate([0,0]) = {p.activate([0,0])}")
print(f"p.activate([0,1]) = {p.activate([0,1])}")
print(f"p.activate([1,0]) = {p.activate([1,0])}")
print(f"p.activate([1,1]) = {p.activate([1,1])}")

# more demonstration about how a SigmoidNeuron works
# "solution" to the first exercise in Chapter 1
amt = 200
bias = -100
bit3n = SigmoidNeuron([0, 0, 0, 0, 0, 0, 0, 0, amt, amt],bias)
bit2n = SigmoidNeuron([0, 0, 0, 0, amt, amt, amt, amt, 0, 0],bias)
bit1n = SigmoidNeuron([0, 0, amt, amt, 0, 0, amt, amt, 0, 0],bias)
bit0n = SigmoidNeuron([0, amt, 0, amt, 0, amt, 0, amt, 0, amt],bias)

# the output of the neurons for the input below should display binary 3 b/c of the 1 in the 3th position 
inputs = [0, 0, 0, 1, 0, 0, 0, 0, 0, 0]
bit3 = round(bit3n.activate(inputs),10)
bit2 = round(bit2n.activate(inputs),10)
bit1 = round(bit1n.activate(inputs),10)
bit0 = round(bit0n.activate(inputs),10)
print("\ndigit 3 represented in binary using the neuron results:")
print(bit3, bit2, bit1, bit0)

# same example as above except showing all 10 digits in a loop
print("\nall 10 digits represented as binary:")
inputs = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
for i in range(10):
    inputs[i] = 1
    if (i>0):
        inputs[i-1] = 0
    bit3 = round(bit3n.activate(inputs),10)
    bit2 = round(bit2n.activate(inputs),10)
    bit1 = round(bit1n.activate(inputs),10)
    bit0 = round(bit0n.activate(inputs),10)
    print(i,' as binary: ',bit3, bit2, bit1, bit0)