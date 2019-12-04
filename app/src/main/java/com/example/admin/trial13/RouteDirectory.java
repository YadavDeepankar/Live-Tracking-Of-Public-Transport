package com.example.admin.trial13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RouteDirectory extends AppCompatActivity {

    Spinner spin1;
    EditText jrtno;
    ListView lvrts;
    TextView textView;
    DatabaseReference databaseReference;

    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spindata;
    public ArrayList<String> rtArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addroute);
        spin1=findViewById(R.id.xmlspin1);
        jrtno=findViewById(R.id.xmlrtno);
        lvrts=findViewById(R.id.rtlist);
        databaseReference= FirebaseDatabase.getInstance().getReference("stops");

       // textView=(TextView)findViewById(R.id.txtlist);
        spindata=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,spindata);
        spin1.setAdapter(adapter);
        retrievedata();
    }

    public void retrievedata()
    {
        spindata.clear();
        //adapter.notifyDataSetChanged();
        listener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    //spindata.add(item.getValue().toString());
                    spindata.add(item.getKey().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addstoptoroute(View view) {
        final String rtno = jrtno.getText().toString().trim();
        final String stname = spin1.getSelectedItem().toString();
        //Toast.makeText(this, "route number "+rtno+" stopname "+stname, Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference("routes").child(rtno).child(stname).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RouteDirectory.this, "route number " + rtno + " stopname " + stname+" added.", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
