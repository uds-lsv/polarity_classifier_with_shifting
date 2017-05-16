# -*- coding: utf-8 -*-
"""
Created on Tue Feb  7 16:20:34 2017

@author: Maximilian
"""

goldDict = dict()
inDict = dict()
with open('gold.txt', 'r') as gold, open('D-mlsa_all_1_scores.txt', 'r') as infile:
    gold = [line.strip() for line in gold]
    infile = [line.strip() for line in infile]
    counti = 0
    countj = 0
    for line in gold:
        goldDict[counti] = line
        counti += 1
    for line in infile:
        inDict[countj] = line
        countj += 1

newGold = dict()
newIn = dict()
print(len(inDict))
for key in goldDict.keys():
    if goldDict[key] != '0':
        newGold[key] = goldDict[key]
        newIn[key] = inDict[key]
print(len(newIn))

"""
with open('gold_only_polar.txt', 'w') as f:
    for item in newGold.items():
        n, score = item
        f.write(score +"\n")
print("Wrote new gold file")
"""

with open('D-mlsa_all_1_scores_only_polar.txt', 'w') as f:
    for item in newIn.items():
        n, score = item
        f.write(score +"\n")
print("Wrote new inFile")