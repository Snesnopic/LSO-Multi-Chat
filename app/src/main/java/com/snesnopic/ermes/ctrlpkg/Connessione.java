package com.snesnopic.ermes.ctrlpkg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connessione extends Thread {
    static PrintWriter pw = null;
    static String statichostname;
    static int staticport;
    private static boolean isConnected = false;
    private BufferedReader bf = null;
    Socket s;
    private static Connessione instance;

    public static Connessione getInstance(String hostname, int port) {
        if (instance == null) {
            statichostname = hostname;
            staticport = port;
            instance = new Connessione();
        }
        return instance;
    }

    private Connessione() { }

    public void run() {
        while (!isConnected) {
            try {
                s = new Socket(statichostname, staticport);
                isConnected = true;
                pw = new PrintWriter(s.getOutputStream(), true);
                bf = new BufferedReader(new InputStreamReader(s.getInputStream()));

            } catch (IOException e) {
                isConnected = false;
                System.out.println("Connessione non trovata, riprovo..");
            }
        }
    }

    public boolean login(String username, String password, boolean register) {
        if (register) send(1);
        else send(0);

        send(username);
        send(password);

        String response = recv();

        //ritorna vero se riceve 1 (login success) altrimenti 0

        try {
            if (response.equals("0")) return false;

            if (response.equals("1")) return !register;

            if (response.equals("2")) return register;

            return false;
        } catch (NullPointerException e) {
            System.out.println("[ERRORE in boolean login || Connessione.java 56] \n Stringa ricevuta uguale a null");
            return false;
        }

    }


    //Metodo che controlla se la connessione è stata stabilita.
    public boolean isConnected() {return isConnected;}

    private void send(String str) {
        if (isConnected) {
            try {
                pw.println(str);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(int n) {
        if (isConnected) {
            try {
                pw.println(n);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String recv() {
        String result = "non ho ricevuto niente";
        if (isConnected) {
            try {
                result = bf.readLine();
                Thread.sleep(500);
                return result;
            }
            catch (InterruptedException | IOException e) {return result;}
        }

        return result;
    }
}

