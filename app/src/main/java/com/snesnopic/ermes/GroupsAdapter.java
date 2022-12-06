package com.snesnopic.ermes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.snesnopic.ermes.datapkg.Group;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupHolder> {
    private final List<Group> groups;
    private final Context context;
    private final int itemResource;
    public GroupsAdapter(Context context,int itemResource, List<Group> groups)
    {
        this.context = context;
        this.groups = groups;
        this.itemResource = itemResource;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listactivity_row_group,parent,false);
        return new GroupHolder(this.context,view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        Group g = this.groups.get(position);
        holder.bindGroup(g);
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
