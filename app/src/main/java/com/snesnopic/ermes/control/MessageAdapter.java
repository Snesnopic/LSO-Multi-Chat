package com.snesnopic.ermes.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.R;
import com.snesnopic.ermes.datapkg.Message;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter{
    private final List<Message> messages;
    private final Context context;
    private final int itemResource;
    public MessageAdapter(Context context,int itemResource, List<Message> messages)
    {
        this.context = context;
        this.messages = messages;
        this.itemResource = itemResource;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listactivity_row_mymessage, parent, false);
            return new MyMessageHolder(context,view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listactivity_row_othersmessage, parent, false);
            return new OthersMessageHolder(context,view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message m = messages.get(position);
        if(Objects.equals(m.senderUsername, "Utente 1"))
            return 1;
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if(holder.getItemViewType() == 1)
            ((MyMessageHolder) holder).bindMessage(message);
        else
            ((OthersMessageHolder) holder).bindMessage(message);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
