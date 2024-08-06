package com.example.nexochatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.nexochatapp.utils.FirebaseUtil;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseUtil.isLoggedIn()){
                    startActivity(new Intent(splashScreen.this, MainActivity.class));

                }else {
                    startActivity(new Intent(splashScreen.this, LoginPhoneNumber.class));
                }
                finish();
            }
        },1000);

    }
}