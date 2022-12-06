package com.snesnopic.ermes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.datapkg.Request;

import java.util.List;

public class RequestsOfGroupAdapter extends RecyclerView.Adapter<RequestHolder> {
    private final List<Request> requests;

    private final Context context;
    public RequestsOfGroupAdapter(Context context, List<Request> requests)
    {
        this.context = context;
        this.requests = requests;
    }
    @Override
    public int getItemCount() {
        return requests.size();
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listactivity_row_requestsofgroup,parent,false);
        return new RequestHolder(this.context,view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
        Request r = requests.get(position);
        holder.bindRequest(r);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
