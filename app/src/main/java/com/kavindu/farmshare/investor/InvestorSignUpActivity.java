package com.kavindu.farmshare.investor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kavindu.farmshare.R;

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

        startAnimations();

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
                Intent intent = new Intent(InvestorSignUpActivity.this,InvestorMainActivity.class);
                startActivity(intent);
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
}