package com.kavindu.farmshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.model.SQLiteHelper;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.notificationBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user",null);

        Gson gson = new Gson();
        UserDto user = gson.fromJson(userJson, UserDto.class);

        ImageView profileImage = findViewById(R.id.investorTransactionShapeableImageView2);
        TextView name = findViewById(R.id.textView150);

        String userName = user.getFname()+" "+user.getLname();
        name.setText(userName);

        if (user.getProfilePic().isEmpty()){
            profileImage.setImageResource(R.drawable.globe);
        }else{
            Glide.with(NotificationActivity.this)
                    .load(user.getProfilePic())
                    .placeholder(R.drawable.loading)
                    .into(profileImage);
        }

        String sqliteName = "";

        if (user.getUserType().equals("Investor")){
            sqliteName = "farmShare.db";
        } else if (user.getUserType().equals("Farmer")) {
            sqliteName = "farmShareFarmer.db";
        }

        SQLiteHelper sqLiteHelper = new SQLiteHelper(NotificationActivity.this, sqliteName, null, 1);

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM `notification` ORDER BY id DESC",new String[]{});

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        LinearLayout container = findViewById(R.id.notificationContainer);

                        while (cursor.moveToNext()){
                            Log.i("FarmShareLog",cursor.getString(0));
                            View item = getLayoutInflater().inflate(R.layout.notification_item,null);
                            TextView title = item.findViewById(R.id.textView152);
                            TextView text = item.findViewById(R.id.textView153);

                            title.setText(cursor.getString(1));
                            text.setText(cursor.getString(2));
                            container.addView(item);
                        }

                    }
                });

            }
        }).start();

    }
}