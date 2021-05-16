package com.brothersplant.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordAuthActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private EditText email_input, password_input;
    private Button register_button, login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password_auth);

        initFields();
        buttonListener();

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        String email = preferences.getString("PW_AUTH_EMAIL", "default");

        if(!"default".equals(email)){
            email_input.setText(email);
        }

    }

    private void initFields() {

        firebaseAuth = FirebaseAuth.getInstance();

        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        register_button = findViewById(R.id.register_button);
        login_button = findViewById(R.id.login_button);
    }

    private void buttonListener() {

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void register() {

        String email = email_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                firebaseUser.sendEmailVerification()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(EmailPasswordAuthActivity.this, "입력하신 이메일로 인증 URL을 전송하였습니다.", Toast.LENGTH_SHORT).show();
                                                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                                                editor.putString("PW_AUTH_EMAIL", email);
                                                editor.apply();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EmailPasswordAuthActivity.this, "이메일 전송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EmailPasswordAuthActivity.this, "계정 생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(EmailPasswordAuthActivity.this, "이메일 또는 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {

        String email = email_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {
                                moveToProfileActivity();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(EmailPasswordAuthActivity.this, "오류 : " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void moveToProfileActivity() {
        Intent profileIntent = new Intent(EmailPasswordAuthActivity.this, ProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }
}