package com.kavindu.farmshare.model;

import java.io.Serializable;

public class HotItemBean implements Serializable {
    private String title;
    private String value;
    private String cropType;
    private String id;
    private boolean isLost;

    public HotItemBean() {
    }

    public HotItemBean(String title, String value, String cropType, String id, boolean isLost) {
        this.title = title;
        this.value = value;
        this.cropType = cropType;
        this.id = id;
        this.isLost = isLost;
    }

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean lost) {
        isLost = lost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }
}
