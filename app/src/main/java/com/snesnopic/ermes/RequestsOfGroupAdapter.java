package com.snesnopic.ermes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.snesnopic.ermes.datapkg.Request;

import java.util.List;

public class RequestsOfGroupAdapter extends BaseAdapter {
    private final List<Request> requests;

    private final Context context;
    public RequestsOfGroupAdapter(Context context, List<Request> requests)
    {
        this.context = context;
        this.requests = requests;
    }
    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int i) {
        return requests.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View v, ViewGroup viewGroup) {
        if (v == null)
        {
            v = LayoutInflater.from(context).inflate(R.layout.listactivity_row_requestsofgroup, null);
        }
        Request g = (Request) getItem(position);
        TextView txt = v.findViewById(R.id.txt_requestUser);
        txt.setText(g.user.username);
        return v;
    }
}
