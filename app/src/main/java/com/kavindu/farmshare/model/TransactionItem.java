package com.kavindu.farmshare.model;

import java.io.Serializable;

public class TransactionItem implements Serializable {

    private String name;
    private String type;
    private String price;
    private String time;

    public TransactionItem() {
    }

    public TransactionItem(String name, String type, String price, String time) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.time = time;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
