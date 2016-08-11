import numpy as np
import re
import itertools
from collections import Counter
import pandas as pd 
import csv

def clean_str(string):
    """
    Tokenization/string cleaning for all datasets
    """
    string = re.sub(r"\'s", " \'s", string)
    string = re.sub(r"\'ve", " \'ve", string)
    string = re.sub(r"n\'t", " n\'t", string)
    string = re.sub(r"\'re", " \'re", string)
    string = re.sub(r"\'d", " \'d", string)
    string = re.sub(r"\'ll", " \'ll", string)
    string = re.sub(r",", " , ", string)
    string = re.sub(r"!", " ! ", string)
    string = re.sub(r"\(", " \( ", string)
    string = re.sub(r"\)", " \) ", string)
    string = re.sub(r"\?", " \? ", string)
    string = re.sub(r"\s{2,}", " ", string)
    return string.strip().lower()


def load_data_and_labels():
    """
    Loads MR data from classification_train, splits the data into words and generates labels.
    Returns split sentences and labels.
    """
    # Load data from files
    training = []
    line_counter = 0
    with open("classification_train.tsv") as tsv:
        for line in csv.reader(tsv, dialect="excel-tab"):
            if line_counter<20001:
                train =  ' '.join([line[0],line[2]])
                training.append(train)
            else:
                break
            line_counter = line_counter+1
    # training_data = list(training)
    training_data = [s.strip() for s in training]
    # Split by words
    x_text = training_data# + negative_examples
    x_text = [clean_str(sent) for sent in x_text]
    # Generate labels
    label =[]
    line_counter = 0
    with open("classification_train.tsv") as tsv:
        for line in csv.reader(tsv, dialect="excel-tab"):
            if line_counter<20001:
                label.append(line[1])
            else:
                break
            line_counter =  line_counter +1
    labels = list(label)

    tempList = []
    labelList = []
    for a in label:
        if not a in tempList:
            tempList.append(a)
            labelList.append(tempList.index(a))
        else:
            labelList.append(tempList.index(a))
    n_labels = len(labelList)
    target = []
    for x in range(0,n_labels):
        print ("x---",x)
        listFori = []
        i=0
        print ("labelList[x]",labelList[x])
        while i < len(tempList):
            if i == labelList[x]:
                print ("i: ",i,"----","labelList[x]: ",labelList[x])
                listFori.append(1)
            else:
                listFori.append(0)
            i = i+1
        print ("interation complete--",x)
        target.append(listFori)
    y = np.concatenate([target],0)

    return [x_text, y]

def batch_iter(data, batch_size, num_epochs, shuffle=True):
    """
    Generates a batch iterator for a dataset.
    """
    data = np.array(data)
    data_size = len(data)
    num_batches_per_epoch = int(len(data)/batch_size) + 1
    for epoch in range(num_epochs):
        # Shuffle the data at each epoch
        if shuffle:
            shuffle_indices = np.random.permutation(np.arange(data_size))
            shuffled_data = data[shuffle_indices]
        else:
            shuffled_data = data
        for batch_num in range(num_batches_per_epoch):
            start_index = batch_num * batch_size
            end_index = min((batch_num + 1) * batch_size, data_size)
            yield shuffled_data[start_index:end_index]

def test_data_and_label():
    testing = []
    count = 0
    with open("classification_train.tsv") as tsv:
        for line in csv.reader(tsv, dialect="excel-tab"):
            if (count>20000 and count<30001):
                    train =  ' '.join([line[0],line[2]])
                    testing.append(train)
            elif (count<20000):
                count = count +1
            else:
                break
            
    # testing_data = list(testing)
    testing_data  = [s.strip() for s in testing ]
    # Split by words
    x_text = testing_data
    x_text = [clean_str(sent) for sent in x_text]
    # Generate labels
    label =[]
    count = 0
    with open("classification_train.tsv") as tsv:
        for line in csv.reader(tsv, dialect="excel-tab"):
            if (count>20000 and count<30001):
                    label.append(line[1])
            elif (count<20000):
                count = count +1
            else:
                break
            
    labels = list(label)

    tempList = []
    labelList = []
    for a in label:
        if not a in tempList:
            tempList.append(a)
            labelList.append(tempList.index(a))
        else:
            labelList.append(tempList.index(a))
    n_labels = len(labelList)
    target = []
    for x in range(0,n_labels):
        print ("x---",x)
        listFori = []
        i=0
        print ("labelList[x]",labelList[x])
        while i < len(tempList):
            if i == labelList[x]:
                print ("i: ",i,"----","labelList[x]: ",labelList[x])
                listFori.append(1)
            else:
                listFori.append(0)
            i = i+1
        print ("interation complete--",x)
        target.append(listFori)
    y = np.concatenate([target],0)

    return [x_text, y]