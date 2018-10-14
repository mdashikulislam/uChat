package com.example.mdashikulislam.uchat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar _toolbar;
    private ImageButton _sendMessageButton;
    private EditText _userMessageInput;
    private ScrollView _scrollview;
    private TextView _displayTextMessage;
    private FirebaseAuth auth;
    private DatabaseReference reference,_groupNameRef,_groupMessageKeyRef;
    private String _currentGroupName,_currentUserId,_currentuserName,_currentDate,_currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        _currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this,_currentGroupName,Toast.LENGTH_SHORT).show();
            auth = FirebaseAuth.getInstance();
            _currentUserId = auth.getCurrentUser().getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("User");
            _groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(_currentGroupName);



        InitializFeild();
        GetUserInfo();

        _sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessageInfoToDatabase();
                _userMessageInput.setText("");
                _scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        _groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
            _displayTextMessage.append(chatName + " :\n"+chatMessage+"\n"+chatTime+"    "+chatDate+"\n\n\n");
            _scrollview.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }

    private void InitializFeild() {
        _toolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(_toolbar);
        getSupportActionBar().setTitle(_currentGroupName);
        _sendMessageButton = findViewById(R.id.send_message_button);
        _userMessageInput = findViewById(R.id.input_group_message);
        _scrollview = findViewById(R.id.my_scroll_view);
        _displayTextMessage = findViewById(R.id.group_chat_text_display);

    }

    private void GetUserInfo() {

        reference.child(_currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    _currentuserName = dataSnapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveMessageInfoToDatabase() {
        String message = _userMessageInput.getText().toString();
        String messageKey = _groupNameRef.push().getKey();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this,"Please write message first",Toast.LENGTH_SHORT).show();
        }else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            _currentDate = dateFormat.format(calendar.getTime());

            Calendar calTime = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            _currentTime = timeFormat.format(calTime.getTime());

            HashMap<String,Object>groupMessageKey = new HashMap<>();
            _groupNameRef.updateChildren(groupMessageKey);
            _groupMessageKeyRef = _groupNameRef.child(messageKey);
            HashMap<String,Object>messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",_currentuserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",_currentDate);
            messageInfoMap.put("time",_currentTime);
            _groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }
}
