package com.example.paid_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Home extends AppCompatActivity {

    Button guide, plant;
    ImageView menu, gallery, reward, camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        guide = findViewById(R.id.guide_button);
        plant = findViewById(R.id.start_planting);
        menu = findViewById(R.id.menu);
        gallery = findViewById(R.id.gallery);
        reward = findViewById(R.id.reward);
        camera = findViewById(R.id.camera);

        guide.setOnClickListener(v -> startActivity(new Intent(Home.this,Guide.class)));

        plant.setOnClickListener(v -> {

            startActivity(new Intent(Home.this,Form.class));
            finish();
        });


        menu.setOnClickListener(v -> startActivity(new Intent(Home.this,Slider.class)));

        gallery.setOnClickListener(v -> startActivity(new Intent(Home.this,Gallery.class)));

        reward.setOnClickListener(v -> startActivity(new Intent(Home.this,Rewards.class)));

        camera.setOnClickListener(v -> startActivity(new Intent(Home.this,Camera.class)));
    }
}