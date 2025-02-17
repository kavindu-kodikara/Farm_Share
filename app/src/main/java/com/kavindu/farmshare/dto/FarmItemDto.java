package com.kavindu.farmshare.dto;

import java.io.Serializable;
import java.util.List;

public class FarmItemDto implements Serializable {
    private String name;
    private String type;
    private String percentage;
    private boolean drop;
    private double price;
    private List<ChartEntruDto> chartEntryList;

    public FarmItemDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<ChartEntruDto> getChartEntryList() {
        return chartEntryList;
    }

    public void setChartEntryList(List<ChartEntruDto> chartEntryList) {
        this.chartEntryList = chartEntryList;
    }
}
