package com.example.mdashikulislam.uchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View _groupFragmentView;
    private ListView _list_view;
    private ArrayAdapter<String> _adapter;
    private ArrayList<String> _listOfGroup = new ArrayList<>();
    private DatabaseReference _databaseRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _groupFragmentView= inflater.inflate(R.layout.fragment_groups, container, false);
        _databaseRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        InitializaFeild();
        retriveAndDisplayGroups();

        _list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName = parent.getItemAtPosition(position).toString();
                Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupName);
                startActivity(groupChatIntent);
            }
        });

        return _groupFragmentView;
    }

    private void InitializaFeild() {
        _list_view = (ListView) _groupFragmentView.findViewById(R.id.list_View);
        _adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,_listOfGroup);
        _list_view.setAdapter(_adapter);
    }




    private void retriveAndDisplayGroups() {
        _databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String>set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                _listOfGroup.clear();
                _listOfGroup.addAll(set);
                _adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
