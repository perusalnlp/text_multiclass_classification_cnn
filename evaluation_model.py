#! /usr/bin/env python

import tensorflow as tf
import numpy as np
import os
import time
import datetime
from model_preparation import TextCNN
from tensorflow.contrib import learn
import csv

# Parameters
# ==================================================

# Eval Parameters
tf.flags.DEFINE_integer("batch_size", 64, "Batch Size (default: 64)")
tf.flags.DEFINE_string("checkpoint_dir", "", "Checkpoint directory from training run")
tf.flags.DEFINE_boolean("eval_train", False, "Evaluate on all training data")

# Misc Parameters
tf.flags.DEFINE_boolean("allow_soft_placement", True, "Allow device soft device placement")
tf.flags.DEFINE_boolean("log_device_placement", False, "Log placement of ops on devices")


FLAGS = tf.flags.FLAGS
FLAGS._parse_flags()
print("\nParameters:")
for attr, value in sorted(FLAGS.__flags.items()):
    print("{}={}".format(attr.upper(), value))
print("")

# CHANGE THIS: Load data. Load your own data here
if FLAGS.eval_train:
    x_raw, y_test = data_and_labels()
    y_test = np.argmax(y_test, axis=1)
else:
    x_raw = ["a masterpiece four years in the making", "everything is off."]
    y_test = [1, 0]

# Map data into vocabulary
vocab_path = os.path.join(FLAGS.checkpoint_dir, "..", "vocab")
vocab_processor = learn.preprocessing.VocabularyProcessor.restore(vocab_path)
x_test = np.array(list(vocab_processor.transform(x_raw)))

print("\nEvaluating...\n")

# Evaluation
# ==================================================
checkpoint_file = tf.train.latest_checkpoint(FLAGS.checkpoint_dir)
graph = tf.Graph()
with graph.as_default():
    session_conf = tf.ConfigProto(
      allow_soft_placement=FLAGS.allow_soft_placement,
      log_device_placement=FLAGS.log_device_placement)
    sess = tf.Session(config=session_conf)
    with sess.as_default():
        # Load the saved meta graph and restore variables
        saver = tf.train.import_meta_graph("{}.meta".format(checkpoint_file))
        saver.restore(sess, checkpoint_file)

        # Get the placeholders from the graph by name
        input_x = graph.get_operation_by_name("input_x").outputs[0]
        # input_y = graph.get_operation_by_name("input_y").outputs[0]
        dropout_keep_prob = graph.get_operation_by_name("dropout_keep_prob").outputs[0]

        # Tensors we want to evaluate
        predictions = graph.get_operation_by_name("output/predictions").outputs[0]

        # Generate batches for one epoch
        batches = data_helpers.batch_iter(list(x_test), FLAGS.batch_size, 1, shuffle=False)

        # Collect the predictions here
        all_predictions = []

        for x_test_batch in batches:
            batch_predictions = sess.run(predictions, {input_x: x_test_batch, dropout_keep_prob: 1.0})
            all_predictions = np.concatenate([all_predictions, batch_predictions])

if y_test is None:
    file = open("TestDataPredictionfile.txt", "w")
    for pred in all_predictions:
        file.write(pred)
    file.close()
# Print accuracy if y_test is defined
if y_test is not None:
    correct_predictions = float(sum(all_predictions == y_test))
    print("Total number of test examples: {}".format(len(y_test)))
    print("Accuracy: {:g}".format(correct_predictions/float(len(y_test))))

def data_and_label():
    testing = []
    count = 0
    with open("classification_train.tsv") as tsv:
        for line in csv.reader(tsv, dialect="excel-tab"):
            if (count>20000 and count<30001):
                    train =  ' '.join([line[0],line[2]])
                    training.append(train)
            elif (count<20000):
                count = count +1
            else:
                break
            
    training_data = list(training)
    training_data = [s.strip() for s in training_data]
    # Split by words
    x_text = training_data# + negative_examples
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