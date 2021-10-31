package com.example.paid_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Rewards extends AppCompatActivity {

    TextView coins, coupon1, coupon2, coupon3, coupon4;
    View Coupon, Coupon2, Coupon3, Coupon4;
    ImageView back;
    public static final String SHARED_PREFS = "sharedPrefs1";
    public static final String TEXT = "text";
    Random rand = new Random();
    FirebaseAuth auth;
    CollectionReference mRef;
    String mUserId,text;
    SharedPreferences sp3;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        coins = findViewById(R.id.coins2);

        coupon1 = findViewById(R.id.couponamount);
        coupon2 = findViewById(R.id.couponamount2);
        coupon3 = findViewById(R.id.couponamount3);
        coupon4 = findViewById(R.id.couponamount4);

        Coupon = findViewById(R.id.coupon);
        Coupon2 = findViewById(R.id.coupon2);
        Coupon3 = findViewById(R.id.coupon3);
        Coupon4 = findViewById(R.id.coupon4);

        back = findViewById(R.id.imageView4);

        auth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance().collection("Users");
        mUserId = auth.getUid();

        SharedPreferences sp = getSharedPreferences("Coins",MODE_PRIVATE);
        coins.setText(sp.getString("Total_Coins","0"));

        //loadData();
        //updateViews();

        String s = coins.getText().toString();
        int r = Integer.parseInt(s);

        int c1 = rand.nextInt(Integer.parseInt(s));
        int c2 = rand.nextInt(Integer.parseInt(s));
        int c3 = rand.nextInt(Integer.parseInt(s));
        int c4 = rand.nextInt(Integer.parseInt(s));

        String s1 = String.valueOf(c1);
        String s2 = String.valueOf(c2);
        String s3 = String.valueOf(c3);
        String s4 = String.valueOf(c4);

        coupon1.setText(s1);
        coupon2.setText(s2);
        coupon3.setText(s3);
        coupon4.setText(s4);

        Coupon.setOnClickListener(v -> {
            int r1 = r - c1 ;
            coins.setText(String.valueOf(r1));
            save();
        });

        Coupon2.setOnClickListener(v -> {
            int r2 = r - c2 ;
            coins.setText(String.valueOf(r2));
            save();
        });

        Coupon3.setOnClickListener(v -> {
            int r3 = r - c3 ;
            coins.setText(String.valueOf(r3));
            save();
        });

        Coupon4.setOnClickListener(v -> {
            int r4 = r - c4 ;
            coins.setText(String.valueOf(r4));
            save();
        });

        back.setOnClickListener(v -> {

            startActivity(new Intent(Rewards.this,Home.class));
        });
        //save();
    }

    public void save(){
        SharedPreferences sp1 = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor e1 = sp1.edit();
        e1.putString(TEXT, coins.getText().toString());
        e1.apply();
        sp3 = getSharedPreferences("coin",MODE_PRIVATE);
        SharedPreferences.Editor e2 = sp3.edit();
        e2.putString("Total_coins",coins.getText().toString());
        e2.apply();
        Progress_bar();
        saveToDatabase();
        loadData();
        updateViews();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "0");
    }

    public void updateViews() {
        coins.setText(text);
    }

    private void saveToDatabase() {
        Map<String,Object> map = new HashMap<>();
        map.put("Total-Coins",coins.getText().toString());
        mRef.document(mUserId).update(map).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Dismiss();
                startActivity(new Intent(Rewards.this,Redeem.class));
            }else{
                Toast.makeText(Rewards.this, "Couldn't save coins", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(Rewards.this, e+"", Toast.LENGTH_SHORT).show());
    }

    private void Progress_bar(){
        progressDialog = new ProgressDialog(Rewards.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void Dismiss(){
        progressDialog.dismiss();
    }
}