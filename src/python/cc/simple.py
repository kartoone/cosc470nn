# -*- coding: utf-8 -*-
"""
Created on Thu Feb 25 11:41:19 2021

@author: karto
"""
import pandas as pd
from fuzzywuzzy import fuzz, process
closeness = fuzz.WRatio('python','ppython')
print(closeness)

data = pd.read_csv('Retailer_Key_Log.csv', usecols=['TransDesc1','MCC','Extra_Retailer_Field'])
print(data)

# trim the Extra_retailer_Field down to just the unique retailers
# all_retailers = data.get("Extra_Retailer_Field")
unique_retailers = data['Extra_Retailer_Field'].unique()
print(len(unique_retailers))

random_txn = data['TransDesc1'].sample()
print(random_txn.values[0])
hits = process.extract(query=random_txn.values[0],choices=unique_retailers,limit=3)
print(hits)


