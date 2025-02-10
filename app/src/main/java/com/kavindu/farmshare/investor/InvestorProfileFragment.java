package com.kavindu.farmshare.investor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kavindu.farmshare.R;


public class InvestorProfileFragment extends Fragment {

    public InvestorProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investor_profile, container, false);

        ImageView menuButton = view.findViewById(R.id.investorProfileMenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InvestorMainActivity) getActivity()).openDrawer();
            }
        });

        return view;
    }
}