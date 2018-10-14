package com.example.mdashikulislam.uchat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar _toolbar;
    private ImageButton _sendMessageButton;
    private EditText _userMessageInput;
    private ScrollView _scrollview;
    private TextView _displayTextMessage;
    private String _currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        _currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this,_currentGroupName,Toast.LENGTH_SHORT).show();

        InitializFeild();


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
}
