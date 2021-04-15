# -*- coding: utf-8 -*-
"""
Created on Wed Jan 29 10:30:47 2020

@author: kartoone
"""

import random

counts = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
for i in range(100):
    num = random.randrange(0,10)
    counts[num] = counts[num] + 1
    
for (key,value) in enumerate(counts):
    print(f"{key} - {value}")
      
students = { "Jonathan Borden":1, "Michael Brewington":16, "Patrick Cothran":22, "Trey Dobyns":8, "Ryan Gibson":28, "Dalton Halliday":21, \
"Jackson Holbrook":18, \
"Jaylen King King":24, \
"Spence Nace":7, \
"Tyler O'Neal":20, \
"Daniel Ontiveros":14,\
"Roland":18,\
"Justin":19,\
"Anisha":30,\
"Sheng":29,\
"Yuan Wang":4,\
"Chris Williams":6 }

print(students["Roland"])

while True:
    luckynum = 18
    luckypeople = [key for (key, value) in students.items() if value==luckynum]
#    for (key,value) in students.items():
#        if value==luckynum:
#            print(key)
    print(luckynum)
    print(luckypeople)
    if(len(luckypeople)>0):
        break


