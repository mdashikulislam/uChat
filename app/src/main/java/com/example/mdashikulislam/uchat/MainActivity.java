package com.example.mdashikulislam.uchat;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager pager;
    private TabLayout tabLayout;
    private TabAccessAdapter tabAccessAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_page_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("uChat");

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        pager = findViewById(R.id.main_tabs_pager);
        tabAccessAdapter = new TabAccessAdapter(getSupportFragmentManager());
        pager.setAdapter(tabAccessAdapter);

        tabLayout = findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(pager);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null){
            sendUserToLoginActivity();
        }else {
            verifyUserExistance();
        }
    }

    private void verifyUserExistance() {
        String currentUserId = auth.getCurrentUser().getUid();
        databaseReference.child("User").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()){

                }else {

                    sendUserToSettingActivityNewTask();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToLoginActivity() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
    private void sendUserToSettingActivityNewTask() {
        Intent i = new Intent(MainActivity.this,SettingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
    private void sendUserToSettingActivity() {
        Intent i = new Intent(MainActivity.this,SettingActivity.class);
        startActivity(i);
    }
    private void sendUserToFindFriendsActivity() {
        Intent i = new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId() == R.id.main_logout_option){
            auth.signOut();
            sendUserToLoginActivity();
         }
        if (item.getItemId() == R.id.main_setting_option){
            sendUserToSettingActivity();
        }
        if (item.getItemId() == R.id.main_find_friends_option){
            sendUserToFindFriendsActivity();
        }
        if (item.getItemId() == R.id.main_create_group_option){
            requestNewGroup();
        }
        return true;
    }

    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter group name :");
        final EditText groupNamefeild = new EditText(MainActivity.this);
        groupNamefeild.setHint("e.g : Friends Group");
        builder.setView(groupNamefeild);
        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNamefeild.getText().toString();

                if (groupName.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter group name",Toast.LENGTH_SHORT).show();
                }else{
            
                    createNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void createNewGroup(final String groupName) {
        databaseReference.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this,groupName+ "  is Create Successfully",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
