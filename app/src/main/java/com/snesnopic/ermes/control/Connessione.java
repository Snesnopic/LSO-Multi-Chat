package com.snesnopic.ermes.control;

import com.snesnopic.ermes.datapkg.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Connessione extends Thread {
    static PrintWriter pw = null;
    static String statichostname;
    static int staticport;
    private static boolean isConnected = false;
    private BufferedReader bf = null;
    private Socket s;
    private static Connessione instance;
    private String result = "null";
    static User thisUser = new User("Utente 1");
    static Group thisRoom;
    public static ArrayList<Group> myGroups;
    public static ArrayList<Group> otherGroups;
    public static ArrayList<Group> requestGroups;
    static Message messaggio;

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
                thisUser = new User();
                thisUser.userid = Integer.parseInt(response);
                thisUser.username = username;
                thisUser.password = password;
                return true;
            }
            else return false;
        } catch (NullPointerException e) {
            System.out.println("[ERRORE in boolean login || Connessione.java 56] \n Stringa ricevuta uguale a null");
            return false;
        } catch(NumberFormatException e) {
            return false;
        }

    }

    //Metodo che controlla se la connessione è stata stabilita.
    public boolean isConnected() {return isConnected;}

    private void send(String str) {
        Thread t = new Thread(() -> {
            if (isConnected) {
                pw.println(str.toCharArray());
                pw.flush();
                String ok = recv();
                System.out.println("++++++++MESSAGGIO INVIATO++++++++++\n"+str);
                while(!ok.equals("-80"));
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
                System.out.println("++++++++MESSAGGIO INVIATO++++++++++\n"+n);
            }
            return;
        });
        t.start();
        while(t.isAlive());
        return;
    }

    private String recv() {
        int c = 0;
        Thread t = new Thread(() -> {
            if (isConnected) {
                try {
                    result = bf.readLine();
                    System.out.println("++++++++MESSAGGIO LETTO++++++++++\n"+result);
                    return;
                }
                catch (IOException e) {e.printStackTrace(); return;}
            }
        });
        t.start();
        try {
            while(t.isAlive());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("--------------------------------------\nErrore nell'attesa del Thread di lettura");
            return result;
        }
    }

    public ArrayList<Group> getRoomJoined(){
        ArrayList<Group> myRooms = new ArrayList<>();
        try {
            send(2);
            String response = recv();
            int j = clearResponse(response);

            for(int i = 0; i < j; i++ ) {
                Group a = new Group();
                a.id = clearResponse(recv());
                a.userid = clearResponse(recv());
                a.name = recv();

                myRooms.add(a);
            }

            for(int i = 0; i < myRooms.size(); i++) {
                myRooms.get(i).messages = getAllMessages(myRooms.get(i).id);
            }
        } catch (NumberFormatException e) {
            System.out.println("--------------- Errore lettura gruppi\n");
            e.printStackTrace();
        }

        return myRooms;
    }

    public ArrayList<Group> getOtherGroups() {
        ArrayList<Group> otherRooms = new ArrayList<>();
        try {
            send(3);
            int j = clearResponse(recv());

            for(int i = 0; i < j; i++ ) {
                Group a = new Group();
                a.id = clearResponse(recv());
                a.userid = clearResponse(recv());
                a.name = recv();

                otherRooms.add(a);
            }
            for(int i = 0; i < otherRooms.size(); i++) {
                otherRooms.get(i).messages = getAllMessages(otherRooms.get(i).id);
                System.out.println("Sono i: "+i);
            }

            return otherRooms;
        } catch (NumberFormatException e) {
            System.out.println("--------------- Errore lettura gruppi esterni\n");
            e.printStackTrace();
        }

        return otherRooms;
    }

    private int clearResponse(String response) {
        int cleared = -1;
        if(response.equals("")) {
            System.out.println("-------------Stringa da convertire vuota! Ritorno -1");
            return cleared;
        }

        try {
            cleared = Integer.parseInt(response);
            return cleared;
        } catch (NumberFormatException e) {
            int j = 0;
            char[] buff = new char[response.length()];
            for(int i = 0; i < response.length(); i++) {
                if((response.charAt(i) <= 57) && (response.charAt(i)) >= 48) {
                    buff[j] = response.charAt(i);
                    j++;
                }
            }
            char[] buff2 = new char[j];
            for(int i = 0; i < j; i++) buff2[i] = buff[i];

            String parsing = String.valueOf(buff2);
            return Integer.parseInt(parsing);
        }
    }

    public ArrayList<Message> getAllMessages(int groupID) {
        ArrayList<Message> msg = new ArrayList();
        send(4);
        send(groupID);

        try {
            int j = clearResponse(recv());
            System.out.println("Sono j, valgo: "+j);

            if(j == 0) {
                Message m = new Message();
                m.senderUsername = "Odisseo";
                m.message = "Nessun messaggio inviato su questo gruppo.";
                m.time = LocalDateTime.now();
                msg.add(m);
                return msg;
            }

            for(int i = 0; i < j; i++) {
                Message m = new Message();
                m.senderUsername = recv();
                m.message = recv();
                m.time = convertStringtoDateTime(recv());
                msg.add(m);
            }

            return msg;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msg;
    }

    private LocalDateTime convertStringtoDateTime(String time) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(time.substring(0, 16), format);

    }

    public boolean createGroup(String groupName) {
        send(5);  //da cambiare
        send(groupName);
        send(thisUser.userid);
        if(clearResponse(recv()) == 1) {
            Group e = new Group();
            ArrayList<Message> msg = new ArrayList<>();
            Message emptyMessage = new Message();
            e.name = groupName;
            e.id = clearResponse(recv());
            e.userid = thisUser.userid;
            emptyMessage.message = "Gruppo appena creato";
            emptyMessage.senderUsername = thisUser.username;
            emptyMessage.time = LocalDateTime.now();
            msg.add(emptyMessage);
            e.messages = msg;
            myGroups.add(e);
            return true;
        }
        else return false;
    }

    public boolean changeUserSettings(int userID, String newUsername, String newUserpassword) {
        send(100); //da cambiare
        send(userID);
        send(newUsername);

        if(newUserpassword.equals("") || newUserpassword.isEmpty()) send(thisUser.password);
        else send(newUserpassword);

        if(clearResponse(recv()) == 1) return true;
        else return false;
    }

    public ArrayList<Group> getRequestOfGroups() {
        requestGroups = new ArrayList<>();

        send(15); //valore fittizio
        send(thisUser.userid);

        int j = clearResponse(recv());
        if(j <= 0) {
            System.out.println("Nessuna richiesta");

            return requestGroups;
        }

        for(int i = 0; i < j; i++) {
            Group p = new Group();
            p.id = clearResponse(recv());
            p.name = recv();
            p.userid = thisUser.userid;
            p.messages = getAllMessages(p.id);

            requestGroups.add(p);
        }

        return requestGroups;
    }
}

