package com.kavindu.farmshare.dto;

import java.io.Serializable;

public class NameIdDto implements Serializable {
    private String name;
    private int id;

    public NameIdDto() {
    }

    public NameIdDto(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
