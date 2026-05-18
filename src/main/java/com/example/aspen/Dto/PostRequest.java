package com.example.aspen.Dto;

public class PostRequest {

    private String caption;

    private Boolean isLandscape;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean getIsLandscape() {
        return isLandscape;
    }

    public void setIsLandscape(Boolean landscape) {
        isLandscape = landscape;
    }
}