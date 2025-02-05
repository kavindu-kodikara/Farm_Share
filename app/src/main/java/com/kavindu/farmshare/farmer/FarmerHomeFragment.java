package com.kavindu.farmshare.farmer;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kavindu.farmshare.R;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.util.ArrayList;
import java.util.List;


public class FarmerHomeFragment extends Fragment {



    public FarmerHomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_farmer_home, container, false);

        ColorfulRingProgressView crpv1 = (ColorfulRingProgressView) view.findViewById(R.id.crpv);
        crpv1.setPercent(75);

        ColorfulRingProgressView crpv2 = (ColorfulRingProgressView) view.findViewById(R.id.crpv2);
        crpv2.setPercent(35);

        TextView riskScoreText = view.findViewById(R.id.riskScoreText);
        TextView riskScoreTitle = view.findViewById(R.id.riskScoreTitle);
        TextView riskScore = view.findViewById(R.id.riskScore);

//        crpv1.setVisibility(View.INVISIBLE);
//        riskScore.setText("85");
//        riskScoreTitle.setText(R.string.test_Risk_Score_text_good);
//        riskScoreText.setText("good hands");
//        riskScoreText.setTextColor(ContextCompat.getColor(view.getContext(), R.color.green));

        crpv2.setVisibility(View.INVISIBLE);
        riskScore.setText("215");
        riskScoreTitle.setText(R.string.test_Risk_Score_text_risk);
        riskScoreText.setText("Risk");
        riskScoreText.setTextColor(ContextCompat.getColor(view.getContext(), R.color.red));

        LinearLayout farmProgressButton = view.findViewById(R.id.farmProgressButton);
        TextView farmProgressText = view.findViewById(R.id.farmProgressText);

        farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_cultivating));
        farmProgressText.setText("Cultivating");

        farmProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String progress = farmProgressText.getText().toString();
                if(progress.equals("Cultivating")){

                    farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_planting));
                    farmProgressText.setText("Planting");

                } else if (progress.equals("Planting")) {

                    farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_growing));
                    farmProgressText.setText("Growing");

                } else  if (progress.equals("Growing")) {

                    farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_harvesting));
                    farmProgressText.setText("Harvesting");

                } else if (progress.equals("Harvesting")) {

                    farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_compleat));
                    farmProgressText.setText("Complete");

                } else if (progress.equals("Complete")) {

                    farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_cultivating));
                    farmProgressText.setText("Cultivating");

                }

            }
        });

        LineChart lineChart = view.findViewById(R.id.lineChart);

// Sample Data
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

// Line DataSet
        LineDataSet dataSet = new LineDataSet(entries, "Stock Price ($)");  // Fill color
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Enable smooth cubic lines
        dataSet.setColor(Color.parseColor("#7AF27B"));
        dataSet.setCircleColor(Color.parseColor("#7AF27B"));
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawFilled(true); // Enable filled area
        dataSet.setFillDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.gradient_farmer_chart)); // Set the gradient
        dataSet.setDrawValues(false);

// Line Data
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh chart

// Customization
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setGranularity(1f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);




        return view;
    }
}