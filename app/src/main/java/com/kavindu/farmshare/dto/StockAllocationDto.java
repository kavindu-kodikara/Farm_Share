package com.kavindu.farmshare.dto;

import java.io.Serializable;

public class StockAllocationDto implements Serializable {
    private String name;
    private double value;

    public StockAllocationDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
