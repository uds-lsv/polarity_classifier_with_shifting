# -*- coding: utf-8 -*-
"""
Created on Thu Jan  5 20:03:02 2017

@author: Maximilian
"""

goldDict = dict()
inDict = dict()
with open('gold.txt', 'r') as gold, open('4.0-dsys4.txt', 'r') as infile:
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


def dict_compare(d1, d2):
    d1_keys = set(d1.keys())
    d2_keys = set(d2.keys())
    intersect_keys = d1_keys.intersection(d2_keys)
    modified = {o : (d1[o], d2[o]) for o in intersect_keys if d1[o] != d2[o]}
    same = set(o for o in intersect_keys if d1[o] == d2[o])
    return modified, same

modified, same = dict_compare(inDict, goldDict)


# P and R
def compute_scores(d1, d2, c):
    tp = 0
    fp = 0
    tn = 0
    fn = 0
    for i in range(len(d1)):
        if d1[i] == c and d2[i] == c:
            tp += 1
        if d1[i] == c and d2[i] != c:
            fp += 1
        if d1[i] != c and d2[i] == c:
            fn += 1
        if d1[i] != c and d2[i] != c:
            tn += 1
            
    precision_p = tp / (tp + fp)
    recall_p = tp / (tp + fn)
    f_p = 2* (precision_p*recall_p / (precision_p + recall_p))
            
    return "P = {}, R = {}, F = {}".format(precision_p, recall_p, f_p)

        
print(compute_scores(inDict, goldDict, "+"))
print(compute_scores(inDict, goldDict, "-"))
print(compute_scores(inDict, goldDict, "0"))