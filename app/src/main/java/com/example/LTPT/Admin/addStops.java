package com.example.LTPT.Admin;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.LTPT.R;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.util.List;

public class addStops extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public LatLng mkloc;
    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    boolean dontgo=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stops);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    public void searchLocation(View view) {
        List<Address> addressList = null;
        EditText locationSearch = findViewById(R.id.editText1);
        String location = locationSearch.getText().toString().trim();
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address;
            address = addressList.get(0);
            mMap.clear();

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location).draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            mMap.addCircle(new CircleOptions().center(latLng).radius(1000).strokeWidth(5).clickable(false).visible(true));
            mkloc=latLng;
            dontgo=false;
        }
        else{
            Toast.makeText(this, "location does not exists", Toast.LENGTH_LONG).show();
            dontgo=true;
            startActivity(new Intent(addStops.this,addStops.class));
        }
    }

    public void addtofirebase(View v) {

        if (dontgo){
            Toast.makeText(this, "click on search first", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditText locationSearch = (EditText) findViewById(R.id.editText1);
        final String stname = locationSearch.getText().toString().toLowerCase();

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                Toast.makeText(addStops.this, "marker drag started", Toast.LENGTH_SHORT).show();
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                Toast.makeText(addStops.this, "marker drag ended", Toast.LENGTH_SHORT).show();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                mkloc=new LatLng(arg0.getPosition().latitude,arg0.getPosition().longitude);
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });

        Double stlng=mkloc.longitude;
        Double stlat=mkloc.latitude;
        LocationHelper st=new LocationHelper(stlng,stlat);
        FirebaseDatabase.getInstance().getReference("stops").child(stname).setValue(st).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                locationSearch.setText("");
                mMap.clear();
                Toast.makeText(addStops.this, "stop "+stname+" inserted ", Toast.LENGTH_SHORT).show();
            }
        });
        dontgo=true;
    }

}