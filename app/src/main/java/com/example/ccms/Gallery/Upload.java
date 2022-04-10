package com.example.ccms.Gallery;

import com.google.firebase.firestore.Exclude;

public class Upload {
    private String photo_name;
    private String url;
    private String mKey;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String photo_name, String url) {
        if (photo_name.trim().equals("")) {
            photo_name = "No Name";
        }
        this.photo_name=photo_name;
        this.url=url;
    }
    public String getPhoto_name(){
        return photo_name;
    }
    public String getUrl(){
        return url;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}
