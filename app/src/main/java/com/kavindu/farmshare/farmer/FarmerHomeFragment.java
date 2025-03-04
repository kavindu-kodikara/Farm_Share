package com.kavindu.farmshare.farmer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.MainActivity;
import com.kavindu.farmshare.NotificationActivity;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.ChartEntruDto;
import com.kavindu.farmshare.dto.FarmerHomeDto;
import com.kavindu.farmshare.dto.NameIdDto;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.SeasonStartDto;
import com.kavindu.farmshare.dto.SoilReportDto;
import com.kavindu.farmshare.dto.StockAllocationTableItemDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.model.SQLiteHelper;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
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

    int farmId;
    Uri soilReportFileUri;

    ImageView soilReportBtn;

    TextView phText ;
    TextView moistureText;
    TextView organicText;
    TextView nutrientText;

    String startDate = null;
    String endDate = null;

    String farmName = "";

    DatePickerDialog datePickerDialog;

    TextView farmProgressText;
    LinearLayout farmProgressButton;

    private ListenerRegistration investorListener;
    private ListenerRegistration commenListener;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    AtomicBoolean isInitialLoad = new AtomicBoolean(true);
    AtomicBoolean isInitialLoadInvestor = new AtomicBoolean(true);



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_farmer_home, container, false);



        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user",null);

        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(userJson, UserDto.class);



        //firebase snapshot listener for commen
        commenListener = firestore.collection("commen")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FarmShareLog", "Listen failed.", error);
                            return;
                        }

                        firebaseNotification(snapshots,view);

                    }
                });



        //firebase snapshot listener for investor
        investorListener = firestore.collection("farmer")
                .whereEqualTo("userId", String.valueOf(userDto.getId()))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FarmShareLog", "Listen failed.", error);
                            return;
                        }

                        firebaseNotificationInvestor(snapshots,view);

                    }
                });


        ImageView notificationButton = view.findViewById(R.id.imageView7);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {

                Intent intent = new Intent(view.getContext(), NotificationActivity.class);
                startActivity(intent);

            }
        });

        crpv1 = (ColorfulRingProgressView) view.findViewById(R.id.crpv);
        crpv1.setPercent(75);

        crpv2 = (ColorfulRingProgressView) view.findViewById(R.id.crpv2);
        crpv2.setPercent(35);

         riskScoreText = view.findViewById(R.id.riskScoreText);
         riskScoreTitle = view.findViewById(R.id.riskScoreTitle);
         riskScore = view.findViewById(R.id.riskScore);
        LineChart lineChart = view.findViewById(R.id.lineChart);

         farmProgressButton = view.findViewById(R.id.farmProgressButton);
         farmProgressText = view.findViewById(R.id.farmProgressText);

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
         soilReportBtn = view.findViewById(R.id.homeSoilReportimageView);

        ActivityResultLauncher<String> soilReportfilePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        soilReportFileUri = uri;
                        Toast.makeText(view.getContext(), "Soil report file Selected: " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
                        soilReportBtn.setImageResource(R.drawable.check);
                    }
                });

        soilReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soilReportfilePickerLauncher.launch("image/*");
            }
        });

        //submit soil report
        Button button = view.findViewById(R.id.buttonSoilSubmit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {
                 phText = view.findViewById(R.id.soileditTextText9);
                 moistureText = view.findViewById(R.id.soileditTextText10);
                 organicText = view.findViewById(R.id.soileditTextText11);
                 nutrientText = view.findViewById(R.id.soileditTextText12);

                if (phText.getText().toString().isBlank()){
                    showErrorDialog("Please enter ph level",view);
                }else if (moistureText.getText().toString().isBlank()){
                    showErrorDialog("Please enter moisture level",view);
                }else if (organicText.getText().toString().isBlank()){
                    showErrorDialog("Please enter organic level",view);
                }else if (nutrientText.getText().toString().isBlank()){
                    showErrorDialog("Please enter nutrient level",view);
                }else if (soilReportFileUri == null){
                    showErrorDialog("Please select soil report",view);
                }else{

                    SoilReportDto soilReportDto = new SoilReportDto();

                    soilReportDto.setPh(phText.getText().toString());
                    soilReportDto.setMoisture(moistureText.getText().toString());
                    soilReportDto.setOrganic(organicText.getText().toString());
                    soilReportDto.setNutrient(nutrientText.getText().toString());

                    uploadImageToFirebase(soilReportFileUri,view,soilReportDto);

                }


            }
        });


        Button riskReviewButton = view.findViewById(R.id.riskReviewButton);
        riskReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),FarmerRiskReviewActivity.class);
                intent.putExtra("id",farmId);
                view.getContext().startActivity(intent);
            }
        });

        //stock relese
        Button stockReleseButton = view.findViewById(R.id.stockReleaseButton);
        EditText stockReleseEditText = view.findViewById(R.id.editTextNumber2);
                stockReleseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {

                if (!stockReleseEditText.getText().toString().isBlank()){

                    Sweetalert pDialog2 = new Sweetalert(view.getContext(), Sweetalert.PROGRESS_TYPE);
                    pDialog2.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog2.setTitleText("Processing");
                    pDialog2.setCancelable(false);
                    pDialog2.show();

                    RequestDto requestDto = new RequestDto();
                    requestDto.setId(farmId);
                    requestDto.setValue(stockReleseEditText.getText().toString());

                    Gson gson = new Gson();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = RequestBody.create(gson.toJson(requestDto),MediaType.get("application/json"));

                            Request request = new Request.Builder()
                                    .url(BuildConfig.URL+"/farm/release-stock")
                                    .post(requestBody)
                                    .build();

                            try {

                                Response response = okHttpClient.newCall(request).execute();
                                ResponseDto responseDto = gson.fromJson(response.body().string(), ResponseDto.class);

                                if (responseDto.isSuccess()){

                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            stockReleseEditText.setText("");
                                            pDialog2.cancel();
                                            new Sweetalert(view.getContext(), Sweetalert.SUCCESS_TYPE)
                                                    .setTitleText("Success")
                                                    .setContentText("Stock released")
                                                    .show();
                                        }
                                    });

                                    loadFarmData(farmId,view);

                                }else{
                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pDialog2.cancel();
                                            showErrorDialog(responseDto.getMessage(),view);
                                        }
                                    });
                                }

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }).start();

                }else{
                    showErrorDialog("Enter stock amount",view);
                }


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

                                                loadFarmData(nameIdDto.getId(),view);
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
                                    new Sweetalert(view.getContext(), Sweetalert.WARNING_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText("Add new farm")
                                            .show();
                                }
                            });

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            Intent intent = new Intent(view.getContext(), FarmerAddFarmActivity.class);
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

    private void loadFarmData(int id, View parent){
        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestDto requestDto = new RequestDto(id);
                Gson gson = new Gson();

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto),MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/farmer-home/load-home-farm")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    FarmerHomeDto farmerHomeDto = gson.fromJson(response.body().string(), FarmerHomeDto.class);

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateData(farmerHomeDto,parent);
                        }
                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    private void updateData(FarmerHomeDto farmerHomeDto,View parent){

        farmName = farmerHomeDto.getFarmName();

        int percentage = (int) ((farmerHomeDto.getRiskScore() * 100) / 200);

        if (farmerHomeDto.getRiskScore() > 120){
            crpv2.setVisibility(View.INVISIBLE);
            crpv1.setVisibility(View.VISIBLE);
            riskScore.setText(String.valueOf(farmerHomeDto.getRiskScore()));
            riskScoreTitle.setText(R.string.test_Risk_Score_text_risk);
            riskScoreText.setText("Risk");
            riskScoreText.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.red));
            crpv1.setPercent(Math.min(percentage, 100));

        }else {
            crpv1.setVisibility(View.INVISIBLE);
            crpv2.setVisibility(View.VISIBLE);
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
        TextView farmName = parent.findViewById(R.id.textView14);
        TextView totStockSr = parent.findViewById(R.id.textView37);
        TextView singleStockTxt = parent.findViewById(R.id.textView144);
        TextView releasedStockSr = parent.findViewById(R.id.textView139);
        View stockProgressBackBar = parent.findViewById(R.id.view2);
        View stockProgressFrontBar = parent.findViewById(R.id.view3);

        String totStock = "S "+String.valueOf(farmerHomeDto.getTotStock());
        String relesedStock = "S "+String.valueOf(farmerHomeDto.getRelesedStock());
        String expIncome = "Rs. "+ new DecimalFormat("#,###").format(farmerHomeDto.getExpectIncome())+" .00";
        String singleStickPrice = "Rs. "+farmerHomeDto.getSingleStockPrice();
        String stockProgressStock = "S "+String.valueOf(farmerHomeDto.getStockProgress());
        String today = new SimpleDateFormat("d  MMMM  yyyy", Locale.getDefault()).format(new Date());

        totStockTxt.setText(totStock);
        relesedStockTxt.setText(relesedStock);
        expIncomeTxt.setText(expIncome);
        stockProgressTxt.setText(stockProgressStock);
        dateTxt.setText(today);farmName.setText(farmerHomeDto.getFarmName());
        totStockSr.setText(totStock);
        releasedStockSr.setText(relesedStock);
        farmProgressText.setText(farmerHomeDto.getFarmStatus());
        singleStockTxt.setText(singleStickPrice);

        if (farmerHomeDto.isPriceDrop()){
            singleStockTxt.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.red));
        }else {
            singleStockTxt.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.green));
        }

        Log.i("FarmShareLog",String.valueOf(farmerHomeDto.getRelesedStock()));

        if (farmerHomeDto.getRelesedStock() > 0 && farmerHomeDto.getStockProgress() > 0){
            int maxValue = farmerHomeDto.getRelesedStock();
            int currentValue = farmerHomeDto.getStockProgress() ;
            int vBackWidth = stockProgressBackBar.getWidth();
            float viewPercentage = (float) currentValue / maxValue;
            int vFrontWidth = (int) (viewPercentage * vBackWidth);
            stockProgressFrontBar.getLayoutParams().width = vFrontWidth;
            stockProgressFrontBar.requestLayout();
        }else{
            stockProgressFrontBar.getLayoutParams().width = 30;
            stockProgressFrontBar.requestLayout();
        }



        //load chart
        List<Entry> entries = new ArrayList<>();

        int ci = 1;
        for (ChartEntruDto chartEntruDto : farmerHomeDto.getChartEntryList()){
            entries.add(new Entry(ci, (float) chartEntruDto.getValue()));
            ci++;
        }

        loadChart(parent,entries, farmerHomeDto.isPriceDrop());

        LinearLayout tableConatiner = parent.findViewById(R.id.investorTableDataContainer);
        TextView investorCount = parent.findViewById(R.id.textView28);

        investorCount.setText(farmerHomeDto.getInvestorsCount());

        farmId = farmerHomeDto.getFarmId();

        for (int i = tableConatiner.getChildCount() - 1; i >= 0; i--) {
            View child = tableConatiner.getChildAt(i);
            if (child.getId() != R.id.headerRow) {
                tableConatiner.removeViewAt(i);
            }
        }

        for (StockAllocationTableItemDto tableData : farmerHomeDto.getTableItemList()){

            View tableItem = getLayoutInflater().inflate(R.layout.fragment_farmer_home_investor_table_item,null);

            ImageView image = tableItem.findViewById(R.id.imageView12);
            TextView name = tableItem.findViewById(R.id.textView32);
            TextView stock = tableItem.findViewById(R.id.textView33);
            TextView amount = tableItem.findViewById(R.id.textView34);

            image.setImageResource(R.drawable.globe);
            name.setText(tableData.getName());
            stock.setText(tableData.getStock());
            amount.setText(tableData.getAmount());

            tableConatiner.addView(tableItem);

        }

        //load status button

        String progress = farmerHomeDto.getFarmStatus();


        if(progress.equals("Start")){
            farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_start));
            farmProgressButton.setOnClickListener(null);
            farmProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewClick) {

                    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                    View startDialog = layoutInflater.inflate(R.layout.farm_status_alert_dialog,null,false);

                    ImageView imageView = startDialog.findViewById(R.id.imageView13);
                    ImageView imageView2 = startDialog.findViewById(R.id.imageView14);
                    TextView selectedDateStart = startDialog.findViewById(R.id.textView141);
                    TextView selectedDateEnd = startDialog.findViewById(R.id.textView142);
                    LinearLayout startDialogButton = startDialog.findViewById(R.id.startDialogButton);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewClick2) {
                            // Get the current date
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            // Create and show DatePickerDialog
                            DatePickerDialog datePickerDialog = new DatePickerDialog(parent.getContext(),
                                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                                        // Show the selected date (Month is zero-based, so add 1)
                                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                        selectedDateStart.setText("Start Date: " + selectedDate);
                                        startDate = selectedDate;
                                    }, year, month, day);

                            datePickerDialog.show();
                        }
                    });

                    imageView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewClick2) {
                            // Get the current date
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            // Create and show DatePickerDialog
                            DatePickerDialog datePickerDialog = new DatePickerDialog(parent.getContext(),
                                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                                        // Show the selected date (Month is zero-based, so add 1)
                                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                        selectedDateEnd.setText("End Date: " + selectedDate);
                                        endDate = selectedDate;
                                    }, year, month, day);

                            datePickerDialog.show();
                        }
                    });

                    startDialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View viewClick) {

                            Sweetalert pDialog = new Sweetalert(parent.getContext(), Sweetalert.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("Updating...");
                            pDialog.setCancelable(false);
                            pDialog.show();


                            if (startDate == null){
                                pDialog.cancel();
                                showErrorDialog("Please select start date",parent);
                            } else if (endDate == null) {
                                pDialog.cancel();
                                showErrorDialog("Please select end date",parent);
                            }else{

                                farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_cultivating));
                                farmProgressText.setText("Cultivating");

                                Gson gson = new Gson();
                                SeasonStartDto seasonStartDto = new SeasonStartDto();
                                seasonStartDto.setStartDate(startDate);
                                seasonStartDto.setEndDate(endDate);
                                seasonStartDto.setFarmId(farmId);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        OkHttpClient okHttpClient = new OkHttpClient();
                                        RequestBody requestBody = RequestBody.create(gson.toJson(seasonStartDto),MediaType.get("application/json"));
                                        Request request = new Request.Builder()
                                                .url(BuildConfig.URL+"/farm/season-start")
                                                .post(requestBody)
                                                .build();

                                        try {

                                            Response response = okHttpClient.newCall(request).execute();
                                            ResponseDto responseDto = gson.fromJson(response.body().string(), ResponseDto.class);

                                            Thread.sleep(500);

                                            if (responseDto.isSuccess()){
                                                loadFarmData(farmId,parent);
                                                requireActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pDialog.cancel();
                                                        new Sweetalert(parent.getContext(), Sweetalert.SUCCESS_TYPE)
                                                                .setTitleText("Success")
                                                                .setContentText(responseDto.getMessage())
                                                                .show();

                                                        selectedDateStart.setText("Start date");
                                                        selectedDateEnd.setText("End date");
                                                        startDate = null;
                                                        endDate = null;

                                                    }
                                                });
                                            }else{
                                                requireActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pDialog.cancel();
                                                        showErrorDialog(responseDto.getMessage(), parent);
                                                    }
                                                });
                                            }

                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                }).start();

                            }



                        }
                    });

                    new AlertDialog.Builder(parent.getContext()).setView(startDialog).show();
                }
            });

        }else if(progress.equals("Cultivating")){
            farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_cultivating));
            farmProgressButton.setOnClickListener(null);
            farmProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewClick) {
                    updateFarmStatus("Planting",parent);
                    farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_planting));
                    farmProgressText.setText("Planting");
                }
            });



        } else if (progress.equals("Planting")) {
            Log.i("FarmShareLog",progress);
            farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_planting));

            farmProgressButton.setOnClickListener(null);
            farmProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewClick) {
                    updateFarmStatus("Growing",parent);
                    farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_growing));
                    farmProgressText.setText("Growing");
                }
            });



        } else  if (progress.equals("Growing")) {
            farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_growing));

            farmProgressButton.setOnClickListener(null);
            farmProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewClick) {
                    updateFarmStatus("Harvesting",parent);
                    farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_harvesting));
                    farmProgressText.setText("Harvesting");
                }
            });



        } else if (progress.equals("Harvesting")) {
            farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_harvesting));

            farmProgressButton.setOnClickListener(null);
            farmProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewClick) {

                    Intent intent =  new Intent(parent.getContext(), FarmerPaymentActivity.class);
                    intent.putExtra("id",farmId);
                    startActivity(intent);

//                    updateFarmStatus("Completed",parent);
//                    farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_compleat));
//                    farmProgressText.setText("Completed");

                }
            });


        } else if (progress.equals("Completed")) {
            farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_compleat));

            farmProgressButton.setOnClickListener(null);
//            farmProgressButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View viewClick) {
//                    updateFarmStatus("Start",parent);
//                    farmProgressButton.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.gradient_start));
//                    farmProgressText.setText("Start");
//
//                }
//            });


        }

    }

    private void loadChart(View parent,List<Entry> entries , boolean isPriceDrop){
        LineChart lineChart = parent.findViewById(R.id.lineChart);


// Line DataSet
        LineDataSet dataSet = new LineDataSet(entries, "Stock Price ($)");  // Fill color
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Enable smooth cubic lines
        dataSet.setColor(Color.parseColor("#7AF27B"));
        dataSet.setCircleColor(Color.parseColor("#7AF27B"));
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawFilled(true); // Enable filled area
        dataSet.setFillDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_farmer_chart)); // Set the gradient
        dataSet.setDrawValues(false);

        if(isPriceDrop){
            dataSet.setColor(Color.parseColor("#f27a7a"));
            dataSet.setCircleColor(Color.parseColor("#f27a7a"));
            dataSet.setFillDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.gradient_farmer_chart_red));
        }

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


    }

    private void uploadImageToFirebase(Uri imageUri,View parent,SoilReportDto soilReportDto) {

        Sweetalert pDialog = new Sweetalert(parent.getContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String imageUrl = uri.toString();
                                Log.d("Firebase", "Image URL: " + imageUrl);

                                soilReportDto.setDocument(imageUrl);
                                soilReportDto.setFarmId(farmId);

                                pDialog.setTitleText("Uploading");

                                Gson gson = new Gson();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        OkHttpClient okHttpClient = new OkHttpClient();

                                        RequestBody requestBody = RequestBody.create(gson.toJson(soilReportDto),MediaType.get("application/json"));
                                        Request request = new Request.Builder()
                                                .url(BuildConfig.URL+"/farm/update-soil-report")
                                                .post(requestBody)
                                                .build();

                                        try {

                                            Response response = okHttpClient.newCall(request).execute();
                                            ResponseDto responseDto = gson.fromJson(response.body().string(), ResponseDto.class);

                                            if (responseDto.isSuccess()){
                                                requireActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pDialog.cancel();
                                                        new Sweetalert(parent.getContext(), Sweetalert.SUCCESS_TYPE)
                                                                .setTitleText("Success")
                                                                .setContentText(responseDto.getMessage())
                                                                .show();

                                                        phText.setText("");
                                                        nutrientText.setText("");
                                                        moistureText.setText("");
                                                        organicText.setText("");
                                                        soilReportFileUri = null;

                                                        soilReportBtn.setImageResource(R.drawable.addfolder);
                                                    }
                                                });
                                            }else{
                                                requireActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pDialog.cancel();
                                                        new Sweetalert(parent.getContext(), Sweetalert.ERROR_TYPE)
                                                                .setTitleText("Oops...")
                                                                .setContentText("Something went wrong try again")
                                                                .show();
                                                    }
                                                });
                                            }

                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }).start();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firebase", "Failed to get download URL", e);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Upload failed", e);
                    }
                });
    }

    private void showErrorDialog(String message,View parent){
        new Sweetalert(parent.getContext(), Sweetalert.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .show();
    }

    private void updateFarmStatus(String status,View parent){
        Sweetalert pDialog = new Sweetalert(parent.getContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Updating...");
        pDialog.setCancelable(false);
        pDialog.show();

        Gson gson = new Gson();
        RequestDto requestDto = new RequestDto();
        requestDto.setValue(status);
        requestDto.setId(farmId);

        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto),MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/farm/update-farm-status")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    ResponseDto responseDto = gson.fromJson(response.body().string(), ResponseDto.class);

                    Thread.sleep(500);

                    if (responseDto.isSuccess()){
                        loadFarmData(farmId,parent);

                        String text = "Exciting news! farm "+farmName+" has moved to the "+status+" stage. Stay tuned for progress updates!";

                        HashMap<String,String> document = new HashMap<>();
                        document.put("title","Farm Status Updated!");
                        document.put("text",text);

                        firestore.collection("commen").add(document)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.i("FarmShareLog","inserted");
                                    }
                                })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("FarmShareLog","insert fail");
                                    }
                                });

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.cancel();
                                new Sweetalert(parent.getContext(), Sweetalert.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText(responseDto.getMessage())
                                        .show();
                            }
                        });
                    }else{
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.cancel();
                                showErrorDialog("Something went wrong try again",parent);
                            }
                        });
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

    }

    private void firebaseNotification(QuerySnapshot snapshots,View parent){
        if (snapshots != null) {
            for (DocumentChange dc : snapshots.getDocumentChanges()) {

                if (isInitialLoad.get()) {
                    continue;
                }

                if (dc.getType().equals(DocumentChange.Type.ADDED)) {

                    DocumentSnapshot document = dc.getDocument();
                    String title = document.getString("title");
                    String text = document.getString("text");

                    SQLiteHelper sqLiteHelper = new SQLiteHelper(parent.getContext(), "farmShareFarmer.db", null, 1);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("title",title);
                            contentValues.put("text",text);

                            long id = sqLiteDatabase.insert("notification",null,contentValues);
                            Log.i("FarmShareLog","Sqlite id : "+String.valueOf(id));

                            sqLiteDatabase.close();

                        }
                    }).start();

                    //notification
                    NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);

                    if (notificationManager == null) {
                        Log.e("NotificationError", "NotificationManager is null");
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(
                                "C1",
                                "Channel1",
                                NotificationManager.IMPORTANCE_HIGH
                        );
                        notificationManager.createNotificationChannel(notificationChannel);
                    }

                    Notification notification = new NotificationCompat.Builder(requireContext(), "C1")
                            .setContentTitle(title)
                            .setContentText(text)
                            .setSmallIcon(R.drawable.newlogo2)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1, notification);
                }
            }

            isInitialLoad.set(false);
        }
    }

    private void firebaseNotificationInvestor(QuerySnapshot snapshots,View parent){
        if (snapshots != null) {
            for (DocumentChange dc : snapshots.getDocumentChanges()) {

                if (isInitialLoadInvestor.get()) {
                    continue;
                }

                if (dc.getType().equals(DocumentChange.Type.ADDED)) {

                    DocumentSnapshot document = dc.getDocument();
                    String title = document.getString("title");
                    String text = document.getString("text");

                    SQLiteHelper sqLiteHelper = new SQLiteHelper(parent.getContext(), "farmShareFarmer.db", null, 1);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("title",title);
                            contentValues.put("text",text);

                            long id = sqLiteDatabase.insert("notification",null,contentValues);
                            Log.i("FarmShareLog","Sqlite id : "+String.valueOf(id));

                            sqLiteDatabase.close();

                        }
                    }).start();

                    //notification
                    NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);

                    if (notificationManager == null) {
                        Log.e("NotificationError", "NotificationManager is null");
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(
                                "C1",
                                "Channel1",
                                NotificationManager.IMPORTANCE_HIGH
                        );
                        notificationManager.createNotificationChannel(notificationChannel);
                    }

                    Notification notification = new NotificationCompat.Builder(requireContext(), "C1")
                            .setContentTitle(title)
                            .setContentText(text)
                            .setSmallIcon(R.drawable.newlogo2)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1, notification);
                }
            }

            isInitialLoadInvestor.set(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (investorListener != null) {
            investorListener.remove();
        }

        if (commenListener != null) {
            commenListener.remove();
        }
    }

}