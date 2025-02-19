package com.kavindu.farmshare.investor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.TransactionDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.model.PayoutItem;
import com.kavindu.farmshare.model.TransactionItem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;


public class InvestorTransactionFragment extends Fragment {


    public InvestorTransactionFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        ImageView menuButton = view.findViewById(R.id.investorTransactionMenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof InvestorMainActivity) {
                    ((InvestorMainActivity) getActivity()).openDrawer();
                }
            }
        });

        ShapeableImageView profileImge = view.findViewById(R.id.investorTransactionShapeableImageView);
        TextView name = view.findViewById(R.id.textView127);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user",null);
        Gson gson = new Gson();

        UserDto userDto = gson.fromJson(userJson, UserDto.class);

        if (userDto.getProfilePic().isEmpty()){
            profileImge.setImageResource(R.drawable.globe);
        }else{
            Glide.with(view.getContext())
                    .load(userDto.getProfilePic())
                    .placeholder(R.drawable.loading)
                    .into(profileImge);
        }

        String userName = userDto.getFname()+" "+userDto.getLname();
        name.setText(userName);

        Sweetalert pDialog = new Sweetalert(view.getContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestDto requestDto = new RequestDto(userDto.getId());

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL+"/investor/load-transaction")
                        .post(requestBody)
                        .build();

                try {

                    Response response = okHttpClient.newCall(request).execute();
                    TransactionDto transactionDto = gson.fromJson(response.body().string(), TransactionDto.class);

                    if (transactionDto.isSuccess()){
                        ArrayList<TransactionItem> transactionItems = transactionDto.getTodayList();
                        ArrayList<TransactionItem> oldTransactionItems = transactionDto.getOldList();

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transactionItemInflater(R.id.todayTransactionsContainer,view,transactionItems);
                                transactionItemInflater(R.id.oldTransactionsContainer,view,oldTransactionItems);
                                pDialog.cancel();
                            }
                        });

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


//        oldTransactionItems.add(new TransactionItem("Test Farm 3","Deposit","+20,870.00 RS","Jan 3, 16:30"));




        return view;
    }

    private void transactionItemInflater(int container,View parent, ArrayList<TransactionItem> itemArrayList){

        LinearLayout itemContainer = parent.findViewById(container);

        for (TransactionItem transactionItem : itemArrayList){

            View item = getLayoutInflater().inflate(R.layout.fragment_transaction_item,null);

            TextView name = item.findViewById(R.id.textView132);
            TextView type = item.findViewById(R.id.textView133);
            TextView price = item.findViewById(R.id.textView134);
            TextView time = item.findViewById(R.id.textView135);
            ImageView image = item.findViewById(R.id.imageView28);

            name.setText(transactionItem.getName());
            type.setText(transactionItem.getType());
            price.setText(transactionItem.getPrice());
            time.setText(transactionItem.getTime());

            if (transactionItem.getType().equals("Deposit")){
                image.setImageResource(R.drawable.down_left_arrow);
                image.setBackground(ContextCompat.getDrawable(parent.getContext(),R.drawable.transaction_bg2));
                price.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.green));
            }


            itemContainer.addView(item);

        }

    }
}