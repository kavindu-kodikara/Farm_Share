package com.kavindu.farmshare.investor;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.model.HotItemBean;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kavindu.farmshare.model.InvestItem;

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

        ImageView farmButton = view.findViewById(R.id.imageView17);
        farmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(),InvestorFarmsActivity.class);
                view.getContext().startActivity(intent);

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

        ArrayList<InvestItem> investItemArrayList = new ArrayList<>();
        investItemArrayList.add(new InvestItem("1","Rice","FDER","+2.5%","Rs.250",false,entries));
        investItemArrayList.add(new InvestItem("1","Corn","KTYG","-1.5%","Rs.120",true,entries));

        ArrayList<InvestItem> popularItemArrayList = new ArrayList<>();
        popularItemArrayList.add(new InvestItem("1","Corn","HJUT","+2.5%","Rs.250",false,entries));
        popularItemArrayList.add(new InvestItem("1","Rice","LAGR","-1.5%","Rs.120",true,entries));
        popularItemArrayList.add(new InvestItem("1","Rice","SRTY","+0.5%","Rs.320",false,entries));

        investItemInflater(R.id.popularItemLinearLayout,view,popularItemArrayList);
        investItemInflater(R.id.myItemLinearLayout,view,investItemArrayList);


        return view;
    }

    private void investItemInflater(int container, View parent, ArrayList<InvestItem> itemArrayList){

        LinearLayout itemContainer = parent.findViewById(container);

        for (InvestItem investItem : itemArrayList){

            View item = getLayoutInflater().inflate(R.layout.fragment_investor_invest__item,null);

            ImageView image = item.findViewById(R.id.itemDesignimageView22);
            TextView title = item.findViewById(R.id.itemDesigntextView76);
            TextView value = item.findViewById(R.id.itemDesigntextView78);
            TextView price = item.findViewById(R.id.itemDesigntextView79);
            LineChart chart = item.findViewById(R.id.itemDesignriskLineChart1);
            ConstraintLayout itemButton = item.findViewById(R.id.itemDesignconstraintLayout8);

            title.setText(investItem.getTitle());
            price.setText(investItem.getPrice());

            if(investItem.getType().equals("Rice")){
                image.setImageResource(R.drawable.rice);
            }else if(investItem.getType().equals("Corn")){
                image.setImageResource(R.drawable.corn);
            }

            LineDataSet dataSet = new LineDataSet(investItem.getChartData(), "");
            dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            dataSet.setLineWidth(2f);
            dataSet.setDrawFilled(true);
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);

            if (investItem.isLost()) {
                value.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.red));
                dataSet.setColor(Color.parseColor("#f27a7a"));
                dataSet.setFillDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_farmer_chart_red));
            } else {
                value.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.green));
                dataSet.setColor(Color.parseColor("#7AF27B"));
                dataSet.setFillDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_farmer_chart));
            }

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();

            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.getXAxis().setEnabled(false);
            chart.getAxisLeft().setEnabled(false);
            chart.getAxisRight().setEnabled(false);


            itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(parent.getContext(),InvestorSingleFarmActivity.class);
                    parent.getContext().startActivity(intent);
                }
            });

            itemContainer.addView(item);

        }

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