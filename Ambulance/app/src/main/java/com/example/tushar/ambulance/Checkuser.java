package com.example.tushar.ambulance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Checkuser extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,LocationListener {
    FirebaseAuth mAuth;
    private TextView us, lt, ln,amb,amblat,amblon;
    String user;
    DatabaseReference db2, db;
    Double lat, lon;
    Button button,logou;
    private GoogleApiClient api;
    LocationRequest locationRequest;
    private static final int request = 0;
    Double latitide,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkuser);
        us = (TextView) findViewById(R.id.txtuser);
        mAuth = FirebaseAuth.getInstance();
        lt = (TextView)findViewById(R.id.txtlat);
        amb=(TextView)findViewById(R.id.txtamb);
        amblat=(TextView)findViewById(R.id.txtamblat);
        amblon=(TextView)findViewById(R.id.txtamblon);

        ln = (TextView)findViewById(R.id.txtlong);
        logou=(Button)findViewById(R.id.logout);
        api = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addOnConnectionFailedListener(this).addConnectionCallbacks(this).build();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(10);
        locationRequest.setFastestInterval(10000);
        locationRequest.setInterval(0);

        button = (Button) findViewById(R.id.bynmap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",lat,lon, "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });


        db2 = FirebaseDatabase.getInstance().getReference().child("Ambulance").child(mAuth.getCurrentUser().getUid());
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                amb.setText("Ambulance Requested");
                amblat.setText(dataSnapshot.child("Longitude").getValue().toString());
                amblon.setText(dataSnapshot.child("Latitude").getValue().toString());
                if (dataSnapshot.hasChild("usercalled")) {

                    user = dataSnapshot.child("usercalled").getValue().toString();



                    Toast.makeText(getApplicationContext(),user,Toast.LENGTH_LONG).show();
                    us.setText(user);
                    db = FirebaseDatabase.getInstance().getReference().child("User").child(user);
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            lat = (Double) dataSnapshot.child("lat").getValue();
                            lon = (Double) dataSnapshot.child("lon").getValue();
                            lt.setText(String.valueOf(lat));
                            ln.setText(String.valueOf(lon));


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else
                    Log.d("No user", "No user found");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
        api.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(api.isConnected()){
            getLoc();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        getLoc();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getLoc() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(isLocationEnabled(this)){
            Location location = LocationServices.FusedLocationApi.getLastLocation(api);
            try{
                latitide=location.getLatitude();
                longitude=location.getLongitude();
            }
            catch (NullPointerException n){
                Toast.makeText(this,"Wait While Retrieving Information",Toast.LENGTH_LONG).show();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(api,locationRequest,this);
        }
        else
            Toast.makeText(this,"Please Turn on Your GPS",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onLocationChanged(Location location) {
        latitide=location.getLatitude();
        longitude=location.getLongitude();
        db2.child("Longitude").setValue(longitude);
        db2.child("Latitude").setValue(latitide);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }}
