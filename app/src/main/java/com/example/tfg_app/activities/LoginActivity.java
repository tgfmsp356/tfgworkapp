package com.example.tfg_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg_app.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etMail;
    private EditText etPasswd;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register_link);
        etMail = findViewById(R.id.et_email);
        etPasswd = findViewById(R.id.et_password);


        // Acceder al Panel
        btnLogin.setOnClickListener(v -> iniciarSesion());

        //tvForgot.setOnClickListener(v -> recuperarPasswd());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAutenticado = auth.getCurrentUser();

        if (usuarioAutenticado != null){
            iniciarPantallaInicio();
        }
    }

    private void iniciarSesion(){
        String email = etMail.getText().toString().trim();
        String password = etPasswd.getText().toString().trim();

        if (email.isEmpty()){
            etMail.setError("Email no valido");
            etMail.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 9){
            etPasswd.setError("La contraseña debe contener almenos 9 caracteres");
            etPasswd.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        iniciarPantallaInicio();
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Error desconocido";
                        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void iniciarPantallaInicio() {
        // De momento volvemos a MainActivity; cuando tengas el panel de usuario, cámbialo
        Intent intent = new Intent(this, NavActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}