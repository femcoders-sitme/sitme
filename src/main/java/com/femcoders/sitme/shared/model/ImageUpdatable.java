package com.femcoders.sitme.shared.model;

public interface ImageUpdatable {
    void setImageUrl(String url);
    void setCloudinaryImageId(String publicId);
    String getCloudinaryImageId();
}
