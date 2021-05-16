package com.brothersplant.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
                googleSignIn();
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

    private void googleSignIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
            
        }
        
    }
    
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
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
                        Toast.makeText(MainActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        
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