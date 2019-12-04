package com.example.admin.trial13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectProfileActivity extends AppCompatActivity {

    public void profile_driver(View v) {
        startActivity(new Intent(this, Driverlogin.class));
    }

    public void profile_user(View v) {
        startActivity(new Intent(this,UserDashBoard.class));
    }

    public void profile_admin(View v) {
        startActivity(new Intent(this,Adminlogin.class));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);
    }

}
