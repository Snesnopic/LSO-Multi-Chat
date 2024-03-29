package com.snesnopic.ermes.activitypkg;

import android.os.Bundle;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        Collections.sort(msg,(Comparator.comparing(o -> o.time)));
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
        chatText = findViewById(R.id.edit_gchat_message);
        sendButton = findViewById(R.id.button_gchat_send);
        int i;
        for(i = 0; i < myGroups.size(); i++) {
            if(myGroups.get(i).name.equals(groupName))  {
                actualGroup = myGroups.get(i);
                break;
            }
        }
        try {
            connection.chatThread(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Group finalActualGroup = actualGroup;
        sendButton.setOnClickListener(v -> {
            sendButton.setEnabled(false);
            connection.sendMessage(chatText.getText().toString(), finalActualGroup);
            chatText.setText("");
            System.out.println("Messaggio inviato");
            sendButton.setEnabled(true);
        });
    }


    @Override
    protected void onStop() {
        connection.stopChat();
        super.onStop();
        MyGroupsFragment.adapter.notifyDataSetChanged();
        finish(); //per essere sicuro che l'activity non sia ancora nello stack
    }
}
