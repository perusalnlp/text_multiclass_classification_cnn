# with open("Target_Brand.csv") as myfile:
#         head = [next(myfile) for x in range(11000)]
# count = 0
# count1 = 0
# temp = []
# for a in head:
# 	count1 = count1 +1
# 	print (a)
# 	if not a in temp:
# 		temp.append(a)
# 		count = count +1
# print ("count is---",count)
# print ("count1 is--",count1)
import csv
cou = 0
cou1=0
training = []
with open("classification_train.tsv") as tsv:
	cou = cou+1
        for line in csv.reader(tsv, dialect="excel-tab"):
            train =  '\t'.join([line[0],line[1],line[2]])
            if not train in training:
            	training.append(train)
            	cou1 = cou1+1
print ("tatal--", cou, "ttotal after duplicate removal--",cou1)


training = []
with open("classification_train.tsv") as tsv:
	for line in csv.reader(tsv, dialect="excel-tab"):
		training.append(line[1])
	count = 0
	count1 = 0
	temp = []
	for a in training:
		count1 = count1 +1
		# print (a)
		if not a in temp:
			temp.append(a)
			count = count +1
print ("count is---",count)
print ("count1 is--",count1)
