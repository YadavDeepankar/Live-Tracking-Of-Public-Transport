package com.example.admin.trial13;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Adminlogin extends AppCompatActivity {
    private static final String TAG = "AdminActivity";
    EditText admin_email,admin_pass;
    Button admin_btn;
    private FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Auth=FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);
        admin_email=findViewById(R.id.admin_mail);
        admin_pass=findViewById(R.id.admin_pwd);
        admin_btn=findViewById(R.id.admin_button);
        Auth=FirebaseAuth.getInstance();

        admin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = admin_email.getText().toString().trim();
                String pass = admin_pass.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(Adminlogin.this, "please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    Toast.makeText(Adminlogin.this, "please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pass.length()<6)
                {
                    Toast.makeText(Adminlogin.this, "password length should be more than 6", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkmail(email)){
                    Auth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(Adminlogin.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(),AdminDashBoard.class));
                                        Toast.makeText(Adminlogin.this, "Admin Login success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Adminlogin.this, "Admin Login failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(Adminlogin.this, "You are not ADMIN !!!", Toast.LENGTH_SHORT).show();
                    admin_email.setText("");
                    admin_pass.setText("");
                    onRestart();
                }
            }
        });
    }

    private boolean checkmail(String email) {
        switch (email){
            case "admin@admin.com": return true;
        }
        return false;
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        startActivity(new Intent(Adminlogin.this,Adminlogin.class));
        finish();
    }

    public void admin_forgotpwd(View v) {
        startActivity(new Intent(this,ForgotPasswordActivity.class));
    }
}
