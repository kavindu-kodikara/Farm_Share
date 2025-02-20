package com.kavindu.farmshare.farmer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.HumanVerifyActivity;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.UserDto;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class FarmerSignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_farmer_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText mobileEditText = findViewById(R.id.FSignInEditTextPhone);
        EditText passwordEditText = findViewById(R.id.FSignInEditTextPassword);

        startAnimations();

        mobileEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.fSignInMobileBg,R.id.fSignInMobileIcon,R.id.FSignIntextView3,R.id.FSignInEditTextPhone);
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.fSignInPasswordBg,R.id.fSignInPasswordIcon,R.id.FSignIntextView4,R.id.FSignInEditTextPassword);
            }
        });

        ImageView backImageView = findViewById(R.id.FSignInBackImageView1);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        LinearLayout button2 = findViewById(R.id.FSignInsignInButton2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        LinearLayout signInButton = findViewById(R.id.FSignInsignInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.fSignInMobileBg,R.id.fSignInMobileIcon,R.id.FSignIntextView3,R.id.FSignInEditTextPhone);
                    errorAnimation(R.id.FSignInmobileLayout);

                } else if (passwordEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.fSignInPasswordBg,R.id.fSignInPasswordIcon,R.id.FSignIntextView4,R.id.FSignInEditTextPassword);
                    errorAnimation(R.id.FSignInPasswordLayout);

                } else{

                    UserDto reqUserDto = new UserDto();
                    reqUserDto.setMobile(mobileEditText.getText().toString());
                    reqUserDto.setPassword(passwordEditText.getText().toString());
                    reqUserDto.setUserType("farmer");

                    Gson gson = new Gson();

                    Sweetalert pDialog = new Sweetalert(FarmerSignInActivity.this, Sweetalert.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Processing");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            OkHttpClient okHttpClient = new OkHttpClient();

                            RequestBody requestBody = RequestBody.create(gson.toJson(reqUserDto), MediaType.get("application/json"));
                            Request request = new Request.Builder()
                                    .url(BuildConfig.URL+"/user/sign-in")
                                    .post(requestBody)
                                    .build();

                            try {

                                Response response = okHttpClient.newCall(request).execute();
                                ResponseDto<UserDto> responseDto = gson.fromJson(response.body().string(), new TypeToken<ResponseDto<UserDto>>(){}.getType());

                                pDialog.cancel();

                                if (responseDto.isSuccess()){

                                    UserDto respUserDto = responseDto.getData();

                                    SharedPreferences sp = getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("user", gson.toJson(respUserDto));
                                    editor.apply();





                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Sweetalert(FarmerSignInActivity.this, Sweetalert.SUCCESS_TYPE)
                                                    .setTitleText("Success")
                                                    .setContentText(responseDto.getMessage())
                                                    .show();
                                        }
                                    });

                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    Intent intent = new Intent(FarmerSignInActivity.this, HumanVerifyActivity.class);
                                    intent.putExtra("type","Farmer");
                                    startActivity(intent);

                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Sweetalert(FarmerSignInActivity.this, Sweetalert.ERROR_TYPE)
                                                    .setTitleText("Oops...")
                                                    .setContentText(responseDto.getMessage())
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
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimations();
    }

    private void startAnimations(){

        LinearLayout mobileLayout = findViewById(R.id.FSignInmobileLayout);
        LinearLayout passwordLayout = findViewById(R.id.FSignInPasswordLayout);
        LinearLayout button1 = findViewById(R.id.FSignInsignInButton);
        LinearLayout button2 = findViewById(R.id.FSignInsignInButton2);

        mobileLayout.setVisibility(View.INVISIBLE);
        passwordLayout.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mobileLayout.setVisibility(View.VISIBLE);
                mobileLayout.startAnimation(AnimationUtils.loadAnimation(FarmerSignInActivity.this,R.anim.from_bottom_fade_in));
            }
        },200);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                passwordLayout.setVisibility(View.VISIBLE);
                passwordLayout.startAnimation(AnimationUtils.loadAnimation(FarmerSignInActivity.this,R.anim.from_bottom_fade_in));
            }
        },400);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button1.setVisibility(View.VISIBLE);
                button1.startAnimation(AnimationUtils.loadAnimation(FarmerSignInActivity.this,R.anim.from_bottom_fade_in));
            }
        },600);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button2.setVisibility(View.VISIBLE);
                button2.startAnimation(AnimationUtils.loadAnimation(FarmerSignInActivity.this,R.anim.from_bottom_fade_in));
            }
        },800);

    }

    private void errorAnimation(int id){
        LinearLayout layout = findViewById(id);
        layout.startAnimation(AnimationUtils.loadAnimation(FarmerSignInActivity.this,R.anim.shake_animation));
    }

    private void setErrorColor(boolean isError,int backgroundId, int iconId,int titleId,int editTextId){
        LinearLayout background = findViewById(backgroundId);
        ImageView icon = findViewById(iconId);
        TextView title = findViewById(titleId);
        EditText editText = findViewById(editTextId);

        int color;
        int titleColor;
        Drawable bgDrawable;

        if(isError){
            color = ContextCompat.getColor(FarmerSignInActivity.this,R.color.error_red);
            titleColor = color;
            bgDrawable = ContextCompat.getDrawable(FarmerSignInActivity.this,R.drawable.custon_error_text_view_bg);

        }else {
            color = ContextCompat.getColor(FarmerSignInActivity.this,R.color.textGray);
            titleColor = ContextCompat.getColor(FarmerSignInActivity.this,R.color.black);
            bgDrawable = ContextCompat.getDrawable(FarmerSignInActivity.this,R.drawable.custom_textview_background);
        }


        background.setBackground(bgDrawable);
        icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        title.setTextColor(titleColor);
        editText.setHintTextColor(color);
    }

}

