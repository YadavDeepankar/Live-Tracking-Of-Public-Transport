package com.example.admin.trial13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.admin.trial13.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    EditText jremail,jrpass,jrcpass,jrname,jrphno,jrbus;
    ProgressBar pgb;
    Button rbtn;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        jremail=findViewById(R.id.xmlremail);
        jrname=findViewById(R.id.xmlrname);
        jrpass=findViewById(R.id.xmlrpass);
        jrcpass=findViewById(R.id.xmlrcpass);
        jrphno=findViewById(R.id.xmlrph);
        jrbus=findViewById(R.id.xmlrbus);
        rbtn=findViewById(R.id.xmlrbtn);
        pgb=findViewById(R.id.xmlrpgb);

        databaseReference=FirebaseDatabase.getInstance().getReference("Driver");

        firebaseAuth=FirebaseAuth.getInstance();
        rbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                final String fname = jrname.getText().toString().trim();
                final String email = jremail.getText().toString().trim();
                final String busno=jrbus.getText().toString().trim();
                final String phno=jrphno.getText().toString().trim();
                String pass = jrpass.getText().toString().trim();
                String cpass = jrcpass.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(signup.this, "please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    Toast.makeText(signup.this, "please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(busno))
                {
                    Toast.makeText(signup.this, "please enter bus number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phno))
                {
                    Toast.makeText(signup.this, "please enter phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(cpass))
                {
                    Toast.makeText(signup.this, "please enter c password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pass.length()<6&&cpass.length()<6)
                {
                    Toast.makeText(signup.this, "password length should be more than 6", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pass.equals(cpass))
                {
                    Toast.makeText(signup.this, "password not matches", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(phno.length()==10))
                {
                    Toast.makeText(signup.this, "phone number should be of 10", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(busno.length()==10))
                {
                    Toast.makeText(signup.this, "bus number should be of 10", Toast.LENGTH_SHORT).show();
                    return;
                }
                pgb.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pgb.setVisibility(View.GONE);
                                    if (task.isSuccessful())
                                    {
                                        SignupHelper info= new SignupHelper(fname,email,phno,busno);
                                        FirebaseDatabase.getInstance().getReference("Driver")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(info)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        startActivity(new Intent(getApplicationContext(),AdminDashBoard.class));
                                                        Toast.makeText(signup.this, "signup success", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        Toast.makeText(signup.this, "signup failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
        });
    }
}
