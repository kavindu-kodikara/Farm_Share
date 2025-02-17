package com.kavindu.farmshare.dto;

import com.kavindu.farmshare.model.HotItemBean;
import com.kavindu.farmshare.model.PayoutItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InvestorHomeDto implements Serializable {
    private boolean success;
    private ArrayList<HotItemBean> hotList;
    private List<InvestItemDto> popularList;
    private List<InvestItemDto> stockHoldingList;
    private List<StockAllocationDto> allocationList;
    private double allocationTot;
    private List<PayoutItem> payoutItemList;

    public InvestorHomeDto() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<HotItemBean> getHotList() {
        return hotList;
    }

    public void setHotList(ArrayList<HotItemBean> hotList) {
        this.hotList = hotList;
    }

    public List<InvestItemDto> getPopularList() {
        return popularList;
    }

    public void setPopularList(List<InvestItemDto> popularList) {
        this.popularList = popularList;
    }

    public List<InvestItemDto> getStockHoldingList() {
        return stockHoldingList;
    }

    public void setStockHoldingList(List<InvestItemDto> stockHoldingList) {
        this.stockHoldingList = stockHoldingList;
    }

    public List<StockAllocationDto> getAllocationList() {
        return allocationList;
    }

    public void setAllocationList(List<StockAllocationDto> allocationList) {
        this.allocationList = allocationList;
    }

    public double getAllocationTot() {
        return allocationTot;
    }

    public void setAllocationTot(double allocationTot) {
        this.allocationTot = allocationTot;
    }

    public List<PayoutItem> getPayoutItemList() {
        return payoutItemList;
    }

    public void setPayoutItemList(List<PayoutItem> payoutItemList) {
        this.payoutItemList = payoutItemList;
    }
}
