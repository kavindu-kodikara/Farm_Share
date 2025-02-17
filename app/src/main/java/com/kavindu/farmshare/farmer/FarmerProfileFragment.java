package com.kavindu.farmshare.farmer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kavindu.farmshare.BuildConfig;
import com.kavindu.farmshare.MainActivity;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.SoilReportDto;
import com.kavindu.farmshare.dto.UserDto;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;


public class FarmerProfileFragment extends Fragment {

    ImageView profileImage;
    EditText mobile;
    EditText fname;
    EditText lname;
    EditText password;
    EditText rePassword;
    Uri profileImageUri;


    public FarmerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_farmer_profile, container, false);

        //load details
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user",null);

        Gson gson = new Gson();
        UserDto user = gson.fromJson(userJson, UserDto.class);

        profileImage = view.findViewById(R.id.farmerProfileshapeableImageView2);
        mobile = view.findViewById(R.id.editTextPhone);
        fname = view.findViewById(R.id.editTextfname);
        lname = view.findViewById(R.id.editTextlname);
        password = view.findViewById(R.id.editTextTextPassword);
        rePassword = view.findViewById(R.id.editTextTextPassword2);

        loadDetails(user,view);


        //profile image select
        ActivityResultLauncher<String> profileImagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        profileImageUri = uri;
                        Toast.makeText(view.getContext(), "Profile image Selected: " + uri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
                        profileImage.setImageURI(uri);
                    }
                });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileImagePickerLauncher.launch("image/*");
            }
        });

        LinearLayout button = view.findViewById(R.id.signUpButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewClick) {

                if(profileImageUri == null){
                    showErrorDialog("Please select profile image",view);
                }else if(fname.getText().toString().isBlank()){
                    showErrorDialog("Please enter first name",view);
                }else if(lname.getText().toString().isBlank()){
                    showErrorDialog("Please enter last name",view);
                }else if(password.getText().toString().isBlank()){
                    showErrorDialog("Please enter password name",view);
                }else if(rePassword.getText().toString().isBlank()){
                    showErrorDialog("Please re type password",view);
                }else{
                    UserDto userDto = new UserDto();
                    userDto.setId(user.getId());
                    userDto.setFname(fname.getText().toString());
                    userDto.setLname(lname.getText().toString());
                    userDto.setPassword(password.getText().toString());
                    userDto.setRePassword(rePassword.getText().toString());

                    uploadImageToFirebase(profileImageUri,view,userDto);

                }

            }
        });

        ImageView logoutButton = view.findViewById(R.id.logoutImageView);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getActivity().getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("user", null);
                editor.apply();

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadDetails(UserDto user,View parent){
        if (user.getProfilePic().isEmpty()){
            profileImage.setImageResource(R.drawable.globe);
        }else{
            Glide.with(parent.getContext())
                    .load(user.getProfilePic())
                    .placeholder(R.drawable.loading)
                    .into(profileImage);
        }

        fname.setText(user.getFname());
        mobile.setText(user.getMobile());
        lname.setText(user.getLname());
        password.setText(user.getPassword());
        rePassword.setText(user.getPassword());
    }

    private void showErrorDialog(String message,View parent){
        new Sweetalert(parent.getContext(), Sweetalert.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .show();
    }

    private void uploadImageToFirebase(Uri imageUri, View parent, UserDto userDto) {

        Sweetalert pDialog = new Sweetalert(parent.getContext(), Sweetalert.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Processing");
        pDialog.setCancelable(false);
        pDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String imageUrl = uri.toString();
                                Log.d("Firebase", "Image URL: " + imageUrl);
                                userDto.setProfilePic(imageUrl);

                                Gson gson = new Gson();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        OkHttpClient okHttpClient = new OkHttpClient();
                                        RequestBody requestBody = RequestBody.create(gson.toJson(userDto),MediaType.get("application/json"));
                                        Request request = new Request.Builder()
                                                .url(BuildConfig.URL+"/user/update")
                                                .post(requestBody)
                                                .build();

                                        try {

                                            Response response = okHttpClient.newCall(request).execute();
                                            ResponseDto<UserDto> responseDto = gson.fromJson(response.body().string(), new TypeToken<ResponseDto<UserDto>>(){}.getType());

                                            if (responseDto.isSuccess()){

                                                UserDto respUserDto = responseDto.getData();

                                                SharedPreferences sp = getActivity().getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.putString("user", gson.toJson(respUserDto));
                                                editor.apply();

                                                requireActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        loadDetails(respUserDto,parent);
                                                        pDialog.cancel();
                                                        new Sweetalert(parent.getContext(), Sweetalert.SUCCESS_TYPE)
                                                                .setTitleText("Success")
                                                                .setContentText("Updated")
                                                                .show();
                                                    }
                                                });

                                            }else {
                                                showErrorDialog(responseDto.getMessage(), parent);
                                            }

                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                }).start();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firebase", "Failed to get download URL", e);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Upload failed", e);
                    }
                });
    }
}