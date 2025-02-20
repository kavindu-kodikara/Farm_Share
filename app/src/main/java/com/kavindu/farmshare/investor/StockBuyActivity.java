package com.kavindu.farmshare.investor;

import android.app.Activity;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.PaymentDto;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.StockPageLoadDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.model.SQLiteHelper;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class StockBuyActivity extends AppCompatActivity {

    String farmId;
    StockPageLoadDto data;
    double tot;

    ImageView image;
    TextView codeName;
    TextView farmName;
    TextView stockPrice;
    EditText minStock;

    private ListenerRegistration investorListener;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    AtomicBoolean isInitialLoadInvestor = new AtomicBoolean(true);

    PaymentDto paymentDto = new PaymentDto();


    private  final ActivityResultLauncher<Intent> payHareLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)){
                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if(serializable instanceof PHResponse){
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                            if (response.isSuccess()){
                                Log.i("FarmShareLog","success");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Gson gson = new Gson();
                                        SharedPreferences sp = getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
                                        String userJson = sp.getString("user",null);
                                        UserDto userDto = gson.fromJson(userJson, UserDto.class);

                                        paymentDto.setUserId(userDto.getId());

                                        OkHttpClient okHttpClient = new OkHttpClient();
                                        RequestBody requestBody = RequestBody.create(gson.toJson(paymentDto),MediaType.get("application/json"));
                                        Request request = new Request.Builder()
                                                .url(BuildConfig.URL+"/stock/make-payment")
                                                .post(requestBody)
                                                .build();

                                        try {

                                            Response httpResponse = okHttpClient.newCall(request).execute();
                                            ResponseDto responseDto = gson.fromJson(httpResponse.body().string(), ResponseDto.class);

                                            if(responseDto.isSuccess()){

                                                String text = "Congrats! You’ve successfully invested in farm "+farmName.getText().toString()+". Watch your stocks grow! ";

                                                HashMap<String,String> document = new HashMap<>();
                                                document.put("title","Investment Successful!");
                                                document.put("text",text);
                                                document.put("userId",String.valueOf(userDto.getId()));

                                                firestore.collection("investor").add(document)
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

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        TextView totPriceTextView = findViewById(R.id.textView117);
                                                        EditText stockEditText = findViewById(R.id.editTextNumber);
                                                        String price = "Rs. 0.00";
                                                        totPriceTextView.setText(price);
                                                        stockEditText.setText("");

                                                        Intent intent = new Intent(StockBuyActivity.this,InvoiceActivity.class);
                                                        intent.putExtra("price",String.valueOf(paymentDto.getPrice()));
                                                        intent.putExtra("farmName",farmName.getText().toString());
                                                        intent.putExtra("stockCount",String.valueOf(paymentDto.getStockCount()));
                                                        startActivity(intent);
                                                    }
                                                });
                                            }

                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                }).start();
                            }else{
                                Log.i("FarmShareLog","fail");
                            }
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_buy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

        farmId = getIntent().getStringExtra("id");

        Sweetalert pDialog = new Sweetalert(StockBuyActivity.this, Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        Spinner spinner = findViewById(R.id.spinner2);
        String[] types = new String[]{"Select","Cash","Crop"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                StockBuyActivity.this,
                android.R.layout.simple_spinner_item,
                types
        );

        spinner.setAdapter(arrayAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                RequestDto requestDto = new RequestDto(Integer.parseInt(farmId));

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/stock/load-page")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    StockPageLoadDto stockPageLoadDto = gson.fromJson(response.body().string(), StockPageLoadDto.class);

                    if (stockPageLoadDto.isSuccess()){
                        data = stockPageLoadDto;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                 image = findViewById(R.id.imageView25);
                                 codeName = findViewById(R.id.textView110);
                                 farmName = findViewById(R.id.textView113);
                                 stockPrice = findViewById(R.id.textView114);
                                 minStock = findViewById(R.id.editTextNumber);

                                if (stockPageLoadDto.getType().equals("Rice")){
                                    image.setImageResource(R.drawable.rice);
                                }else if (stockPageLoadDto.getType().equals("Corn")){
                                    image.setImageResource(R.drawable.corn);
                                }

                                String codeNameTxt = "Buy "+stockPageLoadDto.getCodeName();
                                String stockPriceTxt = "Rs. "+String.valueOf(stockPageLoadDto.getStockPrice())+"/Stock";

                                codeName.setText(codeNameTxt);
                                stockPrice.setText(stockPriceTxt);
                                farmName.setText(stockPageLoadDto.getFarmName());
                                minStock.setHint(String.valueOf(stockPageLoadDto.getMinCount()));

                                pDialog.cancel();
                            }
                        });

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

        ImageView backButton = findViewById(R.id.stockBuyBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getOnBackPressedDispatcher().onBackPressed();

            }
        });

        EditText stockEditText = findViewById(R.id.editTextNumber);
        TextView totPriceTextView = findViewById(R.id.textView117);

        stockEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {

                    int number = Integer.parseInt(charSequence.toString());

                    if (data.getAvailableCount() >= number){
                        double totPrice = data.getStockPrice() * number;
                        String price = "Rs. "+ new DecimalFormat("#,###").format(totPrice);
                        totPriceTextView.setText(price);
                    }else{
                        String message = "Maximum stock count is : " +data.getAvailableCount();
                        new Sweetalert(StockBuyActivity.this, Sweetalert.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(message)
                                .show();
                    }



                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        LinearLayout buyButton = findViewById(R.id.investorStockBuyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!stockEditText.getText().toString().isBlank()){
                    int stockCount = Integer.parseInt(stockEditText.getText().toString());

                    if (stockCount >= data.getAvailableCount()){
                        new Sweetalert(StockBuyActivity.this, Sweetalert.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Invalid stock count")
                                .show();
                    }else if(stockCount <= data.getMinCount()){
                        String message = "Stock count has to be more than : "+data.getMinCount();
                        new Sweetalert(StockBuyActivity.this, Sweetalert.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(message)
                                .show();
                    }else if(spinner.getSelectedItem().toString().equals("Select")){
                        new Sweetalert(StockBuyActivity.this, Sweetalert.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Please select return type")
                                .show();
                    }else{

                        //payment gateway
                        tot = stockCount * data.getStockPrice();
                        paymentDto.setFarmId(Integer.parseInt(farmId));
                        paymentDto.setReturnType(spinner.getSelectedItem().toString());
                        paymentDto.setPrice(tot);
                        paymentDto.setStockCount(stockCount);

                        openPaymentgatway();
                    }

                }else{
                    new Sweetalert(StockBuyActivity.this, Sweetalert.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Please enter stock count")
                            .show();
                }
            }
        });

    }

    private void openPaymentgatway(){
        InitRequest req = new InitRequest();
        req.setMerchantId("1221102");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(tot);             // Final Amount to be charged
        req.setOrderId("230000123");        // Unique Reference ID
        req.setItemsDescription("Door bell wireless");  // Item description title
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName("Saman");
        req.getCustomer().setLastName("Perera");
        req.getCustomer().setEmail("samanp@gmail.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

//Optional Params
//        req.setNotifyUrl(“xxxx”);           // Notifiy Url
//        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
//        req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
//        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
//        req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);

        payHareLauncher.launch(intent);
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

                    SQLiteHelper sqLiteHelper = new SQLiteHelper(StockBuyActivity.this, "farmShare.db", null, 1);

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

                    Notification notification = new NotificationCompat.Builder(StockBuyActivity.this, "C1")
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
    protected void onDestroy() {
        super.onDestroy();
        if (investorListener != null) {
            investorListener.remove();
        }
    }
}