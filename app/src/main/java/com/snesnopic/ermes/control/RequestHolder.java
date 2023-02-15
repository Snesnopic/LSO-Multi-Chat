package com.snesnopic.ermes.control;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.snesnopic.ermes.R;
import com.snesnopic.ermes.activitypkg.RequestsFragment;
import com.snesnopic.ermes.datapkg.Request;

public class RequestHolder extends RecyclerView.ViewHolder{
    private final TextView username;
    private final ImageButton accept;
    private final ImageButton refuse;

    public RequestHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.txt_requestUser);
        accept = itemView.findViewById(R.id.acceptButton);
        refuse = itemView.findViewById(R.id.refuseButton);
    }
    public void bindRequest(Request r)
    {
        username.setText(r.user.username);
        System.out.println("Username: "+r.user.username + " Userid: "+r.user.userid);
        accept.setOnClickListener(v ->
        {
            connection.requestHandler(r.group,r.user.userid,true);
            Connessione.requests.removeIf(request -> request.group.id == r.group.id && request.user.userid == r.user.userid);
            RequestsFragment.adapter.notifyDataSetChanged();
        });
        refuse.setOnClickListener(v ->
        {
            connection.requestHandler(r.group,r.user.userid,false);
            Connessione.requests.removeIf(request -> request.group.id == r.group.id && request.user.userid == r.user.userid);
            RequestsFragment.adapter.notifyDataSetChanged();
        });
    }
}
