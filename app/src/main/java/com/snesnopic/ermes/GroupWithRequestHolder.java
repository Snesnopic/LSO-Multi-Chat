package com.snesnopic.ermes;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.snesnopic.ermes.datapkg.Group;
import com.snesnopic.ermes.datapkg.Request;
import com.snesnopic.ermes.datapkg.User;
import java.util.ArrayList;

public class GroupWithRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final ListView list;
    private final TextView groupName;
    private final Context context;
    private Group g;
    public ArrayList<Request> RequestsOfGroup(Group g)
    {
        ArrayList<Request> requestsOfGroup = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
        {
            Request r = new Request();
            User a = new User();
            a.username = "Utente " + i + "(" + g.name + ")";
            r.user = a;
            requestsOfGroup.add(r);
        }
        return requestsOfGroup;
    }
    public GroupWithRequestHolder(Context context, @NonNull View itemView)
    {
        super(itemView);
        this.context = context;
        groupName = itemView.findViewById(R.id.txt_groupwithrequestname);
        list = itemView.findViewById(R.id.requestsListView);
        itemView.setOnClickListener(this);
    }

    public void bindGroupWithRequest(Group g)
    {
        this.g = g;
        groupName.setText(g.name);
        RequestsOfGroupAdapter rofga = new RequestsOfGroupAdapter(context,RequestsOfGroup(g));
        list.setAdapter(rofga);
    }
    @Override
    public void onClick(View v) {
        if (this.g != null) {

            Toast.makeText(this.context, "Clicked on " + this.g.name, Toast.LENGTH_SHORT ).show();
        }
    }
}
