library(plyr)
# Read in annotaded mlsa data
df = read.table('sentences.tsv', header=TRUE, sep="\t")
# Remove unnecessary columns, only keep the polarity majority column.
keep = c("pol_majority")
d1 = df[keep]

d1 = read.table('gold_only_polar.txt', header=FALSE) # gold
# Read in the output of the bachelor system
dsys = read.table('A-mlsa_standard_scores_only_polar.txt', header=FALSE) # system standard
dsys2 = read.table('B-mlsa_no_shifter_scores_only_polar.txt', header=FALSE) # system no shifter
dsys3 = read.table('C-mlsa_negation_only_scores_only_polar.txt', header=FALSE) # system negation only
dsys4 = read.table('D-mlsa_all_1_scores_only_polar.txt', header=FALSE)

# Change negative numbers to "-", positive to "+", keep 0s.
# First change factors to be numbers
#dsys[, 1] = as.numberic(dsys[, 1])
#dsys[dsys > -0.7 & dsys < 0.7] = "0"
dsys[dsys < 0]= '-' 
dsys[dsys > 0]= '+' 

#dsys2[dsys2 > -0.7 & dsys2 < 0.7] = "0"
dsys2[dsys2 < 0]= "-" 
dsys2[dsys2 > 0]= "+" 

#dsys3[dsys3 > -0.7 & dsys3 < 0.7] = "0"
dsys3[dsys3 < 0]= "-" 
dsys3[dsys3 > 0]= "+" 

dsys4[dsys4 < 0]= "-" 
dsys4[dsys4 > 0]= "+" 

write.table(dsys$V1, "A.dsys_only_polar.txt", row.names=FALSE)
write.table(dsys2$V1, "B.dsys_only_polar.txt", row.names=FALSE)
write.table(dsys3$V1, "C.dsys_only_polar.txt", row.names=FALSE)
write.table(dsys4$V1, "D.dsys_only_polar.txt", row.names=FALSE)

count(dsys == "-")
count(dsys2 == "-")
count(dsys3 == "-")
count(dsys4 == "-")

result = dsys == d1
result2 = dsys2 == d1
result3 = dsys3 == d1
result4 = dsys4 == d1

summary(result)
summary(result2)
summary(result3)
summary(result4)


# P, R, F
# + case
tp = dsys == "+" && dsys == d1
