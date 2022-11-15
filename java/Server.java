import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public class Group
    {
        int group_id;
        String group_name;
        Group(int g, String n)
        {
            group_id = g;
            group_name = n;
        }
    }
    public ArrayList<Group> GetGroupsOfUsers(int user)
    {
        Group g = new Group(1,"ciccio");
        ArrayList<Group> a = new ArrayList<>();
        a.add(g);
        return a;
    }
    public ArrayList<Group> GetGroupsNotOfUsers(int user)
    {
        Group g = new Group(1,"ciccio");
        ArrayList<Group> a = new ArrayList<>();
        a.add(g);
        return a;
    }
    public boolean BelongsToGroup(ClientThread ct, int group)
    {
        //codice che verifica se il client ct appartiene al gruppo, per determinare se può ricevere il messaggio
        return true;
    }
    public void BroadcastMessage(String message, int groupNumber)
    {
        for(ClientThread ct : clients)
        {
            if(BelongsToGroup(ct,groupNumber))
                ct.pw.println(groupNumber + ':' + ct.userId + ':' + message);
        }
    }

    public class ClientThread extends Thread{
        PrintWriter pw;
        int userId;
        Socket s;
        public boolean IsCorrect(int id, String password)
        {
            return true;
        }
        public ClientThread(Socket s)
        {
            this.s = s;
        }
        public void run()
        {
            try {
                InputStreamReader isr = new InputStreamReader(s.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                boolean success = false;
                //loop di login
                while(!success)
                {

                    userId = s.getInputStream().read();
                    String password = br.readLine();
                    success = IsCorrect(userId,password);
                    if(success)
                        s.getOutputStream().write(1);
                    else
                        s.getOutputStream().write(0);
                }
                //prendi i gruppi di cui fa parte l'utente
                ArrayList<Group> groups = GetGroupsOfUsers(userId);
                s.getOutputStream().write(groups.size());
                for(Group g: groups)
                {
                    s.getOutputStream().write(g.group_id);
                    pw.println(g.group_name);
                }
                //prendi i gruppi di cui non fa parte l'utente
                ArrayList<Group> outsideGroups = GetGroupsNotOfUsers(userId);
                s.getOutputStream().write(outsideGroups.size());
                for(Group g: outsideGroups)
                {
                    s.getOutputStream().write(g.group_id);
                    pw.println(g.group_name);
                }
                //loop per ricevere messaggi
                while(s.isConnected())
                {
                    String message = br.readLine();
                    int group = s.getInputStream().read();
                    BroadcastMessage(message,group);
                }
                s.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    static ArrayList<ClientThread> clients;
    public Server(int portNumber)
    {
        clients = new ArrayList<>();
        try
        {
            //create server
            ServerSocket ss = new ServerSocket(portNumber);
            while (true)
            {
                Socket s = ss.accept();
                ClientThread ct = new ClientThread(s);
                ct.pw = new PrintWriter(ct.s.getOutputStream(),true);
                AddThread(ct);
                ct.start();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void AddThread(ClientThread ct)
    {
        clients.add(ct);
    }
    public void RemoveThread(ClientThread ct)
    {
        clients.remove(ct);
    }
    public static void main(String[] args)
    {
        new Server(2560);
    }

}