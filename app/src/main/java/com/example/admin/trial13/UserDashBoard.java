package com.example.admin.trial13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);
    }

    public void u_locate(View v) {
        startActivity(new Intent(this,RetrieveMapActivity.class));
    }
    public void u_viewroute(View v) {
        startActivity(new Intent(this, RouteDirectory.class));
    }
    public void u_share(View v) {
        startActivity(new Intent(this,ShareActivity.class));
    }
    public void u_sos(View v) {
        startActivity(new Intent(this,SOS.class));
    }

}
