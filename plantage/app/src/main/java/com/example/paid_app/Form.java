package com.example.paid_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class Form extends AppCompatActivity implements AdapterView.OnItemSelectedListener,LocationListener {

    Spinner spinner;
    EditText plant_name, local;
    TextView Total_coins;
    Button camera;
    ImageView back;
    int num;
    public static int coins = 0;
    private String text1;
    LocationManager locationManager;
    Editable land, plant;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        spinner = findViewById(R.id.spinner);
        plant_name = findViewById(R.id.Plantname);
        local = findViewById(R.id.Location);
        camera = findViewById(R.id.camera1);
        back = findViewById(R.id.arrowb);
        Total_coins = findViewById(R.id.Total_coins);

        plant = plant_name.getText();
        land = local.getText();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if(ContextCompat.checkSelfPermission(Form.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Form.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        camera.setOnClickListener(v -> {
            Progress_bar();
            getLocation();
        });

        back.setOnClickListener(v -> startActivity(new Intent(Form.this,Home.class)));
        loadData();
        updateViews();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "You have to select only once", Toast.LENGTH_SHORT).show();
        String text = parent.getItemAtPosition(position).toString();
        String[] str = text.split("-");
        String Plant_name = str[0];
        num = Integer.parseInt(str[1]);
        if(coins == 0){
            coins = Integer.parseInt(Total_coins.getText().toString());
        }
        coins = coins + num;
        text1 = String.valueOf(coins);
        Total_coins.setText(text1);

        SharedPreferences sp = getSharedPreferences("Coins", MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("Total_Coins",text1);
        e.putString("Plant_name",Plant_name);
        e.apply();

        if(str[0].equals("Other")){
            Toast.makeText(this, "Enter the Plant Name Below", Toast.LENGTH_SHORT).show();
        }
        else {
            plant_name.setText(Plant_name);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("MissingPermission")
    public void getLocation(){
        try {
            System.out.println("Location Detection Started");
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,3,Form.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            System.out.println("Location Detection Started 2");
            Geocoder geocoder = new Geocoder(Form.this, Locale.getDefault());
            System.out.println("Location Detection Started 3");
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            System.out.println("Location Detection Started 4");
            String address= addresses.get(0).getAddressLine(0);
            //Address.setText(address);
            boolean check = address.contains(land);
            System.out.println(land+"");
            System.out.println("\n"+address+"");
            if(check){
                Intent intent = new Intent(Form.this,Camera.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(this, "Location is Verified", Toast.LENGTH_LONG).show();
                startActivity(intent);
                //saveData();
                //Dismiss();
            }
            else{
                Toast.makeText(this, "Location not verified", Toast.LENGTH_SHORT).show();
                Dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, Total_coins.getText().toString());
        editor.apply();
        //Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text1 = sharedPreferences.getString(TEXT, "0");
    }

    public void updateViews() {
        Total_coins.setText(text1);
    }

    private void Progress_bar(){
        progressDialog = new ProgressDialog(Form.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void Dismiss(){
        progressDialog.dismiss();
    }

}
