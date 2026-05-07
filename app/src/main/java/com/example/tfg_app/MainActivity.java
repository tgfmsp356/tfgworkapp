package com.example.tfg_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // "Empezar ahora" → Register flow (placeholder)
        MaterialButton btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(v -> {
            // TODO: navegar a pantalla de registro
        });

        // "Acceder a mi cuenta" → Login screen
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> prueba = new HashMap<>();
        prueba.put("mensaje", "Hola Firestore");
        prueba.put("timestamp", System.currentTimeMillis());

        db.collection("test").add(prueba)
                .addOnSuccessListener(docRef -> Log.d("FIREBASE", "Documento creado: " + docRef.getId()))
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error", e));
    }
}
