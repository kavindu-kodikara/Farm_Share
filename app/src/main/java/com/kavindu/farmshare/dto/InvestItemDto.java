package com.kavindu.farmshare.dto;

import java.io.Serializable;
import java.util.List;

public class InvestItemDto implements Serializable {
    private String id;
    private String type;
    private String title;
    private String value;
    private String price;
    private String lost;

    private List<ChartEntruDto> chartData;

    public InvestItemDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLost() {
        return lost;
    }

    public void setLost(String lost) {
        this.lost = lost;
    }

    public List<ChartEntruDto> getChartData() {
        return chartData;
    }

    public void setChartData(List<ChartEntruDto> chartData) {
        this.chartData = chartData;
    }
}
