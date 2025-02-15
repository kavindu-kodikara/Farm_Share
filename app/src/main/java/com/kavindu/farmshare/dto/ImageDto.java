package com.kavindu.farmshare.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageDto implements Serializable {
    private List<String> imageArray;
    private int farmId;

    public ImageDto() {
    }

    public ImageDto(List<String> imageArray, int farmId) {
        this.imageArray = imageArray;
        this.farmId = farmId;
    }

    public List<String> getImageArray() {
        return imageArray;
    }

    public void setImageArray(List<String> imageArray) {
        this.imageArray = imageArray;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }
}
