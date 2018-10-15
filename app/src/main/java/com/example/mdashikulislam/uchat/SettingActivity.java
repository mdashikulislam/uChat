package com.example.mdashikulislam.uchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    private StorageReference userPfofileImageRef;
    private static final int GulleryPic = 1;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        auth = FirebaseAuth.getInstance();
        currentuserId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userPfofileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        InitializFeilds();
        _userName.setVisibility(View.INVISIBLE);
        _updateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccountSetting();
            }
        });

        retriveUserInfo();
        _userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gulleryImage = new Intent();
                    gulleryImage.setAction(Intent.ACTION_GET_CONTENT);
                    gulleryImage.setType("image/*");
                    startActivityForResult(gulleryImage,GulleryPic);
            }
        });
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
                    Picasso.get().load(receveUserProfileImage).into(_userProfileImage);

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
        dialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GulleryPic && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                dialog.setTitle("Set Profile Image");
                dialog.setMessage("Please wait, your  profile image updating...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Uri resultUri = result.getUri();
                StorageReference filePath = userPfofileImageRef.child(currentuserId+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SettingActivity.this,"Profile Upload Successfully...",Toast.LENGTH_SHORT).show();
                       final String downLoadImage = task.getResult().getDownloadUrl().toString();
                        databaseReference.child("User").child(currentuserId).child("image")
                                .setValue(downLoadImage)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(SettingActivity.this,"Image Saved into database Successfully...",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }else {
                                            String message = task.getException().toString();
                                            Toast.makeText(SettingActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    }
                });
            }

        }
    }

    private void sendUserToMainActivity() {
        Intent i = new Intent(SettingActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }
}
