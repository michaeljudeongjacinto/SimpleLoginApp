package com.example.simpleloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class AccountRecovery extends AppCompatActivity {

    private EditText email;
    private Button resetPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_recovery);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText) findViewById(R.id.etEmail);
        resetPassword = (Button) findViewById(R.id.btnResetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        // reset password using email address
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = email.getText().toString().trim();

                // validate user input
                if(userEmail.equals("")){
                    Toast.makeText(AccountRecovery.this, "Please enter your registered email account!", Toast.LENGTH_SHORT).show();
                }

                // account recovery (password) using email address
                else{
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AccountRecovery.this, "Password reset, email sent!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(AccountRecovery.this, MainActivity.class));
                            }else{
                                Toast.makeText(AccountRecovery.this, "Error in sending password reset email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    // upper left back button
    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem){

        switch (menuItem.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}