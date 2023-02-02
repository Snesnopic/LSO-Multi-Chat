package com.snesnopic.ermes.control;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;
import static com.snesnopic.ermes.control.Connessione.requestGroups;
import static com.snesnopic.ermes.control.Connessione.requests;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.snesnopic.ermes.R;
import com.snesnopic.ermes.activitypkg.RequestsFragment;
import com.snesnopic.ermes.datapkg.Group;
import com.snesnopic.ermes.datapkg.Request;

import java.util.ArrayList;

public class GroupWithRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final RecyclerView list;
    private final TextView groupName;
    private final Context context;
    private final ImageButton acceptAll;
    private final ImageButton refuseAll;
    private Group g;

    public ArrayList<Request> RequestsOfGroup(Group g) //qui sostituire con call a Connessione che restituisce richieste del gruppo g
    {
        System.out.println("=============Sono il gruppo: "+g.name);
        return connection.getRequests(g);
    }
    public GroupWithRequestHolder(Context context, @NonNull View itemView)
    {
        super(itemView);
        this.context = context;
        groupName = itemView.findViewById(R.id.txt_groupwithrequestname);
        list = itemView.findViewById(R.id.requestsRecyclerView);
        itemView.setOnClickListener(this);
        acceptAll = itemView.findViewById(R.id.acceptAllButton);
        refuseAll = itemView.findViewById(R.id.refuseAllButton);
    }

    public void bindGroupWithRequest(Group group)
    {
        this.g = group;
        groupName.setText(g.name);
        RequestsOfGroupAdapter rofga = new RequestsOfGroupAdapter(context, RequestsOfGroup(g));
        list.setAdapter(rofga);
        list.setLayoutManager(new LinearLayoutManager(context));
        acceptAll.setOnClickListener(v -> {
            for(int i = 0; i < requestGroups.size(); i++) {
                if(requests.get(i).group.id == group.id) {
                   if(connection.requestHandler(requestGroups.get(i), requests.get(i).user.userid, true)) {
                       requests.remove(i);
                       System.out.println("Tutti gli utenti sono stati accettati");
                   }
                }
            }
            RequestsFragment.adapter.notifyDataSetChanged();
        });
        refuseAll.setOnClickListener(v -> {
            for(int i = 0; i < requestGroups.size(); i++) {
                if(requests.get(i).group.id == group.id) {
                    if(connection.requestHandler(requestGroups.get(i), requests.get(i).user.userid, false)) {
                        requests.remove(i);
                        System.out.println("Tutti gli utenti sono stati rifiutati");
                    }
                }
            }
            RequestsFragment.adapter.notifyDataSetChanged();
        });
    }
    @Override
    public void onClick(View v) {
        if (this.g != null) {
            Toast.makeText(this.context, "Clicked on " + this.g.name, Toast.LENGTH_SHORT ).show();
        }
    }
}
