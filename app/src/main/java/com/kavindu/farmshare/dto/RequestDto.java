package com.kavindu.farmshare.dto;

import java.io.Serializable;

public class RequestDto implements Serializable {
    private int id;
    private String value;

    public RequestDto(int id) {
        this.id = id;
    }

    public RequestDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
