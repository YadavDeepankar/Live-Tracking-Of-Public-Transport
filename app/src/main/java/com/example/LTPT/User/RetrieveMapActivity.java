package com.example.LTPT.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.LTPT.R;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RetrieveMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    FirebaseDatabase database;
    DatabaseReference userLocationsRef;
    private boolean isPermission;
    private LocationManager locationManager;
    private LocationManager mLocationManager;
    LatLng cloc;
    String Route;
    Map<String, Marker> mNamedMarkers = new HashMap<String, Marker>();
    public ArrayList<String> Alert=new ArrayList<String>();
    public ArrayList<String> DriverName=new ArrayList<String>();
    public ArrayList<String> BusNumber=new ArrayList<String>();
    public ArrayList<String> DriverUid=new ArrayList<String>();
    Button btn1;
    public ArrayList<String> StopName=new ArrayList<>();
    public ArrayList<LatLng> StopMarker=new ArrayList<>();
    MyListAdapter adapter1;
    ListView listView;
    int btncounter=0;

    ChildEventListener markerUpdateListener = new ChildEventListener() {

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

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            // Unused
//            Log.d(TAG, "Priority for '" + dataSnapshot.getKey() "' was changed.");
        }

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

        listView = findViewById(R.id.listView1);
        btn1=findViewById(R.id.rtlistbtn);
        Bundle bundle = getIntent().getExtras();
        Route = bundle.getString("route");
        btn1.setText(Route);

        fetchstops(Route);
        adapter1=new MyListAdapter(RetrieveMapActivity.this, StopName);
        listView.setAdapter(adapter1);
        final ViewGroup.LayoutParams layoutparams = listView.getLayoutParams();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(btncounter){
                    case 0 : listView.setVisibility(View.VISIBLE);
                        btncounter=1;
                        if(adapter1.getCount() < 5){
                            layoutparams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            layoutparams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            listView.setLayoutParams(layoutparams);
                        }
                        break;
                    case 1 : listView.setVisibility(View.GONE); btncounter=0; break;
                }
            }
        });
        if (requestSinglePermission()) {

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fetchmarker(StopName.get(position));
            }
        });
    }

    private void fetchmarker(final String stname12) {
        FirebaseDatabase.getInstance().getReference("stops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double lng = dataSnapshot.child(stname12).child("longitude").getValue(Double.class);
                Double lat = dataSnapshot.child(stname12).child("latitude").getValue(Double.class);
                LatLng loc=new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(loc).title(stname12).snippet(null).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                mMap.addCircle(new CircleOptions().center(loc).radius(500).strokeWidth(5).clickable(false).visible(true));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void fetchstops(String rtname) {
        StopName.clear();
        FirebaseDatabase.getInstance().getReference("routes").child(rtname).orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item:dataSnapshot.getChildren()){
                    StopName.add(item.getKey());
                    fetchmarker(item.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private boolean checkLocation() {

        if (!isLocationEnabled()) {
            showAlert();
        }
        return isLocationEnabled();

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        database = FirebaseDatabase.getInstance();
        userLocationsRef = database.getReference("DriverAvail").child(Route);
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        retrievedata(Route);
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("DriverAvail" + "/" + Route)) {
                    userLocationsRef.addChildEventListener(markerUpdateListener);
                } else {
                    Toast.makeText(RetrieveMapActivity.this, "No services found in " +Route.toUpperCase()+ " at this moment\n Please try after some time", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RetrieveMapActivity.this, "NO SERVICE AVAILABLE IN YOUR AREA AT THIS MOMENT, TRY AFTER SOME TIME", Toast.LENGTH_LONG).show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                float[] result = new float[3];
                double distance;
                //speed in meter/sec
                double speedd = 11.11;
                double timme;
                Location.distanceBetween(cloc.latitude, cloc.longitude,
                        marker.getPosition().latitude, marker.getPosition().longitude, result);
                distance = (double) result[0];
                distance = distance + (distance * 0.25);
                timme = (distance / speedd);
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(RetrieveMapActivity.this);
                if (marker.getSnippet()!=null){
                    int ppos=DriverUid.indexOf(marker.getTitle());
                    mbuilder.setTitle("Bus : "+BusNumber.get(ppos)).setCancelable(false);
                    mbuilder.setMessage(
                            "Message : "+Alert.get(ppos)+"\n\n"+
                                    "Pilot : "+DriverName.get(ppos)+"\n"+
                                    "Distance : " + convertTokm(distance) + "\n" +
                                    "Duration : " + convertTohhmm(timme));
                }
                else {
                    mbuilder.setTitle(marker.getTitle()).setCancelable(false);
                    mbuilder.setMessage("Distance : " + convertTokm(distance) + "\n" +
                            "Duration : " + convertTohhmm(timme));
                }
                mbuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mbuilder.create().show();
                return false;
            }
        });

    }

    public void retrievedata(String alst){
        DriverUid.clear();
        Alert.clear();
        DriverName.clear();
        BusNumber.clear();
        FirebaseDatabase.getInstance().getReference("DriverAvail").child(alst)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String uuiid=dataSnapshot.getKey();
                        DriverUid.add(uuiid);
                        int pos=DriverUid.indexOf(uuiid);
                        Alert.add(pos,(String) dataSnapshot.child("Alert").getValue());
                        getdetails(uuiid,pos);
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

    private void getdetails(String suid, final int spos) {
        FirebaseDatabase.getInstance().getReference("Driver").child(suid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DriverName.add(spos,(String) dataSnapshot.child("FullName").getValue());
                BusNumber.add(spos,(String) dataSnapshot.child("Busno").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String convertTohhmm(double timme) {
        int dd,hh,mm,ss;
        dd= (int) (timme/84600);
        timme=timme-(84600*dd);
        hh=(int) (timme/3600);
        timme=timme-(3600*hh);
        mm=(int) (timme/60);
        ss= (int) (timme%60);
        return dd+" D, "+hh+" H, "+mm+" m, "+ss+" s";
    }

    public String convertTokm(double distance) {
        int km= (int) (distance/1000);
        int mtrs= (int) (distance%1000);
        return km+" km and "+mtrs+" m.";
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
        return new MarkerOptions().title(key).snippet("Route: "+Route).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus));
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

        cloc=latLng;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13F));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
}