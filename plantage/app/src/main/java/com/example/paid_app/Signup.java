package com.example.paid_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Signup extends AppCompatActivity implements LocationListener {

    EditText  email, password, fname, lname, country, landmark, pincode, city, dob, phoneNo, Locality;
    Button Sign_up;
    DatePickerDialog pickerDialog;
//    TextView re_login;
    String Name, Email, Password, Fname, Lname, Country, Landmark, Pincode, City, birth, phone, address;
    FirebaseAuth mAuth;
    CollectionReference mRef;
    SharedPreferences sp;
    ProgressDialog progressDialog;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dob = findViewById(R.id.dob);
        phoneNo = findViewById(R.id.phoneNo);
        Locality = findViewById(R.id.Locality);

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.name6);
        email = findViewById(R.id.name2);
        country = findViewById(R.id.Email);
        landmark = findViewById(R.id.name3);
        city = findViewById(R.id.name4);
        pincode = findViewById(R.id.name5);
        password = findViewById(R.id.Password);
        Sign_up = findViewById(R.id.signup);
//        re_login = findViewById(R.id.relogin);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance().collection("Users");



        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                pickerDialog = new DatePickerDialog(Signup.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        dob.setText(i2 + "/" + (i1 + 1) + "/" + i);
                    }
                }, year, month, day);
                pickerDialog.show();
            }
        });

//        re_login.setOnClickListener(v -> {
//            startActivity(new Intent(Signup.this,Login.class));
//        });

        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndLogin();
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        getLocation();
    }




    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(Signup.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to fetch your address")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 150, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            city.setText(addresses.get(0).getLocality());
            landmark.setText(addresses.get(0).getAdminArea());
            country.setText(addresses.get(0).getCountryName());
            pincode.setText(addresses.get(0).getPostalCode());
            Locality.setText(addresses.get(0).getAddressLine(0));

        } catch (Exception e) {
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void checkDataAndLogin(){
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();
        Fname = fname.getText().toString().trim();
        Lname = lname.getText().toString().trim();
        Landmark = landmark.getText().toString().trim();
        City = city.getText().toString().trim();
        Pincode = pincode.getText().toString().trim();
        Country = country.getText().toString().trim();
        address = Locality.getText().toString().trim();
        birth = dob.getText().toString().trim();
        phone = phoneNo.getText().toString();

        if(Fname.isEmpty() || Fname.equals("")){
            fname.setError("This is compulsory");
            fname.requestFocus();
            return;
        }
        if(Lname.isEmpty() || Lname.equals("")){
            lname.setError("This is compulsory");
            lname.requestFocus();
            return;
        }
        if(Email.isEmpty() || Email.equals("")){
            email.setError("This is compulsory");
            email.requestFocus();
            return;
        }

        if(Password.isEmpty() || Password.equals("")){
            password.setError("This is compulsory");
            password.requestFocus();
            return;
        }
        if(Password.length() < 6){
            password.setError("Too Short (8 to 12)");
            password.requestFocus();
            return;
        }

        Progress_bar();
        sign_up();
        sp = getSharedPreferences("Profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Username",Fname+" "+Lname);
        editor.putString("Email",Email);
        editor.apply();
    }
    private void sign_up(){
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(Signup.this, task.getException().getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveData();
                }
            }
        });
    }
    private void saveData(){
        Map<String,String> map = new HashMap<>();
        map.put("First Name",Fname);
        map.put("Last Name",Lname);
        map.put("Email",Email);
        map.put("Landmark",Landmark);
        map.put("City",City);
        map.put("Pincode",Pincode);
        map.put("Country",Country);
        map.put("Password",Password);
        map.put("Address",address);
        map.put("Birth",birth);
        map.put("phone",phone);
        map.put("Id",mAuth.getUid());
        mRef.document(mAuth.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(Signup.this, "Registration not done", Toast.LENGTH_SHORT).show();
                }
                else {
                    Dismiss();
                    Toast.makeText(Signup.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Signup.this,Home.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Signup.this, "Registration not done", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Progress_bar(){
        progressDialog = new ProgressDialog(Signup.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void Dismiss(){
        progressDialog.dismiss();
    }


}