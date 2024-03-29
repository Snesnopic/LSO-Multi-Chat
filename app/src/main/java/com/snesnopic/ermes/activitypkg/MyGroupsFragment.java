package com.snesnopic.ermes.activitypkg;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;
import static com.snesnopic.ermes.control.Connessione.myGroups;

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

public class MyGroupsFragment extends Fragment {
    //è statico perché da altre parti del codice si usa l'adapter per notificare la lista che i dati sono cambiati
    public static GroupsAdapter adapter;
    public List<Group> GetMyGroups() {
        myGroups = connection.getRoomJoined();
        return myGroups;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mygroups, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = view.findViewById(R.id.mygroupsRecyclerView);
        List<Group> myGroups = GetMyGroups();
        adapter = new GroupsAdapter(view.getContext(), myGroups.size(), myGroups);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            pullToRefresh.setRefreshing(false);
            List<Group> newGroups = GetMyGroups();
            adapter = new GroupsAdapter(view.getContext(), newGroups.size(), newGroups);
            list.setAdapter(adapter);
        });
    }
}