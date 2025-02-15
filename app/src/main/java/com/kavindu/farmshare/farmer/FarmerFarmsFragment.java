package com.kavindu.farmshare.farmer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.RequestDto;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.model.FarmItem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;


public class FarmerFarmsFragment extends Fragment {

    public FarmerFarmsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_farms, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.famsRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        //load farms
        Sweetalert pDialog = new Sweetalert(view.getContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        SharedPreferences sp = getActivity().getSharedPreferences("com.kavindu.farmshare.data",Context.MODE_PRIVATE);
        String user = sp.getString("user",null);

        if (user != null){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();

                    UserDto userDto = gson.fromJson(user,UserDto.class);

                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestDto requestDto = new RequestDto(userDto.getId());

                    RequestBody requestBody = RequestBody.create(gson.toJson(requestDto), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(BuildConfig.URL+"/farm/load-my-farms")
                            .post(requestBody)
                            .build();

                    try {

                        Response response = okHttpClient.newCall(request).execute();
                        ResponseDto<ArrayList<FarmItem>> responseDto = gson.fromJson(response.body().string(), new TypeToken<ResponseDto<ArrayList<FarmItem>>>(){}.getType());

                        if (responseDto.isSuccess()){

                            ArrayList<FarmItem> farmItems = responseDto.getData();

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(new Adapter(farmItems));
                                    pDialog.cancel();
                                }
                            });

                        }else{
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.cancel();
                                    new Sweetalert(view.getContext(), Sweetalert.ERROR_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText("Something went wrong")
                                            .show();
                                }
                            });
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }).start();

        }else {
            pDialog.cancel();
            new Sweetalert(view.getContext(), Sweetalert.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Something went wrong please try again later")
                    .show();
        }


        FrameLayout addFarmButton = view.findViewById(R.id.addFarmButton);
        addFarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),FarmerAddFarmActivity.class);
                view.getContext().startActivity(i);
            }
        });

        return view;
    }
}




class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder1>{

    static class ViewHolder1 extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView farmNameTextView;
        TextView risktextTextView;
        View riskIndicator;
        TextView stockCountTextView;
        ConstraintLayout farmItem;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView15);
            farmNameTextView = itemView.findViewById(R.id.textView39);
            risktextTextView = itemView.findViewById(R.id.textView40);
            riskIndicator = itemView.findViewById(R.id.view5);
            stockCountTextView = itemView.findViewById(R.id.textView41);

        }
    }

    ArrayList<FarmItem> farmItemArrayList;

    public Adapter(ArrayList<FarmItem> farmItemArrayList) {
        this.farmItemArrayList = farmItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_farmer_farm__item,parent,false);
        ViewHolder1 vh1 = new ViewHolder1(view);

        return vh1;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder1 holder, int position) {

        FarmItem farmItem = farmItemArrayList.get(position);

        if(farmItem.getCropType().equals("Rice")){
            holder.imageView.setImageResource(R.drawable.rice);
        } else if (farmItem.getCropType().equals("Corn")) {
            holder.imageView.setImageResource(R.drawable.corn);
        }


        if (farmItem.getIsAtRisk().equals("true")){
            holder.riskIndicator.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.gradient_risk));
            holder.risktextTextView.setText("At risk");
        }else if (farmItem.getIsAtRisk().equals("false")) {
            holder.riskIndicator.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.gradient_good));
            holder.risktextTextView.setText("In good hands");
        }

        holder.farmNameTextView.setText(farmItem.getFarmName());
        holder.stockCountTextView.setText("S "+farmItem.getStockCount());



    }

    @Override
    public int getItemCount() {
        return farmItemArrayList.size();
    }
}