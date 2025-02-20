package com.kavindu.farmshare.investor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherKt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.NotificationActivity;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.ChartEntruDto;
import com.kavindu.farmshare.dto.InvestItemDto;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.SearchDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.model.InvestItem;
import com.kavindu.farmshare.model.SQLiteHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class InvestorFarmsActivity extends AppCompatActivity {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    AtomicBoolean isInitialLoad = new AtomicBoolean(true);
    AtomicBoolean isInitialLoadInvestor = new AtomicBoolean(true);

    private ListenerRegistration investorListener;
    private ListenerRegistration commenListener;

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

        //firebase snapshot listener for commen
        commenListener = firestore.collection("commen")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FarmShareLog", "Listen failed.", error);
                            return;
                        }

                        firebaseNotification(snapshots);

                    }
                });

        SharedPreferences sharedPreferences = getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user",null);

        Gson gson = new Gson();
        UserDto user = gson.fromJson(userJson, UserDto.class);

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

                        firebaseNotificationInvestor(snapshots);

                    }
                });

        ImageView notificationButton = findViewById(R.id.investorFarmsNotification);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {

                Intent intent = new Intent(InvestorFarmsActivity.this, NotificationActivity.class);
                startActivity(intent);

            }
        });


        Sweetalert pDialog = new Sweetalert(InvestorFarmsActivity.this, Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create("", MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/investor/load-search-farms")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    SearchDto searchDto = gson.fromJson(response.body().string(), SearchDto.class);

                    if (searchDto.isSuccess()){

                        ArrayList<InvestItemDto> investItemDtoList = searchDto.getItemList();
                        ArrayList<InvestItem> investItemArrayList = new ArrayList<>();

                        for (InvestItemDto investItemDto : investItemDtoList){
                            List<Entry> entries = new ArrayList<>();
                            for (ChartEntruDto chartEntruDto : investItemDto.getChartData()){
                                entries.add(new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue()));
                            }

                            InvestItem investItem = new InvestItem();
                            investItem.setLost(investItemDto.getLost().equals("true"));
                            investItem.setPrice(investItemDto.getPrice());
                            investItem.setType(investItemDto.getType());
                            investItem.setTitle(investItemDto.getTitle());
                            investItem.setId(investItemDto.getId());
                            investItem.setChartData(entries);

                            investItemArrayList.add(investItem);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                investItemInflater(R.id.farmItemContainer,investItemArrayList);
                                pDialog.cancel();
                            }
                        });

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

        ImageView backButton  = findViewById(R.id.investorFarmBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        ImageView searchButton  = findViewById(R.id.imageView23);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText searchTxt = findViewById(R.id.editTextText8);
                if(!searchTxt.getText().toString().isEmpty()){
                    searchFarm(searchTxt.getText().toString());
                }


            }
        });



    }

    private void investItemInflater(int container,  ArrayList<InvestItem> itemArrayList){

        LinearLayout itemContainer = findViewById(container);

        itemContainer.removeAllViews();

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

                    Intent intent = new Intent(InvestorFarmsActivity.this, InvestorSingleFarmActivity.class);
                    intent.putExtra("farmId",investItem.getId());
                    startActivity(intent);

                }
            });

            itemContainer.addView(item);

        }

    }

    private void searchFarm(String searchTxt){

        Sweetalert pDialog = new Sweetalert(InvestorFarmsActivity.this, Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Searching");
        pDialog.setCancelable(false);
        pDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                RequestDto requestDto = new RequestDto();
                requestDto.setValue(searchTxt);

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto),MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/investor/search-farms")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    SearchDto searchDto = gson.fromJson(response.body().string(), SearchDto.class);

                    if (searchDto.isSuccess()){

                        ArrayList<InvestItemDto> investItemDtoList = searchDto.getItemList();
                        ArrayList<InvestItem> investItemArrayList = new ArrayList<>();

                        for (InvestItemDto investItemDto : investItemDtoList){
                            List<Entry> entries = new ArrayList<>();
                            for (ChartEntruDto chartEntruDto : investItemDto.getChartData()){
                                entries.add(new Entry(chartEntruDto.getDate(), (float) chartEntruDto.getValue()));
                            }

                            InvestItem investItem = new InvestItem();
                            investItem.setLost(investItemDto.getLost().equals("true"));
                            investItem.setPrice(investItemDto.getPrice());
                            investItem.setType(investItemDto.getType());
                            investItem.setTitle(investItemDto.getTitle());
                            investItem.setId(investItemDto.getId());
                            investItem.setChartData(entries);

                            investItemArrayList.add(investItem);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                investItemInflater(R.id.farmItemContainer,investItemArrayList);
                                pDialog.cancel();
                            }
                        });

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    private void firebaseNotification(QuerySnapshot snapshots){
        if (snapshots != null) {
            for (DocumentChange dc : snapshots.getDocumentChanges()) {

                if (isInitialLoad.get()) {
                    continue;
                }

                if (dc.getType().equals(DocumentChange.Type.ADDED)) {

                    DocumentSnapshot document = dc.getDocument();
                    String title = document.getString("title");
                    String text = document.getString("text");

                    SQLiteHelper sqLiteHelper = new SQLiteHelper(InvestorFarmsActivity.this, "farmShare.db", null, 1);

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
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);

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

                    Notification notification = new NotificationCompat.Builder(InvestorFarmsActivity.this, "C1")
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

    private void firebaseNotificationInvestor(QuerySnapshot snapshots){
        if (snapshots != null) {
            for (DocumentChange dc : snapshots.getDocumentChanges()) {

                if (isInitialLoadInvestor.get()) {
                    continue;
                }

                if (dc.getType().equals(DocumentChange.Type.ADDED)) {

                    DocumentSnapshot document = dc.getDocument();
                    String title = document.getString("title");
                    String text = document.getString("text");

                    SQLiteHelper sqLiteHelper = new SQLiteHelper(InvestorFarmsActivity.this, "farmShare.db", null, 1);

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
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);

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

                    Notification notification = new NotificationCompat.Builder(InvestorFarmsActivity.this, "C1")
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


