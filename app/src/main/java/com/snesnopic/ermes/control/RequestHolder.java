package com.snesnopic.ermes.control;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.snesnopic.ermes.R;
import com.snesnopic.ermes.datapkg.Request;

public class RequestHolder extends RecyclerView.ViewHolder{
    private final TextView username;
    private final Context context;
    private final ImageButton accept;
    private final ImageButton refuse;
    private Request r;
    public RequestHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        username = itemView.findViewById(R.id.txt_requestUser);
        accept = itemView.findViewById(R.id.acceptButton);
        refuse = itemView.findViewById(R.id.refuseButton);
    }
    public void bindRequest(Request r)
    {
        this.r = r;
        username.setText(r.user.username);
        accept.setOnClickListener(v -> {
            //TODO: accetta richiesta di utente 'username' nel gruppo r.group.name
        });
        refuse.setOnClickListener(v -> {
            //TODO: rifiuta richiesta di utente 'username' nel gruppo r.group.name
        });
    }
}
