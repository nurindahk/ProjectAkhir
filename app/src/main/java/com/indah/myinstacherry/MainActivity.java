package com.indah.myinstacherry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    EditText email, password;
    Button btnSignIn;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "MainActivity";
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_pass);
        btnSignIn = findViewById(R.id.btn_login);
        tvSignIn = findViewById(R.id.tv_signin);
        signInButton = findViewById(R.id.btn_google);

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
                    Toast.makeText(MainActivity.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                }
                else if( ! (EmailL.isEmpty() && Pass.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(EmailL, Pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            }
                        }
                    });
                }

                else{
                    Toast.makeText(MainActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                Intent IntGoogle = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(IntGoogle);
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IntToLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(IntToLogin);
            }
        });
    }

    private void signIn(){
        Intent SignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(SignInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Signed Is Successfully", Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Successsful", Toast.LENGTH_LONG).show();
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    updateUI(user);
                }
                else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser fUser){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            Toast.makeText(MainActivity.this, personName + personEmail, Toast.LENGTH_LONG).show();
        }
    }
}
