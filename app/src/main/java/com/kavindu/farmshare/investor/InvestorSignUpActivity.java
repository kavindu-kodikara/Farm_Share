package com.kavindu.farmshare.investor;

import android.content.Intent;
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

import com.kavindu.farmshare.R;
import com.kavindu.farmshare.farmer.FarmerSignupActivity;

public class InvestorSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_investor_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText mobileEditText = findViewById(R.id.ISignUpeditTextPhone);
        EditText fnameEditText = findViewById(R.id.ISignUpeditTextfname);
        EditText lnameEditText = findViewById(R.id.ISignUpeditTextlname);
        EditText passwordEditText = findViewById(R.id.ISignUpeditTextTextPassword);
        EditText rePasswordEditText = findViewById(R.id.ISignUpeditTextTextPassword2);

        startAnimations();


        mobileEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.iMobileBg,R.id.iMobileIcon,R.id.ISignUptextView8,R.id.ISignUpeditTextPhone);
            }
        });

        fnameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.iFnameBg,R.id.iFnameIcon,R.id.ISignUptextView9,R.id.ISignUpeditTextfname);
            }
        });

        lnameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.iLnameBg,R.id.iLnameIcon,R.id.ISignUptextView10,R.id.ISignUpeditTextlname);
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.iPasswordBg,R.id.iPasswordIcon,R.id.ISignUptextView11,R.id.ISignUpeditTextTextPassword);
            }
        });

        rePasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setErrorColor(false,R.id.iRePasswordBg,R.id.iRePasswordIcon,R.id.ISignUptextView12,R.id.ISignUpeditTextTextPassword2);
            }
        });



        ImageView backButton = findViewById(R.id.ISignUpbackImageView1);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        LinearLayout button2 = findViewById(R.id.ISignUpsignUpButton2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InvestorSignUpActivity.this,InvestorSignInActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout buttonSignUp = findViewById(R.id.ISignUpsignUpButton);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mobileEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.iMobileBg,R.id.iMobileIcon,R.id.ISignUptextView8,R.id.ISignUpeditTextPhone);
                    errorAnimation(R.id.ISignUpmobileLayout);

                } else if (fnameEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.iFnameBg,R.id.iFnameIcon,R.id.ISignUptextView9,R.id.ISignUpeditTextfname);
                    errorAnimation(R.id.ISignUpfnameLayout);

                } else if (lnameEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.iLnameBg,R.id.iLnameIcon,R.id.ISignUptextView10,R.id.ISignUpeditTextlname);
                    errorAnimation(R.id.ISignUplnameLayout);

                } else if (passwordEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.iPasswordBg,R.id.iPasswordIcon,R.id.ISignUptextView11,R.id.ISignUpeditTextTextPassword);
                    errorAnimation(R.id.ISignUppwLayout);

                } else if (rePasswordEditText.getText().toString().isBlank()){

                    setErrorColor(true,R.id.iRePasswordBg,R.id.iRePasswordIcon,R.id.ISignUptextView12,R.id.ISignUpeditTextTextPassword2);
                    errorAnimation(R.id.ISignUppwConformLayout);

                } else{

                    Toast.makeText(InvestorSignUpActivity.this, "hi", Toast.LENGTH_SHORT).show();

                }

//                Intent intent = new Intent(InvestorSignUpActivity.this,InvestorMainActivity.class);
//                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimations();
    }

    private void startAnimations(){
        LinearLayout mobileLayout = findViewById(R.id.ISignUpmobileLayout);
        LinearLayout fnameLayout = findViewById(R.id.ISignUpfnameLayout);
        LinearLayout lnameLayout = findViewById(R.id.ISignUplnameLayout);
        LinearLayout pwLayout = findViewById(R.id.ISignUppwLayout);
        LinearLayout pwConformLayout = findViewById(R.id.ISignUppwConformLayout);
        LinearLayout signUpButton = findViewById(R.id.ISignUpsignUpButton);
        LinearLayout signUpButton2 = findViewById(R.id.ISignUpsignUpButton2);

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
                mobileLayout.startAnimation(AnimationUtils.loadAnimation(InvestorSignUpActivity.this,R.anim.from_bottom_fade_in));
            }
        },100);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fnameLayout.setVisibility(View.VISIBLE);
                fnameLayout.startAnimation(AnimationUtils.loadAnimation(InvestorSignUpActivity.this,R.anim.from_bottom_fade_in));
            }
        },200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lnameLayout.setVisibility(View.VISIBLE);
                lnameLayout.startAnimation(AnimationUtils.loadAnimation(InvestorSignUpActivity.this,R.anim.from_bottom_fade_in));
            }
        },300);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pwLayout.setVisibility(View.VISIBLE);
                pwLayout.startAnimation(AnimationUtils.loadAnimation(InvestorSignUpActivity.this,R.anim.from_bottom_fade_in));
            }
        },400);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pwConformLayout.setVisibility(View.VISIBLE);
                pwConformLayout.startAnimation(AnimationUtils.loadAnimation(InvestorSignUpActivity.this,R.anim.from_bottom_fade_in));
            }
        },500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                signUpButton.setVisibility(View.VISIBLE);
                signUpButton.startAnimation(AnimationUtils.loadAnimation(InvestorSignUpActivity.this,R.anim.from_bottom_fade_in));
            }
        },600);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                signUpButton2.setVisibility(View.VISIBLE);
                signUpButton2.startAnimation(AnimationUtils.loadAnimation(InvestorSignUpActivity.this,R.anim.from_bottom_fade_in));
            }
        },700);
    }

    private void errorAnimation(int id){
        LinearLayout layout = findViewById(id);
        layout.startAnimation(AnimationUtils.loadAnimation(InvestorSignUpActivity.this,R.anim.shake_animation));
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
            color = ContextCompat.getColor(InvestorSignUpActivity.this,R.color.error_red);
            titleColor = color;
            bgDrawable = ContextCompat.getDrawable(InvestorSignUpActivity.this,R.drawable.custon_error_text_view_bg);

        }else {
            color = ContextCompat.getColor(InvestorSignUpActivity.this,R.color.textGray);
            titleColor = ContextCompat.getColor(InvestorSignUpActivity.this,R.color.black);
            bgDrawable = ContextCompat.getDrawable(InvestorSignUpActivity.this,R.drawable.custom_textview_background);
        }


        background.setBackground(bgDrawable);
        icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        title.setTextColor(titleColor);
        editText.setHintTextColor(color);
    }
}