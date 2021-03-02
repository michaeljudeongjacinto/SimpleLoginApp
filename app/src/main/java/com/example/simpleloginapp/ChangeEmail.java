package com.example.simpleloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.view.Change;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ChangeEmail extends AppCompatActivity {

    private EditText newEmail, confirmPassword;
    private Button changeEmail;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private AuthCredential authCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newEmail = (EditText) findViewById(R.id.etNewEmail);
        confirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        changeEmail = (Button) findViewById(R.id.btnChangeEmailAdd);

        firebaseAuth = FirebaseAuth.getInstance();

        // change email button
        changeEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String userNewEmail = newEmail.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
                String userConfirmPassword = confirmPassword.getText().toString();

                // validate user input
                if (userNewEmail.equals("")) {
                    Toast.makeText(ChangeEmail.this, "New email is required!", Toast.LENGTH_SHORT).show();
                } else if (!(userNewEmail.matches(emailPattern))) {
                    Toast.makeText(ChangeEmail.this, "Invalid email address!", Toast.LENGTH_SHORT).show();
                } else if (userConfirmPassword.equals("")) {
                    Toast.makeText(ChangeEmail.this, "Password is required!", Toast.LENGTH_SHORT).show();
                }

                // update email address
                else {
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userConfirmPassword);

                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sendEmailVerification();
                                            Toast.makeText(ChangeEmail.this, "Email changed!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChangeEmail.this, "Email not changed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    // send email verification link
    private void sendEmailVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseAuth.signOut();
                        Toast.makeText(ChangeEmail.this, "Verification mail has been sent!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ChangeEmail.this, MainActivity.class));
                    } else {
                        Toast.makeText(ChangeEmail.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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