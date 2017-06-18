package com.bharathacks.salute_police;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;

public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    DatabaseReference dref, dref1;
    Double jsonlat, jsonlong;
    FirebaseAuth mauth;
    private GoogleApiClient api;
    LocationRequest locationRequest;
    private static final int request = 0;
    GoogleMap map;
    Double ab;
    Double latitide,longitude;
    int i = 0, index = 0, flag = 0;
    Double a[];
    Double lat[], lan[];
    Marker mPositionMarker;
    String key[];
    Button req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dref = FirebaseDatabase.getInstance().getReference().child("Ambulance");
        dref1 = FirebaseDatabase.getInstance().getReference().child("User");
        jsonlat=new Double(0);
        jsonlong=new Double(0);
        mauth = FirebaseAuth.getInstance();
        lat = new Double[10];
        lan = new Double[10];
        mPositionMarker=null;
        key = new String[10];
        a = new Double[10];
        ab = new Double(0);
        setContentView(R.layout.activity_main2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, request);
        }

        api = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addOnConnectionFailedListener(this).addConnectionCallbacks(this).build();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(10);
        locationRequest.setFastestInterval(10000);
        locationRequest.setInterval(0);

//    dref= FirebaseDatabase.getInstance().getReference().child("Ambulance");
//        dref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot childsnapshot:dataSnapshot.getChildren()){
//                    String key=childsnapshot.getKey();
//
//                     lat[i] =(Double)(dataSnapshot.child(key).child("Latitude").getValue());
//                    lan[i] =(Double)(dataSnapshot.child(key).child("Longitude").getValue());
//
//
//
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
//        LatLng sydney = new LatLng(-33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions().position(sydney)
//                .title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        LatLng abc = new LatLng(-33.333, 151.245);
//        googleMap.addMarker(new MarkerOptions().position(abc)
//                .title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(abc));
        final LatLng abc = new LatLng(28.644960, 77.074939);

        googleMap.addMarker(new MarkerOptions().position(abc)
                .title("Police"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(abc));
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot childsnapshot : dataSnapshot.getChildren()) {
                        key[i] = childsnapshot.getKey();

                        lat[i] = (Double) (dataSnapshot.child(key[i]).child("Latitude").getValue());
                        lan[i] = (Double) (dataSnapshot.child(key[i]).child("Longitude").getValue());
                        LatLng sydney = new LatLng(lat[i], lan[i]);
                        googleMap.addMarker(new MarkerOptions().position(sydney)
                                .title("Ambulance"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                        a[i] = (Double) CalculationByDistance(abc, sydney);
                        Log.d("ABC", String.valueOf(a[i]));
                        i++;



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
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
        api.connect();
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
        latitide = location.getLatitude();
        longitude = location.getLongitude();
        for (int i = 0; a[i] != null && i < a.length; i++) {
            if (a[i] < 7.0000000000) {
                new AlertDialog.Builder(getApplicationContext()).setMessage("Ambulance Nearby").create().show();
            }
        }
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


    }

}