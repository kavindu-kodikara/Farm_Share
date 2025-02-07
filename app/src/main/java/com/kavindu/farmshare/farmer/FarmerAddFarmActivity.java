package com.kavindu.farmshare.farmer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.kavindu.farmshare.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FarmerAddFarmActivity extends AppCompatActivity {

    private GoogleMap map;




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

        SupportMapFragment supportMapFragment = new SupportMapFragment();

        //farm location google map

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mapFrameLayout,supportMapFragment);
        fragmentTransaction.commit();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);

                }else {

                    map = googleMap;

                    String[] permissionArray = new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    };

                    requestPermissions(permissionArray,100);

                }



            }
        });

        //farm image selection

        List<Uri> imageUris = new ArrayList<>();

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
                    }
                });

        soilReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soilReportfilePickerLauncher.launch("*/*");
            }
        });

    }

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
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_image, parent, false);
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
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}