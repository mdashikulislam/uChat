package com.example.mdashikulislam.uchat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(MainActivity.this,"",Toast.LENGTH_SHORT).show();
                    sendUserToSettingActivity();
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
    private void sendUserToSettingActivity() {
        Intent i = new Intent(MainActivity.this,SettingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
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

        }
        return true;
    }
}
