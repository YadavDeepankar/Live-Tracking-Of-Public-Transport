package com.example.admin.trial13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RouteViewActivity extends AppCompatActivity {

    Spinner spinv,spinv2;
    DatabaseReference databaseReference,myref;
    ValueEventListener listener,listener2;
    ArrayAdapter<String> adapter,ada2;
    ArrayList<String> spinvdata,spinv2data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_view);
        spinv=findViewById(R.id.xmlspinview);
        databaseReference= FirebaseDatabase.getInstance().getReference("routes");
        spinvdata=new ArrayList<String>();
        spinv2data=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,spinvdata);
        spinv.setAdapter(adapter);
        retrieveviewdata();
//        ada2=new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_dropdown_item,spinvdata);
//        spinv2.setAdapter(ada2);
        //itemclick();
    }

    private void retrieveviewdata() {
        spinvdata.clear();
        listener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    spinvdata.add(item.getKey().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        //  itemclick();
    }}
