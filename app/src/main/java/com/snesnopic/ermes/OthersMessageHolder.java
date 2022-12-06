package com.snesnopic.ermes;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.snesnopic.ermes.datapkg.Message;
import java.time.format.DateTimeFormatter;

public class OthersMessageHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TextView date;
    private TextView message;
    private TextView timestamp;
    private Message m;
    public OthersMessageHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        date = itemView.findViewById(R.id.text_gchat_date_other);
        message = itemView.findViewById(R.id.text_gchat_message_other);
        timestamp = itemView.findViewById(R.id.text_gchat_timestamp_other);
    }
    public void bindMessage(Message m)
    {
        this.m = m;
        date.setText(m.time.toLocalDate().toString());
        message.setText(m.message);
        timestamp.setText(m.time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}
