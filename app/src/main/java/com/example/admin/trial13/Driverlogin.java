package com.example.admin.trial13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Driverlogin extends AppCompatActivity {
    private static final String TAG = "DriverActivity";
    EditText driver_email,driver_pass;
    Button driver_btn;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(Driverlogin.this, DriverDashBoard.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverlogin);
        driver_email=findViewById(R.id.driver_email);
        driver_pass=findViewById(R.id.driver_pass);
        driver_btn=findViewById(R.id.driver_btn);
        firebaseAuth=FirebaseAuth.getInstance();

        driver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = driver_email.getText().toString().trim();
                String pass = driver_pass.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(Driverlogin.this, "please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    Toast.makeText(Driverlogin.this, "please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pass.length()<6)
                {
                    Toast.makeText(Driverlogin.this, "password length should be more than 6", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(Driverlogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(),DriverDashBoard.class));
                                    Toast.makeText(Driverlogin.this, "Driver login success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Driverlogin.this, "Driver login failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
    public void driver_forgotpwd(View v) {
        startActivity(new Intent(this,ForgotPasswordActivity.class));
   }
}
