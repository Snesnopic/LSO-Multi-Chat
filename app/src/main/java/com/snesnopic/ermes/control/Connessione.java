package com.snesnopic.ermes.control;

import com.snesnopic.ermes.activitypkg.ChatActivity;
import com.snesnopic.ermes.datapkg.Group;
import com.snesnopic.ermes.datapkg.Message;
import com.snesnopic.ermes.datapkg.Request;
import com.snesnopic.ermes.datapkg.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Connessione extends Thread {
    static PrintWriter pw = null;
    static String statichostname;
    static int staticport;
    private static boolean isConnected = false;
    private BufferedReader bf = null;
    private static Connessione instance;
    private String result = "";
    public static User thisUser = new User("Utente 1","Password");
    static Group thisRoom;
    public static ArrayList<Group> myGroups;
    public static ArrayList<Group> otherGroups = new ArrayList<>();
    public static ArrayList<Group> requestGroups;
    public static ArrayList<Request> requests;
    static Message messaggio;
    private Thread t;
    private boolean isRunning = false;
    private boolean canSend = true;

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
                Socket s = new Socket(statichostname, staticport);
                s.setSoTimeout(250);
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

        int id = clearResponse(recv());
        //ritorna vero se riceve un userID diverso da 0 (login success) altrimenti falso
        try {
            if(id != 0) {
                thisUser = new User();
                thisUser.userid = id;
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
        });
        t.start();
        while(t.isAlive());
    }

    private void send(int n) {
        Thread t = new Thread(() -> {
            if (isConnected) {
                pw.println(n);
                while(!recv().equals("-80"));
                System.out.println("++++++++MESSAGGIO INVIATO++++++++++\n"+n);
            }
        });
        t.start();
        while(t.isAlive());
    }

    private String recv() {

        Thread t = new Thread(() -> {
            if (isConnected) {
                try {
                    result = bf.readLine();
                    System.out.println("++++++++MESSAGGIO LETTO++++++++++\n"+result);
                }
                catch (Exception ignored) {
                }
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
        myGroups = new ArrayList<>();
        try {
            send(2);
            String response = recv();
            int j = clearResponse(response);

            for(int i = 0; i < j; i++ ) {
                Group a = new Group();
                a.id = clearResponse(recv());
                a.userid = clearResponse(recv());
                a.name = recv();
                a.accessPermitted = true;
                myGroups.add(a);
            }

            for(int i = 0; i < myGroups.size(); i++) {
                myGroups.get(i).messages = getAllMessages(myGroups.get(i).id);
            }
        } catch (NumberFormatException e) {
            System.out.println("--------------- Errore lettura gruppi\n");
            e.printStackTrace();
        }
        return myGroups;
    }

    public ArrayList<Group> getOtherGroups() {
        otherGroups = new ArrayList<>();
        try {
            send(3);
            int j = clearResponse(recv());

            for(int i = 0; i < j; i++ ) {
                Group a = new Group();
                a.id = clearResponse(recv());
                a.userid = clearResponse(recv());
                a.name = recv();
                a.accessPermitted = false;
                otherGroups.add(a);
            }
            for(int i = 0; i < otherGroups.size(); i++) {
                otherGroups.get(i).messages = getAllMessages(otherGroups.get(i).id);
                System.out.println("Sono i: "+i);
            }

            return otherGroups;
        } catch (NumberFormatException e) {
            System.out.println("--------------- Errore lettura gruppi esterni\n");
            e.printStackTrace();
        }

        return otherGroups;
    }

    private int clearResponse(String response) {
        int cleared = -1;
        if(Objects.isNull(response)) {
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
            System.out.println("+++++DEBUG: Sono in cleared response, sono parsing: "+parsing+"Sono Integer parsing: "+Integer.parseInt(parsing));
            return Integer.parseInt(parsing);
        }
    }

    public ArrayList<Message> getAllMessages(int groupID) {
        ArrayList<Message> msg = new ArrayList<>();
        send(4);
        send(groupID);

        try {
            int j = clearResponse(recv());

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
        send(7);
        send(groupName);
        System.out.println(thisUser.userid);
        send(thisUser.userid);
        if(!recv().equals("-1")) {
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
            e.accessPermitted = true;
            myGroups.add(e);
            return true;
        }
        else return false;
    }

    public boolean changeUsername(String newUsername) {
        send(8);
        send(newUsername);
        send(thisUser.userid);

        if(clearResponse(recv()) == 1)
        {
            thisUser.username = newUsername;
            return true;
        }

        else return false;
    }

    public boolean changeUserPassword(String newPassword) {
        send(9);
        send(newPassword);
        send(thisUser.userid);

        if(clearResponse(recv()) == 1) {
            thisUser.password = newPassword;
            return true;
        }
        else return false;
    }

    public ArrayList<Group> getRequestOfGroups() {
        /* Questo codice funziona, ma dal lato Server succedono troppe cose strane, bisogna ricavarsi i gruppi in un altro modo, altrimenti si perde la sanita' mentale (vedi case 11)
        requestGroups = new ArrayList<>();

        send(11);
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

            requestGroups.add(p);
        }
        for(int i = 0; i < requestGroups.size(); i++) {
            System.out.println("--------------------Sono requestGroup size: "+requestGroups.size());
            requestGroups.get(i).messages = getAllMessages(requestGroups.get(i).id);
        }

        return requestGroups;

         */

        requestGroups = new ArrayList<>();
        for(int i = 0; i < myGroups.size(); i++) {

            System.out.println(+myGroups.size()+"<- Grandezza sono il gruppo: "+myGroups.get(i).name+" con id: "+myGroups.get(i).userid+" mentre sono userid: "+thisUser.userid);

            if (thisUser.userid == myGroups.get(i).userid) {
                requestGroups.add(myGroups.get(i));
            }
        }

        return requestGroups;
    }

    public ArrayList<Request> getRequests(Group g) {
        requests = new ArrayList<>();

        send(5);
        send(g.id);
        send(thisUser.userid);
        int j = clearResponse(recv());

        if (j <= 0) {
            Request r = new Request();
            r.user = new User();
            r.group = g;
            r.user.username = "";
        } else {
            for(int i = 0; i < j; i++) {
                Request r = new Request();
                r.user = new User();
                r.group = g;

                r.user.username = recv();
                r.user.userid = clearResponse(recv());
                requests.add(r);
            }
        }
        return requests;

    }

    public void sendMessage(String text, Group actualRoom) {
        if(text.isEmpty() || Objects.isNull(text)) return;

        String time = LocalDateTime.now().toString();


        if(chatThreadHandler(true)) {
            send(6);
            System.out.println(text);
            send(text);
            send(thisUser.username);
            send(time.substring(0, 16));
            send(thisUser.userid);
            send(actualRoom.id);
            chatThreadHandler(false);
        }
        else {
            System.out.println("Impossibile inviare il messaggio. Riprovare.");
        }
    }

    public boolean makeJoinRequest(Group g) {
        send(12);
        send(thisUser.userid);
        send(g.id);
        int response = clearResponse(recv());
        return response == 1;
    }

    public boolean requestHandler(Group g, int userID, boolean accepted) {
        if(accepted) {
            send(13);
            send(userID);
            send(g.id);

            int response = clearResponse(recv());
            if(response == 1) return true;
            else return false;
        }
        else {
            send(14);
            send(userID);
            send(g.id);

            int response = clearResponse(recv());
            if(response == 1) return true;
            else return false;
        }
    }

    public void chatThread(int index) throws IOException {
        send(999);
        t = new Thread(() -> {
            while(isConnected) {
                while(isRunning) {
                    canSend = false;
                    String buff = recv();
                    if(buff.equals("-777")) {
                        Message msg = new Message();
                        msg.senderUsername = recv();
                        msg.time = LocalDateTime.now();
                        msg.message = recv();
                        int groupID = clearResponse(recv());
                        for(int i = 0; i < myGroups.size(); i++){
                            if(myGroups.get(i).id == groupID)  {
                                myGroups.get(i).messages.add(msg);
                                if(i == index)  {
                                    try {
                                        ChatActivity.adapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        System.out.println("Ignoro le cagate delle exception. Godo");
                                    }
                                }
                            }
                        }
                    }
                }
                canSend = true;
            }
            System.out.println("Connessione assente!");
        });
        isRunning = true;
        t.start();
    }

    public boolean chatThreadHandler(boolean kill) {
        if(kill) {
            if(isRunning) {
                try {
                    isRunning = false;
                    while(!canSend);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("--------ATTENZIONE: Thread chat non chiuso");
                    return false;
                }
            }
            else return false;
        } else {
            if(isRunning)  {
                System.out.println("------------ATTENZIONE: Thread chat gia' attivo!");
                return false;
            }
            else {
                isRunning = true;
                System.out.println("Thread acceso");
                return true;
            }
        }
    }

    public boolean stopChat() {
        send(888);
        t.interrupt();
        return t.isAlive();
    }

    public void removeGroup(Group g) {
        if (g.userid != thisUser.userid) {
            send(14);
            send(thisUser.userid);
        } else
            send(13);
        send(g.id);
        if (recv().equals("1")) {
            for (int i = 0; i < myGroups.size(); i++) {
                if (myGroups.get(i).id == g.id) {
                    otherGroups.add(g);
                    myGroups.remove(i);
                    break;
                }
            }
        } else
            System.out.println("Gruppo non eliminato.");
    }

}
