package com.example.mdashikulislam.uchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiveUserId,currentState,sendUserId;
    private TextView userProfileName,userProfileStatus;
    private CircleImageView profileImage;
    private Button sendMessageButtonRequest,cancleChatRequestButton;
    private DatabaseReference reference,chatRequestRef,contactsRef;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("User");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("chat Request");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiveUserId = getIntent().getExtras().get("visit_user_id").toString();
        sendUserId = auth.getCurrentUser().getUid();

        InitilizFeild();
        retriveUserInfo();
    }

    private void retriveUserInfo() {
        reference.child(receiveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(profileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    manageChatRequest();

                }else{
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    manageChatRequest();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest() {

        chatRequestRef.child(sendUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiveUserId)){

                    String request_type = dataSnapshot.child(receiveUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")){
                        currentState = "request_sent";
                        sendMessageButtonRequest.setText("Cancle Request");
                    }else if(request_type.equals("received")){
                        currentState="request_received";
                        sendMessageButtonRequest.setText("Accept Chat Request");
                        cancleChatRequestButton.setVisibility(View.VISIBLE);
                        cancleChatRequestButton.setEnabled(true);
                        cancleChatRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancleChatRequest();
                            }
                        });
                    }
                }else{
                    contactsRef.child(sendUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiveUserId)){
                                        currentState = "friends";
                                        sendMessageButtonRequest.setText("Remove From Contacts");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!sendUserId.equals(receiveUserId)){
            sendMessageButtonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageButtonRequest.setEnabled(false);
                    if (currentState.equals("new")){
                        sendChatRequest();
                    }
                    if (currentState.equals("request_sent")){
                        cancleChatRequest();
                    }
                    if (currentState.equals("request_received")){
                        acceptChatRequest();
                    }
                    if (currentState.equals("friends")){
                        removeSpecificContacts();
                    }
                }
            });
        }else{
            sendMessageButtonRequest.setVisibility(View.INVISIBLE);

        }
    }

    private void removeSpecificContacts() {
        contactsRef.child(sendUserId).child(receiveUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    contactsRef.child(receiveUserId).child(sendUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendMessageButtonRequest.setEnabled(true);
                                currentState = "new";
                                sendMessageButtonRequest.setText("Send Message");
                                cancleChatRequestButton.setVisibility(View.INVISIBLE);
                                cancleChatRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void acceptChatRequest() {
        contactsRef.child(sendUserId).child(receiveUserId).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactsRef.child(receiveUserId).child(sendUserId).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                chatRequestRef.child(sendUserId).child(receiveUserId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    chatRequestRef.child(receiveUserId).child(sendUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageButtonRequest.setVisibility(View.VISIBLE);
                                                                                    sendMessageButtonRequest.setEnabled(true);
                                                                                    currentState="friends";
                                                                                    sendMessageButtonRequest.setText("Remove From Contacts");
                                                                                    cancleChatRequestButton.setVisibility(View.INVISIBLE);
                                                                                    cancleChatRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancleChatRequest() {
        chatRequestRef.child(sendUserId).child(receiveUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    chatRequestRef.child(receiveUserId).child(sendUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendMessageButtonRequest.setEnabled(true);
                                currentState = "new";
                                sendMessageButtonRequest.setText("Send Message");
                                cancleChatRequestButton.setVisibility(View.INVISIBLE);
                                cancleChatRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendChatRequest() {
        chatRequestRef.child(sendUserId).child(receiveUserId).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            chatRequestRef.child(receiveUserId).child(sendUserId).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessageButtonRequest.setEnabled(true);
                                                currentState = "request_sent";
                                                sendMessageButtonRequest.setText("Cancle Request");


                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void InitilizFeild(){
        userProfileName = findViewById(R.id.visitProfileUsername);
        userProfileStatus = findViewById(R.id.visitProfileStatus);
        profileImage = findViewById(R.id.visitProfileImage);
        sendMessageButtonRequest = findViewById(R.id.sendMessageButton);
        cancleChatRequestButton = findViewById(R.id.cancleMessageButton);
        currentState = "new";
    }
}
