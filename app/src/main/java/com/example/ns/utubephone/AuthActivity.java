package com.example.ns.utubephone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    private LinearLayout mPhoneLayout;
    private LinearLayout mcodeLayout;

    private EditText mPhoneText;
    private EditText mCodeText;

    private ProgressBar mPhoneBar;
    private ProgressBar mCodeBar;

    private Button mSendButton;

    private TextView mErrorText;

    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    private int btntype = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mPhoneLayout = findViewById(R.id.phoneLayout);
        mcodeLayout = findViewById(R.id.codeLayout);

        mPhoneText = findViewById(R.id.phoneEditText);
        mCodeText =  findViewById(R.id.codeEditText);

        mPhoneBar = findViewById(R.id.phoneProgress);
        mCodeBar = findViewById(R.id.codeProgress);

        mSendButton = findViewById(R.id.sendbtn);

        mErrorText = findViewById(R.id.errorText);
        mAuth = FirebaseAuth.getInstance();

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btntype == 0) {

                    mPhoneBar.setVisibility(View.VISIBLE);
                    mPhoneText.setEnabled(false);
                    mSendButton.setEnabled(false);

                    String phonenumber = mPhoneText.getText().toString();
                    String dPhonenumber;

                    dPhonenumber = "+91" + phonenumber;
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            dPhonenumber,
                            60,
                            TimeUnit.SECONDS,
                            AuthActivity.this,
                            mCallBacks
                    );
                }
                else{
                    mSendButton.setEnabled(false);
                    mCodeBar.setVisibility(View.VISIBLE);

                    String verificationcode = mCodeText.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mErrorText.setText("Authentication failed");
                mErrorText.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                btntype = 1;
                mCodeBar.setVisibility(View.INVISIBLE);
                mcodeLayout.setVisibility(View.VISIBLE);
                mSendButton.setText("Verify Code");
                mSendButton.setEnabled(true);
                mcodeLayout.setVisibility(View.VISIBLE);
                mCodeBar.setVisibility(View.VISIBLE);

                // ...
            }
        };



    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(AuthActivity.this,MainActivity.class));
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            mErrorText.setText("Problem in Login");
                            mErrorText.setVisibility(View.VISIBLE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
