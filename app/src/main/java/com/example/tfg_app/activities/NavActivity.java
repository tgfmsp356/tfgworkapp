package com.example.tfg_app.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.tfg_app.ChatBot.SupportFragment;
import com.example.tfg_app.R;
import com.example.tfg_app.fragments.CreateFragment;
import com.example.tfg_app.fragments.HomeFragment;
import com.example.tfg_app.fragments.ProfileFragment;
import com.example.tfg_app.fragments.ServicesFragment;

public class NavActivity extends AppCompatActivity {

    private LinearLayout navHome, navServices, navCreate, navSupport, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        navHome     = findViewById(R.id.nav_home);
        navServices = findViewById(R.id.nav_services);
        navCreate   = findViewById(R.id.nav_create);
        navSupport  = findViewById(R.id.nav_support);
        navProfile  = findViewById(R.id.nav_profile);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            updateNavSelection(navHome);
        }

        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateNavSelection(navHome);
        });
        navServices.setOnClickListener(v -> {
            loadFragment(new ServicesFragment());
            updateNavSelection(navServices);
        });
        navCreate.setOnClickListener(v -> {
            loadFragment(new CreateFragment());
            updateNavSelection(navCreate);
        });
        navSupport.setOnClickListener(v -> {
            loadFragment(new SupportFragment());
            updateNavSelection(navSupport);
        });
        navProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            updateNavSelection(navProfile);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }


    //Gestion de la marca azil en el fragment activo
    private void updateNavSelection(LinearLayout selected) {
        LinearLayout[] items = {navHome, navServices, navCreate, navSupport, navProfile};
        int colorActive   = ContextCompat.getColor(this, R.color.color_bottom_nav_active);
        int colorInactive = ContextCompat.getColor(this, R.color.color_bottom_nav_inactive);

        for (LinearLayout item : items) {
            boolean isSelected = (item == selected);
            int color = isSelected ? colorActive : colorInactive;

            // El icono es el primer hijo, el texto el segundo
            if (item.getChildAt(0) instanceof android.widget.ImageView) {
                ((android.widget.ImageView) item.getChildAt(0)).setColorFilter(color);
            }
            if (item.getChildAt(1) instanceof TextView) {
                ((TextView) item.getChildAt(1)).setTextColor(color);
            }
        }
    }
}