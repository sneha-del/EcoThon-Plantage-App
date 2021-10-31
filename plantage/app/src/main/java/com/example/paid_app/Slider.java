package com.example.paid_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Slider extends AppCompatActivity {

    TextView coins, user_details, plant_name;
    Button about;
    public static final String SHARED_COINS = "COINS";
    public static final String COIN = "coin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        coins = findViewById(R.id.coins);
        user_details = findViewById(R.id.UserDetails);
        plant_name = findViewById(R.id.namep);
        about = findViewById(R.id.AboutUs);

        SharedPreferences sp = getSharedPreferences("Coins",MODE_PRIVATE);
        String name = sp.getString("Plant_name","Tulsi");
        //String coin = sp.getString("Total_coins","0");
        plant_name.setText(name);
        //coins.setText(coin);

        SharedPreferences sp2 = getSharedPreferences("coin",MODE_PRIVATE);
        coins.setText(sp2.getString("Total_coins","0"));

        user_details.setOnClickListener(v -> startActivity(new Intent(Slider.this,Profile.class)));

        about.setOnClickListener(v -> Toast.makeText(Slider.this, "Developed by Nisha, Sneha and Vikas", Toast.LENGTH_SHORT).show());
    }
}