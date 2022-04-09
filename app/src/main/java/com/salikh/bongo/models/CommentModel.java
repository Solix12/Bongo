package com.salikh.bongo.models;

public class CommentModel {


    private String comment, id, commentID, postID, uid, name, profileImageUrl;

    public CommentModel() {
    }

    public CommentModel(String comment, String id, String commentID, String postID, String uid, String name, String profileImageUrl) {
        this.comment = comment;
        this.id = id;
        this.commentID = commentID;
        this.postID = postID;
        this.uid = uid;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
