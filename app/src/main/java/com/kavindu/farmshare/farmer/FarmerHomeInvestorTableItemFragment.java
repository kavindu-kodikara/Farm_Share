package com.kavindu.farmshare.farmer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kavindu.farmshare.R;


public class FarmerHomeInvestorTableItemFragment extends Fragment {


    public FarmerHomeInvestorTableItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_farmer_home_investor_table_item, container, false);
    }
}