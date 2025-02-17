package com.kavindu.farmshare.dto;

import java.io.Serializable;
import java.util.List;

public class SingleFarmDto implements Serializable {
    private String codeName;
    private String farmName;
    private String stockPrice;
    private String stockPriceCents;
    private String valuePrice;
    private String valuePercentage;
    private boolean drop;
    private String farmType;
    private String avgIncome;
    private String riskScore;
    private String ownerName;
    private String ownerDate;
    private String seasonMonths;
    private String landSize;
    private String avgYield;
    private String farmStatus;
    private String lat;
    private String lng;
    private List<String> imageList;
    private List<ChartEntruDto> weekChartData;
    private List<ChartEntruDto> monthChartData;
    private List<ChartEntruDto> seasonChartData;

    public SingleFarmDto() {
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public String getStockPriceCents() {
        return stockPriceCents;
    }

    public void setStockPriceCents(String stockPriceCents) {
        this.stockPriceCents = stockPriceCents;
    }

    public String getValuePrice() {
        return valuePrice;
    }

    public void setValuePrice(String valuePrice) {
        this.valuePrice = valuePrice;
    }

    public String getValuePercentage() {
        return valuePercentage;
    }

    public void setValuePercentage(String valuePercentage) {
        this.valuePercentage = valuePercentage;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public String getFarmType() {
        return farmType;
    }

    public void setFarmType(String farmType) {
        this.farmType = farmType;
    }

    public String getAvgIncome() {
        return avgIncome;
    }

    public void setAvgIncome(String avgIncome) {
        this.avgIncome = avgIncome;
    }

    public String getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerDate() {
        return ownerDate;
    }

    public void setOwnerDate(String ownerDate) {
        this.ownerDate = ownerDate;
    }

    public String getSeasonMonths() {
        return seasonMonths;
    }

    public void setSeasonMonths(String seasonMonths) {
        this.seasonMonths = seasonMonths;
    }

    public String getLandSize() {
        return landSize;
    }

    public void setLandSize(String landSize) {
        this.landSize = landSize;
    }

    public String getAvgYield() {
        return avgYield;
    }

    public void setAvgYield(String avgYield) {
        this.avgYield = avgYield;
    }

    public String getFarmStatus() {
        return farmStatus;
    }

    public void setFarmStatus(String farmStatus) {
        this.farmStatus = farmStatus;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public List<ChartEntruDto> getWeekChartData() {
        return weekChartData;
    }

    public void setWeekChartData(List<ChartEntruDto> weekChartData) {
        this.weekChartData = weekChartData;
    }

    public List<ChartEntruDto> getMonthChartData() {
        return monthChartData;
    }

    public void setMonthChartData(List<ChartEntruDto> monthChartData) {
        this.monthChartData = monthChartData;
    }

    public List<ChartEntruDto> getSeasonChartData() {
        return seasonChartData;
    }

    public void setSeasonChartData(List<ChartEntruDto> seasonChartData) {
        this.seasonChartData = seasonChartData;
    }
}
