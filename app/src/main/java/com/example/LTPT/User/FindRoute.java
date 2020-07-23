package com.example.LTPT.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.LTPT.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindRoute extends AppCompatActivity {

    ListView ListView;
    Spinner spinstart,spinend;
    static String placeholderSource="SOURCE";
    static String placeholderDest="DESTINATION";

    int ssize;
    int counter=0;
    DatabaseReference myref;
    ValueEventListener listener;
    ArrayAdapter<String> RouteListAdapter,SourceAdapter,DestinationAdapter;
    ArrayList<String> RouteList,spinSource,spinDest,q1,q2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_route);

        spinstart=findViewById(R.id.xmlstart);
        spinend=findViewById(R.id.xmlend);
        ListView=findViewById(R.id.routelist);

        RouteList=new ArrayList<String>();
        spinSource=new ArrayList<String>();
        spinDest=new ArrayList<String>();
        q1=new ArrayList<String>();
        q2=new ArrayList<String>();

        SourceAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,spinSource);
        spinstart.setAdapter(SourceAdapter);

        DestinationAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,spinDest);
        spinend.setAdapter(DestinationAdapter);

        retrieveviewdatastops();


        RouteListAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,RouteList);
        ListView.setAdapter(RouteListAdapter);
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(FindRoute.this, RetrieveMapActivity.class);
                String selected = (String) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("route",selected);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private void retrieveviewdatastops() {
        spinSource.clear();
        spinDest.clear();
        spinSource.add(placeholderSource);
        spinDest.add(placeholderDest);
        listener=FirebaseDatabase.getInstance().getReference("stops").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    spinSource.add(item.getKey());
                    spinDest.add(item.getKey());
                }
                SourceAdapter.notifyDataSetChanged();
                DestinationAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    public void Show_Routes(View view) {
        RouteListAdapter.clear();
        myref = FirebaseDatabase.getInstance().getReference("routes");
        listener = myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    RouteList.add(item.getKey());
                }
                RouteListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void Search_Routes(View view) {
        {
            final String startstop=spinstart.getSelectedItem().toString();
            final String endstop=spinend.getSelectedItem().toString();
            RouteListAdapter.clear();
            myref=FirebaseDatabase.getInstance().getReference("routes");
            RouteList.clear();

            if (startstop.equals(endstop))
            {
                Toast.makeText(this, "Please Select Different Stops !!!", Toast.LENGTH_SHORT).show();
            }
            else if (startstop.equals(placeholderSource)||endstop.equals(placeholderDest))
            {
                Toast.makeText(this, "Please Select a Stop !!!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                FirebaseDatabase.getInstance().getReference("routes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            String rtnum=item.getKey();
                            if (dataSnapshot.child(rtnum).hasChild(startstop) &&
                                    dataSnapshot.child(rtnum).hasChild(endstop)){
                                RouteList.add(rtnum);
                            }
                        }
                        RouteListAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }
    }
}
