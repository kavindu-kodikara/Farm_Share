package com.kavindu.farmshare.investor;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.model.HotItemBean;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class InvestorHomeFragment extends Fragment {

    public InvestorHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investor_home, container, false);

        ImageView menyButton = view.findViewById(R.id.investorHomeMenu);
        menyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof InvestorMainActivity) {
                    ((InvestorMainActivity) getActivity()).openDrawer();
                }
            }
        });


        RecyclerView hotItemRecyclerView = view.findViewById(R.id.hotItemRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        hotItemRecyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<HotItemBean> hotItemList = new ArrayList<>();
        hotItemList.add(new HotItemBean("FAHS","-2.5%","Rice","1",true));
        hotItemList.add(new HotItemBean("HYDF","+3.5%","Corn","1",false));
        hotItemList.add(new HotItemBean("KSGD","+0.5%","Rice","1",false));
        hotItemList.add(new HotItemBean("LKHY","-0.5%","Corn","1",true));

        hotItemRecyclerView.setAdapter(new HotItemAdapter(hotItemList));


        LineChart lineChart1 = view.findViewById(R.id.riskLineChart1);
        LineChart lineChart2 = view.findViewById(R.id.riskLineChart2);

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1990, 60));
        entries.add(new Entry(1994, 30));
        entries.add(new Entry(1998, 90));
        entries.add(new Entry(2002, 60));
        entries.add(new Entry(2006, 100));
        entries.add(new Entry(2010, 70));
        entries.add(new Entry(2014, 30));
        entries.add(new Entry(2018, 80));
        entries.add(new Entry(2022, 120));

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(Color.parseColor("#7AF27B"));
        dataSet.setCircleColor(Color.parseColor("#7AF27B"));
        dataSet.setLineWidth(2f);
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.gradient_farmer_chart));
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart1.setData(lineData);
        lineChart1.invalidate();

        lineChart1.getDescription().setEnabled(false);
        lineChart1.getXAxis().setDrawLabels(false);
        lineChart1.getAxisLeft().setDrawLabels(false);
        lineChart1.getAxisRight().setEnabled(false);
        lineChart1.getXAxis().setDrawGridLines(false);
        lineChart1.getAxisLeft().setDrawGridLines(false);
        lineChart1.getLegend().setEnabled(false);

        lineChart2.setData(lineData);
        lineChart2.invalidate();

        lineChart2.getDescription().setEnabled(false);
        lineChart2.getXAxis().setDrawLabels(false);
        lineChart2.getAxisLeft().setDrawLabels(false);
        lineChart2.getAxisRight().setEnabled(false);
        lineChart2.getXAxis().setDrawGridLines(false);
        lineChart2.getAxisLeft().setDrawGridLines(false);
        lineChart2.getLegend().setEnabled(false);


        return view;
    }
}

class HotItemAdapter extends RecyclerView.Adapter <HotItemAdapter.HotItemViewHolder>{

    ArrayList<HotItemBean> itemList;

    public HotItemAdapter(ArrayList<HotItemBean> itemList) {
        this.itemList = itemList;
    }

    static class HotItemViewHolder extends RecyclerView.ViewHolder{

        TextView titileView;
        TextView valueView;
        ConstraintLayout hotItem;
        ImageView imageView;

        public HotItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titileView = itemView.findViewById(R.id.textView73);
            valueView = itemView.findViewById(R.id.textView74);
            hotItem = itemView.findViewById(R.id.hotItemBtn);
            imageView = itemView.findViewById(R.id.imageView16);
        }
    }

    @NonNull
    @Override
    public HotItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_hot_item,parent,false);
        HotItemViewHolder holder = new HotItemViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull HotItemViewHolder holder, int position) {

        HotItemBean hotItemBean = itemList.get(position);

        holder.titileView.setText(hotItemBean.getTitle());
        holder.valueView.setText(hotItemBean.getValue());

        if (hotItemBean.getCropType().equals("Rice")){
            holder.imageView.setImageResource(R.drawable.rice);
        }else if (hotItemBean.getCropType().equals("Corn")){
            holder.imageView.setImageResource(R.drawable.corn);
        }

        if (hotItemBean.isLost()){
            holder.valueView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.red));
        }else {
            holder.valueView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.green));
        }

        holder.hotItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}