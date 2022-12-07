package com.snesnopic.ermes.ctrlpkg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.R;
import com.snesnopic.ermes.datapkg.Group;
import java.util.List;

public class GroupsWithRequestsAdapter extends RecyclerView.Adapter<GroupWithRequestHolder>  {
    private final List<Group> groups;
    private final int itemResource;
    private final Context context;
    public GroupsWithRequestsAdapter(Context context,int itemResource, List<Group> groups)
    {
        this.context = context;
        this.groups = groups;
        this.itemResource = itemResource;
    }

    @NonNull
    @Override
    public GroupWithRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listactivity_row_groupwithrequests,parent,false);
        return new GroupWithRequestHolder(this.context,view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupWithRequestHolder holder, int position) {
        Group g = this.groups.get(position);
        holder.bindGroupWithRequest(g);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

}
