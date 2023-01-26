package com.snesnopic.ermes.activitypkg;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;
import static com.snesnopic.ermes.control.Connessione.myGroups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.R;
import com.snesnopic.ermes.control.MessageAdapter;
import com.snesnopic.ermes.datapkg.Group;
import com.snesnopic.ermes.datapkg.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    public static MessageAdapter adapter;
    public Button sendButton;
    public EditText chatText;

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
        Group actualGroup = new Group();


        ArrayList<Message> messages = GetMessagesFromGroup(groupName);
        adapter = new MessageAdapter(this, messages.size(),messages);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        chatText = (EditText) findViewById(R.id.edit_gchat_message);
        sendButton = (Button) findViewById(R.id.button_gchat_send);
        for(int i = 0; i < myGroups.size(); i++) {
            if(myGroups.get(i).name.equals(groupName))  {
                actualGroup = myGroups.get(i);
                break;
            }
        }
        Group finalActualGroup = actualGroup;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connection.sendMessage(chatText.getText().toString(), finalActualGroup))
                    System.out.println("Messaggio inviato");

            }
        });
    }




}
