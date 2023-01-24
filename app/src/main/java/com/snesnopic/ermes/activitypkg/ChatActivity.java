package com.snesnopic.ermes.activitypkg;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.R;
import com.snesnopic.ermes.control.MessageAdapter;
import com.snesnopic.ermes.datapkg.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    ArrayList<Message> GetMessagesFromGroup(String groupName)
    {
        ArrayList<Message> messages = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
        {
            Message msg = new Message();
            msg.senderUsername = "Utente "+i;
            msg.message = "PROVA " + i;
            msg.time = LocalDateTime.now();
            messages.add(msg);
        }
        return messages;
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
