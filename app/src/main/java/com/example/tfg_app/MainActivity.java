package com.example.tfg_app;

import com.example.tfg_app.R;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navServices;
    private LinearLayout navProfile;
    private LinearLayout navHome;
    private LinearLayout navCreate;
    private LinearLayout navSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View bottomNavView = findViewById(R.id.bottom_nav_include);

        navServices = bottomNavView.findViewById(R.id.nav_services);
        navProfile = bottomNavView.findViewById(R.id.nav_profile);
        navHome = bottomNavView.findViewById(R.id.nav_home);
        navCreate = bottomNavView.findViewById(R.id.nav_create);
        navSupport = bottomNavView.findViewById(R.id.nav_support);

        navServices.setOnClickListener(v -> loadFragment(new ServicesFragment()));
        navProfile.setOnClickListener(v -> loadFragment(new ProfileFragment()));
        navHome.setOnClickListener(v -> loadFragment(new HomeFragment()));
        navCreate.setOnClickListener(v -> loadFragment(new CreateFragment()));
        navSupport.setOnClickListener(v -> loadFragment(new SupportFragment()));

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
