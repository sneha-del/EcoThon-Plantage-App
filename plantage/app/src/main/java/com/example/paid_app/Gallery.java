package com.example.paid_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.core.View;

import java.util.Objects;

public class Gallery extends AppCompatActivity {

    FirebaseAuth mAuth;
    DocumentReference mRef;
    String mUID;

    private ImageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mAuth = FirebaseAuth.getInstance();
        mUID = mAuth.getUid();
        mRef = FirebaseFirestore.getInstance().collection("Users").document(mUID);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = mRef.collection("Images").orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ImageModel> options = new FirestoreRecyclerOptions.Builder<ImageModel>()
                .setQuery(query, ImageModel.class)
                .build();

        adapter = new ImageAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}