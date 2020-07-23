package com.example.LTPT.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.LTPT.R;
import com.example.LTPT.Extras.SOS;
import com.example.LTPT.Extras.SelectProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DriverDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dash_board);
    }

    public void broadcast(View v) {
        startActivity(new Intent(this, ChooseRoute.class));
    }
    public void sos(View v) {
        startActivity(new Intent(this, SOS.class));
    }
    public void LOGOUT(View v) {

        FirebaseDatabase.getInstance().getReference("DriverAvail").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SelectProfileActivity.class));

    }

    @Override
    public void onBackPressed()
    {

    }
}
