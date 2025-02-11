package com.kavindu.farmshare.model;

import java.io.Serializable;

public class PayoutItem implements Serializable {
    private String title;
    private String date;
    private String stock;
    private String returnType;
    private String price;

    public PayoutItem() {
    }

    public PayoutItem(String title, String date, String returnType, String price) {
        this.title = title;
        this.date = date;
        this.returnType = returnType;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
