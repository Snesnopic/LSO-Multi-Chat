package com.snesnopic.ermes.ctrlpkg;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.activitypkg.ChatActivity;
import com.snesnopic.ermes.R;
import com.snesnopic.ermes.datapkg.Group;
import com.snesnopic.ermes.datapkg.Message;
import java.time.format.DateTimeFormatter;

public class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView groupName;
    private final TextView lastMessage;
    private final TextView dateTime;
    private final Context context;
    private Group g;
    public GroupHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        groupName = itemView.findViewById(R.id.txt_group_name);
        lastMessage = itemView.findViewById(R.id.txt_group_lastmessage);
        dateTime = itemView.findViewById(R.id.txt_group_datetime);
        itemView.setOnClickListener(this);
    }
    public void bindGroup(Group g)
    {
        this.g = g;
        groupName.setText(g.name);
        Message message = g.messages.get(g.messages.size() - 1);
        lastMessage.setText(message.message);
        dateTime.setText(message.time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    @Override
    public void onClick(View v) {
        if (this.g != null) {
            Toast.makeText(this.context, "Clicked on " + this.g.name, Toast.LENGTH_SHORT ).show();
            Intent mainIntent = new Intent(context, ChatActivity.class);
            mainIntent.putExtra("groupName",g.name);
            context.startActivity(mainIntent);
        }
    }
}
