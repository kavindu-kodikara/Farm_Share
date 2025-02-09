package com.kavindu.farmshare.investor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.farmer.FarmerAddFarmActivity;

import java.util.ArrayList;
import java.util.List;

public class InvestorSingleFarmActivity extends AppCompatActivity {

    private GoogleMap map;

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

        ImageView imageView = findViewById(R.id.investorSingleFarmBack);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 50));
        entries.add(new Entry(2, 55));
        entries.add(new Entry(3, 52));
        entries.add(new Entry(4, 60));
        entries.add(new Entry(5, 58));
        entries.add(new Entry(6, 65));
        entries.add(new Entry(7, 62));
        entries.add(new Entry(8, 70));
        entries.add(new Entry(9, 75));
        entries.add(new Entry(10, 68));
        entries.add(new Entry(11, 80));
        entries.add(new Entry(12, 78));
        entries.add(new Entry(13, 85));
        entries.add(new Entry(14, 63));
        entries.add(new Entry(15, 60));
        entries.add(new Entry(16, 68));
        entries.add(new Entry(17, 70));
        entries.add(new Entry(18, 73));
        entries.add(new Entry(19, 75));
        entries.add(new Entry(20, 70));


        loadChart(entries,false);

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


        RecyclerView rvImages = findViewById(R.id.investorSingleRecyclerView);
        List<Integer> imageUris = new ArrayList<>();
        imageUris.add(R.drawable.img1);
        imageUris.add(R.drawable.img2);
        imageUris.add(R.drawable.img3);
        imageUris.add(R.drawable.img4);

        rvImages.setLayoutManager(new LinearLayoutManager(InvestorSingleFarmActivity.this, LinearLayoutManager.HORIZONTAL, false));
        ImageAdapter imageAdapter = new ImageAdapter(InvestorSingleFarmActivity.this, imageUris);
        rvImages.setAdapter(imageAdapter);

        FrameLayout stockButContainer = findViewById(R.id.stockBuyFrameLayout);
        View buyView = getLayoutInflater().inflate(R.layout.fragment_stock_buy1,null);
        LinearLayout buyButton = buyView.findViewById(R.id.singleFarmBuyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InvestorSingleFarmActivity.this,StockBuyActivity.class);
                startActivity(intent);
            }
        });

//        View buyView = getLayoutInflater().inflate(R.layout.fragment_stock_buy2,null);
        stockButContainer.addView(buyView);





    }

    private void loadChart(List<Entry> entries,boolean isLost){

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setLineWidth(2f);
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);
        dataSet.setFillDrawable(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, android.R.color.transparent));
        dataSet.setDrawCircles(false);

        if (isLost){
            dataSet.setColor(Color.parseColor("#f27a7a"));
            dataSet.setFillDrawable(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_farmer_chart_red));
        }else {

            dataSet.setColor(Color.parseColor("#7AF27B"));
            dataSet.setFillDrawable(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_farmer_chart));

        }


        dataSet.setColor(Color.parseColor("#7AF27B"));
        dataSet.setFillDrawable(ContextCompat.getDrawable(InvestorSingleFarmActivity.this, R.drawable.gradient_farmer_chart));


        LineData lineData = new LineData(dataSet);

        LineChart chart = findViewById(R.id.singleFarmLineChart);

        chart.setData(lineData);
        chart.invalidate();


        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.getXAxis().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.animateY(1000);

        chart.getAxisLeft().setEnabled(true);
        chart.getAxisLeft().setDrawLabels(true);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setTextColor(R.color.textGray);
        chart.getAxisLeft().setTextSize(12f);

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);

        chart.setHighlightPerTapEnabled(true);
        chart.setHighlightPerDragEnabled(true);
    }
}

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<Integer> imageUris;
    private Context context;

    public ImageAdapter(Context context, List<Integer> imageUris) {
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

        holder.imageView.setImageResource(imageUris.get(position));
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