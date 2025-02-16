package com.kavindu.farmshare.dto;

import java.io.Serializable;

public class StockAllocationTableItemDto implements Serializable {
    private String profileUrl;
    private String name;
    private String amount;
    private String stock;

    public StockAllocationTableItemDto() {
    }

    public StockAllocationTableItemDto(String profileUrl, String name, String amount, String stock) {
        this.profileUrl = profileUrl;
        this.name = name;
        this.amount = amount;
        this.stock = stock;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
