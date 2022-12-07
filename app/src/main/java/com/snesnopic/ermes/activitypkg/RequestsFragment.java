package com.snesnopic.ermes.activitypkg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snesnopic.ermes.R;
import com.snesnopic.ermes.ctrlpkg.GroupsWithRequestsAdapter;
import com.snesnopic.ermes.datapkg.Group;
import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {
    public List<Group> GetGroupsWithRequests()
    {
        ArrayList<Group> groupsWithRequests = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
        {
            Group a = new Group();
            a.name = "Gruppo " + i;
            groupsWithRequests.add(a);
        }
        return groupsWithRequests;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groupswithrequests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = view.findViewById(R.id.groupsWithRequestRecyclerView);
        List<Group> groupsWithRequests = GetGroupsWithRequests();
        GroupsWithRequestsAdapter adapter = new GroupsWithRequestsAdapter(view.getContext(),groupsWithRequests.size(),groupsWithRequests);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }
}