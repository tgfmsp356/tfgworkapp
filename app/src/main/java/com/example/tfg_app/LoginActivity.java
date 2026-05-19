package com.example.tfg_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etMail;
    private EditText etPasswd;
    private MaterialButton btnLogin;
    private LinearLayout btnGoogle;
    private TextView tvForgot;
    private TextView tvRegister;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //config inicializacion firebase auth
        auth = FirebaseAuth.getInstance();

        // iniciar botones y vistas
        btnLogin = findViewById(R.id.btn_login);
        btnGoogle = findViewById(R.id.btn_google);
        tvForgot = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register_link);
        etMail = findViewById(R.id.et_email);
        etPasswd = findViewById(R.id.et_password);


        // Acceder al Panel
        btnLogin.setOnClickListener(v -> iniciarSesion());

        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Continuar con Google",Toast.LENGTH_SHORT).show();
        });

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
                        Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
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


