package com.kavindu.farmshare.investor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.kavindu.farmshare.R;
import com.kavindu.farmshare.farmer.FarmerHomeFragment;
import com.kavindu.farmshare.farmer.FarmerProfileFragment;

public class InvestorMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_investor_main);

        // Handling window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setup Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set user data in navigation header
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.user_name);
        TextView userEmail = headerView.findViewById(R.id.user_email);
        userName.setText("Kavindu");
        userEmail.setText("kavindu@example.com");

//        Button btnOpenDrawer = findViewById(R.id.btn_open_drawer);
//        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openDrawer();
//            }
//        });

        // Load Default Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new InvestorHomeFragment())
                .commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_home) {

            transaction.replace(R.id.content_frame, new InvestorHomeFragment());
        } else if (id == R.id.nav_investments) {

            transaction.replace(R.id.content_frame, new FarmerProfileFragment());
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_logout) {

        }

        // Commit the transaction
        transaction.commit();

        // Close the drawer after selecting an item
        drawerLayout.closeDrawers();
        return true;
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
