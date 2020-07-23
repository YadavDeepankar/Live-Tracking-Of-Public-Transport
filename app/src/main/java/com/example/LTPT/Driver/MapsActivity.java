package com.example.LTPT.Driver;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.LTPT.Admin.LocationHelper;
import com.example.LTPT.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private ValueEventListener listener;
    private long UPDATE_INTERVAL = 200;
    private long FASTEST_INTERVAL = 500;
    private LocationManager locationManager;
    private LatLng latLng;
    String user,Route;
    EditText AlertText;
    Date curdate;
    private boolean isPermission;




    @Override
    protected void onCreate(Bundle savedInstanceRoute) {
        super.onCreate(savedInstanceRoute);
        setContentView(R.layout.activity_maps);
        AlertText= findViewById(R.id.xmlnotice);
        if(AlertText.equals(""))
            AlertText.setText("Bus on Time");

        Bundle bundle = getIntent().getExtras();
        Route = bundle.getString("route");
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        curdate = Calendar.getInstance().getTime();
        FirebaseDatabase.getInstance().getReference("DriverAvail").child(Route).child(user).child("Alert").setValue("Bus on Time");
        FirebaseDatabase.getInstance().getReference("Log").child(user).child(curdate.toString()).setValue("Journey Started");

        if(requestSinglePermission()){

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            checkLocation();
        }
    }

    private boolean checkLocation() {

        if(!isLocationEnabled()){
            showAlert();
        }
        return isLocationEnabled();

    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean requestSinglePermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        isPermission = true;
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            isPermission = false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }


                }).check();

        return isPermission;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(latLng!=null){
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14F));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LocationHelper helper = new LocationHelper(
                location.getLongitude(),
                location.getLatitude()
        );

        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("DriverAvail").child(Route).child(user).child("Location")
                .setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
//                    Toast.makeText(MapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MapsActivity.this, "Location Not Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mGoogleApiClient !=null){
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onStop() {
        curdate = Calendar.getInstance().getTime();
        FirebaseDatabase.getInstance().getReference("Log").child(user).child(curdate.toString()).setValue("Journey Ended");
        super.onStop();

        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                Toast.makeText(MapsActivity.this, "BROADCASTING STARTED", Toast.LENGTH_SHORT).show();
        }

        switch (v.getId()){
            case R.id.stop:
                Toast.makeText(MapsActivity.this, "BROADCASTING STOPPED", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference("DriverAvail").child(Route).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                startActivity(new Intent(getApplicationContext(), DriverDashBoard.class));
        }
    }

    @Override
    public void onBackPressed()
    {

    }

    public void notify_user(View view) {
        String Notification=(String) AlertText.getText().toString().trim();
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        curdate = Calendar.getInstance().getTime();
        if (Notification.isEmpty())
        {
            Notification="Be patient, Bus is on time !!";
        }
        FirebaseDatabase.getInstance().getReference("Log").child(user).child(curdate.toString()).setValue(Notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MapsActivity.this, "Alert Delivered", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MapsActivity.this, "Alert Not Delivered", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FirebaseDatabase.getInstance().getReference("DriverAvail").child(Route).child(user).child("Alert").setValue(Notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
//                    Toast.makeText(MapsActivity.this, "Alert Delivered", Toast.LENGTH_SHORT).show();
                }
                else{
//                    Toast.makeText(MapsActivity.this, "Alert Not Delivered", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertText.setText("");
    }

}