package com.snesnopic.ermes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GroupsAdapter extends BaseAdapter {
    private final List<Group> groups;
    private final Context context;
    public GroupsAdapter(Context context, List<Group> groups)
    {
        this.context = context;
        this.groups = groups;
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
            v = LayoutInflater.from(context).inflate(R.layout.listactivity_row_group, null);
        }
        Group g = (Group) getItem(position);
        TextView txt = v.findViewById(R.id.txt_group_name);
        txt.setText(g.name);
        txt = v.findViewById(R.id.txt_group_lastmessage);
        Message lastMessage = g.messages.get(g.messages.size() - 1);
        txt.setText(lastMessage.message);
        txt = v.findViewById(R.id.txt_group_datetime);
        txt.setText(lastMessage.time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return v;
    }
}
