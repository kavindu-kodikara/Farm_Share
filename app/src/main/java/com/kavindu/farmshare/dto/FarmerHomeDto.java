package com.kavindu.farmshare.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FarmerHomeDto implements Serializable {
    private ArrayList<NameIdDto> chipArray = new ArrayList<>();
    private boolean success;
    private String message;

    private double riskScore;
    private String cropType;
    private String farmName;
    private int totStock;
    private int relesedStock;
    private double expectIncome;
    private int stockProgress;

    public FarmerHomeDto() {
    }

    public ArrayList<NameIdDto> getChipArray() {
        return chipArray;
    }

    public void setChipArray(ArrayList<NameIdDto> chipArray) {
        this.chipArray = chipArray;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
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

    public int getTotStock() {
        return totStock;
    }

    public void setTotStock(int totStock) {
        this.totStock = totStock;
    }

    public int getRelesedStock() {
        return relesedStock;
    }

    public void setRelesedStock(int relesedStock) {
        this.relesedStock = relesedStock;
    }

    public double getExpectIncome() {
        return expectIncome;
    }

    public void setExpectIncome(double expectIncome) {
        this.expectIncome = expectIncome;
    }

    public int getStockProgress() {
        return stockProgress;
    }

    public void setStockProgress(int stockProgress) {
        this.stockProgress = stockProgress;
    }
}
