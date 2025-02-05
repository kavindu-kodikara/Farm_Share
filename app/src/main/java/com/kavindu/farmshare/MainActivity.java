package com.kavindu.farmshare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kavindu.farmshare.farmer.FarmerSignupActivity;
import com.kavindu.farmshare.investor.InvestorSignUpActivity;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView1;
    TextView textView2 ;
    TextView textView3 ;

    LinearLayout button1;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Force Light Mode
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         imageView = findViewById(R.id.imageView1);
         textView1 = findViewById(R.id.textView1);
         textView2 = findViewById(R.id.textView2);
         textView3 = findViewById(R.id.textView3);

         button1 = findViewById(R.id.button1);
         button2 = findViewById(R.id.button2);

        startAnimations();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, FarmerSignupActivity.class);
                startActivity(i);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InvestorSignUpActivity.class);
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
        textView1.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        textView3.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);

//        Animation animation1 = AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce);
        Animation animation1 = AnimationUtils.loadAnimation(MainActivity.this,R.anim.zoom_out);
        imageView.startAnimation(animation1);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView1.setVisibility(View.VISIBLE);
                textView1.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.from_bottom_fade_in));
            }
        },300);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView2.setVisibility(View.VISIBLE);
                textView2.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.from_bottom_fade_in));
            }
        },600);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView3.setVisibility(View.VISIBLE);
                textView3.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.from_bottom_fade_in));
            }
        }, 900);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button1.setVisibility(View.VISIBLE);
                button1.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.from_bottom_fade_in));
            }
        }, 1200);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button2.setVisibility(View.VISIBLE);
                button2.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.from_bottom_fade_in));
            }
        }, 1500);



    }
}