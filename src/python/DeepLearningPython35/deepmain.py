#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Apr 24 17:46:39 2021

@author: briantoone
"""

# THESE IMPORTS BELOW SHOULD CAUSE ALL THE TOP LEVEL CODE TO RUN IN EACH FILE!!!
import deep1
import deep2

# COMBINE THE RESUTLS FROM THE SIMPLE NN (DEEP1) AND THE ACTUAL DEEP NN (DEEP2)
results1 = deep1.getResults()
results2 = deep2.getResults()
results = results1 + results2
print(results)
