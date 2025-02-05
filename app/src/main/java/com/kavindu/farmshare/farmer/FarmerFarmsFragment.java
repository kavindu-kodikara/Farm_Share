package com.kavindu.farmshare.farmer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kavindu.farmshare.R;
import com.kavindu.farmshare.model.FarmItem;

import java.util.ArrayList;


public class FarmerFarmsFragment extends Fragment {

    public FarmerFarmsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_farms, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.famsRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<FarmItem> farmItemList = new ArrayList<>();
        farmItemList.add(new FarmItem("Rice","Test Farm 1",false,"670"));
        farmItemList.add(new FarmItem("Corn","Test Farm 2",false,"260"));
        farmItemList.add(new FarmItem("Rice","Test Farm 3",true,"430"));
        farmItemList.add(new FarmItem("Corne","Test Farm 4",true,"730"));

        recyclerView.setAdapter(new Adapter(farmItemList));

        FrameLayout addFarmButton = view.findViewById(R.id.addFarmButton);
        addFarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),FarmerAddFarmActivity.class);
                view.getContext().startActivity(i);
            }
        });

        return view;
    }
}




class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder1>{

    static class ViewHolder1 extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView farmNameTextView;
        TextView risktextTextView;
        View riskIndicator;
        TextView stockCountTextView;
        ConstraintLayout farmItem;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView15);
            farmNameTextView = itemView.findViewById(R.id.textView39);
            risktextTextView = itemView.findViewById(R.id.textView40);
            riskIndicator = itemView.findViewById(R.id.view5);
            stockCountTextView = itemView.findViewById(R.id.textView41);

        }
    }

    ArrayList<FarmItem> farmItemArrayList;

    public Adapter(ArrayList<FarmItem> farmItemArrayList) {
        this.farmItemArrayList = farmItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_farmer_farm__item,parent,false);
        ViewHolder1 vh1 = new ViewHolder1(view);

        return vh1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder1 holder, int position) {

        FarmItem farmItem = farmItemArrayList.get(position);

        if(farmItem.getCropType().equals("Rice")){
            holder.imageView.setImageResource(R.drawable.rice);
        } else if (farmItem.getCropType().equals("Corn")) {
            holder.imageView.setImageResource(R.drawable.corn);
        }

        if (farmItem.isAtRisk()){
            holder.riskIndicator.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.gradient_risk));
            holder.risktextTextView.setText("At risk");
        }else {
            holder.riskIndicator.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.gradient_good));
            holder.risktextTextView.setText("In good hands");
        }

        holder.farmNameTextView.setText(farmItem.getFarmName());
        holder.stockCountTextView.setText("S "+farmItem.getStockCount());



    }

    @Override
    public int getItemCount() {
        return farmItemArrayList.size();
    }
}