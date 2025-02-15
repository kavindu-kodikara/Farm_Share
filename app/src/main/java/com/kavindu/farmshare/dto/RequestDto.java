package com.kavindu.farmshare.dto;

import java.io.Serializable;

public class RequestDto implements Serializable {
    private int id;

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
}
