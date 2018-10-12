package com.example.mdashikulislam.uchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button _CreateAccountButton;
    private EditText _UserEmail,_UserPassword;
    private TextView _AlreadyHaveAccountLink;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        InitializFeild();
        _AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });
        _CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

    }

    /**
     * Account creation method
     */
    private void createNewAccount() {

        String email = _UserEmail.getText().toString().trim();
        String password =_UserPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Pleasr enter your email...",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Pleasr enter your Password...",Toast.LENGTH_SHORT).show();
        }else {
            dialog.setTitle("Creating New Account");
            dialog.setMessage("Please wait,while we are creating a new account for you.");
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String currentUserId = auth.getCurrentUser().getUid();
                                reference.child("User").child(currentUserId).setValue("");
                                sendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this,"Account create Successful...",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent i = new Intent(RegisterActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }

    /**
     * Initialization method
     */
    private void InitializFeild() {

        _CreateAccountButton = findViewById(R.id.register_button);
        _UserEmail = findViewById(R.id.register_emial);
        _UserPassword = findViewById(R.id.register_password);
        _AlreadyHaveAccountLink = findViewById(R.id.already_have_account_link);
        dialog = new ProgressDialog(this);
    }

    /**
     *
     */
    private void sendUserToLoginActivity() {
        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);startActivity(i);
    }
}
