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

    public class UploadSocketThread extends Thread
    {
        //loop per mandare messaggi
        public void run()
        {

        }
    }
    public class ClientThread extends Thread{
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
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                boolean success = false;
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
                ArrayList<Group> groups = GetGroupsOfUsers(userId);
                s.getOutputStream().write(groups.size());
                for(Group g: groups)
                {
                    s.getOutputStream().write(g.group_id);
                    pw.println(g.group_name);
                }
                ArrayList<Group> outsideGroups = GetGroupsOfUsers(userId);
                s.getOutputStream().write(outsideGroups.size());
                for(Group g: outsideGroups)
                {
                    s.getOutputStream().write(g.group_id);
                    pw.println(g.group_name);
                }
                //loop per ricevere messaggi
                while(true)
                {
                    int group = s.getInputStream().read();
                    String message = br.readLine();

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    int portNumber = 2560;
    static ArrayList<ClientThread> clients;
    public Server()
    {
        clients = new ArrayList<>();
        try
        {
            //create server
            ServerSocket ss = new ServerSocket(portNumber);
            while (true)
            {
                Socket s = ss.accept();
                ClientThread ct = new ClientThread( s);
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
        new Server();
    }

}