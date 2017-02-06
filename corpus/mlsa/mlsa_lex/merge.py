# -*- coding: utf-8 -*-
"""
Created on Mon Feb  6 13:43:26 2017

@author: Maximilian
"""

# Read in mlsa_lex_orig
mlsa = dict()
with open("mlsa_lex_orig.txt", 'r', encoding='utf-8') as f:
    content = f.readlines()
    content = [x.strip() for x in content]

for entry in content:
    lemma, value, pos = entry.split()
    mlsa[lemma] = "{} {}".format(value, pos)
    
# Read in germanlex
german = dict()
with open("germanlex_no_comments.txt",'r', encoding='utf-8') as f:
    contentg = f.readlines()
    contentg = [x.strip() for x in contentg]

for entry in contentg:
    try:
        lemma, value, pos = entry.split()
    except ValueError:
        print("Error in this line: " + str(entry))
        continue
    german[lemma] = "{} {}".format(value, pos)
    
# Compare and overwrite entries if applicable
count = 0
for key in mlsa:
    if key in german:
        #print("before: " + str(key) + " " + str(mlsa[key]))
        mlsa[key] = german[key]
        count += 1
        #print("after: " + str(key) + " " + str(mlsa[key]))

print("{} of {} entries updated.".format(count, len(mlsa)))

# Write new lex to file
with open("mlsa_ger_lex.txt", 'w', encoding='utf-8') as f:
    for item in mlsa.items():
        lemma, rest = item
        f.write(lemma +" "+ rest +"\n")
print("Wrote new lexicon to mlsa_ger_lex.txt")