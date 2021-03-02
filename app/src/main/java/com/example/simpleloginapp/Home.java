package com.example.simpleloginapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth= FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // logout (firebase auth)
    private void logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(Home.this, MainActivity.class));
    }

    // menu item
    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem){
         switch (menuItem.getItemId()){
             // log out
             case R.id.logoutMenu: {
                 logout();
                 break;
             }
             // profile
             case R.id.profileMenu: {
                 startActivity(new Intent(Home.this, UserProfile.class));
                 break;
             }
             // about me
             case R.id.aboutMeMenu: {
                 Toast.makeText(Home.this, "Android Developer:\nMichael Jude O. Jacinto\nApp Version:\n1.0", Toast.LENGTH_LONG).show();
                 break;
             }
         }
         return super.onOptionsItemSelected(menuItem);
    }

}