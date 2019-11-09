package com.example.admin.trial13;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RetrieveMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mLocation;

    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Current Location");

        ValueEventListener listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mMap.clear();

                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                LatLng location = new LatLng(latitude,longitude);


                Toast.makeText(RetrieveMapActivity.this, "your current location is :"+latitude+" and "+longitude, Toast.LENGTH_SHORT).show();

//                mMap.addMarker(new MarkerOptions().position(location).title(getCOmpleteAddress(latitude,longitude)));
                mMap.addMarker(new MarkerOptions().position(location).title("ANVT-GZB").icon(BitmapDescriptorFactory.fromResource(R.drawable.index)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14F));




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
