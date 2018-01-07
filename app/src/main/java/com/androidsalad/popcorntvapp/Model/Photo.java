package com.androidsalad.popcorntvapp.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Photo {

    private String photoId;
    private String photoFullUrl;
    private String photoThumbUrl;

    public Photo() {
    }

    public Photo(String photoId, String photoFullUrl, String photoThumbUrl) {
        this.photoId = photoId;
        this.photoFullUrl = photoFullUrl;
        this.photoThumbUrl = photoThumbUrl;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoFullUrl() {
        return photoFullUrl;
    }

    public void setPhotoFullUrl(String photoFullUrl) {
        this.photoFullUrl = photoFullUrl;
    }

    public String getPhotoThumbUrl() {
        return photoThumbUrl;
    }

    public void setPhotoThumbUrl(String photoThumbUrl) {
        this.photoThumbUrl = photoThumbUrl;
    }

    @Exclude
    public HashMap<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("photoId", photoId);
        result.put("photoFullUrl", photoFullUrl);
        result.put("photoThumbUrl", photoThumbUrl);

        return result;
    }

}
