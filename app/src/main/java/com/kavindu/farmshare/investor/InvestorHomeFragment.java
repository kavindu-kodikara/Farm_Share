package com.kavindu.farmshare.investor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.MainActivity;
import com.kavindu.farmshare.NotificationActivity;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.ChartEntruDto;
import com.kavindu.farmshare.dto.InvestItemDto;
import com.kavindu.farmshare.dto.InvestorHomeDto;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.StockAllocationDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.model.HotItemBean;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kavindu.farmshare.model.InvestItem;
import com.kavindu.farmshare.model.PayoutItem;
import com.kavindu.farmshare.model.SQLiteHelper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class InvestorHomeFragment extends Fragment {


    UserDto user;
    View viewParent;

    private ListenerRegistration investorListener;
    private ListenerRegistration commenListener;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    AtomicBoolean isInitialLoad = new AtomicBoolean(true);
    AtomicBoolean isInitialLoadInvestor = new AtomicBoolean(true);

    public InvestorHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_investor_home, container, false);
        viewParent = view;

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user",null);

        Gson gson = new Gson();

        if (userJson == null){
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent);
        }else{

            user = gson.fromJson(userJson, UserDto.class);
        }

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
        investorListener = firestore.collection("investor")
                .whereEqualTo("userId", String.valueOf(user.getId()))
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



        TextView fnameText = view.findViewById(R.id.textView70);
        fnameText.setText(user.getFname());


        ImageView menuButton = view.findViewById(R.id.investorHomeMenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
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

        ImageView notificationButton = view.findViewById(R.id.investorHomeNotification);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {

                Intent intent = new Intent(view.getContext(), NotificationActivity.class);
                startActivity(intent);

            }
        });




        RecyclerView hotItemRecyclerView = view.findViewById(R.id.hotItemRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        hotItemRecyclerView.setLayoutManager(linearLayoutManager);


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


        //load stock allocation chart
//        loadStockAllocationChart(view);



        //load data from server
        Sweetalert pDialog = new Sweetalert(view.getContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Gson gson = new Gson();
                RequestDto requestDto = new RequestDto(user.getId());

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/investor/load-home")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    InvestorHomeDto investorHomeDto = gson.fromJson(response.body().string(), InvestorHomeDto.class);

                    if (investorHomeDto.isSuccess()){

                        //load hot items
                        ArrayList<HotItemBean> hotItemBeans = investorHomeDto.getHotList();

                        //load popular items
                        ArrayList<InvestItemDto> popularItemList = (ArrayList<InvestItemDto>) investorHomeDto.getPopularList();
                        ArrayList<InvestItem> popularItemArrayList = new ArrayList<>();

                        for (InvestItemDto investItemDto : popularItemList){
                            InvestItem investItem = new InvestItem();
                            investItem.setId(investItemDto.getId());
                            investItem.setType(investItemDto.getType());
                            investItem.setTitle(investItemDto.getTitle());
                            investItem.setValue(investItemDto.getValue());
                            investItem.setPrice(investItemDto.getPrice());
                            investItem.setLost(investItemDto.getLost().equals("true"));

                            List<Entry> chartData = new ArrayList<>();

                            for (ChartEntruDto chartEntruDto : investItemDto.getChartData()){
                                chartData.add(new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue()));
                            }

                            investItem.setChartData(chartData);
                            popularItemArrayList.add(investItem);


                        }


                        //load stock holding
                        ArrayList<InvestItemDto>stockHItemList = (ArrayList<InvestItemDto>) investorHomeDto.getStockHoldingList();
                        ArrayList<InvestItem> stockHItemArrayList = new ArrayList<>();

                        for (InvestItemDto investItemDto : stockHItemList){
                            InvestItem investItem = new InvestItem();
                            investItem.setId(investItemDto.getId());
                            investItem.setType(investItemDto.getType());
                            investItem.setTitle(investItemDto.getTitle());
                            investItem.setValue(investItemDto.getValue());
                            investItem.setPrice(investItemDto.getPrice());
                            investItem.setLost(investItemDto.getLost().equals("true"));

                            List<Entry> chartData = new ArrayList<>();

                            for (ChartEntruDto chartEntruDto : investItemDto.getChartData()){
                                chartData.add(new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue()));
                            }

                            investItem.setChartData(chartData);
                            stockHItemArrayList.add(investItem);


                        }


                        //load stock allocation
                        ArrayList<PieEntry> pieEntryList = new ArrayList<>();
                        for (StockAllocationDto stockAllocationDto : investorHomeDto.getAllocationList()){
                            double percentage = (stockAllocationDto.getValue() / investorHomeDto.getAllocationTot()) * 100;
                            pieEntryList.add(new PieEntry((float) percentage,stockAllocationDto.getName()));
                        }

                        String allocationTot = "Rs. "+ new DecimalFormat("#,###").format(investorHomeDto.getAllocationTot());

                        //load payouts
                        ArrayList<PayoutItem> payoutItems = (ArrayList<PayoutItem>) investorHomeDto.getPayoutItemList();

                        Thread.sleep(500);

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hotItemRecyclerView.setAdapter(new HotItemAdapter(hotItemBeans));
                                investItemInflater(R.id.popularItemLinearLayout,view,popularItemArrayList);
                                stockHoldingItemInflater(view,stockHItemArrayList);
                                TextView totText = view.findViewById(R.id.textView122);
                                totText.setText(allocationTot);
                                loadStockAllocationChart(view,pieEntryList);
                                payoutItemInflater(view,payoutItems);
                                pDialog.cancel();
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
            value.setText(investItem.getValue());

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
                    intent.putExtra("farmId",investItem.getId());
                    parent.getContext().startActivity(intent);
                }
            });

            itemContainer.addView(item);

        }

    }

    private void stockHoldingItemInflater(View parent, ArrayList<InvestItem> itemArrayList){

        LinearLayout itemContainer = parent.findViewById(R.id.SHmyItemLinearLayout);

        for (InvestItem investItem : itemArrayList){

            View item = getLayoutInflater().inflate(R.layout.fragment_investor_stock_holding_item,null);

            ImageView image = item.findViewById(R.id.SHstockHoldingItemImageView22);
            TextView title = item.findViewById(R.id.SHitemDesigntextView120);
            TextView value = item.findViewById(R.id.SHitemDesigntextView121);
            TextView price = item.findViewById(R.id.SHitemDesigntextView122);
            LineChart chart = item.findViewById(R.id.SHstockHoldingItemLineChart);
            ConstraintLayout itemButton = item.findViewById(R.id.SHstockHoldingItemConstraintLayout8);

            title.setText(investItem.getTitle());
            price.setText(investItem.getPrice());
            value.setText(investItem.getValue());

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
                    intent.putExtra("farmId",investItem.getId());
                    parent.getContext().startActivity(intent);
                }
            });

            itemContainer.addView(item);

        }

    }

    private void payoutItemInflater(View parent, ArrayList<PayoutItem> itemArrayList){

        LinearLayout itemContainer = parent.findViewById(R.id.upcomingPaymentsContainer);

        for (PayoutItem payoutItem : itemArrayList){

            View item = getLayoutInflater().inflate(R.layout.fragment_upcoming_payout_item,null);

            TextView title = item.findViewById(R.id.textView125);
            TextView date = item.findViewById(R.id.textView126);
            TextView returnType = item.findViewById(R.id.textView128);
            TextView price = item.findViewById(R.id.textView129);

            title.setText(payoutItem.getTitle());
            price.setText(payoutItem.getPrice());
            date.setText(payoutItem.getDate());
            returnType.setText(payoutItem.getReturnType());

            if(payoutItem.getReturnType().equals("Cash")){
                returnType.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.warm_orange));
            }else if(payoutItem.getReturnType().equals("Crop")){
                returnType.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.green));
            }

                itemContainer.addView(item);

        }

    }

    private void loadStockAllocationChart(View parent,ArrayList<PieEntry> pieEntryList){
        PieChart pieChart1 = parent.findViewById(R.id.pieChart);

        // Create Data Set
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");

        // Generate Random Colors
        ArrayList<Integer> colorArrayList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < pieEntryList.size(); i++) {
            colorArrayList.add(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }

        pieDataSet.setColors(colorArrayList);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);

        // Create Pie Data
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(18f);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        Legend legend = pieChart1.getLegend();
        legend.setTextSize(16f);
        legend.setFormSize(10f);

        // Set Data & Style
        pieChart1.setData(pieData);
        pieChart1.setUsePercentValues(true);
        pieChart1.setDrawHoleEnabled(true);
        pieChart1.setHoleRadius(40f);
        pieChart1.setTransparentCircleRadius(45f);
        pieChart1.setDrawEntryLabels(false);
        pieChart1.getDescription().setEnabled(false);


        // Animate & Refresh
        pieChart1.animateY(2000, Easing.EaseInCirc);
        pieChart1.invalidate();
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

                    SQLiteHelper sqLiteHelper = new SQLiteHelper(parent.getContext(), "farmShare.db", null, 1);

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

                    SQLiteHelper sqLiteHelper = new SQLiteHelper(parent.getContext(), "farmShare.db", null, 1);

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
                Intent intent = new Intent(holder.itemView.getContext(), InvestorSingleFarmActivity.class);
                intent.putExtra("farmId",hotItemBean.getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}