package com.brothersplant.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MobileAuthActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private EditText mobile_digit_input, verification_code_input;
    private Button send_verification_button, verify_button, reset_button;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_auth);

        initFields();
        buttonListener();
        phoneVerification();

    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        mobile_digit_input = findViewById(R.id.mobile_digit_input);
        verification_code_input = findViewById(R.id.verification_code_input);
        send_verification_button = findViewById(R.id.send_verification_button);
        verify_button = findViewById(R.id.verify_button);
        reset_button = findViewById(R.id.reset_button);
    }

    private void buttonListener() {
        send_verification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone_digit = mobile_digit_input.getText().toString().trim();

                if(!TextUtils.isEmpty(phone_digit)){

                    String phone_number = "+82"+phone_digit;

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                            .setPhoneNumber(phone_number)
                            .setTimeout(120L, TimeUnit.SECONDS)
                            .setActivity(MobileAuthActivity.this)
                            .setCallbacks(callbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                }

            }
        });

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verification_code = verification_code_input.getText().toString().trim();

                if (!TextUtils.isEmpty(verification_code)){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verification_code);
                    signInWithCredential(credential);
                }
            }
        });

        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobile_digit_input.setEnabled(true);

                send_verification_button.setEnabled(true); //인증번호 발송 버튼 활성화
                verify_button.setEnabled(false); // 인증 버트 비활성화
                reset_button.setEnabled(false); // 초기화 버튼 활성화

                verification_code_input.setVisibility(View.GONE); // 인증번호 입력란 제거
            }
        });
    }

    private void phoneVerification() {

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(MobileAuthActivity.this, "인증번호 발송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                
                mVerificationId = verificationId;

                Toast.makeText(MobileAuthActivity.this, "인증번호가 발송되었습니다.", Toast.LENGTH_SHORT).show();

                mobile_digit_input.setEnabled(false);
                send_verification_button.setEnabled(false); //인증번호 발송 버튼 비활성화
                verify_button.setEnabled(true); // 인증 버트 활성화
                reset_button.setEnabled(true); // 초기화 버튼 활성화

                verification_code_input.setVisibility(View.VISIBLE);

            }
        };
    }

    private void signInWithCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        moveToProfileActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MobileAuthActivity.this, "모바일 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void moveToProfileActivity() {
        Intent profileIntent = new Intent(MobileAuthActivity.this, ProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }
}