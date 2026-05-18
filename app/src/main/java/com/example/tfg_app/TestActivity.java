package com.example.tfg_app;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class TestActivity extends AppCompatActivity {

    private LinearLayout navHome, navServices, navSupport, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // 0 abajo → la nav ocupa ese espacio
            return insets;
        });

        // Referencias al bottom nav
        navHome     = findViewById(R.id.nav_home);
        navServices = findViewById(R.id.nav_services);
        navSupport  = findViewById(R.id.nav_support);
        navProfile  = findViewById(R.id.nav_profile);

        // Fragment inicial
        if (savedInstanceState == null) {
            loadFragment(new SupportFragment()); // Arranca en Soporte durante desarrollo
        }

        // Listeners de navegación
        navHome.setOnClickListener(v -> loadFragment(new SupportFragment())); // placeholder
        navServices.setOnClickListener(v -> loadFragment(new SupportFragment())); // placeholder
        navSupport.setOnClickListener(v -> loadFragment(new SupportFragment()));
        navProfile.setOnClickListener(v -> loadFragment(new SupportFragment())); // placeholder
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }
}