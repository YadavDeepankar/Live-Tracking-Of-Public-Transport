package com.example.LTPT.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LTPT.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddRoute extends AppCompatActivity {

    EditText jrtno;
    Spinner spin1;
    ListView lvrts;
    Button viewbtn;
    public boolean dontconform=true;

    ValueEventListener listener;
    ArrayAdapter<String> adapter,adapterList;
    long maxid=0;
    public ArrayList<String> rtArray,spindata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addroute);
        viewbtn=findViewById(R.id.vrtbtn);
        jrtno=findViewById(R.id.xmlrtno);
        spin1=findViewById(R.id.xmlspin1);
        lvrts=findViewById(R.id.rtlist);
        spindata=new ArrayList<>();
        rtArray=new ArrayList<>();
        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,spindata);
        spin1.setAdapter(adapter);
        retrievedata();

        viewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                if (checkrtname())
                    showListdata(jrtno.getText().toString().trim());
            }
        });
    }

    private boolean checkrtname() {
        if(TextUtils.isEmpty(jrtno.getText().toString().trim()))
        {
            Toast.makeText(AddRoute.this, "please enter route number", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }
    public void retrievedata() {
        spindata.clear();
        listener=FirebaseDatabase.getInstance().getReference("stops").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    spindata.add(item.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void showListdata(String rtnum) {
        adapterList=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,rtArray);
        lvrts.setAdapter(adapterList);

        rtArray.clear();
        FirebaseDatabase.getInstance().getReference("routes").child(rtnum).orderByValue().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    rtArray.add(item.getKey().toString());
                }
                adapterList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addstoptoroute(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);

        final String rtno = jrtno.getText().toString().trim();
        if(TextUtils.isEmpty(jrtno.getText().toString().trim())) {
            Toast.makeText(AddRoute.this, "please enter route number", Toast.LENGTH_SHORT).show();
            return ;
        }
        FirebaseDatabase.getInstance().getReference("routes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid=dataSnapshot.child(rtno).getChildrenCount();
                Toast.makeText(AddRoute.this, ""+maxid, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        showListdata(rtno);
        dontconform=false;
    }

    public void addconfirm(View view) {
        if (dontconform){
            Toast.makeText(this, "Please Click ADD ROUTE first!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            final String rtno = jrtno.getText().toString().trim();
            final String stname = spin1.getSelectedItem().toString();
            maxid+=1;
            FirebaseDatabase.getInstance().getReference("routes").child(rtno).child(stname).setValue(maxid)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddRoute.this, "route number : " + rtno + "\nstopname : " + stname+"\n added.", Toast.LENGTH_SHORT).show();
                            showListdata(rtno);
                        }
                    });
            dontconform=true;
        }
    }
}