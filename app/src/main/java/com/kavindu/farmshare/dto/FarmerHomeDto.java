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
    private  ArrayList<ChartEntruDto> chartEntryList;
    private boolean priceDrop;
    private List<StockAllocationTableItemDto> tableItemList;
    private String investorsCount;
    private int farmId;
    private String farmStatus;
    private double singleStockPrice;


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

    public boolean isPriceDrop() {
        return priceDrop;
    }

    public String getFarmStatus() {
        return farmStatus;
    }

    public void setFarmStatus(String farmStatus) {
        this.farmStatus = farmStatus;
    }

    public void setPriceDrop(boolean priceDrop) {
        this.priceDrop = priceDrop;
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

    public ArrayList<ChartEntruDto> getChartEntryList() {
        return chartEntryList;
    }

    public void setChartEntryList(ArrayList<ChartEntruDto> chartEntryList) {
        this.chartEntryList = chartEntryList;
    }

    public List<StockAllocationTableItemDto> getTableItemList() {
        return tableItemList;
    }

    public void setTableItemList(List<StockAllocationTableItemDto> tableItemList) {
        this.tableItemList = tableItemList;
    }

    public String getInvestorsCount() {
        return investorsCount;
    }

    public void setInvestorsCount(String investorsCount) {
        this.investorsCount = investorsCount;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    public double getSingleStockPrice() {
        return singleStockPrice;
    }

    public void setSingleStockPrice(double singleStockPrice) {
        this.singleStockPrice = singleStockPrice;
    }
}
