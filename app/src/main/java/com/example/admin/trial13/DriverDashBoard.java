package com.example.admin.trial13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DriverDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dash_board);
    }

    public void broadcast(View v) {
        startActivity(new Intent(this,MapsActivity.class));
    }
    public void sos(View v) {
        startActivity(new Intent(this,SOS.class));
    }
    public void LOGOUT(View v) {

        FirebaseDatabase.getInstance().getReference("DriverAvail").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Location").removeValue();
        FirebaseDatabase.getInstance().getReference("DriverAvail").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("routeno").removeValue();

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,SelectProfileActivity.class));

    }

    @Override
    public void onBackPressed()
    {

    }
}
