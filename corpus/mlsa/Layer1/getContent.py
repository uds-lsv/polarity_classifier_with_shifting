# -*- coding: utf-8 -*-
"""
Created on Thu Jan  5 20:03:02 2017

@author: Maximilian
"""

from xml.dom import minidom
xmldoc = minidom.parse('corpus.xml')
itemlist = xmldoc.getElementsByTagName('content')
print(len(itemlist))

print(itemlist[0].childNodes[0].nodeValue)

raw = open('raw.txt', 'w', encoding='utf8')
for s in itemlist:
    text = s.childNodes[0].nodeValue.strip()
    print(text)
    raw.write(text + '\n')
    
raw.close()

