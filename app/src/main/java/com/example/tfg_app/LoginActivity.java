package com.example.tfg_app;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Acceder al Panel
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            // TODO: implementar lógica de autenticación
            Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();
        });

        // Google sign-in
        LinearLayout btnGoogle = findViewById(R.id.btn_google);
        btnGoogle.setOnClickListener(v -> {
            // TODO: implementar Google Sign-In
            Toast.makeText(this, "Continuar con Google", Toast.LENGTH_SHORT).show();
        });

        // ¿Olvidó su contraseña?
        TextView tvForgot = findViewById(R.id.tv_forgot_password);
        tvForgot.setOnClickListener(v -> {
            // TODO: navegar a pantalla de recuperación
            Toast.makeText(this, "Recuperar contraseña", Toast.LENGTH_SHORT).show();
        });

        // Regístrate ahora
        TextView tvRegister = findViewById(R.id.tv_register_link);
        tvRegister.setOnClickListener(v -> {
            // Volver a MainActivity
            finish();
        });
    }
}
