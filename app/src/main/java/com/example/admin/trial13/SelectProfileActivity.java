package com.example.admin.trial13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectProfileActivity extends AppCompatActivity {

    public void driver(View v) {
        startActivity(new Intent(this,login.class));
    }

    public void user(View v) {
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);
    }

}
