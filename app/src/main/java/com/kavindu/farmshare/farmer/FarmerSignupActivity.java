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
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kavindu.farmshare.BuildConfig;
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

public class FarmerSignupActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_farmer_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText mobileEditText = findViewById(R.id.farmerEditTextMobile);
        EditText fnameEditText = findViewById(R.id.farmerEditTextfname);
        EditText lnameEditText = findViewById(R.id.farmerEditTextlname);
        EditText passwordEditText = findViewById(R.id.farmerEditTextPassword);
        EditText rePasswordEditText = findViewById(R.id.farmerEditTextPassword2);

        startAnimations();

        mobileEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.farmerMobileBg,R.id.farmerMobileIcon,R.id.textView8,R.id.farmerEditTextMobile);
            }
        });

        fnameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.farmerFnameBg,R.id.farmerFnameIcon,R.id.textView9,R.id.farmerEditTextfname);
            }
        });

        lnameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.farmerLnameBg,R.id.farmerLnameIcon,R.id.textView10,R.id.farmerEditTextlname);
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.farmerPasswordBg,R.id.farmerPasswordIcon,R.id.textView11,R.id.farmerEditTextPassword);
            }
        });

        rePasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.farmerRePasswordBg,R.id.farmerRePasswordIcon,R.id.textView12,R.id.farmerEditTextPassword2);
            }
        });

        ImageView backImage = findViewById(R.id.backImageView1);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        LinearLayout signUpButton2 = findViewById(R.id.signUpButton2);
        signUpButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FarmerSignupActivity.this, FarmerSignInActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mobileEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.farmerMobileBg,R.id.farmerMobileIcon,R.id.textView8,R.id.farmerEditTextMobile);
                    errorAnimation(R.id.mobileLayout);

                } else if (fnameEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.farmerFnameBg,R.id.farmerFnameIcon,R.id.textView9,R.id.farmerEditTextfname);
                    errorAnimation(R.id.fnameLayout);

                } else if (lnameEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.farmerLnameBg,R.id.farmerLnameIcon,R.id.textView10,R.id.farmerEditTextlname);
                    errorAnimation(R.id.lnameLayout);

                } else if (passwordEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.farmerPasswordBg,R.id.farmerPasswordIcon,R.id.textView11,R.id.farmerEditTextPassword);
                    errorAnimation(R.id.pwLayout);

                } else if (rePasswordEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.farmerRePasswordBg,R.id.farmerRePasswordIcon,R.id.textView12,R.id.farmerEditTextPassword2);
                    errorAnimation(R.id.pwConformLayout);

                } else{

                    UserDto userDto = new UserDto();
                    userDto.setMobile(mobileEditText.getText().toString());
                    userDto.setFname(fnameEditText.getText().toString());
                    userDto.setLname(lnameEditText.getText().toString());
                    userDto.setPassword(passwordEditText.getText().toString());
                    userDto.setRePassword(rePasswordEditText.getText().toString());
                    userDto.setUserType("farmer");

                    Gson gson = new Gson();

                    Sweetalert pDialog = new Sweetalert(FarmerSignupActivity.this, Sweetalert.PROGRESS_TYPE);
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

                            RequestBody requestBody = RequestBody.create(gson.toJson(userDto), MediaType.get("application/json"));
                            Request request = new Request.Builder()
                                    .url(BuildConfig.URL+"/user/sign-up")
                                    .post(requestBody)
                                    .build();

                            try {

                                Response response = okHttpClient.newCall(request).execute();

                                ResponseDto<UserDto> responseDto = gson.fromJson(response.body().string(), new TypeToken<ResponseDto<UserDto>>(){}.getType());

                                UserDto responseUser = responseDto.getData();


                                pDialog.cancel();

                                if (responseDto.isSuccess()){

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Sweetalert(FarmerSignupActivity.this, Sweetalert.SUCCESS_TYPE)
                                                    .setTitleText("Success")
                                                    .setContentText(responseDto.getMessage())
                                                    .show();
                                        }
                                    });



                                    SharedPreferences sp = getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("user",gson.toJson(responseUser));
                                    editor.apply();

                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    Intent intent = new Intent(FarmerSignupActivity.this, FarmerMainActivity.class);
                                    startActivity(intent);

                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Sweetalert(FarmerSignupActivity.this, Sweetalert.ERROR_TYPE)
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


//
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimations();
    }

    private void startAnimations(){
        LinearLayout mobileLayout = findViewById(R.id.mobileLayout);
        LinearLayout fnameLayout = findViewById(R.id.fnameLayout);
        LinearLayout lnameLayout = findViewById(R.id.lnameLayout);
        LinearLayout pwLayout = findViewById(R.id.pwLayout);
        LinearLayout pwConformLayout = findViewById(R.id.pwConformLayout);
        LinearLayout signUpButton = findViewById(R.id.signUpButton);
        LinearLayout signUpButton2 = findViewById(R.id.signUpButton2);

        mobileLayout.setVisibility(View.INVISIBLE);
        fnameLayout.setVisibility(View.INVISIBLE);
        lnameLayout.setVisibility(View.INVISIBLE);
        pwLayout.setVisibility(View.INVISIBLE);
        pwConformLayout.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.INVISIBLE);
        signUpButton2.setVisibility(View.INVISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mobileLayout.setVisibility(View.VISIBLE);
                mobileLayout.startAnimation(AnimationUtils.loadAnimation(FarmerSignupActivity.this,R.anim.from_bottom_fade_in));
            }
        },100);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fnameLayout.setVisibility(View.VISIBLE);
                fnameLayout.startAnimation(AnimationUtils.loadAnimation(FarmerSignupActivity.this,R.anim.from_bottom_fade_in));
            }
        },200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lnameLayout.setVisibility(View.VISIBLE);
                lnameLayout.startAnimation(AnimationUtils.loadAnimation(FarmerSignupActivity.this,R.anim.from_bottom_fade_in));
            }
        },300);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pwLayout.setVisibility(View.VISIBLE);
                pwLayout.startAnimation(AnimationUtils.loadAnimation(FarmerSignupActivity.this,R.anim.from_bottom_fade_in));
            }
        },400);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pwConformLayout.setVisibility(View.VISIBLE);
                pwConformLayout.startAnimation(AnimationUtils.loadAnimation(FarmerSignupActivity.this,R.anim.from_bottom_fade_in));
            }
        },500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                signUpButton.setVisibility(View.VISIBLE);
                signUpButton.startAnimation(AnimationUtils.loadAnimation(FarmerSignupActivity.this,R.anim.from_bottom_fade_in));
            }
        },600);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                signUpButton2.setVisibility(View.VISIBLE);
                signUpButton2.startAnimation(AnimationUtils.loadAnimation(FarmerSignupActivity.this,R.anim.from_bottom_fade_in));
            }
        },700);
    }

    private void errorAnimation(int id){
        LinearLayout layout = findViewById(id);
        layout.startAnimation(AnimationUtils.loadAnimation(FarmerSignupActivity.this,R.anim.shake_animation));
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
             color = ContextCompat.getColor(FarmerSignupActivity.this,R.color.error_red);
             titleColor = color;
             bgDrawable = ContextCompat.getDrawable(FarmerSignupActivity.this,R.drawable.custon_error_text_view_bg);

        }else {
            color = ContextCompat.getColor(FarmerSignupActivity.this,R.color.textGray);
            titleColor = ContextCompat.getColor(FarmerSignupActivity.this,R.color.black);
            bgDrawable = ContextCompat.getDrawable(FarmerSignupActivity.this,R.drawable.custom_textview_background);
        }


        background.setBackground(bgDrawable);
        icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        title.setTextColor(titleColor);
        editText.setHintTextColor(color);
    }



}