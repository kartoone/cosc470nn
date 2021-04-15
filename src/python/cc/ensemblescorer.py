# -*- coding: utf-8 -*-
"""
Created on Thu Feb 25 11:41:19 2021

run the four algorithms: fuzzy, jaro, dl, and normal dl against extra_retailer 
AND against other transactions that map to same retailer


@author: karto
"""
import jaro
import pandas as pd
from fuzzywuzzy import fuzz, process
from pyxdameraulevenshtein import damerau_levenshtein_distance, normalized_damerau_levenshtein_distance

# run jaro, dl, and more? against choices return best hit in dictionary
def processtxn(txn, choices):
    maxscoreJ = 0
    matchstrJ = ""
    maxscoreDL = 0
    matchstrDL = ""
    maxscoreNDL = 0
    matchstrNDL = ""
    for c in choices:
        scoreJ = jaro.jaro_metric(txn, c)
        scoreDL = 1000-damerau_levenshtein_distance(txn, c)
        scoreNDL = 1-normalized_damerau_levenshtein_distance(txn, c)
        if scoreJ>maxscoreJ:
            matchstrJ = c
            maxscoreJ = scoreJ
        if scoreDL>maxscoreDL:
            matchstrDL = c
            maxscoreDL = scoreDL
        if scoreNDL>maxscoreNDL:
            matchstrNDL = c
            maxscoreNDL = scoreNDL
    return {'jaro':matchstrJ,'dl':matchstrDL,'ndl':matchstrNDL}    


data = pd.read_csv('Retailer_Key_Log.csv', usecols=['TransDesc1','MCC','Extra_Retailer_Field'])

# preprocess the data to make sure all the retailers and all transactions are all lowercase with leading/trailing spaces removed
for i, row in data.iterrows():
    data.at[i,'Extra_Retailer_Field'] = data.at[i,'Extra_Retailer_Field'].strip().lower()
    data.at[i,'TransDesc1'] = data.at[i,'TransDesc1'].strip().lower()

# trim the Extra_retailer_Field down to just the unique retailers
unique_retailers = data['Extra_Retailer_Field'].unique()
print(len(unique_retailers), "unique retailers")

# now let's group the transactions based on unique retailers


totalsamples = 50
print(totalsamples, "total samples")
correct = []
wrong = []
nearmiss = []
off2 = []
jarocorrect = []
jarowrong = []
dlcorrect = []
dlwrong = []
ndlcorrect = []
ndlwrong = []
for cnt in range(totalsamples):
    sample = data.sample()
    txn = sample.values[0][0].strip().lower()
    actual = sample.values[0][2].strip().lower()
    hits = process.extract(query=txn,choices=unique_retailers,limit=3)
    extra = processtxn(txn,unique_retailers)
    jhit = extra['jaro']
    dlhit = extra['dl']
    ndlhit = extra['ndl']
    if hits[0][0] == actual:
        app = correct
    elif hits[1][0] == actual:
        app = nearmiss
    elif hits[2][0] == actual:
        app = off2
    else:
        app = wrong
    app.append({'txn':txn,'actual':actual,'hits':hits})
    app = jarocorrect if jhit == actual else jarowrong
    app.append({'txn':txn,'actual':actual,'jhit':jhit})
    app = dlcorrect if dlhit == actual else dlwrong
    app.append({'txn':txn,'actual':actual,'dlhit':dlhit})
    app = ndlcorrect if ndlhit == actual else ndlwrong
    app.append({'txn':txn,'actual':actual,'dlhit':ndlhit})
    if (cnt+1)%10==0:
        print(f"Processed {cnt+1} samples")
        print(f"{len(nearmiss)} off by one")
        print(f"{len(off2)} off by two")
        print(f"{len(wrong)} not in top 3")
        print(f"{len(correct)} FUZZYWUZZY correct")
        print(f"{len(jarocorrect)} JARO correct")
        print(f"{len(dlcorrect)} DL correct")
        print(f"{len(ndlcorrect)} NDL correct")
           
print("finished")
print("JARO CORRECT")
print(jarocorrect)
print("JARO WRONG")
print(jarowrong)
print("DL CORRECT")
print(dlcorrect)
print("DL WRONG")
print(dlwrong)
print("NDL CORRECT")
print(ndlcorrect)
print("NDL WRONG")
print(ndlwrong)



