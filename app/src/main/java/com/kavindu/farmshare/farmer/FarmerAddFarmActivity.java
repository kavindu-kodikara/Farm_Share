package com.kavindu.farmshare.farmer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import com.kavindu.farmshare.dto.ImageDto;
import com.kavindu.farmshare.dto.ResponseDto;
import com.kavindu.farmshare.dto.UserDto;
import com.kavindu.farmshare.investor.InvestorMainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taimoor.sultani.sweetalert2.Sweetalert;

public class FarmerAddFarmActivity extends AppCompatActivity {

    private GoogleMap map;

    private Uri nicFrontUri;
    private Uri nicBackUri;
    private Uri ownershipDocUri;
    private Uri analysisDocUri;
    private Uri soilReportDocUri;

    private List<Uri> imageUris;

    private LatLng farmLocatin;
    private String farmType;
    private String farmName;
    private double farmSize;
    private int farmAvgIncome;
    private int farmMinInvest;
    private String farmCodeName;
    private String farmDescription;
    private String farmNicNumber;
    private double soilPh;
    private double soilMoisture;
    private double soilOrganicMatter;
    private double soilNutrient;

    private Sweetalert pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_farmer_add_farm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView backButton = findViewById(R.id.addFarmBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        //crop type Spinner


        Spinner spinner = findViewById(R.id.spinner);

        ArrayList<String> cropArrayList = new ArrayList<>();
        cropArrayList.add("Select");
        cropArrayList.add("Rice");
        cropArrayList.add("Corn");

        CropAdapter cropAdapter = new CropAdapter(
                FarmerAddFarmActivity.this,
                R.layout.fragment_custom_spinner_item,
                cropArrayList,
                R.layout.fragment_cuntom_spinner_selected
        );

        spinner.setAdapter(cropAdapter);


        //farm location google map

        SupportMapFragment supportMapFragment = new SupportMapFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mapFrameLayout,supportMapFragment);
        fragmentTransaction.commit();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                map = googleMap;

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);

                }else {



                    String[] permissionArray = new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    };

                    requestPermissions(permissionArray,100);

                }

                // Set default location
                LatLng defaultLocation = new LatLng(6.9271, 79.8612); // Example: Colombo, Sri Lanka
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));

                // Detect when map stops moving
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        LatLng centerLatLng = googleMap.getCameraPosition().target;
                    }
                });



            }
        });

        Button mapButton = findViewById(R.id.addFarmLocationButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    farmLocatin = map.getCameraPosition().target;
                    Toast.makeText(FarmerAddFarmActivity.this, "location Selected: ", Toast.LENGTH_LONG).show();
                    Log.i("FarmShareLog",String.valueOf(farmLocatin.latitude));
                    Log.i("FarmShareLog",String.valueOf(farmLocatin.longitude));
                }
            }
        });

        //farm image selection

        imageUris = new ArrayList<>();

        ImageView btnSelectImages = findViewById(R.id.btnSelectImages);
        RecyclerView rvImages = findViewById(R.id.rvImages);

        rvImages.setLayoutManager(new LinearLayoutManager(FarmerAddFarmActivity.this, LinearLayoutManager.HORIZONTAL, false));
        ImageAdapter imageAdapter = new ImageAdapter(FarmerAddFarmActivity.this, imageUris);
        rvImages.setAdapter(imageAdapter);

        ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    imageUris.clear();
                    imageUris.addAll(uris);
                    imageAdapter.notifyDataSetChanged();
                });

        btnSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickerLauncher.launch("image/*");
            }
        });


        // NIC selection

        //nic Front
        ImageView nicFrontImageView = findViewById(R.id.nicFrontImageView);
        ActivityResultLauncher<String> nicFrontImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        nicFrontImageView.setImageURI(uri);
                        nicFrontUri = uri;
                    }
                }
        );

        nicFrontImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nicFrontImagePickerLauncher.launch("image/*");
            }
        });

        //nic Back
        ImageView nicBackImageView = findViewById(R.id.nicBackImageView);
        ActivityResultLauncher<String> nicBackImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        nicBackImageView.setImageURI(uri);
                        nicBackUri = uri;
                    }
                }
        );
        nicBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nicBackImagePickerLauncher.launch("image/*");
            }
        });

        //ownership document
        AtomicReference<Uri> ownershipFileUri = new AtomicReference<>();

        LinearLayout btnSelectFile = findViewById(R.id.ownerShipDoclinearLayout);

        ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        ownershipFileUri.set(uri);
                        TextView text = findViewById(R.id.textView54);
                        text.setText(uri.getLastPathSegment());
                        Toast.makeText(FarmerAddFarmActivity.this, "Ownership file Selected: " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();

                        ownershipDocUri = uri;

                    }
                });

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePickerLauncher.launch("*/*");
            }
        });

        // output record document
        AtomicReference<Uri> outputRecordFileUri = new AtomicReference<>();

        LinearLayout outputRecordBtn = findViewById(R.id.outputRecordlinearLayout);

        ActivityResultLauncher<String> outputRecorfilePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        outputRecordFileUri.set(uri);
                        TextView text = findViewById(R.id.textView56);
                        text.setText(uri.getLastPathSegment());
                        Toast.makeText(FarmerAddFarmActivity.this, "Output record file Selected: " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();

                        analysisDocUri = uri;

                    }
                });

        outputRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputRecorfilePickerLauncher.launch("*/*");
            }
        });

        //soil repost document
        AtomicReference<Uri> soilReportFileUri = new AtomicReference<>();

        LinearLayout soilReportBtn = findViewById(R.id.soilReportlinearLayout);

        ActivityResultLauncher<String> soilReportfilePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        soilReportFileUri.set(uri);
                        TextView text = findViewById(R.id.textView57);
                        text.setText(uri.getLastPathSegment());
                        Toast.makeText(FarmerAddFarmActivity.this, "Soil report file Selected: " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();

                        soilReportDocUri = uri;

                    }
                });

        soilReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soilReportfilePickerLauncher.launch("*/*");
            }
        });


        // submit button
        LinearLayout submitButton = findViewById(R.id.addFarmSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Spinner farmTypeSpinner = findViewById(R.id.spinner);
                EditText farmNameEditText = findViewById(R.id.editTextText2);
                EditText farmSizeEditText = findViewById(R.id.editTextText3);
                EditText farmAvgEditText = findViewById(R.id.editTextText4);
                EditText farmMinInvestEditText = findViewById(R.id.editTextText5);
                EditText farmCodeNameEditText = findViewById(R.id.editTextText7);
                EditText farmDescriptionEditText = findViewById(R.id.editTextTextMultiLine);
                EditText farmNicNumberEditText = findViewById(R.id.editTextText6);
                EditText farmSoilPhEditText = findViewById(R.id.editTextText9);
                EditText farmSoilMoisturEditText = findViewById(R.id.editTextText10);
                EditText farmSoilOrganicEditText = findViewById(R.id.editTextText11);
                EditText farmSoilNutrientEditText = findViewById(R.id.editTextText12);

                if (farmTypeSpinner.getSelectedItem().toString().equals("Select")){
                    showErrorDialog("Select crop type");
                } else if (farmNameEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter farm name");
                }else if (farmSizeEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter farm size");
                }else  if (farmAvgEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter farm Avg income");
                }else if (farmMinInvestEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter Min invest");
                }else if (farmCodeNameEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter farm code name");
                }else if (farmDescriptionEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter farm description");
                }else if (farmNicNumberEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter owner NIC");
                }else if (farmSoilPhEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter soil PH level");
                }else if (farmSoilMoisturEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter soil moisture content");
                }else if (farmSoilOrganicEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter soil organic matter");
                }else if (farmSoilNutrientEditText.getText().toString().isBlank()) {
                    showErrorDialog("Please enter farm nutrient level");
                }else if (nicFrontUri == null) {
                    showErrorDialog("Please select nic front image");
                }else if (nicBackUri == null) {
                    showErrorDialog("Please select nic back image");
                }else if (ownershipDocUri == null) {
                    showErrorDialog("Please select ownership document");
                }else if (analysisDocUri == null) {
                    showErrorDialog("Please select farm output record document");
                }else if (soilReportDocUri == null) {
                    showErrorDialog("Please select farm soil report document");
                }else if (imageUris.isEmpty()) {
                    showErrorDialog("Please select farm images");
                }else if (farmLocatin == null) {
                    showErrorDialog("Please select farm location");
                }else{

                    farmType = farmTypeSpinner.getSelectedItem().toString();
                    soilNutrient = Double.parseDouble(farmSoilNutrientEditText.getText().toString());
                    soilOrganicMatter = Double.parseDouble(farmSoilOrganicEditText.getText().toString());
                    soilMoisture = Double.parseDouble(farmSoilMoisturEditText.getText().toString());
                    soilPh = Double.parseDouble(farmSoilPhEditText.getText().toString());
                    farmNicNumber = farmNicNumberEditText.getText().toString();
                    farmDescription = farmDescriptionEditText.getText().toString();
                    farmCodeName = farmCodeNameEditText.getText().toString();
                    farmMinInvest = Integer.parseInt(farmMinInvestEditText.getText().toString());
                    farmAvgIncome = Integer.parseInt(farmAvgEditText.getText().toString());
                    farmSize = Double.parseDouble(farmSizeEditText.getText().toString());
                    farmName = farmNameEditText.getText().toString();

                    Map<String, Uri> fileMap = new HashMap<>();
                    fileMap.put("nicFront", nicFrontUri);
                    fileMap.put("nicBack", nicBackUri);
                    fileMap.put("ownershipFile", ownershipDocUri);
                    fileMap.put("analysisFile", analysisDocUri);
                    fileMap.put("soilReportFile", soilReportDocUri);

                    pDialog = new Sweetalert(FarmerAddFarmActivity.this, Sweetalert.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Processing farm data");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    uploadFilesToFirebase(fileMap);

                }



            }
        });


    }


    //Runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                map.setMyLocationEnabled(true);
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);

            }
        }

    }


    // upload fire store
    private void uploadFilesToFirebase(Map<String, Uri> fileMap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Map<String, String> uploadedUrls = new HashMap<>();
        AtomicInteger uploadCount = new AtomicInteger(0);
        int totalFiles = fileMap.size();

        for (Map.Entry<String, Uri> entry : fileMap.entrySet()) {
            String fileType = entry.getKey();
            Uri fileUri = entry.getValue();

            String fileExtension = fileType.contains("File") ? ".pdf" : ".jpg"; // PDFs for documents, JPGs for images
            StorageReference storageRef = storage.getReference().child(fileType + "/" + System.currentTimeMillis() + fileExtension);

            storageRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadedUrls.put(fileType, uri.toString());
                                    Log.i("Firebase", fileType + " URL: " + uri.toString());

                                    // Check if all files are uploaded
                                    if (uploadCount.incrementAndGet() == totalFiles) {



                                        sendUrlsToBackend(uploadedUrls);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firebase", "Upload failed for " + fileType, e);
                        }
                    });
        }
    }

    // add farm request
    private void sendUrlsToBackend(Map<String, String> data) {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        SharedPreferences sharedPreferences = getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user",null);

        if (userJson != null){

            UserDto user = gson.fromJson(userJson, UserDto.class);

            data.put("farmType",farmType);
            data.put("userId",String.valueOf(user.getId()));
            data.put("soilNutrient",String.valueOf(soilNutrient));
            data.put("soilOrganicMatter",String.valueOf(soilOrganicMatter));
            data.put("soilMoisture",String.valueOf(soilMoisture));
            data.put("soilPh",String.valueOf(soilPh));
            data.put("ownerNic",farmNicNumber);
            data.put("description",farmDescription);
            data.put("codeName",farmCodeName);
            data.put("minInvest",String.valueOf(farmMinInvest));
            data.put("avgIncome",String.valueOf(farmAvgIncome));
            data.put("farmSize",String.valueOf(farmSize));
            data.put("farmName",farmName);
            data.put("lat",String.valueOf(farmLocatin.latitude));
            data.put("lng",String.valueOf(farmLocatin.longitude));

            new Thread(new Runnable() {
                @Override
                public void run() {

                    RequestBody requestBody = RequestBody.create(gson.toJson(data), MediaType.get("application/json"));
                    Request request = new Request.Builder()
                            .url(BuildConfig.URL + "/farm/add-farm")
                            .post(requestBody)
                            .build();

                    try {

                        Response response = client.newCall(request).execute();
                        ResponseDto<Integer> responseDto = gson.fromJson(response.body().string(), new TypeToken<ResponseDto<Integer>>(){}.getType());

                        if (responseDto.isSuccess()){

                            Log.i("FarmShareLog",String.valueOf(responseDto.getData()));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.setTitleText("Uploading images");
                                }
                            });

                            uploadImagesToFirebase(imageUris,responseDto.getData());

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.cancel();
                                    new Sweetalert(FarmerAddFarmActivity.this, Sweetalert.ERROR_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText(responseDto.getMessage())
                                            .show();
                                }
                            });
                        }

                    } catch (IOException e) {
                        Log.e("Backend", "Network error while sending URLs", e);
                    }
                }
            }).start();

        }


    }

    // upload farm images
    private void uploadImagesToFirebase(List<Uri> productImages,int farmId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        List<String> uploadedUrls = new ArrayList<>();
        AtomicInteger uploadCount = new AtomicInteger(0);
        int totalFiles = productImages.size(); // Add product images count

        for (Uri productImageUri : productImages) {
            StorageReference storageRef = storage.getReference().child("productImages/" + System.currentTimeMillis() + ".jpg");

            storageRef.putFile(productImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Save the image URL in the map with a specific key for product images
                                    uploadedUrls.add(uri.toString());
                                    Log.i("Firebase", "Product image URL: " + uri.toString());

                                    // Check if all files are uploaded
                                    if (uploadCount.incrementAndGet() == totalFiles) {
                                        sendImagesToBackend(uploadedUrls,farmId);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firebase", "Upload failed for product image", e);
                        }
                    });
        }
    }

    // add farm images request
    private void sendImagesToBackend(List<String> uploadedUrls,int farmId) {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        ImageDto imageDto = new ImageDto(uploadedUrls,farmId);

        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestBody requestBody = RequestBody.create(gson.toJson(imageDto), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BuildConfig.URL + "/farm/save-images")
                        .post(requestBody)
                        .build();

                try {

                    Response response = client.newCall(request).execute();
                    ResponseDto responseDto = gson.fromJson(response.body().string(),ResponseDto.class);

                    if (responseDto.isSuccess()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.cancel();
                                new Sweetalert(FarmerAddFarmActivity.this, Sweetalert.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText(responseDto.getMessage())
                                        .show();
                            }
                        });

                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(FarmerAddFarmActivity.this,FarmerMainActivity.class);
                                startActivity(intent);
                            }
                        });

                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pDialog.cancel();
                                new Sweetalert(FarmerAddFarmActivity.this, Sweetalert.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText(responseDto.getMessage())
                                        .show();
                            }
                        });
                    }

                } catch (IOException e) {
                    Log.e("Backend", "Network error while sending URLs", e);
                }
            }
        }).start();
    }

    private void showErrorDialog(String message){
        new Sweetalert(FarmerAddFarmActivity.this, Sweetalert.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .show();
    }





}

class CropAdapter extends ArrayAdapter<String>{

    List<String> cropList;
    int layout;
    int selectedLayout;

    public CropAdapter(@NonNull Context context, int resource, @NonNull List<String> objects,int selectedResource) {
        super(context, resource, objects);
        this.cropList = objects;
        this.layout = resource;
        this.selectedLayout = selectedResource;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layout,parent,false);
        ImageView image = view.findViewById(R.id.imageView8);
        TextView text = view.findViewById(R.id.textView50);

        String crop = cropList.get(position);
        if(crop.equals("Select")){
            image.setImageResource(R.drawable.empty_crop);
            text.setText("Select");
        } else if(crop.equals("Rice")){
            image.setImageResource(R.drawable.rice);
            text.setText("Rice");
        } else if (crop.equals("Corn")) {
            image.setImageResource(R.drawable.corn);
            text.setText("Corn");
        }

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        @SuppressLint("ViewHolder") View view = inflater.inflate(selectedLayout,parent,false);
        ImageView image = view.findViewById(R.id.imageViewSelected);

        String crop = cropList.get(position);
        if(crop.equals("Select")){
            image.setImageResource(R.drawable.empty_crop);
        } else if(crop.equals("Rice")){
            image.setImageResource(R.drawable.rice);
        } else if (crop.equals("Corn")) {
            image.setImageResource(R.drawable.corn);
        }

        return view;
    }
}

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<Uri> imageUris;
    private Context context;

    public ImageAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_farm_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        Glide.with(context).load(imageUri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.singleImgeShapeableImageView);
        }
    }
}