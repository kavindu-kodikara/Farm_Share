package com.kavindu.farmshare.investor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kavindu.farmshare.R;


public class InvestorStockHoldingItemFragment extends Fragment {


    public InvestorStockHoldingItemFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_investor_stock_holding_item, container, false);
    }
}