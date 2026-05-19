package com.example.tfg_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_app.POJOS.Usuario;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etMail;
    private EditText etPasswd;
    private MaterialButton btnRegister;
    private LinearLayout btnGoogle;
    private TextView tvForgot;
    private TextView tvRegister;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        //config inicializacion firebase auth
        auth = FirebaseAuth.getInstance();

        // iniciar botones y vistas
        btnRegister = findViewById(R.id.btn_register);
        btnGoogle = findViewById(R.id.btn_google);
        etMail = findViewById(R.id.et_email);
        etPasswd = findViewById(R.id.et_password);
        etUsername = findViewById(R.id.et_username);

        btnRegister.setOnClickListener(v -> registrarse());

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAutenticado = auth.getCurrentUser();

        if (usuarioAutenticado != null){
            iniciarPantallaInicio();
        }
    }

    private void registrarse(){
        String username = etUsername.getText().toString().trim();
        String email = etMail.getText().toString().trim();
        String password = etPasswd.getText().toString().trim();

        if (username.isEmpty()){
            etUsername.setError("Username no valido");
            etUsername.requestFocus();
            return;
        }

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

        btnRegister.setEnabled(false);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    guardarUsuarioEnFirestore(user.getUid(), username, email);
                } else {
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Error: no se pudo obtener el usuario", Toast.LENGTH_SHORT).show();
                }
            } else {
                btnRegister.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarUsuarioEnFirestore(String uid, String username, String email) {
        String fechaRegistro = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(new java.util.Date());

        Usuario nuevoUsuario = new Usuario(
                uid,              // id (coincide con el UID de Firebase Auth)
                uid,              // firebase_uid
                username,         // nombre (lo usamos como nombre completo provisional)
                username,         // nombre_usuario
                email,            // email
                "",               // foto_perfil (vacío de momento)
                "",               // descripcion (vacío de momento)
                fechaRegistro     // fecha_registro
        );

        FirestoreHelper.addUsuario(nuevoUsuario).addOnCompleteListener(saveTask -> {
            if (saveTask.isSuccessful()) {
                iniciarPantallaInicio();
            } else {
                btnRegister.setEnabled(true);
                Toast.makeText(this, "Error al guardar datos: " + saveTask.getException().getMessage(), Toast.LENGTH_LONG).show();
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