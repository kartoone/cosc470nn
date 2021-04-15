# -*- coding: utf-8 -*-
"""
Created on Wed Feb 17 10:54:27 2021

@author: csAdmin
"""

lis = [1, 2, 3, 4, 5]
seq = (1, 2, 3, 4, 5)
newlis = lis[1:4]
del(lis[0])
del(lis[3])
lis.remove(2)
print(lis.index(3))
print(newlis)
print(lis)

newseq = seq[1:4]
# del(seq[0]) <-- fails b/c seq is read-only
# seq.remove(3) # <-- fails b/c seq doesn't have a remove method

terms = {
    "zeroth":"concept in computer science b/c of the way memory works", 
    "null":"represents a missing value or empty value in many lanuages",
    "dictionary":"represents a collection of key value pairs",
}
print(terms["zeroth"])
del(terms["zeroth"])
print(len(terms))

for i, val in enumerate(lis):
    print(f"{i} - {val}")
    

for key, val in terms.items():
    print(f"{key} - {terms[key]}")




