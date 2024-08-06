
package com.example.nexochatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumber extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);
        countryCodePicker = findViewById(R.id.login_countryCode);
        phoneInput = findViewById(R.id.login_mobileNumber);
        sendOtpBtn = findViewById(R.id.login_SendOtpButton);
        progressBar = findViewById(R.id.login_ProgressBar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener((v)->{
               if(!countryCodePicker.isValidFullNumber()){
                        phoneInput.setError("Phone number not valid");
                        return;
               }
            Intent intent = new Intent(LoginPhoneNumber.this,LoginOtp.class);
               intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
               startActivity(intent);
        });

    }
}