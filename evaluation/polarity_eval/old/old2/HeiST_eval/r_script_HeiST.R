library(plyr)
# Read in annotaded heist data
d1 = read.table('gold_scores_total.txt', header=FALSE) # gold
# Read in the output of the bachelor system
dsys = read.table('A-heist_total_standard_scores.txt', header=FALSE) # system standard
dsys2 = read.table('B-heist_total_no_shifter_scores.txt', header=FALSE) # system no shifter
dsys3 = read.table('C-heist_total_negation_only_scores.txt', header=FALSE) # system negation only

# Change negative numbers to "-", positive to "+", keep 0s.
#dsys[dsys > -0.7 & dsys < 0.7] = "0"
dsys[dsys < 0]= '-' 
dsys[dsys > 0]= '+' 

#dsys2[dsys2 > -0.7 & dsys2 < 0.7] = "0"
dsys2[dsys2 < 0]= "-" 
dsys2[dsys2 > 0]= "+" 

#dsys3[dsys3 > -0.7 & dsys3 < 0.7] = "0"
dsys3[dsys3 < 0]= "-" 
dsys3[dsys3 > 0]= "+" 


write.table(dsys$V1, "A.dsys.txt", row.names=FALSE)
write.table(dsys2$V1, "B.dsys.txt", row.names=FALSE)
write.table(dsys3$V1, "C.dsys.txt", row.names=FALSE)

count(d1 == "-")
count(dsys == "-")
count(dsys2 == "-")
count(dsys3 == "-")

result = dsys == d1
result2 = dsys2 == d1
result3 = dsys3 == d1

summary(result)
summary(result2)
summary(result3)


# P, R, F
# + case
tp = dsys == "+" && dsys == d1
