package com.example.simpleloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText oldPassword, newPassword, confirmNewPassword;
    private TextView newPasswordValidation;
    private Button changePassword;
    private FirebaseUser firebaseUser;
    private AuthCredential authCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        oldPassword = (EditText)findViewById(R.id.etOldPassword);
        newPassword = (EditText)findViewById(R.id.etNewPassword);
        confirmNewPassword = (EditText)findViewById(R.id.etConfirmPassword);
        newPasswordValidation = (TextView)findViewById(R.id.tvNewPasswordValidation);
        changePassword = (Button)findViewById(R.id.btnChangePass);

        // change password button
        changePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String userOldPassword = oldPassword.getText().toString();
                String userNewPassword = newPassword.getText().toString();
                String userConfirmNewPassword = confirmNewPassword.getText().toString();

                // validate user input
                if (userOldPassword.equals("")){
                    Toast.makeText(ChangePassword.this, "Old password is required!", Toast.LENGTH_SHORT).show();
                }
                else if (userNewPassword.equals("")){
                    Toast.makeText(ChangePassword.this, "New password is required!", Toast.LENGTH_SHORT).show();
                }
                else if (userNewPassword.length() < 7) {
                    Toast.makeText(ChangePassword.this, "Password is too short!", Toast.LENGTH_SHORT).show();
                }
                else if (!userNewPassword.equals(userConfirmNewPassword)){
                    newPasswordValidation.setText("Passwords don't match.");
                }

                // update password
                else{
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userOldPassword);

                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                firebaseUser.updatePassword(userConfirmNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(ChangePassword.this, "Password changed!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(ChangePassword.this, "Password not changed!", Toast.LENGTH_SHORT).show();
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