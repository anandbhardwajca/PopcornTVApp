package com.androidsalad.popcorntvapp.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Post {


    private String postId;
    private String celebId;
    private String celebName;
    private String celebThumbUrl;
    private String postDesc;
    private int postViews;

    public Post() {
    }

    public Post(String postId, String celebId, String celebName, String celebThumbUrl, String postDesc, int postViews) {
        this.postId = postId;
        this.celebId = celebId;
        this.celebName = celebName;
        this.celebThumbUrl = celebThumbUrl;
        this.postDesc = postDesc;
        this.postViews = postViews;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCelebId() {
        return celebId;
    }

    public void setCelebId(String celebId) {
        this.celebId = celebId;
    }

    public String getCelebName() {
        return celebName;
    }

    public void setCelebName(String celebName) {
        this.celebName = celebName;
    }

    public String getCelebThumbUrl() {
        return celebThumbUrl;
    }

    public void setCelebThumbUrl(String celebThumbUrl) {
        this.celebThumbUrl = celebThumbUrl;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public int getPostViews() {
        return postViews;
    }

    public void setPostViews(int postViews) {
        this.postViews = postViews;
    }

    @Exclude
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("celebId", celebId);
        result.put("celebName", celebName);
        result.put("celebThumbUrl", celebThumbUrl);
        result.put("postDesc", postDesc);
        result.put("postViews", postViews);

        return result;

    }


}
