package com.example.simpleloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

public class UserProfile extends AppCompatActivity {

    ImageView userProfilePic;
    private TextView fullname, email, gender, birthdate, age;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        userProfilePic = (ImageView) findViewById(R.id.ivUserProfilePic);
        fullname = (TextView) findViewById(R.id.tvUserProfileFullname);
        email = (TextView) findViewById(R.id.tvUserProfileEmail);
        gender = (TextView) findViewById(R.id.tvUserProfileGender);
        birthdate = (TextView) findViewById(R.id.tvUserProfileBirthdate);
        age = (TextView) findViewById(R.id.tvUserProfileAge);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        Calendar calendar = Calendar.getInstance();

        // retrieve image from firebase storage
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(userProfilePic);
            }
        });

        // retrieve data from firebase database
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Person person = dataSnapshot.getValue(Person.class);

                fullname.setText("Name: " + person.getFirstname() + " " + person.getLastname());
                email.setText("Email: " + person.getEmail());
                gender.setText("Gender: " + person.getGender());
                birthdate.setText("Birthdate: " + person.getBirthdate());

                String birthday = birthdate.getText().toString();     // text view converts to string
                String[] date = birthday.split("/");          // string method and array
                int currentYear = calendar.get(Calendar.YEAR);       // get the current year
                int getAge = currentYear - Integer.parseInt(date[2]); // current year minus birth year
                age.setText("Age: " + getAge + " years old");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // refresh or reload updated profile picture
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                StorageReference storageReference = firebaseStorage.getReference();
                storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerCrop().into(userProfilePic);
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profilemenu, menu);
        return true;
    }

    // show delete dialog
    private void showDialog() {
        DeleteAccount deleteAccount = new DeleteAccount();
        deleteAccount.show(getSupportFragmentManager(), "delete dialog");
    }

    // profile menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            // back button (upper left)
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            // edit
            case R.id.editMenu: {
                startActivity(new Intent(UserProfile.this, UpdateUserInformation.class));
                break;
            }
            // change profile picture
            case R.id.changeProfilePicMenu: {
                startActivity(new Intent(UserProfile.this, UpdateProfilePicture.class));
                break;
            }
            // change password
            case R.id.changePasswordMenu: {
                startActivity(new Intent(UserProfile.this, ChangePassword.class));
                break;
            }
            // change email
            case R.id.changeEmailMenu: {
                startActivity(new Intent(UserProfile.this, ChangeEmail.class));
                break;
            }
            // delete account
            case R.id.deleteAccountMenu: {
                showDialog();
                break;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

}