package com.brothersplant.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private String emailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initFields();

        Intent intent = getIntent();
        if(intent.getData() != null){
            emailLink = intent.getData().toString();
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Your Code
                moveToMainActivity();

            }
        }, 1200);
    }

    private void initFields(){

        progressBar = findViewById(R.id.progressBar_circle);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void moveToMainActivity(){
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.putExtra("emailLink", emailLink);
        startActivity(mainIntent);
        finish();
    }
}