package com.indah.myinstacherry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    EditText email, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_pass);
        btnSignIn = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_signin);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    Toast.makeText(LoginActivity.this, "You're Logged In", Toast.LENGTH_LONG).show();
                    Intent IntToHome = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(IntToHome);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_LONG).show();
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EmailL = email.getText().toString();
                String Pass = password.getText().toString();
                if(EmailL.isEmpty()){
                    email.setError("Please Enter Email");
                    email.requestFocus();
                }
                else if(Pass.isEmpty()){
                    password.setError("Please Enter Your Password");
                    password.requestFocus();
                }
                else if(EmailL.isEmpty() && Pass.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                }
                else if( ! (EmailL.isEmpty() && Pass.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(EmailL, Pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login Error, Please Login Again!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent IToHome = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(IToHome);
                            }
                        }
                    });
                }

                else{
                    Toast.makeText(LoginActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IntSignUp = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(IntSignUp);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
