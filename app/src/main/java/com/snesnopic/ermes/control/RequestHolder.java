package com.snesnopic.ermes.control;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.R;
import com.snesnopic.ermes.datapkg.Request;

public class RequestHolder extends RecyclerView.ViewHolder{
    private final TextView username;
    private final Context context;
    private Request r;
    public RequestHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        username = itemView.findViewById(R.id.txt_requestUser);
    }
    public void bindRequest(Request r)
    {
        this.r = r;
        username.setText(r.user.username);
    }
}
