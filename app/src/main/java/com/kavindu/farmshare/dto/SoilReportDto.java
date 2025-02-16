package com.kavindu.farmshare.dto;

public class SoilReportDto {
    private String ph;
    private String moisture;
    private String organic;
    private String nutrient;
    private String document;
    private int farmId;

    public SoilReportDto() {
    }

    public String getPh() {
        return ph;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getMoisture() {
        return moisture;
    }

    public void setMoisture(String moisture) {
        this.moisture = moisture;
    }

    public String getOrganic() {
        return organic;
    }

    public void setOrganic(String organic) {
        this.organic = organic;
    }

    public String getNutrient() {
        return nutrient;
    }

    public void setNutrient(String nutrient) {
        this.nutrient = nutrient;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
