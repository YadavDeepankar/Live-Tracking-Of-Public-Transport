package com.example.admin.trial13;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RouteViewActivity extends AppCompatActivity {

    ListView ListView;
    Spinner spinv;
    DatabaseReference databaseReference,myref;
    ValueEventListener listener;
    ArrayAdapter<String> adapter,ada2;
    ArrayList<String> spinvdata,spinv2data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_view);
        spinv=findViewById(R.id.xmlspinview);
        ListView=findViewById(R.id.LVRT);
        databaseReference= FirebaseDatabase.getInstance().getReference("routes");
        spinvdata=new ArrayList<String>();
        spinv2data=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,spinvdata);
        spinv.setAdapter(adapter);
        retrieveviewdata();
        ada2=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,spinv2data);
        ListView.setAdapter(ada2);
        spinv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                if (spinv.getSelectedItem().toString()=="Tap to Select Route"){
                }
                else
                {
                    String items=spinv.getSelectedItem().toString();
                    ClickMe(items);}
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
    }

    private void retrieveviewdata() {
        spinvdata.clear();
        spinvdata.add("Tap to Select Route");
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
    }

    public void ClickMe(String text) {

        myref=FirebaseDatabase.getInstance().getReference("routes").child(text);//Enter Child Value
        spinv2data.clear();
        myref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String value=dataSnapshot.getKey().toString();
                spinv2data.add(value);
                ada2.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
