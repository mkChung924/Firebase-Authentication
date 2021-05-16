package com.brothersplant.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button email_password_auth_button, email_auth_button, mobile_auth_button, gmail_auth_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기화
        intiFields();
        // Listener
        buttonListener();

        Intent intent = getIntent();
        String emailLink = intent.getStringExtra("emailLink");

        if(!TextUtils.isEmpty(emailLink)){
            moveToEmailAuthActivity(emailLink);
        }

    }

    private void intiFields() {
        firebaseAuth = FirebaseAuth.getInstance();

        email_password_auth_button = findViewById(R.id.email_password_auth_button);
        email_auth_button = findViewById(R.id.email_auth_button);
        mobile_auth_button = findViewById(R.id.mobile_auth_button);
        gmail_auth_button = findViewById(R.id.gmail_auth_button);
    }

    private void buttonListener() {

        email_password_auth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToEmailPasswordAuthActivity();
            }
        });

        email_auth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToEmailAuthActivity();
            }
        });

        mobile_auth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMobileAuthActivity();
            }
        });

        gmail_auth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void moveToEmailPasswordAuthActivity() {
        Intent emailIntent = new Intent(MainActivity.this, EmailPasswordAuthActivity.class);
        startActivity(emailIntent);
    }

    private void moveToEmailAuthActivity() {
        Intent emailIntent = new Intent(MainActivity.this, EmailAuthActivity.class);
        startActivity(emailIntent);
    }

    private void moveToEmailAuthActivity(String emailLink) {
        Intent emailIntent = new Intent(MainActivity.this, EmailAuthActivity.class);
        emailIntent.putExtra("emailLink", emailLink);
        startActivity(emailIntent);
    }

    private void moveToMobileAuthActivity() {
        Intent emailIntent = new Intent(MainActivity.this, MobileAuthActivity.class);
        startActivity(emailIntent);
    }

    private void moveToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            //Toast.makeText(this, "firebaseuser is not null", Toast.LENGTH_SHORT).show();
            moveToProfileActivity();
        } else {
            //Toast.makeText(this, "firebaseuser is null", Toast.LENGTH_SHORT).show();
        }

    }
}