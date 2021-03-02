package com.example.simpleloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class UpdateUserInformation extends AppCompatActivity {

    private ImageView userProfilePic;
    private EditText userFirstname, userLastname, userBirthdate, userEmail;
    String newUserFirstname, newUserLastname, newUserGender, newUserBirthdate, newUserEmail;
    private RadioButton male, female;
    private Button updateUserInfo;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_information);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userProfilePic = (ImageView)findViewById(R.id.ivViewProfilePic);
        userFirstname = (EditText)findViewById(R.id.etNewFirstName);
        userLastname = (EditText)findViewById(R.id.etNewLastname);
        male = (RadioButton)findViewById(R.id.rbMale);
        female = (RadioButton)findViewById(R.id.rbFemale);
        userBirthdate = (EditText)findViewById(R.id.etNewBirthdate);
        userEmail = (EditText)findViewById(R.id.etNewEmail);
        updateUserInfo = (Button)findViewById(R.id.btnUpdateInformation);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        final Calendar calendar = Calendar.getInstance();

        // retrieve image from firebase storage
        final StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(userProfilePic);
            }
        });

        // retrieve data from database storage
        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Person person = dataSnapshot.getValue(Person.class);

                userFirstname.setText(person.getFirstname());
                userLastname.setText(person.getLastname());
                userBirthdate.setText(person.getBirthdate());
                userEmail.setText(person.getEmail());

                if (person.getGender().equals("Male")) {
                    male.setChecked(true);
                } else {
                    female.setChecked(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateUserInformation.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // calendar
        userBirthdate.setOnClickListener(new View.OnClickListener(){

            int year, month, day;
            String DOB;

            @Override
            public void onClick(View view) {

                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateUserInformation.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DOB = (month+1)+"/"+day+"/"+year;
                        userBirthdate.setText(DOB);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        // update user information (firebase database)
        updateUserInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                newUserFirstname = userFirstname.getText().toString();
                newUserLastname = userLastname.getText().toString();
                newUserBirthdate = userBirthdate.getText().toString();
                newUserEmail = userEmail.getText().toString();

                if(male.isChecked()){ newUserGender = "Male"; }
                if(female.isChecked()){ newUserGender = "Female"; }

                Person person = new Person(newUserFirstname, newUserLastname, newUserGender, newUserBirthdate, newUserEmail);
                databaseReference.setValue(person);

                finish();
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