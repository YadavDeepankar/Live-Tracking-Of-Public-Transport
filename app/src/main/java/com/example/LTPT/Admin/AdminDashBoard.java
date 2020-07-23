package com.example.LTPT.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.LTPT.User.FindRoute;
import com.example.LTPT.R;
import com.example.LTPT.Extras.SelectProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);
    }

    public void a_addroute(View v) {
        startActivity(new Intent(this,addStops.class));
    }
    public void a_managedriver(View v) {
        startActivity(new Intent(this, signup.class));
    }
    public void a_viewroute(View v) {
        startActivity(new Intent(this, AddRoute.class));
    }

    public void a_LOGOUT(View v) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SelectProfileActivity.class));
    }

    public void a_view1(View view) {
            startActivity(new Intent(this, FindRoute.class));

    }

    @Override
    public void onBackPressed()
    {

    }
}
