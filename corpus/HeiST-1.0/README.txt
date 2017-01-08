# HeiST 1.0 #

## A German dataset for Compositional Sentiment Analysis ##

HeiST originated in the MA project of Michael Haas (Weakly Supervised Learning
for Compositional Sentiment Recognition) as a German counterpart to the
Stanford Sentiment Treebank, and has been constructed in a similar fashion.
The textual basis of HeiST are creative-commons-licensed reviews from the
German movie review site Filmrezensionen.de, from which we extracted the
evaluation summary ("Fazit") sentences.

HeiST comprises 1184 trees where each node has a sentiment label.

The crowdsourcing of HeiST has been supported in part by the
Institute of Computational Linguistics and by Yannick Versley's private funds.

## Files ##

* HeiST.ptb: Full HeiST data set
* HeiST.no_neutral_root.ptb: Negative and positive sentences only
* HeiST.no_neutral_root.splits: Splits used for the experiments in
  the NAACL-HLT 2015 paper
* HeiST_parses_binarized.ptb: HeiST parse trees. Binarized and
  unary nodes collapsed
* crowdflower_aggregate/: Aggregated judgements for phrases
* crowdflower_full/: Individual judgements for phrases. IP addresses
  and other personal information is removed.

## Labels ##

* 0: very negative
* 1: slightly negative
* 2: neutral
* 3: slightly positive
* 4: very positive
 
## Links ##

* HeiST website: http://www.cl.uni-heidelberg.de/~versley/HeiST/
* Code for experiments: https://github.com/mhaas/sentiment-shoestring-naacl15
* Michael Haas' MA thesis: http://www.michael-haas.org/pages/ma-thesis.html

## Reconstructing HeiST from annotations ##

HeiST is ready to use as-is. It is not necessary to apply the
annotation to the trees yourself.

The data annotated with CrowdFlower contains duplicates and superfluous
phrases. Thus, constructing HeiST from the annotations requires some
filtering. A script, *naacl_crowdflower_to_tree.sh*, is available in the
GitHub repository. 

## Cite ##

Michael Haas and Yannick Versley (2015) Subsentential Sentiment on a
Shoestring: A Crosslingual Analysis of Compositional Classification.
In Proceedings of NAACL-HLT 2015.

Download:
http://www.cl.uni-heidelberg.de/~versley/HeiST/naacl15.pdf

## Contact ##

* haas@cl.uni-heidelberg.de
* versley@cl.uni-heidelberg.de

## License ##

The source data is licensed under the Creative Commons BY-NC-SA 4.0
license. The HeiST release follows the same license:
https://creativecommons.org/licenses/by-nc-sa/4.0/



## History ##

* 1.0, 2015-03-20
