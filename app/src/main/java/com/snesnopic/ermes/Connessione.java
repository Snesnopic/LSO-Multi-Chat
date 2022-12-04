package com.snesnopic.ermes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connessione extends Thread {
    static PrintWriter pw;
    static String statichostname;
    static int staticport;
    private static boolean isConnected = false;
    BufferedReader input = null;
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

    private Connessione() {

    }
    public void run() {
        while(!isConnected) {
            try {
                s = new Socket(statichostname, staticport);
                isConnected = true;
                pw = new PrintWriter(s.getOutputStream(), true);
            } catch (IOException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }

        /* ESEMPIO DI SEND FROM SOCKET
        try {
            DataOutputStream output = new DataOutputStream(s.getOutputStream());
            output.writeBytes("Claudio sei un piscione");
            output.flush();
            output.writeBytes("Claudio sei un piscione x2");
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            pw.println(email);
            pw.println(password);
            pw.flush();
            //ritorna vero se riceve 1 (login success) altrimenti 0
            int response = s.getInputStream().read();
            switch(response) {
                default:
                case 0:
                    return false;
                case 1:
                    if(register) {
                        s.getOutputStream().write(1);
                        return false;
                    }
                    else
                    {
                        s.getOutputStream().write(0);
                        return true;
                    }
                case 2:
                    if(register) {
                        s.getOutputStream().write(1);
                        return true;
                    }
                    else
                    {
                        s.getOutputStream().write(0);
                        return false;
                    }

            }

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
