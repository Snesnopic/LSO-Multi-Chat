package com.snesnopic.ermes.activitypkg;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;
import static com.snesnopic.ermes.control.Connessione.otherGroups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.snesnopic.ermes.R;
import com.snesnopic.ermes.control.GroupsAdapter;
import com.snesnopic.ermes.datapkg.Group;

import java.util.List;

public class OtherGroupsFragment extends Fragment {
    public static GroupsAdapter adapter;
    public List<Group> GetOtherGroups() {
        otherGroups = connection.getOtherGroups();
        return otherGroups;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_othergroups, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = view.findViewById(R.id.otherGroupsRecyclerView);
        List<Group> otherGroups = GetOtherGroups();
        adapter = new GroupsAdapter(view.getContext(),otherGroups.size(),otherGroups);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            pullToRefresh.setRefreshing(false);
            List<Group> newGroups = GetOtherGroups();
            adapter = new GroupsAdapter(view.getContext(), newGroups.size(), newGroups);
            list.setAdapter(adapter);
        });
    }
}