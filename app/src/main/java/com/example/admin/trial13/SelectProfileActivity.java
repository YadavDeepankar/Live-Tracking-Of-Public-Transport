package com.example.admin.trial13;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class SelectProfileActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);

        builder = new AlertDialog.Builder(this);
        if(!internetIsConnected())
        {
            builder.setMessage("YOU ARE NOT CONNECTED TO INTERNET.\n\nPLEASE CONNECT TO WIFI/MOBILE DATA.")
                    .setCancelable(true)
                    .setNegativeButton("close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Internet Status");
            alert.show();
        }

    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public void profile_driver(View v) {
        startActivity(new Intent(this, Driverlogin.class));
    }

    public void profile_user(View v) {
        startActivity(new Intent(this, UserDashBoard.class));
    }

    public void profile_admin(View v) {
        startActivity(new Intent(this, Adminlogin.class));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            startActivity(new Intent(SelectProfileActivity.this,SelectProfileActivity.class));
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
