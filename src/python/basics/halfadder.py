#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  4 09:01:22 2021

@author: briantoone
"""

from perceptron import Perceptron

class Halfadder:
    p1 = Perceptron([-2,-2],3)
    p2 = Perceptron([-2,-2],3)
    p3 = Perceptron([-2,-2],3)
    p4 = Perceptron([-4],3)
    p5 = Perceptron([-2,-2],3)

    def add(self, x1, x2):
        p1out = self.p1.activate([x1,x2])
        p2out = self.p2.activate([x1,p1out])
        p3out = self.p3.activate([p1out,x2])
        c = self.p4.activate([p1out])
        s = self.p5.activate([p2out,p3out])
        return (s, c)
        
# demonstrates functionality of half adder
ha = Halfadder()
s,c = ha.add(0, 0)
print(f"0 + 0 = {s} with carry out of {c}")

s,c = ha.add(0, 1)
print(f"0 + 1 = {s} with carry out of {c}")

s,c = ha.add(1, 0)
print(f"1 + 0 = {s} with carry out of {c}")

s,c = ha.add(1, 1)
print(f"1 + 1 = {s} with carry out of {c}")