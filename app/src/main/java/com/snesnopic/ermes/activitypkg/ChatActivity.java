package com.snesnopic.ermes.activitypkg;

import android.os.Bundle;
import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;
import static com.snesnopic.ermes.activitypkg.LoginActivity.myGroups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.R;
import com.snesnopic.ermes.control.MessageAdapter;
import com.snesnopic.ermes.datapkg.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ArrayList<Message> GetMessagesFromGroup(String groupName) {
        ArrayList<Message> msg;
        for(int i = 0; i < myGroups.size(); i++) {
            if(myGroups.get(i).name.equals(groupName)) return (ArrayList<Message>) myGroups.get(i).messages;
        }
        msg = new ArrayList<>();
        Message e = new Message();
        e.time = LocalDateTime.now();
        e.message = "Nessun messaggio inviato su questo gruppo.";
        e.senderUsername = "Odisseo";

        msg.add(e);

        return msg;

    }
    RecyclerView list;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        list = findViewById(R.id.recycler_gchat);
        String groupName = getIntent().getExtras().getString("groupName");
        ChatActivity.this.setTitle(groupName);
        ArrayList<Message> messages = GetMessagesFromGroup(groupName);
        MessageAdapter adapter = new MessageAdapter(this, messages.size(),messages);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }
}
