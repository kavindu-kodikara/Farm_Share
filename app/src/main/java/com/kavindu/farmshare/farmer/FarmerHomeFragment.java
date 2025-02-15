package com.kavindu.farmshare.farmer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.MainActivity;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.FarmerHomeDto;
import com.kavindu.farmshare.dto.NameIdDto;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.UserDto;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;


public class FarmerHomeFragment extends Fragment {



    public FarmerHomeFragment() {
        // Required empty public constructor
    }

    ColorfulRingProgressView crpv1;
    ColorfulRingProgressView crpv2;

    TextView riskScoreText;
    TextView riskScoreTitle;
    TextView riskScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_farmer_home, container, false);

        crpv1 = (ColorfulRingProgressView) view.findViewById(R.id.crpv);
        crpv1.setPercent(75);

        crpv2 = (ColorfulRingProgressView) view.findViewById(R.id.crpv2);
        crpv2.setPercent(35);

         riskScoreText = view.findViewById(R.id.riskScoreText);
         riskScoreTitle = view.findViewById(R.id.riskScoreTitle);
         riskScore = view.findViewById(R.id.riskScore);


        LinearLayout farmProgressButton = view.findViewById(R.id.farmProgressButton);
        TextView farmProgressText = view.findViewById(R.id.farmProgressText);

        farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_start));

        farmProgressText.setText("Start");

        farmProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String progress = farmProgressText.getText().toString();
                if(progress.equals("Start")){

                    farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_cultivating));
                    farmProgressText.setText("Cultivating");

                } else if(progress.equals("Cultivating")){

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

                    farmProgressButton.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.gradient_start));
                    farmProgressText.setText("Start");

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


        //soil repost document
        AtomicReference<Uri> soilReportFileUri = new AtomicReference<>();

        ImageView soilReportBtn = view.findViewById(R.id.homeSoilReportimageView);

        ActivityResultLauncher<String> soilReportfilePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        soilReportFileUri.set(uri);
                        Toast.makeText(view.getContext(), "Soil report file Selected: " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
                    }
                });

        soilReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soilReportfilePickerLauncher.launch("*/*");
            }
        });

        Button riskReviewButton = view.findViewById(R.id.riskReviewButton);
        riskReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),FarmerRiskReviewActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        //load data
        Sweetalert pDialog = new Sweetalert(view.getContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        SharedPreferences sp = getActivity().getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
        String user = sp.getString("user",null);

        if (user != null){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    OkHttpClient okHttpClient = new OkHttpClient();
                    Gson gson = new Gson();



                    UserDto userDto = gson.fromJson(user,UserDto.class);
                    RequestDto requestDto = new RequestDto(userDto.getId());

                    RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(BuildConfig.URL+"/farmer-home/load-home")
                            .post(requestBody)
                            .build();

                    try {

                        Response response = okHttpClient.newCall(request).execute();
                        FarmerHomeDto farmerHomeDto = gson.fromJson(response.body().string(), FarmerHomeDto.class);

                        ArrayList<NameIdDto> chipArrayList = farmerHomeDto.getChipArray();

                        if(farmerHomeDto.isSuccess()){
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    //add chips
                                    ChipGroup chipGroup = view.findViewById(R.id.homeChipgroup);

                                    int heightInPixels = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, 55, view.getContext().getResources().getDisplayMetrics());


                                    for (int i = 0; i < chipArrayList.size(); i++) {

                                        NameIdDto nameIdDto = chipArrayList.get(i);

                                        Chip chip = new Chip(view.getContext());
                                        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT, heightInPixels);

                                        chip.setLayoutParams(params);

                                        chip.setCheckable(true);
                                        chip.setChecked(i == 0);
                                        chip.setText(nameIdDto.getName());
                                        ColorStateList textColor = ContextCompat.getColorStateList(view.getContext(), R.color.chip_text_color);
                                        chip.setTextColor(textColor);
                                        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                        ColorStateList chipBackgroundColor = ContextCompat.getColorStateList(view.getContext(), R.color.chip_selector);
                                        chip.setChipBackgroundColor(chipBackgroundColor);
                                        chip.setChipStrokeWidth(0);

                                        chip.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View clickCiew) {
                                                Toast.makeText(view.getContext(), String.valueOf(nameIdDto.getId()), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        chipGroup.addView(chip);
                                    }


                                    chipGroup.invalidate();
                                    updateData(farmerHomeDto,view);
                                    pDialog.cancel();
                                }
                            });

                        }else {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.cancel();
                                    new Sweetalert(view.getContext(), Sweetalert.ERROR_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText("Something went wrong")
                                            .show();
                                }
                            });

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            Intent intent = new Intent(view.getContext(), FarmerSignInActivity.class);
                            startActivity(intent);
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }).start();
        }



        return view;
    }

    private void updateData(FarmerHomeDto farmerHomeDto,View parent){

        int percentage = (int) ((farmerHomeDto.getRiskScore() * 100) / 200);

        if (farmerHomeDto.getRiskScore() > 120){
            crpv2.setVisibility(View.INVISIBLE);
            riskScore.setText(String.valueOf(farmerHomeDto.getRiskScore()));
            riskScoreTitle.setText(R.string.test_Risk_Score_text_risk);
            riskScoreText.setText("Risk");
            riskScoreText.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.red));
            crpv1.setPercent(Math.min(percentage, 100));

        }else {
            crpv1.setVisibility(View.INVISIBLE);
            riskScore.setText(String.valueOf(farmerHomeDto.getRiskScore()));
            riskScoreTitle.setText(R.string.test_Risk_Score_text_good);
            riskScoreText.setText("good hands");
            riskScoreText.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.green));
            crpv2.setPercent(Math.min(percentage, 100));

        }

        TextView totStockTxt = parent.findViewById(R.id.textView19);
        TextView relesedStockTxt = parent.findViewById(R.id.textView21);
        TextView expIncomeTxt = parent.findViewById(R.id.textView23);
        TextView stockProgressTxt = parent.findViewById(R.id.textView26);
        TextView dateTxt = parent.findViewById(R.id.textView24);
        View stockProgressBackBar = parent.findViewById(R.id.view2);
        View stockProgressFrontBar = parent.findViewById(R.id.view3);

        String totStock = "S "+String.valueOf(farmerHomeDto.getTotStock());
        String relesedStock = "S "+String.valueOf(farmerHomeDto.getRelesedStock());
        String expIncome = "Rs. "+ new DecimalFormat("#,###").format(farmerHomeDto.getExpectIncome())+" .00";
        String stockProgressStock = "S "+String.valueOf(farmerHomeDto.getStockProgress());
        String today = new SimpleDateFormat("d  MMMM  yyyy", Locale.getDefault()).format(new Date());

        totStockTxt.setText(totStock);
        relesedStockTxt.setText(relesedStock);
        expIncomeTxt.setText(expIncome);
        stockProgressTxt.setText(stockProgressStock);
        dateTxt.setText(today);

        int maxValue = farmerHomeDto.getRelesedStock();
        int currentValue = farmerHomeDto.getStockProgress();
        int vBackWidth = stockProgressBackBar.getWidth();
        float viewPercentage = (float) currentValue / maxValue;
        int vFrontWidth = (int) (viewPercentage * vBackWidth);
        stockProgressFrontBar.getLayoutParams().width = vFrontWidth;
        stockProgressFrontBar.requestLayout();





    }
}