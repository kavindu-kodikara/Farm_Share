package com.kavindu.farmshare.investor;

import android.Manifest;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.ChartEntruDto;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.SingleFarmDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.farmer.FarmerAddFarmActivity;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class InvestorSingleFarmActivity extends AppCompatActivity {

    private GoogleMap map;
    private int farmId;
    private List<Entry> weekData = new ArrayList<>();
    private List<Entry> monthData = new ArrayList<>();
    private List<Entry> seasonData = new ArrayList<>();
    private boolean isPriceDrop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_investor_single_farm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String id = getIntent().getStringExtra("farmId");

        if (id == null){
            Intent intent = new Intent(InvestorSingleFarmActivity.this,InvestorMainActivity.class);
            startActivity(intent);
        }else{
            farmId = Integer.parseInt(id);
        }

        FrameLayout stockBuyContainer = findViewById(R.id.stockBuyFrameLayout);
        View buyView = getLayoutInflater().inflate(R.layout.fragment_stock_buy1,null);
        View investedView = getLayoutInflater().inflate(R.layout.fragment_stock_buy2,null);


        //load data
        Sweetalert pDialog = new Sweetalert(InvestorSingleFarmActivity.this, Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();

                SharedPreferences sharedPreferences = getSharedPreferences("com.kavindu.farmshare.data",Context.MODE_PRIVATE);
                String userJson = sharedPreferences.getString("user",null);
                UserDto userDto = gson.fromJson(userJson,UserDto.class);

                RequestDto requestDto = new RequestDto(farmId);
                requestDto.setValue(String.valueOf(userDto.getId()));

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/investor/load-single-farm")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    SingleFarmDto singleFarmDto = gson.fromJson(response.body().string(), SingleFarmDto.class);

                    if (singleFarmDto.isSuccess()){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                TextView codeName = findViewById(R.id.textView81);
                                TextView farmname = findViewById(R.id.textView82);
                                TextView stockPrice1 = findViewById(R.id.textView84);
                                TextView stockPriceCen = findViewById(R.id.textView85);
                                TextView stockValue = findViewById(R.id.textView86);
                                ImageView valueDropImage = findViewById(R.id.imageView24);
                                ImageView cropTypeImg = findViewById(R.id.investorSingleImageViewSelected);
                                TextView avgIncomePrice = findViewById(R.id.textView89);
                                TextView riskScore = findViewById(R.id.investorSingleriskScore);
                                TextView ownerName = findViewById(R.id.textView91);
                                TextView ownerDate = findViewById(R.id.textView92);
                                TextView seasonMonth = findViewById(R.id.textView98);
                                TextView farmSize = findViewById(R.id.textView94);
                                TextView avgYield = findViewById(R.id.textView96);
                                TextView farmStatus = findViewById(R.id.signFarmfarmProgressText);
                                ShapeableImageView profileImg = findViewById(R.id.shapeableImageView);
                                LinearLayout farmProgressButton = findViewById(R.id.signFarmfarmProgressButton);

                                ColorfulRingProgressView crpv1 =  findViewById(R.id.crpvInvestorSingle);
                                ColorfulRingProgressView crpv2 = findViewById(R.id.crpvInvestorSingle2);

                                isPriceDrop = singleFarmDto.isDrop();

                                codeName.setText(singleFarmDto.getCodeName());
                                farmname.setText(singleFarmDto.getFarmName());
                                stockPrice1.setText(singleFarmDto.getStockPrice());
                                String priceCent = "."+singleFarmDto.getStockPriceCents();
                                stockPriceCen.setText(priceCent);

                                String valueTxt = "Rs."+singleFarmDto.getValuePrice()+"("+singleFarmDto.getValuePercentage()+")";
                                stockValue.setText(valueTxt);
                                cropTypeImg.setImageResource(singleFarmDto.getFarmType().equals("Rice") ? R.drawable.rice : R.drawable.corn);
                                avgIncomePrice.setText(singleFarmDto.getAvgIncome());
                                ownerName.setText(singleFarmDto.getOwnerName());
                                ownerDate.setText(singleFarmDto.getOwnerDate());
                                seasonMonth.setText(singleFarmDto.getSeasonMonths());
                                farmSize.setText(singleFarmDto.getLandSize());
                                avgYield.setText(singleFarmDto.getAvgYield());
                                farmStatus.setText(singleFarmDto.getFarmStatus());

                                double lat = Double.parseDouble(singleFarmDto.getLat());
                                double lng = Double.parseDouble(singleFarmDto.getLng());

                                Log.i("FarmShareLog",String.valueOf(lat));
                                Log.i("FarmShareLog",String.valueOf(lng));

                                LatLng latLng = new LatLng(lat,lng);
                                loadMap(latLng);


                                if(singleFarmDto.getProfileImg() != null){
                                    Glide.with(InvestorSingleFarmActivity.this)
                                            .load(singleFarmDto.getProfileImg())
                                            .placeholder(R.drawable.loading)
                                            .into(profileImg);
                                }else{
                                    profileImg.setImageResource(R.drawable.globe);
                                }


                                if (singleFarmDto.isDrop()){
                                    valueDropImage.setImageResource(R.drawable.down_arrow);
                                    stockValue.setTextColor(ContextCompat.getColor(InvestorSingleFarmActivity.this,R.color.red));
                                }else{
                                    valueDropImage.setImageResource(R.drawable.up_arrow);
                                    stockValue.setTextColor(ContextCompat.getColor(InvestorSingleFarmActivity.this,R.color.green));
                                }

                                for (ChartEntruDto chartEntruDto : singleFarmDto.getWeekChartData()){
                                    weekData.add(new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue()));
                                }

                                for (ChartEntruDto chartEntruDto : singleFarmDto.getMonthChartData()){
                                    monthData.add(new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue()));
                                }

                                for (ChartEntruDto chartEntruDto : singleFarmDto.getSeasonChartData()){
                                    seasonData.add(new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue()));

                                }

                                  riskScore.setText(singleFarmDto.getRiskScore());
                                double riskS = Double.parseDouble(singleFarmDto.getRiskScore());

                                int percentage = (int) ((riskS * 100) / 200);

                                if (riskS > 120){
                                    crpv2.setVisibility(View.INVISIBLE);
                                    crpv1.setVisibility(View.VISIBLE);
                                    crpv1.setPercent(Math.min(percentage, 100));

                                }else {
                                    crpv1.setVisibility(View.INVISIBLE);
                                    crpv2.setVisibility(View.VISIBLE);
                                    crpv2.setPercent(Math.min(percentage, 100));

                                }

                                String progress = singleFarmDto.getFarmStatus();

                                switch (progress) {
                                    case "Start":
                                        farmProgressButton.setBackground(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_cultivating));

                                        break;
                                    case "Cultivating":
                                        farmProgressButton.setBackground(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_cultivating));


                                        break;
                                    case "Planting":
                                        farmProgressButton.setBackground(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_planting));

                                        break;
                                    case "Growing":
                                        farmProgressButton.setBackground(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_growing));

                                        break;
                                    case "Harvesting":
                                        farmProgressButton.setBackground(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_harvesting));

                                        break;
                                    case "Completed":
                                        farmProgressButton.setBackground(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_compleat));
                                        break;
                                }

                                RecyclerView rvImages = findViewById(R.id.investorSingleRecyclerView);

                                rvImages.setLayoutManager(new LinearLayoutManager(InvestorSingleFarmActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                ImageAdapter imageAdapter = new ImageAdapter(InvestorSingleFarmActivity.this, singleFarmDto.getImageList());
                                rvImages.setAdapter(imageAdapter);

                                if (singleFarmDto.isInvested()){
                                    Log.i("FarmShareLog","Invested");

                                    TextView investedStock = investedView.findViewById(R.id.textView108);
                                    TextView investedPercentage = investedView.findViewById(R.id.textView109);
                                    TextView investedIncome = investedView.findViewById(R.id.textView111);
                                    ImageView investedIncomeImg = investedView.findViewById(R.id.imageView110);

                                    investedStock.setText(singleFarmDto.getInvestedStock());
                                    investedPercentage.setText(singleFarmDto.getInvestedPercentage());
                                    investedIncome.setText(singleFarmDto.getExpectIncome());


                                    if (singleFarmDto.isInvestDrop()){
                                        investedIncomeImg.setImageResource(R.drawable.down_arrow);
                                        investedIncome.setTextColor(ContextCompat.getColor(InvestorSingleFarmActivity.this,R.color.red));
                                    }else{
                                        investedIncomeImg.setImageResource(R.drawable.up_arrow);
                                        investedIncome.setTextColor(ContextCompat.getColor(InvestorSingleFarmActivity.this,R.color.green));
                                    }

                                    stockBuyContainer.addView(investedView);
                                }else{
                                    Log.i("FarmShareLog","Not invested");

                                    LinearLayout buyButton = buyView.findViewById(R.id.singleFarmBuyButton);
                                    TextView txt1 = buyView.findViewById(R.id.textView102);
                                    TextView txt2 = buyView.findViewById(R.id.textView103);
                                    TextView txt3 = buyView.findViewById(R.id.textView106);
                                    ImageView img1 = buyView.findViewById(R.id.imageView105);

                                    txt1.setText(singleFarmDto.getStockPrice());
                                    txt2.setText(priceCent);
                                    txt3.setText(valueTxt);

                                    if (singleFarmDto.isDrop()){
                                        img1.setImageResource(R.drawable.down_arrow);
                                        txt3.setTextColor(ContextCompat.getColor(InvestorSingleFarmActivity.this,R.color.red));
                                    }else{
                                        img1.setImageResource(R.drawable.up_arrow);
                                        txt3.setTextColor(ContextCompat.getColor(InvestorSingleFarmActivity.this,R.color.green));
                                    }

                                    buyButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if(singleFarmDto.isStockReleased()){
                                                Intent intent = new Intent(InvestorSingleFarmActivity.this,StockBuyActivity.class);
                                                intent.putExtra("id",String.valueOf(farmId));
                                                startActivity(intent);
                                            }else {
                                                new Sweetalert(InvestorSingleFarmActivity.this, Sweetalert.ERROR_TYPE)
                                                        .setTitleText("Oops...")
                                                        .setContentText("Stock not released")
                                                        .show();
                                            }

                                        }
                                    });

                                    stockBuyContainer.addView(buyView);
                                }

                                loadChart(weekData,singleFarmDto.isDrop());

                                pDialog.cancel();
                            }
                        });
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();


        ImageView imageView = findViewById(R.id.investorSingleFarmBack);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        Chip chipWeek = findViewById(R.id.chip5);
        Chip chipMonth = findViewById(R.id.chip6);
        Chip chipSeason = findViewById(R.id.chip7);

        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (!checkedIds.isEmpty()) {
                    int checkedId = checkedIds.get(0);
                    if (checkedId == R.id.chip6) {
                        loadChart(monthData, isPriceDrop);
                    } else if (checkedId == R.id.chip5) {
                        loadChart(weekData, isPriceDrop);
                    } else if (checkedId == R.id.chip7) {
                        loadChart(seasonData, isPriceDrop);
                    }
                }
            }
        });



    }

    private void loadChart(List<Entry> entries, boolean isLost) {
        // Sort entries by X values to prevent rendering gaps
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                return Float.compare(e1.getX(), e2.getX());
            }
        });

        // Create dataset
        LineDataSet dataSet = new LineDataSet(entries, "price");
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setLineWidth(2f);
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setFillDrawable(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, android.R.color.transparent));

        if (isLost) {
            dataSet.setColor(Color.parseColor("#f27a7a"));
            dataSet.setFillDrawable(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_farmer_chart_red));
        } else {
            dataSet.setColor(Color.parseColor("#7AF27B"));
            dataSet.setFillDrawable(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_farmer_chart));
        }

        // Set data to chart
        LineData lineData = new LineData(dataSet);
        LineChart chart = findViewById(R.id.singleFarmLineChart);

        chart.clear();  // Clear previous data completely
        chart.setData(lineData);
        chart.invalidate();  // Refresh chart

        // Adjust X-Axis dynamically based on new dataset
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(findMinX(entries));
        xAxis.setAxisMaximum(findMaxX(entries));
        xAxis.setEnabled(false);

        // Configure Y-Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.resetAxisMaximum();
        leftAxis.resetAxisMinimum();
        leftAxis.setDrawLabels(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextColor(ContextCompat.getColor(this, R.color.textGray)); // Fix color fetching
        leftAxis.setTextSize(12f);

        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(1000);

        // Ensure interactive features are enabled
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);
        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);
    }

    private float findMinX(List<Entry> entries) {
        float min = Float.MAX_VALUE;
        for (Entry e : entries) {
            if (e.getX() < min) min = e.getX();
        }
        return min;
    }

    private float findMaxX(List<Entry> entries) {
        float max = Float.MIN_VALUE;
        for (Entry e : entries) {
            if (e.getX() > max) max = e.getX();
        }
        return max;
    }

    private void loadMap(LatLng latLng){
        //farm location google map
        SupportMapFragment supportMapFragment = new SupportMapFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.singleFarmMapContainer,supportMapFragment);
        fragmentTransaction.commit();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    googleMap.getUiSettings().setCompassEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);

                    googleMap.addMarker(
                            new MarkerOptions()
                                    .position(latLng)
                    );

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(12)
                                    .build()
                    ));



                }else {

                    map = googleMap;

                    String[] permissionArray = new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    };

                    requestPermissions(permissionArray,100);

                }



            }
        });
    }

}

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<String> imageUris;
    private Context context;

    public ImageAdapter(Context context, List<String> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_farm_image_item, parent, false);
        return new ImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(imageUris.get(position))
                .placeholder(R.drawable.loading)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.singleImgeShapeableImageView);
        }
    }
}