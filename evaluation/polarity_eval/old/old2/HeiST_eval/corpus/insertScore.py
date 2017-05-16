# -*- coding: utf-8 -*-
"""
Created on Tue Feb  7 17:30:15 2017

@author: Maximilian
"""

def trim():
    with open("HeiST_stripped.txt", "r", encoding="utf-8") as f:
        raw = [line.strip() for line in f]
    tokens = []
    for s in raw:
        x = ' '.join(s.split())
        tokens.append(x)
    with open("tokens_all.txt", "w", encoding="utf-8") as f:
        for t in tokens:
            f.write(t + "\n")
#trim()
            
            
def getScores():
    with open("HeiST.ptb", "r", encoding="utf-8") as f:
        lines = [line.strip()[1:2:] for line in f]
    with open("scores_all.txt", "w", encoding="utf-8") as f:
        for n in lines:
            if n == '0' or n == '1':
                f.write('-' + "\n")
            elif n == '3' or n == '4':
                f.write('+' + "\n")
            else:
                f.write('0' + "\n")
            
    
getScores()