package com.snesnopic.ermes.control;
import com.snesnopic.ermes.datapkg.*;

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
    private Socket s;
    private static Connessione instance;
    private String result = "null";
    static User utente;
    static Group room;
    static Message messaggio;
    static Request richiesta;

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

                System.out.println("+++++++++Ho stabilito la connessione");

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
        //ritorna vero se riceve un userID diverso da 0 (login success) altrimenti falso
        try {
            if(!response.equals("0")) {
                utente = new User();
                utente.userid = Integer.parseInt(response);
                utente.username = username;
                utente.password = password;
                return true;
            }
            else return false;
        } catch (NullPointerException e) {
            System.out.println("[ERRORE in boolean login || Connessione.java 56] \n Stringa ricevuta uguale a null");
            return false;
        }

    }

    //Metodo che controlla se la connessione è stata stabilita.
    public boolean isConnected() {return isConnected;}

    private void send(String str) {
        Thread t = new Thread(() -> {
            if (isConnected) {
                pw.println(str);
                try {
                    Thread.sleep(200); //una wait per far elaborare la scrittura sulla socket (altrimenti i messaggi veranno inviati uniti)
                } catch (InterruptedException e) {e.printStackTrace(); return;}
                System.out.println("++++++++MESSAGGIO INVIATO++++++++++\n"+str);
            }
            return;
        });
        t.start();
        while(t.isAlive());
        return;
    }

    private void send(int n) {
        Thread t = new Thread(() -> {
            if (isConnected) {

                pw.println(n);
                try {
                    Thread.sleep(200); //una wait per far elaborare la scrittura sulla socket (altrimenti i messaggi veranno inviati uniti)
                } catch (InterruptedException e) {e.printStackTrace();}
                System.out.println("++++++++MESSAGGIO INVIATO++++++++++\n"+n);
            }
            return;
        });
        t.start();
        while(t.isAlive());
        return;
    }

    private String recv() {
        Thread t = new Thread(() -> {
            if (isConnected) {
                try {
                    Thread.sleep(100); //una wait per far elaborare la lettura della socket
                    result = bf.readLine();
                    System.out.println("++++++++MESSAGGIO LETTO++++++++++\n"+result);
                    Thread.sleep(100); //una wait per far elaborare la lettura della socket
                }
                catch (InterruptedException | IOException e) {System.out.println("Errore nella recv");}
            }
            return;
        });
        t.start();
        try {
            while(t.isAlive());
            return result;
        } catch (Exception e) {
            System.out.println("--------------------------------------\nErrore nell'attesa del Thread di lettura");
            return result;
        }
    }

    public void getRoomJoined(){}

}

