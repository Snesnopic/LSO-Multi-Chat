package com.snesnopic.ermes;

import java.io.IOException;
import java.net.Socket;

public class Connessione extends Thread {
    static String statichostname;
    static int staticport;
    Socket socket;
    private static Connessione instance;
    public static Connessione getInstance(String hostname, int port){
        if(instance == null) {
            statichostname = hostname;
            staticport = port;
            return new Connessione();

        }
        else
            return instance;
    }

    private Connessione() {

    }
    public void run() {
        try {
            socket = new Socket(statichostname, staticport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
