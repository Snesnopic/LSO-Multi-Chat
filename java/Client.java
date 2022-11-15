import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    InputStreamReader isr;
    PrintWriter pw;
    Scanner sc;
    Socket s;
    class DownloadSocketThread extends Thread
    {
        public void run()
        {
            try {
                InputStreamReader isr = new InputStreamReader(s.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                while (true)
                {
                    SortMessages(br.readLine());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    void SortMessages(String s)
    {

    }
    ArrayList<Group> groups;
    ArrayList<Group> outsideGroups;
    void SendMessage(String message, int group_id)
    {
        try {
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            s.getOutputStream().write(group_id);
            pw.println(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public Client()
    {
        try
        {
            groups = new ArrayList<>();
            outsideGroups = new ArrayList<>();
            s = new Socket("localhost",portNumber);
            sc = new Scanner(System.in);
            pw = new PrintWriter(s.getOutputStream(), true);
            isr = new InputStreamReader(s.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            boolean success = false;
            while(!success)
            {
                //input id utente
                s.getOutputStream().write(sc.nextInt());
                //input password
                pw.println(sc.next());
                if(s.getInputStream().read() == 1)
                    success = true;
            }
            //quanti gruppi ha questo utente?
            int groupnumber = s.getInputStream().read();
            for(int i = 0; i < groupnumber; ++i)
            {
                int groupid = s.getInputStream().read();
                String groupName = br.readLine();
                Group g = new Group(groupid,groupName);
                groups.add(g);
            }
            groupnumber = s.getInputStream().read();
            for(int i = 0; i < groupnumber; ++i)
            {
                int groupid = s.getInputStream().read();
                String groupName = br.readLine();
                Group g = new Group(groupid,groupName);
                outsideGroups.add(g);
            }

            DownloadSocketThread dst = new DownloadSocketThread();
            dst.start();
            while(true)
            {
                //messaggio da inviare
                pw.println(sc.next());
                //gruppo a cui inviare
                s.getOutputStream().write(sc.nextInt());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
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
    static int portNumber = 2560;
    public static void main(String[] args) {
        new Client();
    }
}