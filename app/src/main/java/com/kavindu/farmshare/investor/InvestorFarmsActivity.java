package com.kavindu.farmshare.investor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherKt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.model.InvestItem;

import java.util.ArrayList;
import java.util.List;

public class InvestorFarmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_investor_farms);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton  = findViewById(R.id.investorFarmBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

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
        investItemArrayList.add(new InvestItem("1","Rice","FDER","+2.5%","Rs.250.00",false,entries));
        investItemArrayList.add(new InvestItem("1","Corn","KTYG","-1.5%","Rs.120.00",true,entries));
        investItemArrayList.add(new InvestItem("1","Rice","FDER","+2.5%","Rs.250.00",false,entries));
        investItemArrayList.add(new InvestItem("1","Corn","KTYG","-1.5%","Rs.120.00",true,entries));
        investItemArrayList.add(new InvestItem("1","Rice","FDER","+2.5%","Rs.250.00",false,entries));
        investItemArrayList.add(new InvestItem("1","Corn","KTYG","-1.5%","Rs.120.00",true,entries));
        investItemArrayList.add(new InvestItem("1","Rice","FDER","+2.5%","Rs.250.00",false,entries));
        investItemArrayList.add(new InvestItem("1","Corn","KTYG","-1.5%","Rs.120.00",true,entries));
        investItemArrayList.add(new InvestItem("1","Rice","FDER","+2.5%","Rs.250.00",false,entries));
        investItemArrayList.add(new InvestItem("1","Corn","KTYG","-1.5%","Rs.120.00",true,entries));

        investItemInflater(R.id.farmItemContainer,investItemArrayList);

    }

    private void investItemInflater(int container,  ArrayList<InvestItem> itemArrayList){

        LinearLayout itemContainer = findViewById(container);

        for (InvestItem investItem : itemArrayList){

            View item = getLayoutInflater().inflate(R.layout.investor_farm_item,null);

            TextView title = item.findViewById(R.id.textView78);
            TextView type = item.findViewById(R.id.textView79);
            TextView price = item.findViewById(R.id.textView80);
            LineChart chart = item.findViewById(R.id.investorFarmItemLineChart1);
            ConstraintLayout itemButton = item.findViewById(R.id.investorFarmItemConstraint);

            title.setText(investItem.getTitle());
            price.setText(investItem.getPrice());
            type.setText(investItem.getType());


            LineDataSet dataSet = new LineDataSet(investItem.getChartData(), "");
            dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            dataSet.setLineWidth(2f);
            dataSet.setDrawFilled(true);
            dataSet.setDrawValues(false);
            dataSet.setFillDrawable(ContextCompat.getDrawable(InvestorFarmsActivity.this, android.R.color.transparent));
            dataSet.setDrawCircles(false);

            if (investItem.isLost()) {
                price.setTextColor(ContextCompat.getColor(InvestorFarmsActivity.this, R.color.red));
                dataSet.setColor(Color.parseColor("#f27a7a"));
            } else {
                price.setTextColor(ContextCompat.getColor(InvestorFarmsActivity.this, R.color.green));
                dataSet.setColor(Color.parseColor("#7AF27B"));
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
                    Toast.makeText(InvestorFarmsActivity.this, "Item clicked", Toast.LENGTH_SHORT).show();
                }
            });

            itemContainer.addView(item);

        }

    }
}