package com.salikh.bongo.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostImageModel {


    private String imageUrl, id, description, uid;

    @ServerTimestamp
    private Date timeTamp;

    public PostImageModel() {
    }


    public PostImageModel(String imageUri, String id, String description, String uid, Date timeTamp) {
        this.imageUrl = imageUri;
        this.id = id;
        this.description = description;
        this.timeTamp = timeTamp;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeTamp() {
        return timeTamp;
    }

    public void setTimeTamp(Date timeTamp) {
        this.timeTamp = timeTamp;
    }
}
