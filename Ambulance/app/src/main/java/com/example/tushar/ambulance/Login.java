package com.example.tushar.ambulance;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private FirebaseAuth mAuth;
    Button btn;

    DatabaseReference dref;
    private EditText user, passw;
    private GoogleApiClient api;
    LocationRequest locationRequest;
    private static final int request = 0;
    Double latitide,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (EditText) findViewById(R.id.user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, request);
        }

        api = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addOnConnectionFailedListener(this).addConnectionCallbacks(this).build();
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(10);
        locationRequest.setFastestInterval(10000);
        locationRequest.setInterval(0);
        dref = FirebaseDatabase.getInstance().getReference().child("Ambulance");


   btn=(Button)findViewById(R.id.login);
        passw = (EditText)findViewById(R.id.pass);
        mAuth = FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),latitide.toString()+longitude.toString(),Toast.LENGTH_LONG).show();
                post();
            }
        });
    }

    public void post() {
        final String use=user.getText().toString();
        final String pas=passw.getText().toString();
      mAuth.createUserWithEmailAndPassword(use,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
              if(task.isSuccessful()){
                  String uid=mAuth.getCurrentUser().getUid();
                  DatabaseReference dref2=dref.child(uid);

                  dref2.child("Name").setValue(use);
                  dref2.child("Password").setValue(pas);
                  dref2.child("Latitude").setValue(String.valueOf(latitide));
                  dref2.child("Longitude").setValue(String.valueOf(longitude));
                  dref2.child("usercalled").setValue("user");
                  startActivity(new Intent(Login.this,Checkuser.class));

              }
              else Toast.makeText(getApplicationContext(),"Not ",Toast.LENGTH_LONG).show();
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
     latitide=location.getLatitude();
        longitude=location.getLongitude();
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

