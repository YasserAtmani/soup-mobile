package com.example.spotifyceri;

import com.example.spotifyceri.Demo.ASRPrx;
import com.example.spotifyceri.Demo.NLPPrx;
import com.example.spotifyceri.Demo.ServeursPrx;
import com.example.spotifyceri.Demo.VLCPrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.InitializationData;
import com.zeroc.Ice.Util;

public class IceServers {

    protected static Communicator communicator;
    public static ServeursPrx server1;
    public static ASRPrx asrServer;
    public static NLPPrx nlpServer;
    public static VLCPrx serverVLC;

    public IceServers() {
        InitializationData initData = new InitializationData();
        initData.properties = Util.createProperties();
        initData.properties.setProperty("Ice.Default.Locator", "SoupIceGrid/Locator:default -h 134.209.226.109 -p 10000");
        communicator = Util.initialize(initData);
        server1 = ServeursPrx.checkedCast(communicator.stringToProxy("serveur1"));
        asrServer = ASRPrx.checkedCast(communicator.stringToProxy("asrServer"));
        nlpServer = NLPPrx.checkedCast(communicator.stringToProxy("nlpServer"));
        serverVLC = VLCPrx.checkedCast(communicator.stringToProxy("serveurVLC"));
    }

    public void play(){
        //serverVLC.playStream();
        System.out.println("play");
    }

    public void pause(){
        //serverVLC.pauseStream();
        System.out.println("pause");
    }

    public void stop(){
        //serverVLC.stopStream();
        System.out.println("stop");
    }
}
