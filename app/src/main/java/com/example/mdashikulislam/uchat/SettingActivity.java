package com.example.mdashikulislam.uchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Button _updateAccountSetting;
    private EditText _userName,_userStatus;
    private CircleImageView  _userProfileImage;
    private ProgressDialog progressDialog;
    private String currentuserId;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        auth = FirebaseAuth.getInstance();
        currentuserId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        InitializFeilds();
        _userName.setVisibility(View.INVISIBLE);
        _updateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccountSetting();
            }
        });

        retriveUserInfo();
    }

    private void retriveUserInfo() {
        databaseReference.child("User").child(currentuserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && ((dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image")))){

                    String receveUserName = dataSnapshot.child("name").getValue().toString();
                    String receveUserStatus = dataSnapshot.child("status").getValue().toString();
                    String receveUserProfileImage = dataSnapshot.child("image").getValue().toString();

                    _userName.setText(receveUserName);
                    _userStatus.setText(receveUserStatus);


                }else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String receveUserName = dataSnapshot.child("name").getValue().toString();
                    String receveUserStatus = dataSnapshot.child("status").getValue().toString();

                    _userName.setText(receveUserName);
                    _userStatus.setText(receveUserStatus);
                }else {
                    _userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingActivity.this,"Please Update your profile...",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateAccountSetting() {
        String userName = _userName.getText().toString();
        String userStatus = _userStatus.getText().toString();

        if (TextUtils.isEmpty(userName)){
            Toast.makeText(this,"Please write your name...",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(userStatus)){
            Toast.makeText(this,"Please write your status...",Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentuserId);
            profileMap.put("name",userName);
            profileMap.put("status",userStatus);
            databaseReference.child("User").child(currentuserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SettingActivity.this,"Profile Update Successful..",Toast.LENGTH_SHORT).show();
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void InitializFeilds() {

        _updateAccountSetting = findViewById(R.id.update_setting_button);
        _userName = findViewById(R.id.set_user_name);
        _userStatus = findViewById(R.id.set_profile_status);
        _userProfileImage = findViewById(R.id.set_profile_image);
        progressDialog = new ProgressDialog(this);
    }

    private void sendUserToMainActivity() {
        Intent i = new Intent(SettingActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }
}
