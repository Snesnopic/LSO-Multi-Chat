package com.snesnopic.ermes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;


public class MyGroupsFragment extends Fragment {
    public List<Group> GetGroups(int userID)
    {
        return null;
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

        ListView list = view.findViewById(R.id.listView1);
        String s1 = "1";
        String s2 = "2";
        String array[] = {"1","2"};



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.fragment_mygroups,R.id.textView,array);
        list.setAdapter(adapter);
    }
}