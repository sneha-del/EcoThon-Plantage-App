package com.example.paid_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ImageAdapter extends FirestoreRecyclerAdapter<ImageModel, ImageAdapter.ImageHolder> {

    public ImageAdapter(@NonNull FirestoreRecyclerOptions<ImageModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageHolder holder, int position, @NonNull ImageModel model) {
        holder.name.setText(model.getPlantName());
        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(model.getImageUrl())
                .fitCenter()
                .centerCrop()
                .into(holder.image);
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item,parent,false);
        return new ImageHolder(v);
    }

    static class ImageHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView image;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.text_view_name);
            image = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
