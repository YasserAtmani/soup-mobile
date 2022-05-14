import signal
import os
import sys
import Ice
import speech_recognition as sr
import subprocess
import time

Ice.loadSlice('Servers.ice')
import Demo


class ASRI(Demo.ASR):
    dbname = 'soup_database1.db'
    def __init__(self, name):
        self.name = name

    def recognize(self, speech, current=None):
        print("uploading speech")
        songFile = open('binarySpeech.wav', 'wb+')
        songFile.write(speech)
        songFile.close()
        songFile = open('speech.wav', 'wb+')
        songFile.write(speech)
        songFile.close()
        os.system("sudo ffmpeg -y -i binarySpeech.wav speech.wav &")
        time.sleep(1)
        r = sr.Recognizer()
        with sr.AudioFile('speech.wav') as source:
            audio = r.listen(source)
            try:
                text = r.recognize_google(audio, language="fr-FR")
                print(text)
                return text
            except:
                print("Erreur ASR")
                return "Erreur"
        return "void"
    

#ffmpeg -y -i binarySpeech.wav speech.wav

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
    adapter = communicator.createObjectAdapter("asrAdapter")
    id = Ice.stringToIdentity(properties.getProperty("Identity"))
    adapter.add(ASRI(properties.getProperty("Ice.ProgramName")), id)
    adapter.activate()
    communicator.waitForShutdown()