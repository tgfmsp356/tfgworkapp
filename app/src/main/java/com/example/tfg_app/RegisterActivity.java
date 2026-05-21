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

    private EditText etUsername, etNombre, etApellido1, etApellido2, etTelefono, etMail, etPasswd;
    private MaterialButton btnRegister;
    private LinearLayout btnGoogle;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        etUsername  = findViewById(R.id.et_username);
        etNombre    = findViewById(R.id.et_nombre);
        etApellido1 = findViewById(R.id.et_apellido1);
        etApellido2 = findViewById(R.id.et_apellido2);
        etTelefono  = findViewById(R.id.et_telefono);
        etMail      = findViewById(R.id.et_email);
        etPasswd    = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        btnGoogle   = findViewById(R.id.btn_google);

        btnRegister.setOnClickListener(v -> registrarse());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            iniciarPantallaInicio();
        }
    }

    private void registrarse() {
        String username  = etUsername.getText().toString().trim();
        String nombre    = etNombre.getText().toString().trim();
        String apellido1 = etApellido1.getText().toString().trim();
        String apellido2 = etApellido2.getText().toString().trim();
        String telefono  = etTelefono.getText().toString().trim();
        String email     = etMail.getText().toString().trim();
        String password  = etPasswd.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("El nombre de usuario es obligatorio");
            etUsername.requestFocus();
            return;
        }
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            etNombre.requestFocus();
            return;
        }
        if (apellido1.isEmpty()) {
            etApellido1.setError("El primer apellido es obligatorio");
            etApellido1.requestFocus();
            return;
        }
        if (telefono.isEmpty()) {
            etTelefono.setError("El teléfono es obligatorio");
            etTelefono.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etMail.setError("El email no es válido");
            etMail.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 9) {
            etPasswd.setError("La contraseña debe tener al menos 9 caracteres");
            etPasswd.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    guardarUsuarioEnFirestore(user.getUid(), username, nombre, apellido1, apellido2, telefono, email);
                } else {
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Error: no se pudo obtener el usuario", Toast.LENGTH_SHORT).show();
                }
            } else {
                btnRegister.setEnabled(true);
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarUsuarioEnFirestore(String uid, String username, String nombre,
                                           String apellido1, String apellido2,
                                           String telefono, String email) {
        String fechaRegistro = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(new java.util.Date());

        Usuario nuevoUsuario = new Usuario(
                uid,          // id
                uid,          // firebase_uid
                nombre,       // nombre
                apellido1,    // apellido1
                apellido2,    // apellido2 (puede estar vacío)
                telefono,     // telefono
                username,     // nombre_usuario
                email,        // email
                "",           // foto_perfil
                "",           // descripcion
                fechaRegistro // fecha_registro
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
        Intent intent = new Intent(this, NavActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
