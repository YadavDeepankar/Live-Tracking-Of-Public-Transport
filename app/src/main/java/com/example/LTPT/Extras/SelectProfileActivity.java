package com.example.LTPT.Extras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.LTPT.Admin.Adminlogin;
import com.example.LTPT.Driver.Driverlogin;
import com.example.LTPT.R;
import com.example.LTPT.User.UserDashBoard;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectProfileActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    AlertDialog.Builder builder;
    public String avname,avcode;
    public boolean dontgo=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);

        builder = new AlertDialog.Builder(this);
        if(!internetIsConnected())
        {
            builder.setMessage("YOU ARE NOT CONNECTED TO INTERNET.\n\nPLEASE CONNECT TO WIFI/MOBILE DATA.")
                    .setCancelable(false)
                    .setNegativeButton("close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                            System.exit(0);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Internet Status");
            alert.show();
        }
        else {
            getversion();
        }
    }
    private void getversion() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                checkversion((String) dataSnapshot.child("version").child("vcode").getValue(),(String) dataSnapshot.child("version").child("vname").getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    private void checkversion(String cvcode,String cvname) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            avname = packageInfo.versionName;
            avcode = String.valueOf(packageInfo.versionCode);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (TextUtils.equals(avcode,cvcode)&& TextUtils.equals(avname,cvname)){
            Toast.makeText(this, "latest version", Toast.LENGTH_SHORT).show();
            dontgo=true;
        }
        else
        {
            AlertDialog.Builder abuilder=new AlertDialog.Builder(SelectProfileActivity.this);
            abuilder.setMessage("YOU ARE RUNNING ON A OLD VERSION.\n\nPLEASE UPDATE TO LATEST VERSION.")
                    .setCancelable(false)
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                            System.exit(0);
                        }
                    });
            AlertDialog alert = abuilder.create();
            alert.setTitle("UPDATE APP ! ! !");
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
        if (dontgo) {startActivity(new Intent(this, Driverlogin.class));}
    }

    public void profile_user(View v) {
        if (dontgo){startActivity(new Intent(this, UserDashBoard.class));}
    }

    public void profile_admin(View v) {
        if (dontgo){startActivity(new Intent(this, Adminlogin.class));}
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

