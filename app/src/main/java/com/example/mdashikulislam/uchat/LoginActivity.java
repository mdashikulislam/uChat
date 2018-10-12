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

public class LoginActivity extends AppCompatActivity {
    private Button _LoginButton,_PhoneLoginButton;
    private EditText _UserEmail,_UserPassword;
    private TextView _NeedNewAccountLink,_ForgetAccountLink;
    private FirebaseAuth auth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitializFeild();
        auth = FirebaseAuth.getInstance();

        _LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUserToLogin();
            }
        });
        _NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });
    }

    private void allowUserToLogin() {

        String email = _UserEmail.getText().toString().trim();
        String password =_UserPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Pleasr enter your email...",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Pleasr enter your Password...",Toast.LENGTH_SHORT).show();
        }else {
            dialog.setTitle("Sign in");
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this,"Login Successful...",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitializFeild() {
        _LoginButton = findViewById(R.id.login_button);
        _PhoneLoginButton = findViewById(R.id.phone_login_button);
        _UserEmail = findViewById(R.id.login_emial);
        _UserPassword = findViewById(R.id.login_password);
        _NeedNewAccountLink = findViewById(R.id.need_new_account_link);
        _ForgetAccountLink = findViewById(R.id.forget_password_link);
        dialog = new ProgressDialog(this);

    }

    private void sendUserToMainActivity() {
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }
    private void sendUserToRegisterActivity() {
        Intent i = new Intent(LoginActivity.this,RegisterActivity.class);startActivity(i);
    }
}
