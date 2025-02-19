package com.kavindu.farmshare.dto;

import com.kavindu.farmshare.model.TransactionItem;

import java.io.Serializable;
import java.util.ArrayList;

public class TransactionDto implements Serializable {
    private boolean success;
    private ArrayList<TransactionItem> todayList = new ArrayList<>();
    private ArrayList<TransactionItem> oldList = new ArrayList<>();

    public TransactionDto() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<TransactionItem> getTodayList() {
        return todayList;
    }

    public void setTodayList(ArrayList<TransactionItem> todayList) {
        this.todayList = todayList;
    }

    public ArrayList<TransactionItem> getOldList() {
        return oldList;
    }

    public void setOldList(ArrayList<TransactionItem> oldList) {
        this.oldList = oldList;
    }
}
