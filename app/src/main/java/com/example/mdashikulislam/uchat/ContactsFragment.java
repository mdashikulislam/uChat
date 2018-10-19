package com.example.mdashikulislam.uchat;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView contactRecylerList;
    private DatabaseReference reference,userRef;
    private String currentUserId;
    private FirebaseAuth auth;
    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef =  FirebaseDatabase.getInstance().getReference().child("User");
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactRecylerList = contactsView.findViewById(R.id.contactRecylerList);
        contactRecylerList.setLayoutManager(new LinearLayoutManager(getContext()));
        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(reference,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder>adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                String userId = getRef(position).getKey();
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")){
                            String userImage = dataSnapshot.child("image").getValue().toString();
                            String userName = dataSnapshot.child("name").getValue().toString();
                            String userStatus = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(userName);
                            Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            holder.status.setText(userStatus);
                        }else {
                            String userName = dataSnapshot.child("name").getValue().toString();
                            String userStatus = dataSnapshot.child("status").getValue().toString();
                            holder.username.setText(userName);
                            holder.status.setText(userStatus);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_profile,viewGroup,false);
                ContactsViewHolder contactsViewHolder = new ContactsViewHolder(view);
                return contactsViewHolder;
            }
        };

        contactRecylerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView username,status;
        CircleImageView profileImage;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.users_profile_name);
            status = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
