#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Mar  5 21:50:54 2021

@author: briantoone
"""

t = input("What is the current temperature? ")
t = int(t)
if t > 75:
  print("hot")
elif t < 50:
  print("cold")
else:
  print("just right")