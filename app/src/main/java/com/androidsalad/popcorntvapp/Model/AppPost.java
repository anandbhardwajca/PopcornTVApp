package com.androidsalad.popcorntvapp.Model;

import java.util.List;

public class AppPost {

    private String postId;
    private String celebId;
    private String celebName;
    private String celebThumbUrl;
    private String postDesc;
    private int postViews;
    private List<String> photoList;

    public AppPost() {
    }

    public AppPost(String postId, String celebId, String celebName, String celebThumbUrl, String postDesc, int postViews, List<String> photoList) {
        this.postId = postId;
        this.celebId = celebId;
        this.celebName = celebName;
        this.celebThumbUrl = celebThumbUrl;
        this.postDesc = postDesc;
        this.postViews = postViews;
        this.photoList = photoList;
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

    public List<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
    }
}
