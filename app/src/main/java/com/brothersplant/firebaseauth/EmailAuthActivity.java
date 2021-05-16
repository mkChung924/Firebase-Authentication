package com.brothersplant.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailAuthActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ActionCodeSettings actionCodeSettings;

    private EditText email_input;
    private Button send_button;

    private String emailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_auth);

        initFields();
        buttonListener();

        Intent intent = getIntent();
        emailLink = intent.getStringExtra("emailLink");

        if(!TextUtils.isEmpty(emailLink)){
            confirmSignIn();

        } else {

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("PREFS", MODE_PRIVATE);
            String email = preferences.getString("AUTH_EMAIL", "default");

            if(!"default".equals(email)){
                email_input.setText(email);
            }

        }

    }

    private void initFields() {

        email_input = findViewById(R.id.email_input);
        send_button = findViewById(R.id.send_button);

        actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://brothersplant.page.link/email")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.brothersplant.firebaseauth", true, "1")
                .build();

        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void buttonListener() {

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_input.getText().toString().trim();

                if(!TextUtils.isEmpty(email)){

                    firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("Auth", "이메일 전송 완료 !");

                                        Toast.makeText(EmailAuthActivity.this, "이메일로 인증 URL을 전송하였습니다.", Toast.LENGTH_SHORT).show();

                                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                                        editor.putString("AUTH_EMAIL", email);
                                        editor.apply();

                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmailAuthActivity.this, "실패 !", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }

    private void confirmSignIn() {

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        String email = preferences.getString("AUTH_EMAIL", "default");

        if("default".equals(email)){

            Toast.makeText(this, "이메일 인증을 진행해주세요.", Toast.LENGTH_SHORT).show();
            email_input.setEnabled(true);
            send_button.setVisibility(View.VISIBLE);

        } else {

            email_input.setEnabled(false);
            send_button.setVisibility(View.INVISIBLE);

            Toast.makeText(this, "이메일 : " + email, Toast.LENGTH_SHORT).show();
            email_input.setText(email);

            // Confirm the link is a sign-in with email link.
            if (firebaseAuth.isSignInWithEmailLink(emailLink)) {

                firebaseAuth.signInWithEmailLink(email, emailLink)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    AuthResult result = task.getResult();
                                    // You can access the new user via result.getUser()
                                    // Additional user info profile *not* available via:
                                    // result.getAdditionalUserInfo().getProfile() == null
                                    // You can check if the user is new or existing:
                                    // result.getAdditionalUserInfo().isNewUser()
                                    Toast.makeText(EmailAuthActivity.this, "로그인을 진행합니다.", Toast.LENGTH_SHORT).show();
                                    moveToProfileActivity();
                                }
                            }
                        });
            }

        } // end else

    }

    private void moveToProfileActivity() {
        Intent profileIntent = new Intent(EmailAuthActivity.this, ProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

}