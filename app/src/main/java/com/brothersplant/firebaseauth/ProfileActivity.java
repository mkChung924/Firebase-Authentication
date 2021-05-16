package com.brothersplant.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView user_auth_id, email_unverified_text;
    private Button logout_button;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initFields();
        buttonListener();

        if(!firebaseUser.isEmailVerified() && TextUtils.isEmpty(firebaseUser.getPhoneNumber())){
            email_unverified_text.setVisibility(View.VISIBLE);
        } else {
            user_auth_id.setVisibility(View.VISIBLE);

            currentUserId = firebaseUser.getUid();
            user_auth_id.setText(currentUserId);
        }

    }

    private void initFields() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        user_auth_id = findViewById(R.id.user_auth_id);
        email_unverified_text = findViewById(R.id.email_unverified_text);
        logout_button = findViewById(R.id.logout_button);
    }

    private void buttonListener() {

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
                Toast.makeText(ProfileActivity.this, "로그아웃 완료", Toast.LENGTH_SHORT).show();
            }
        });
    }
}