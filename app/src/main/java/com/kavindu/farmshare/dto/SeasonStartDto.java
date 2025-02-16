package com.kavindu.farmshare.dto;

import java.io.Serializable;

public class SeasonStartDto implements Serializable {
    private int farmId;
    private String startDate;
    private String endDate;

    public SeasonStartDto() {
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
