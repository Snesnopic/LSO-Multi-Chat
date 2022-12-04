package com.snesnopic.ermes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.snesnopic.ermes.datapkg.Group;
import com.snesnopic.ermes.datapkg.Request;
import com.snesnopic.ermes.datapkg.User;

import java.util.ArrayList;
import java.util.List;

public class GroupsWithRequestsAdapter extends BaseAdapter {
    private final List<Group> groups;

    private final Context context;
    public GroupsWithRequestsAdapter(Context context, List<Group> groups)
    {
        this.context = context;
        this.groups = groups;
    }
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
    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int i) {
        return groups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View v, ViewGroup viewGroup) {
        if (v == null)
        {
            v = LayoutInflater.from(context).inflate(R.layout.listactivity_row_groupwithrequests, null);
        }
        Group g = (Group) getItem(position);
        TextView txt = v.findViewById(R.id.txt_groupwithrequestname);
        txt.setText(g.name);
        ListView lv = v.findViewById(R.id.requestsListView);
        RequestsOfGroupAdapter rofga = new RequestsOfGroupAdapter(context,RequestsOfGroup((Group) getItem(position)));
        lv.setAdapter(rofga);

        return v;
    }
}
