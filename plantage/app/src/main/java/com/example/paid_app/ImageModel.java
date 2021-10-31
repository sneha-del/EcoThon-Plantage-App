package com.example.paid_app;

import android.net.Uri;

import java.util.Date;

public class ImageModel {
    private String Id, PlantName, ImageUrl;
    private long TimeStamp;

    public ImageModel(){

    }

    public ImageModel(String id, String plantName, String imageUrl, long timeStamp) {
        Id = id;
        PlantName = plantName;
        ImageUrl = imageUrl;
        TimeStamp = timeStamp;
    }
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPlantName() {
        return PlantName;
    }

    public void setPlantName(String plantName) {
        PlantName = plantName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }
}
