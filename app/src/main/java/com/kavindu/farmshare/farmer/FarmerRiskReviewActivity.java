package com.kavindu.farmshare.farmer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kavindu.farmshare.R;

import java.util.ArrayList;
import java.util.List;

public class FarmerRiskReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_farmer_risk_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.riskReviewBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        //wether chart

        LineChart lineChart1 = findViewById(R.id.riskLineChart1);


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


        LineDataSet dataSet = new LineDataSet(entries, "Weather");  // Fill color
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Enable smooth cubic lines
        dataSet.setColor(Color.parseColor("#7AE2F2"));
        dataSet.setCircleColor(Color.parseColor("#7AE2F2"));
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_farmer_chart_wether)); // Set the gradient
        dataSet.setDrawValues(false);


        LineData lineData = new LineData(dataSet);
        lineChart1.setData(lineData);
        lineChart1.invalidate();


        lineChart1.getDescription().setEnabled(false);
        lineChart1.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart1.getAxisRight().setEnabled(false);
        lineChart1.getXAxis().setGranularity(1f);

        XAxis xAxis = lineChart1.getXAxis();
        xAxis.setDrawGridLines(false);


        //Soil chart

        LineChart lineChart2 = findViewById(R.id.riskLineChart2);


        List<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(1990, 60));
        entries2.add(new Entry(1994, 30));
        entries2.add(new Entry(1998, 90));
        entries2.add(new Entry(2002, 60));
        entries2.add(new Entry(2006, 100));
        entries2.add(new Entry(2010, 70));
        entries2.add(new Entry(2014, 30));
        entries2.add(new Entry(2018, 80));
        entries2.add(new Entry(2022, 120));


        LineDataSet dataSet2 = new LineDataSet(entries, "Soil");  // Fill color
        dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Enable smooth cubic lines
        dataSet2.setColor(Color.parseColor("#F2B87A"));
        dataSet2.setCircleColor(Color.parseColor("#F2B87A"));
        dataSet2.setLineWidth(2f);
        dataSet2.setValueTextSize(12f);
        dataSet2.setDrawFilled(true);
        dataSet2.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.gradient_farmer_chart_soil)); // Set the gradient
        dataSet2.setDrawValues(false);


        LineData lineData2 = new LineData(dataSet2);
        lineChart2.setData(lineData2);
        lineChart2.invalidate();


        lineChart2.getDescription().setEnabled(false);
        lineChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart2.getAxisRight().setEnabled(false);
        lineChart2.getXAxis().setGranularity(1f);

        XAxis xAxis2 = lineChart2.getXAxis();
        xAxis2.setDrawGridLines(false);


    }
}