package com.example.nexochatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.nexochatapp.model.UserModel;
import com.example.nexochatapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class LoginUserName extends AppCompatActivity {
    EditText userNameInput;
    Button letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);

        userNameInput = findViewById(R.id.login_userName);
        letMeInBtn = findViewById(R.id.login_letMeIn_Button);
        progressBar = findViewById(R.id.login_ProgressBar);
       phoneNumber = getIntent().getExtras().getString("phone");
        getUserName();

        letMeInBtn.setOnClickListener((view->{
            setUserName();
        }));


    }
    void setUserName(){

        String userName = userNameInput.getText().toString();
        if(userName.isEmpty()||userName.length()<3){
        userNameInput.setError("Username should be at least 3 characters");
        return;
        }
        setInProgress(true);
        if(userModel!=null){
            userModel.setUserName(userName);
        }else{
            userModel = new UserModel(phoneNumber,userName, Timestamp.now(), FirebaseUtil.currentUserId().toString());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            setInProgress(false);
            if(task.isSuccessful()){
                Intent intent = new Intent(LoginUserName.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            }
        });
    }
    void getUserName(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            setInProgress(false);
            if(task.isSuccessful()){
                 userModel = task.getResult().toObject(UserModel.class);
                if(userModel!=null){
                    userNameInput.setText(userModel.getUserName());
                }
            }
            }
        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}