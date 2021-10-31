package com.example.paid_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity {

    TextView Username, Email, coins;
    Button Logout;
    ImageView back;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Username = findViewById(R.id.username);
        Email = findViewById(R.id.mailID);
        coins = findViewById(R.id.coins);
        Logout = findViewById(R.id.Logout);
        back = findViewById(R.id.arrowb2);
        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Profile", Context.MODE_PRIVATE);
        String name = sp.getString("Username","");
        String mail = sp.getString("Email","");
        Username.setText(name);
        Email.setText(mail);

        /*SharedPreferences sp2 = getSharedPreferences("Coins",MODE_PRIVATE);
        coins.setText(sp2.getString("Total_Coins","0"));*/

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Profile.this, "You have clicked on Logout button", Toast.LENGTH_SHORT).show();
                mAuth.signOut();//logout
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,Home.class));
            }
        });
    }
}