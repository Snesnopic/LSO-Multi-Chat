package com.snesnopic.ermes.activitypkg;

import static com.snesnopic.ermes.activitypkg.LoginActivity.connection;

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
import com.snesnopic.ermes.control.GroupsAdapter;
import com.snesnopic.ermes.datapkg.Group;
import com.snesnopic.ermes.datapkg.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OtherGroupsFragment extends Fragment {
    public List<Group> GetOtherGroups()
    {
        return connection.getOtherGroups();
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
        GroupsAdapter adapter = new GroupsAdapter(view.getContext(),otherGroups.size(),otherGroups);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }
}