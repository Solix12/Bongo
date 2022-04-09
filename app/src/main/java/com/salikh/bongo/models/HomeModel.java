package com.salikh.bongo.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class HomeModel {

    private String userName;

    @ServerTimestamp
    private Date timeTamp;


    private String profileImage;
    private String imageUrl;
    private String uid;
    private String description;
    private String id;

    private List<String> likes;


    public HomeModel() {
    }

    public HomeModel(String userName, Date timeTamp,
                     String profileImage, String imageUrl,
                     String uid, String description, String id,
                     List<String> likes) {
        this.userName = userName;
        this.timeTamp = timeTamp;
        this.profileImage = profileImage;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.description = description;
        this.id = id;
        this.likes = likes;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getTimeTamp() {
        return timeTamp;
    }

    public void setTimeTamp(Date timeTamp) {
        this.timeTamp = timeTamp;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
