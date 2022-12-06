package com.snesnopic.ermes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Connessione extends Thread {
    static PrintWriter send;
    static String statichostname;
    static int staticport;
    private static boolean isConnected = false;
    private BufferedReader recv = null;
    Socket s;
    private static Connessione instance;

    public static Connessione getInstance(String hostname, int port){
        if(instance == null) {
            statichostname = hostname;
            staticport = port;
            instance = new Connessione();
        }
        return instance;
    }

    private Connessione() {}


    public void run() {
        while(!isConnected) {
            try {
                s = new Socket(statichostname, staticport);
                isConnected = true;
                send = new PrintWriter(s.getOutputStream(), true);
                recv = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }

        /*
         ESEMPIO DI RECEIVE FROM SOCKET
        try {
            input = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            System.out.println("Non ci sono riuscito");
            e.printStackTrace();
        }
        try {
            System.out.println("++++++Sono buffered: "+input.readLine());
        } catch (IOException e) {
            System.out.println("Non ci sono riuscito");
            e.printStackTrace();
        }*/
    }
    boolean login(String email, String password, boolean register) {

        try {
            if(register)
                send.println(1);
            else
                send.println(0);
            send.println(email);
            send.println(password);

            int response = s.getInputStream().read();
            return response == 1;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Metodo che controlla se la connessione è stata stabilita.
    public boolean isConnected() {
        return isConnected;
    }

}
