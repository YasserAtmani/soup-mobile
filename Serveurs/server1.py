import signal
import os
import sys
import Ice
from soup_db import getAll

Ice.loadSlice('Servers.ice')
import Demo

class ServeursI(Demo.Serveurs):
    dbname = 'soup_database1.db'
    def __init__(self, name):
        self.name = name

    def getAllSongs(self, current=None):
        fetched = getAll()
        songs = []
        for row in fetched:
            songs.append(Demo.Song(*row))
        return songs
    
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
    adapter = communicator.createObjectAdapter("server1Adapter")
    id = Ice.stringToIdentity(properties.getProperty("Identity"))
    adapter.add(ServeursI(properties.getProperty("Ice.ProgramName")), id)
    adapter.activate()
    communicator.waitForShutdown()