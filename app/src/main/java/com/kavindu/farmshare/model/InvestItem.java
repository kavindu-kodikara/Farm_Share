package com.kavindu.farmshare.model;

import com.github.mikephil.charting.data.Entry;

import java.io.Serializable;
import java.util.List;

public class InvestItem implements Serializable {
    private String id;
    private String type;
    private String title;
    private String value;
    private String price;
    private boolean isLost;
    private List<Entry> chartData;

    public InvestItem() {
    }

    public InvestItem(String id, String type, String title, String value, String price, boolean isLost, List<Entry> chartData) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.value = value;
        this.price = price;
        this.isLost = isLost;
        this.chartData = chartData;
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

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean lost) {
        isLost = lost;
    }

    public List<Entry> getChartData() {
        return chartData;
    }

    public void setChartData(List<Entry> chartData) {
        this.chartData = chartData;
    }
}
