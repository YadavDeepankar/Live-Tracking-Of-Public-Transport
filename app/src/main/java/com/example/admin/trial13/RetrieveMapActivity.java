package com.example.admin.trial13;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class RetrieveMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    FirebaseDatabase database;
    DatabaseReference userLocationsRef;



    Map<String, Marker> mNamedMarkers = new HashMap<String,Marker>();

    ChildEventListener markerUpdateListener = new ChildEventListener() {

        /**
         * Adds each existing/new location of a marker.
         *
         * Will silently update any existing markers as needed.
         * @param dataSnapshot  The new location data
         * @param previousChildName  The key of the previous child event
         */
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();
//            Log.d(TAG, "Adding location for '" + key + "'");

            Double lng = dataSnapshot.child("Location").child("longitude").getValue(Double.class);
            Double lat = dataSnapshot.child("Location").child("latitude").getValue(Double.class);
            LatLng location = new LatLng(lat, lng);

            Marker marker = mNamedMarkers.get(key);

            if (marker == null) {
                MarkerOptions options = getMarkerOptions(key);
                marker = mMap.addMarker(options.position(location));
                mNamedMarkers.put(key, marker);
            } else {
                // This marker-already-exists section should never be called in this listener's normal use, but is here to handle edge cases quietly.
                // TODO: Confirm if marker title/snippet needs updating.
                marker.setPosition(location);
            }
        }

        /**
         * Updates the location of a previously loaded marker.
         *
         * Will silently create any missing markers as needed.
         * @param dataSnapshot  The new location data
         * @param previousChildName  The key of the previous child event
         */
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();
//            Log.d(TAG, "Location for '" + key + "' was updated.");

            Double lng = dataSnapshot.child("Location").child("longitude").getValue(Double.class);
            Double lat = dataSnapshot.child("Location").child("latitude").getValue(Double.class);
            LatLng location = new LatLng(lat, lng);

            Marker marker = mNamedMarkers.get(key);

            if (marker == null) {
                // This null-handling section should never be called in this listener's normal use, but is here to handle edge cases quietly.
//                Log.d(TAG, "Expected existing marker for '" + key + "', but one was not found. Added now.");
                MarkerOptions options = getMarkerOptions(key); // TODO: Read data from database for this marker (e.g. Name, Driver, Vehicle type)
                marker = mMap.addMarker(options.position(location));
                mNamedMarkers.put(key, marker);
            } else {
                // TODO: Confirm if marker title/snippet needs updating.
                marker.setPosition(location);
            }
        }

        /**
         * Removes the marker from its GoogleMap instance
         * @param dataSnapshot  The removed data
         */
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
//            Log.d(TAG, "Location for '" + key + "' was removed.");

            Marker marker = mNamedMarkers.get(key);
            if (marker != null)
                marker.remove();
        }

        /**
         * Ignored.
         * @param dataSnapshot  The moved data
         * @param previousChildName  The key of the previous child event
         */
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            // Unused
//            Log.d(TAG, "Priority for '" + dataSnapshot.getKey() "' was changed.");
        }

        /**
         * Error handler when listener is canceled.
         * @param databaseError  The error object
         */
        @Override
        public void onCancelled(DatabaseError databaseError) {
//            Log.w(TAG, "markerUpdateListener:onCancelled", databaseError.toException());
            Toast.makeText(RetrieveMapActivity.this, "Failed to load location markers.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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

        database = FirebaseDatabase.getInstance();
        userLocationsRef = database.getReference("Users");

        userLocationsRef.addChildEventListener(markerUpdateListener);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }



    private MarkerOptions getMarkerOptions(String key) {

        // TODO: Read data from database for the given marker (e.g. Name, Driver, Vehicle type)
        return new MarkerOptions().title(key).snippet(key).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
               //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
}
