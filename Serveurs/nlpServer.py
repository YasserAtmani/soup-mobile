import signal
import os
import sys
import Ice
import unidecode
from textblob import TextBlob
from textblob.classifiers import NaiveBayesClassifier
from nltk import word_tokenize,sent_tokenize

Ice.loadSlice('Servers.ice')
import Demo

def NLP_Commands(command):
    with open("commands.json", 'r') as fp:
        cl = NaiveBayesClassifier(fp, format="json")
        blob = TextBlob(command, classifier=cl)
        prob_dist = cl.prob_classify(command)

    return prob_dist.max()

def NLP_Songs(command):
    with open('commands_songs.json', 'r') as fp:
        cl = NaiveBayesClassifier(fp, format="json")
        blob = TextBlob(command, classifier=cl)
        prob_dist = cl.prob_classify(command)

    return prob_dist.max()

def NLP(command):
    if(NLP_Commands(command)=="lancer" or "ecouter" in command):
            print("l'app doit lancer la musique " + NLP_Songs(command))
    else:
        print("l'app doit " + NLP_Commands(command) +" le son")

class NLPI(Demo.NLP):
    def __init__(self, name):
        self.name = name

    def process(self, text, current=None):
        text = unidecode.unidecode(text)
        print("processing command : ", text)
        if(NLP_Commands(text)=="lancer" or "ecouter" in text):
            return("lancer+"+NLP_Songs(text))
        else:
            print(NLP_Commands(text))
            return(NLP_Commands(text))
    
#
#
# Ice.initialize returns an initialized Ice communicator,
# the communicator is destroyed once it goes out of scope.
#
with Ice.initialize(sys.argv) as communicator:

    #
    # Install a signal handler to shutdown the communicator on Ctrl-C
    #
    signal.signal(signal.SIGINT, lambda signum, frame: communicator.shutdown())

    #
    # The communicator initialization removes all Ice-related arguments from argv
    #
    if len(sys.argv) > 1:
        print(sys.argv[0] + ": too many arguments")
        sys.exit(1)

    properties = communicator.getProperties()
    adapter = communicator.createObjectAdapter("nlpAdapter")
    id = Ice.stringToIdentity(properties.getProperty("Identity"))
    adapter.add(NLPI(properties.getProperty("Ice.ProgramName")), id)
    adapter.activate()
    communicator.waitForShutdown()
    