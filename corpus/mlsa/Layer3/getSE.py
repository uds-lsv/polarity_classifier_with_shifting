# -*- coding: utf-8 -*-
"""
Created on Thu Jan  5 20:03:02 2017

@author: Maximilian
"""
from xml.dom import minidom

xmldoc = minidom.parse('layer3.annotation.merged.xml')
DOMTree = minidom.parse('layer3.annotation.merged.xml')
collection = DOMTree.documentElement

# Collect all Terminal Ids and their respective lemmas
terminals = collection.getElementsByTagName("t")
ter_lemma_dict = dict()
for t in terminals:
    t_id = t.getAttribute("id")
    t_lemma = t.getAttribute("lemma")
    t_pos = t.getAttribute("pos")
    ter_lemma_dict[t_id] = [str(t_lemma), str(t_pos)]
                  
# Get all frames and look for ESE and DSE
frames = collection.getElementsByTagName("frame")
se_dict = dict()

for frame in frames:
    frame_name = frame.getAttribute("name")
    if frame_name == "DSE" or frame_name == "ESE":
        if frame.getElementsByTagName('target'):
            target = frame.getElementsByTagName('target')[0]
            fenode = target.getElementsByTagName('fenode')[0]
            fe_id = fenode.getAttribute("idref")
            
            flags = frame.getElementsByTagName('flag')
            if flags:
                flag_name = ""
                for flag in flags:
                    flag_name = flag.getAttribute('name')
                    if flag_name == 'Positive':
                        se_dict[fe_id] = 'POS=0.5'
                    elif flag_name == 'Negative':
                        se_dict[fe_id] = 'NEG=0.5'
                        
                    else:
                        se_dict[fe_id] = 'NEU=0.0'

# Replace ids with lemmas
for key in ter_lemma_dict:
    if key in se_dict:
        lemma = ter_lemma_dict[key][0]
        pos = ter_lemma_dict[key][1]
        se_dict[lemma] =   "{} {}".format(se_dict[key], pos)
        del se_dict[key]


# Write to file
with open("mlsa_lex.txt", 'w') as f:
    for (l, v) in se_dict.items():
        f.write("{} {}\n".format(l, v))
