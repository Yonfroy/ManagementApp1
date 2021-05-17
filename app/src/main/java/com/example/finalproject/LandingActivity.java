package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class LandingActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    Button logOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
//
//    public void profile(View view){
//        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//        finish();
//    }

    public void profile2(View view){
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        finish();
    }
}