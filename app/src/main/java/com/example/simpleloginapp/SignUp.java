package com.example.simpleloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.Calendar;

public class SignUp extends AppCompatActivity {

    private EditText firstname, lastname, birthdate, email, password;
    String userFirstname, userLastname, userGender, userBirthdate, userEmail, userPassword;
    private RadioButton male, female;
    private Button signup;
    private TextView login;
    private ImageView profilePic;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static int PICK_IMAGE = 123;
    Uri imagePath;

    private void setupUIViews() {
        profilePic = (ImageView) findViewById(R.id.ivProfilePic);
        firstname = (EditText) findViewById(R.id.etFirstname);
        lastname = (EditText) findViewById(R.id.etLastname);
        male = (RadioButton) findViewById(R.id.rbMale);
        female = (RadioButton) findViewById(R.id.rbFemale);
        birthdate = (EditText) findViewById(R.id.etBirthdate);
        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        signup = (Button) findViewById(R.id.btnSignup);
        login = (TextView) findViewById(R.id.tvLogin);
    }

    // validate and get user input
    private boolean validate() {

        boolean result = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";

        userFirstname = firstname.getText().toString();
        userLastname = lastname.getText().toString();
        userBirthdate = birthdate.getText().toString();
        userEmail = email.getText().toString().trim();
        userPassword = password.getText().toString();

        if(male.isChecked()){ userGender = "Male"; }
        if(female.isChecked()){ userGender = "Female"; }

        if (imagePath == null){
            Toast.makeText(this, "Please upload an image!", Toast.LENGTH_SHORT).show();
        }
        else if (userFirstname.isEmpty()){
            firstname.setError("First name is required!");
        }
        else if (userLastname.isEmpty()){
            lastname.setError("Last name is required!");
        }
        else if (userBirthdate.isEmpty()) {
            birthdate.setError("Birth date is required!");
        }
        else if (userEmail.isEmpty()) {
            email.setError("Email address is required!");
        }
        else if (!(userEmail.matches(emailPattern))) {
            email.setError("Invalid email address!");
        }
        else if (userPassword.isEmpty()) {
            password.setError("Password is required!");
        }
        else if (userPassword.length() < 7) {
            password.setError("Password is too short!");
        } else {
            result = true;
        }
        return result;
    }

    // get image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profilePic.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        final Calendar calendar = Calendar.getInstance();

        // click and select image
        profilePic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

        // calendar
        birthdate.setOnClickListener(new View.OnClickListener(){

            int year,month,day;
            String DOB;

            @Override
            public void onClick(View view) {

                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DOB = (month+1)+"/"+day+"/"+year;
                        birthdate.setText(DOB);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        // sign up button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()) {

                    String userEmail = email.getText().toString().trim();
                    String userPassword = password.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                                Toast.makeText(SignUp.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUp.this, "Email address is already taken!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        // login button (if account exists)
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
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
                        sendUserData();
                        firebaseAuth.signOut();
                        Toast.makeText(SignUp.this, "Verification mail has been sent!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(SignUp.this, MainActivity.class));
                    } else {
                        Toast.makeText(SignUp.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // send and store user registration to firebase database and storage
    private void sendUserData(){
        // firebase database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        // firebase storage
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic");
        UploadTask uploadTask = imageReference.putFile(imagePath);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SignUp.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
            }
        });
        Person person = new Person (userFirstname, userLastname, userGender, userBirthdate, userEmail);
        myRef.setValue(person);
    }

}