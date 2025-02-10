package com.kavindu.farmshare.investor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kavindu.farmshare.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link transactionItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class transactionItemFragment extends Fragment {


    public transactionItemFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_item, container, false);
    }
}