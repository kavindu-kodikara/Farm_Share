package com.kavindu.farmshare.investor;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kavindu.farmshare.R;


public class StockBuy1Fragment extends Fragment {

public StockBuy1Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_stock_buy1, container, false);

        LinearLayout button = view.findViewById(R.id.singleFarmBuyButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StockBuy1Fragment.class);
                view.getContext().startActivity(intent);
            }
        });

        return view;
    }
}