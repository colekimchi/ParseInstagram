package com.example.parseinstagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.parseinstagram.fragments.ComposeFragment;
import com.example.parseinstagram.fragments.ProfileFragment;
import com.example.parseinstagram.fragments.TimelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements ProfileFragment.OnItemSelectedListener {

    private final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        //check if a user is already logged in
        checkForUser();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment composeFragment = new ComposeFragment();
        final Fragment timelineFragment = new TimelineFragment();
        final Fragment profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment targetFragment = new Fragment();
                switch(menuItem.getItemId()){
                    case R.id.action_home:
                        targetFragment = timelineFragment;
                        break;
                    case R.id.action_compose:
                        targetFragment = composeFragment;
                        break;
                    case R.id.action_profile:
                        targetFragment = profileFragment;
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, targetFragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    public void logout(){
        ParseUser.logOut();
        goLoginActivity();
    }

    private void checkForUser(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
        } else {
           goLoginActivity();
        }
    }

    private void goLoginActivity(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
