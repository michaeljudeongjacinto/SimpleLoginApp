package com.example.simpleloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView forgotPass, signup, attempts;
    String userEmail, userPassword;
    private int counter = 5;
    private Button login;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        forgotPass = (TextView) findViewById(R.id.tvForgotPassword);
        attempts = (TextView) findViewById(R.id.tvAttempts);
        signup = (TextView) findViewById(R.id.tvSignUp);
        login = (Button) findViewById(R.id.btnLogin);

        attempts.setText("No. of attempts remaining: 5");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // validate (if the user is not null)
        if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this, Home.class));
        }

        // login button (if account exists)
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputValidation();
            }
        });

        // sign up button (if account not exists)
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        // forgot password button
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AccountRecovery.class));
            }
        });
    }

    // validate user input
    private void inputValidation(){

        userEmail = email.getText().toString();
        userPassword = password.getText().toString();

        if (userEmail.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter your email!", Toast.LENGTH_SHORT).show();
        }
        else if(userPassword.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter your password!", Toast.LENGTH_SHORT).show();
        }
        else{
            validate(userEmail, userPassword);
        }
    }

    // validate email and password (firebase auth)
    private void validate(String email, String password) {

        // loading
        progressDialog.setMessage("Logging in....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    checkEmailVerification();
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect email or password. Please try again!", Toast.LENGTH_SHORT).show();
                    counter--;
                    attempts.setText("No. of attempts remaining: " + counter);
                    progressDialog.dismiss();
                    if (counter == 0) {
                        login.setEnabled(false);
                    }
                }
            }
        });
    }

    // validate email verification
    private void checkEmailVerification(){

        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();

        if(emailflag){
            finish();
            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, Home.class));
       }else{
            Toast.makeText(this, "Verify your email address!", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

}