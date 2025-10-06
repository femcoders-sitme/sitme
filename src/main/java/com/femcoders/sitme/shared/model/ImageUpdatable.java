package com.femcoders.sitme.shared.model;

public interface ImageUpdatable {
    String getImageUrl();
    void setImageUrl(String url);
    void setCloudinaryImageId(String publicId);
    String getCloudinaryImageId();
}
