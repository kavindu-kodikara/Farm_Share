package com.kavindu.farmshare.farmer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.ChartEntruDto;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.RiskReviewDto;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class FarmerRiskReviewActivity extends AppCompatActivity {

    int fardId;

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

        //load data
        Intent intent = getIntent();
        fardId = intent.getIntExtra("id",0);

        Sweetalert pDialog = new Sweetalert(FarmerRiskReviewActivity.this, Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                RequestDto requestDto = new RequestDto();
                requestDto.setId(fardId);

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/farm/load-risk-review")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    RiskReviewDto riskReviewDto = gson.fromJson(response.body().string(),RiskReviewDto.class);

                    ArrayList<Entry> weatherEntryList = new ArrayList<>();
                    ArrayList<Entry> soilEntryList = new ArrayList<>();

                    for (ChartEntruDto chartEntruDto : riskReviewDto.getWeatherChartList()){
                        Entry entry = new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue());
                        weatherEntryList.add(entry);

                    }

                    for (ChartEntruDto chartEntruDto : riskReviewDto.getSoilChartList()){
                        Entry entry = new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue());
                        soilEntryList.add(entry);

                    }

                    ColorfulRingProgressView crpv1 = findViewById(R.id.riskChart2);
                    ColorfulRingProgressView crpv2  = findViewById(R.id.riskChart1);
                    TextView riskScore  = findViewById(R.id.riskScoreText);

                    int percentage = (int) ((Double.parseDouble(riskReviewDto.getRiskScore()) * 100) / 200);

                    Thread.sleep(500);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.cancel();
                                loadWeatherChart(weatherEntryList);
                                loadSoilChart(soilEntryList);

                                if (riskReviewDto.isRisk()){
                                    crpv2.setVisibility(View.INVISIBLE);
                                    crpv1.setVisibility(View.VISIBLE);
                                    riskScore.setText(riskReviewDto.getRiskScore());
                                    crpv1.setPercent(Math.min(percentage, 100));

                                }else {
                                    crpv1.setVisibility(View.INVISIBLE);
                                    crpv2.setVisibility(View.VISIBLE);
                                    riskScore.setText(riskReviewDto.getRiskScore());
                                    crpv2.setPercent(Math.min(percentage, 100));

                                }

                            }
                        });

                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }
        }).start();







    }

    private void loadWeatherChart(ArrayList<Entry> entryList){
        //wether chart

        LineChart lineChart1 = findViewById(R.id.riskLineChart1);
        LineDataSet dataSet = new LineDataSet(entryList, "Weather");  // Fill color
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
    }

    private void loadSoilChart(ArrayList<Entry> entryList){
        //Soil chart

        LineChart lineChart2 = findViewById(R.id.riskLineChart2);

        LineDataSet dataSet2 = new LineDataSet(entryList, "Soil");  // Fill color
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