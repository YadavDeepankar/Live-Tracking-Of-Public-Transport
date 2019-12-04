package com.example.admin.trial13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

        private EditText UserMail;
        private Button UserPwd;
        private FirebaseAuth auth;
        private ProgressBar progressBar;
        Toolbar toolbar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_forgot_password);
            UserMail = (EditText) findViewById(R.id.EnterEmail);
            UserPwd = (Button) findViewById(R.id.ForgotPwd);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            auth = FirebaseAuth.getInstance();

            UserPwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = UserMail.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            });
        }
}
