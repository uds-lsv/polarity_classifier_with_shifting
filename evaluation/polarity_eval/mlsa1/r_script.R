library(plyr)
# Read in annotaded mlsa data
df = read.table('sentences.tsv', header=TRUE, sep="\t")
# Remove unnecessary columns, only keep the polarity majority column.
keep = c("pol_majority")
d1 = df[keep]

# Read in the output of the bachelor system
dsys = read.table('1.0-scores.txt', header=FALSE) # system standard
dsys2 = read.table('2.0-scores.txt', header=FALSE) # system no shifter
dsys3 = read.table('3.0-scores.txt', header=FALSE) # system negation only
dsys4 = read.table('1.0-scores.txt', header=FALSE) # system standard, count 0.3 as 0

# Change negative numbers to "-", positive to "+", keep 0s.
# First change factors to be numbers
#dsys[, 1] = as.numberic(dsys[, 1])
dsys[dsys > -0.4 & dsys < 0.4] = "0"
dsys[dsys < 0]= '-' 
dsys[dsys > 0]= '+' 

dsys2[dsys2 > -0.4 & dsys2 < 0.4] = "0"
dsys2[dsys2 < 0]= "-" 
dsys2[dsys2 > 0]= "+" 

dsys3[dsys3 > -0.4 & dsys3 < 0.4] = "0"
dsys3[dsys3 < 0]= "-" 
dsys3[dsys3 > 0]= "+" 

dsys4[dsys4 > -0.4 & dsys4 < 0.4] = "0"
dsys4[dsys4 < 0] = "-"
dsys4[dsys4 > 0] = "+"

#write.table(dsys4$V1, "1.1-dsys.txt", row.names=FALSE)

#summary(dsys)
#count(dsys == "-")
#summary(dsys2)
#count(dsys2 == "0")
#count(dsys4 == "0")
#summary(d1)

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
