package com.kavindu.farmshare.farmer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.investor.InvestorSignUpActivity;
import com.kavindu.farmshare.investor.InvoiceActivity;
import com.kavindu.farmshare.investor.StockBuyActivity;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class FarmerPaymentActivity extends AppCompatActivity {

    int farmId;
    double totPrice;

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

                                Sweetalert pDialog = new Sweetalert(FarmerPaymentActivity.this, Sweetalert.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Processing");
                                pDialog.setCancelable(false);
                                pDialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Gson gson = new Gson();
                                        RequestDto requestDto = new RequestDto(farmId);
                                        OkHttpClient okHttpClient = new OkHttpClient();
                                        RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                                        Request request = new Request.Builder()
                                                .url(BuildConfig.URL+"/farm/make-payment")
                                                .post(requestBody)
                                                .build();

                                        okHttpClient.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                Log.e("FarmShareLog","Request fail");
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                                ResponseDto responseDto = gson.fromJson(response.body().string(),ResponseDto.class);

                                                if (responseDto.isSuccess()){
                                                    try {
                                                        Thread.sleep(500);
                                                    } catch (InterruptedException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            TextView textView = findViewById(R.id.textView156);
                                                            textView.setText("Rs. 0 .00");
                                                            pDialog.cancel();
                                                            updateFarmStatus("Completed");

                                                        }
                                                    });

                                                }

                                            }
                                        });


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
        setContentView(R.layout.activity_farmer_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        farmId = getIntent().getIntExtra("id",0);
        Button button = findViewById(R.id.button3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                RequestDto requestDto = new RequestDto(farmId);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/farm/load-payment")
                        .post(requestBody)
                        .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e("FarmShareLog","Request fail");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            ResponseDto responseDto = gson.fromJson(response.body().string(),ResponseDto.class);

                            if (responseDto.isSuccess()){

                                String priceText = "Rs. "+ new DecimalFormat("#,###").format(Double.parseDouble(responseDto.getMessage()));
                                Log.i("FarmShareLog",priceText);
                                totPrice = Double.parseDouble(responseDto.getMessage());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        button.setVisibility(View.VISIBLE);
                                        TextView textView = findViewById(R.id.textView156);
                                        textView.setText(priceText);
                                    }
                                });

                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        button.setVisibility(View.INVISIBLE);
                                        new Sweetalert(FarmerPaymentActivity.this, Sweetalert.WARNING_TYPE)
                                                .setTitleText("You don't have any cash payment")
                                                .setContentText("")
                                                .show();
                                    }
                                });
                            }

                        }
                    });


            }
        }).start();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPaymentgatway();
            }
        });

    }

    private void openPaymentgatway(){
        InitRequest req = new InitRequest();
        req.setMerchantId("1221102");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(1000);             // Final Amount to be charged
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

    private void updateFarmStatus(String status){

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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Sweetalert(FarmerPaymentActivity.this, Sweetalert.SUCCESS_TYPE)
                                        .setTitleText("Payment sent")
                                        .setContentText("")
                                        .show();
                            }
                        });

                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        Intent intent = new Intent(FarmerPaymentActivity.this,FarmerMainActivity.class);
                        startActivity(intent);
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