package com.example.admin.trial13;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void nextactivity(View v) {
        startActivity(new Intent(this,MapsActivity.class));
    }
    public void btnLocation(View v) {
        startActivity(new Intent(this,RetrieveMapActivity.class));
    }
    public void btn3(View view) {
        startActivity(new Intent(this,addStops.class));
    }
    public void btn4(View view) {
        startActivity(new Intent(this,addroute.class));
    }
    public void btn5(View view) {
        startActivity(new Intent(this,uploadprofile.class));
    }
//    public void btn6(View view) { startActivity(new Intent(this,signup.class));}
    public void LOGOUT(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,SelectProfileActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Double click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 400);
// empty so nothing happens
    }
}
