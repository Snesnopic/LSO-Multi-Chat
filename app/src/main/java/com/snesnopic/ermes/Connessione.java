package com.snesnopic.ermes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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

    private Connessione() {}
    public void run() {
        while (!isConnected) {
            try {
                s = new Socket(statichostname, staticport);
                isConnected = true;
                pw = new PrintWriter(s.getOutputStream(), true);
                bf = new BufferedReader(new InputStreamReader(s.getInputStream()));

            } catch (IOException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }

        try {
            boolean eliminami = login("Claudio", "sei un piscione", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean login(String email, String password, boolean register) throws IOException {
        if (register) send(1);
        else send(0);

        send(email);
        send(password);
        String response = recv();
        System.out.println("2++++++++++++++++++++++");
        System.out.println(response);

        //ritorna vero se riceve 1 (login success) altrimenti 0

        if (response.equals("0")) return false;
        if (response.equals("1")) {
            if (register) {
                return false;
            } else {
                return true;
            }
        }
        if (response.equals("2")) {
            if (register) return true;
            else return false;
        }
        return false;
    }


    //Metodo che controlla se la connessione è stata stabilita.
    public boolean isConnected() {
        return isConnected;
    }

    private void send(String str) {
        if (isConnected()) {
            try {
                pw.println(str);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(int n) {
        if (isConnected()) {
            try {
                pw.println(n);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String recv()  {
        DataInputStream data = null;
        try {
            data = new DataInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = "null";
        if (isConnected()) {
            try {
                Thread.sleep(104);
                result = data.readUTF();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        return result;
    }

}

