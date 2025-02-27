package com.kavindu.farmshare.investor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import com.kavindu.farmshare.farmer.FarmerMainActivity;
import com.kavindu.farmshare.farmer.FarmerSignInActivity;
import com.kavindu.farmshare.model.SQLiteHelper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class InvestorSignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_investor_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startAnimations();

        EditText mobileEditText = findViewById(R.id.ISignInEditTextPhone);
        EditText passwordEditText = findViewById(R.id.ISignInEditTextPassword);

        startAnimations();

        mobileEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.iSignInMobileBg,R.id.iSignInMobileIcon,R.id.ISignIntextView3,R.id.ISignInEditTextPhone);
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.iSignInPasswordBg,R.id.iSignInPasswordIcon,R.id.ISignIntextView4,R.id.ISignInEditTextPassword);
            }
        });

        ImageView backImageView = findViewById(R.id.ISignInBackImageView1);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        LinearLayout button2 = findViewById(R.id.ISignInsignInButton2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        LinearLayout signInButton = findViewById(R.id.ISignInsignInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.iSignInMobileBg,R.id.iSignInMobileIcon,R.id.ISignIntextView3,R.id.ISignInEditTextPhone);
                    errorAnimation(R.id.ISignInmobileLayout);

                } else if (passwordEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.iSignInPasswordBg,R.id.iSignInPasswordIcon,R.id.ISignIntextView4,R.id.ISignInEditTextPassword);
                    errorAnimation(R.id.ISignInPasswordLayout);

                } else{

                    UserDto reqUserDto = new UserDto();
                    reqUserDto.setMobile(mobileEditText.getText().toString());
                    reqUserDto.setPassword(passwordEditText.getText().toString());
                    reqUserDto.setUserType("investor");

                    Gson gson = new Gson();

                    Sweetalert pDialog = new Sweetalert(InvestorSignInActivity.this, Sweetalert.PROGRESS_TYPE);
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
                                            new Sweetalert(InvestorSignInActivity.this, Sweetalert.SUCCESS_TYPE)
                                                    .setTitleText("Success")
                                                    .setContentText(responseDto.getMessage())
                                                    .show();
                                        }
                                    });

                                    SQLiteHelper sqLiteHelper = new SQLiteHelper(InvestorSignInActivity.this,"farmShare.db",null,1);
                                    SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();

                                    SQLiteHelper sqLiteHelper2 = new SQLiteHelper(InvestorSignInActivity.this,"farmShareFarmer.db",null,1);
                                    SQLiteDatabase sqLiteDatabase2 = sqLiteHelper2.getWritableDatabase();

                                    if (sqLiteDatabase != null){
                                        sqLiteDatabase.execSQL("DELETE FROM `notification`");
                                    }

                                    if (sqLiteDatabase2 != null){
                                        sqLiteDatabase2.execSQL("DELETE FROM `notification`");
                                    }



                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    Intent intent = new Intent(InvestorSignInActivity.this, HumanVerifyActivity.class);
                                    intent.putExtra("type","Investor");
                                    startActivity(intent);

                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Sweetalert(InvestorSignInActivity.this, Sweetalert.ERROR_TYPE)
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

        LinearLayout mobileLayout = findViewById(R.id.ISignInmobileLayout);
        LinearLayout passwordLayout = findViewById(R.id.ISignInPasswordLayout);
        LinearLayout button1 = findViewById(R.id.ISignInsignInButton);
        LinearLayout button2 = findViewById(R.id.ISignInsignInButton2);

        mobileLayout.setVisibility(View.INVISIBLE);
        passwordLayout.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mobileLayout.setVisibility(View.VISIBLE);
                mobileLayout.startAnimation(AnimationUtils.loadAnimation(InvestorSignInActivity.this,R.anim.from_bottom_fade_in));
            }
        },200);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                passwordLayout.setVisibility(View.VISIBLE);
                passwordLayout.startAnimation(AnimationUtils.loadAnimation(InvestorSignInActivity.this,R.anim.from_bottom_fade_in));
            }
        },400);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button1.setVisibility(View.VISIBLE);
                button1.startAnimation(AnimationUtils.loadAnimation(InvestorSignInActivity.this,R.anim.from_bottom_fade_in));
            }
        },600);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button2.setVisibility(View.VISIBLE);
                button2.startAnimation(AnimationUtils.loadAnimation(InvestorSignInActivity.this,R.anim.from_bottom_fade_in));
            }
        },800);

    }

    private void errorAnimation(int id){
        LinearLayout layout = findViewById(id);
        layout.startAnimation(AnimationUtils.loadAnimation(InvestorSignInActivity.this,R.anim.shake_animation));
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
            color = ContextCompat.getColor(InvestorSignInActivity.this,R.color.error_red);
            titleColor = color;
            bgDrawable = ContextCompat.getDrawable(InvestorSignInActivity.this,R.drawable.custon_error_text_view_bg);

        }else {
            color = ContextCompat.getColor(InvestorSignInActivity.this,R.color.textGray);
            titleColor = ContextCompat.getColor(InvestorSignInActivity.this,R.color.black);
            bgDrawable = ContextCompat.getDrawable(InvestorSignInActivity.this,R.drawable.custom_textview_background);
        }


        background.setBackground(bgDrawable);
        icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        title.setTextColor(titleColor);
        editText.setHintTextColor(color);
    }
}