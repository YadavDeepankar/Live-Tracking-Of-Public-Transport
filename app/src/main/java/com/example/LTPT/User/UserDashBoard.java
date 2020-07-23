package com.example.LTPT.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.LTPT.R;
import com.example.LTPT.Extras.SOS;

public class UserDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);
    }
    public void u_viewroute(View v) {
        startActivity(new Intent(this, FindRoute.class));
    }

    public void u_sos(View v) {
        startActivity(new Intent(this, SOS.class));
    }

    public void u_feedback(View v) {
        startActivity(new Intent(this,FeedBack.class));
    }

}
