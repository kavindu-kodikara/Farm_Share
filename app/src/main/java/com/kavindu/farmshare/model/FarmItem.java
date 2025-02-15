package com.kavindu.farmshare.model;

import java.io.Serializable;

public class FarmItem implements Serializable {
    private String cropType;
    private String farmName;
    private String isAtRisk;
    private String stockCount;

    public FarmItem() {
    }

    public FarmItem(String cropType, String farmName, String isAtRisk, String stockCount) {
        this.cropType = cropType;
        this.farmName = farmName;
        this.isAtRisk = isAtRisk;
        this.stockCount = stockCount;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getIsAtRisk() {
        return isAtRisk;
    }

    public void setIsAtRisk(String isAtRisk) {
        this.isAtRisk = isAtRisk;
    }

    public String getStockCount() {
        return stockCount;
    }

    public void setStockCount(String stockCount) {
        this.stockCount = stockCount;
    }
}
