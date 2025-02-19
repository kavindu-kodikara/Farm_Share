package com.kavindu.farmshare.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchDto implements Serializable {
    private ArrayList<InvestItemDto> itemList = new ArrayList<>();
    private boolean success;

    public SearchDto() {
    }

    public ArrayList<InvestItemDto> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<InvestItemDto> itemList) {
        this.itemList = itemList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
