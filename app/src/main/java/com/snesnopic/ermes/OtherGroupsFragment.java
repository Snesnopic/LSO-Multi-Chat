package com.snesnopic.ermes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.snesnopic.ermes.datapkg.Group;
import com.snesnopic.ermes.datapkg.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OtherGroupsFragment extends Fragment {
    public List<Group> GetOtherGroups()
    {
        ArrayList<Group> otherGroups = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
        {
            Group a = new Group();
            a.name = "Gruppo " + i;
            Message msg = new Message();
            msg.message = "Ultimo messaggio gruppo " + i;
            msg.time = LocalDateTime.now();
            a.messages = new ArrayList<>();
            a.messages.add(msg);
            otherGroups.add(a);
        }
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
        ListView list = view.findViewById(R.id.otherGroupsListView);
        GroupsAdapter adapter = new GroupsAdapter(view.getContext(),GetOtherGroups());
        list.setAdapter(adapter);
    }
}