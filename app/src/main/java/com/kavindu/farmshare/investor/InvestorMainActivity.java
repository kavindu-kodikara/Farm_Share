package com.kavindu.farmshare.investor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

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
import com.kavindu.farmshare.MainActivity;
import com.kavindu.farmshare.R;

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

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        // Setup Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();




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
            navigationView.setCheckedItem(R.id.nav_home);
        } else if (id == R.id.nav_farms) {

            Intent intent = new Intent(InvestorMainActivity.this,InvestorFarmsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_transaction) {

            transaction.replace(R.id.content_frame, new InvestorTransactionFragment());
            navigationView.setCheckedItem(R.id.nav_transaction);

        } else if (id == R.id.nav_profile) {

            transaction.replace(R.id.content_frame, new InvestorProfileFragment());
            navigationView.setCheckedItem(R.id.nav_profile);

        } else if (id == R.id.nav_logout) {
            SharedPreferences sp = getSharedPreferences("com.kavindu.farmshare.data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("user", null);
            editor.apply();

            Intent intent = new Intent(InvestorMainActivity.this, MainActivity.class);
            startActivity(intent);
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
