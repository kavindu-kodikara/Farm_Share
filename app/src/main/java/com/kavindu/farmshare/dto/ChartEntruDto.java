package com.kavindu.farmshare.dto;

public class ChartEntruDto {
    private int date;
    private double value;

    public ChartEntruDto() {
    }

    public ChartEntruDto(int date, double value) {
        this.date = date;
        this.value = value;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
