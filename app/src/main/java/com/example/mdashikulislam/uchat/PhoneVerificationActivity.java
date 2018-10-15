package com.example.mdashikulislam.uchat;

import android.app.ProgressDialog;
import android.content.Intent;
import java.util.concurrent.TimeUnit;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneVerificationActivity extends AppCompatActivity {
    private Button _sendVerificationCodeButton,_verifyBytton;
    private EditText _inputPhoneNumber,_inputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String _verificationid;
    private PhoneAuthProvider.ForceResendingToken  resendToken;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        auth = FirebaseAuth.getInstance();

        InitializFeild();

        _sendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = _inputPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneVerificationActivity.this,"Phone Number is required",Toast.LENGTH_SHORT).show();

                }else {
                    dialog.setTitle("Phone Verification");
                    dialog.setMessage("Please wait, We are Authenticate your phone");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneVerificationActivity.this,
                            callbacks
                    );
                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                dialog.dismiss();
                Toast.makeText(PhoneVerificationActivity.this,"Please enter correct phone number with country code",Toast.LENGTH_SHORT).show();
                _sendVerificationCodeButton.setVisibility(View.VISIBLE);
                _inputPhoneNumber.setVisibility(View.VISIBLE);
                _inputVerificationCode.setVisibility(View.INVISIBLE);
                _verifyBytton.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                _verificationid = verificationId;
                resendToken = token;
                dialog.dismiss();
                Toast.makeText(PhoneVerificationActivity.this,"Verification Code has been send.Please check your inbox",Toast.LENGTH_SHORT).show();

                _sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                _inputPhoneNumber.setVisibility(View.INVISIBLE);
                _inputVerificationCode.setVisibility(View.VISIBLE);
                _verifyBytton.setVisibility(View.VISIBLE);
            }
        };
        _verifyBytton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                _inputPhoneNumber.setVisibility(View.INVISIBLE);
                String verificationCode = _inputVerificationCode.getText().toString();
                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneVerificationActivity.this,"Please Write the verification Code first...",Toast.LENGTH_SHORT).show();
                }else {
                    dialog.setTitle("Code Verification");
                    dialog.setMessage("Please wait, We are Authenticate your phone");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(_verificationid, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(PhoneVerificationActivity.this,"Congratulation you are looged in",Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        } else {
                                String message = task.getException().toString();
                            Toast.makeText(PhoneVerificationActivity.this,"Error : "+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                });
    }

    private void sendUserToMainActivity() {
        Intent i = new Intent(PhoneVerificationActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    private void InitializFeild() {
        _sendVerificationCodeButton = findViewById(R.id.send_verification_Code);
        _verifyBytton = findViewById(R.id.verify_button);
        _inputPhoneNumber = findViewById(R.id.phone_number_input);
        _inputVerificationCode = findViewById(R.id.verification_code_input);
        dialog = new ProgressDialog(this);
    }
}
