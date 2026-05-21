package com.example.tfg_app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tfg_app.activities.MainActivity;
import com.example.tfg_app.POJOS.Usuario;
import com.example.tfg_app.R;
import com.example.tfg_app.utils.CloudinaryHelper;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvFullName, tvEmail, tvDescription, tvRegisterDate;
    private Button btnSignOut;
    private ShapeableImageView ivProfilePic;
    private ImageView ivEditDescription;
    private FirebaseAuth auth;
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK
                        && result.getData() != null
                        && result.getData().getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    subirFotoPerfil(selectedImageUri);
                }
            });

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvFullName = view.findViewById(R.id.tv_full_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvRegisterDate = view.findViewById(R.id.tv_register_date);
        btnSignOut = view.findViewById(R.id.btn_signOut);
        ivProfilePic = view.findViewById(R.id.iv_profile_pic);
        ivEditDescription = view.findViewById(R.id.iv_edit_description);
        tvDescription = view.findViewById(R.id.tv_description);

        auth = FirebaseAuth.getInstance();

        btnSignOut.setOnClickListener(v -> setBtnSignOut());

        ivProfilePic.setOnClickListener(v -> abrirGaleria());
        ivEditDescription.setOnClickListener(v -> mostrarDialogoEditarDescripcion());

        return view;
    }

    private void mostrarDialogoEditarDescripcion() {
        if (getContext() == null) return;

        // EditText prellenado con la descripción actual
        EditText input = new EditText(getContext());
        input.setText(tvDescription.getText().toString().equals("Sin descripción disponible.")
                ? ""
                : tvDescription.getText().toString());
        input.setHint("Cuéntanos algo sobre ti...");
        input.setMinLines(3);
        input.setMaxLines(6);
        input.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);

        int padding = (int) (16 * getResources().getDisplayMetrics().density);

        // Contenedor con padding para que no quede pegado a los bordes
        android.widget.FrameLayout container = new android.widget.FrameLayout(getContext());
        container.setPadding(padding, padding / 2, padding, 0);
        container.addView(input);

        new AlertDialog.Builder(getContext())
                .setTitle("Editar descripción")
                .setView(container)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevaDescripcion = input.getText().toString().trim();
                    guardarDescripcion(nuevaDescripcion);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarDescripcion(String descripcion) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        FirestoreHelper.updateUsuarioField(user.getUid(), "descripcion", descripcion)
                .addOnSuccessListener(aVoid -> {
                    // Actualizar el TextView inmediatamente
                    if (descripcion.isEmpty()) {
                        tvDescription.setText("Sin descripción disponible.");
                    } else {
                        tvDescription.setText(descripcion);
                    }
                    Toast.makeText(getContext(), "Descripción actualizada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            obtenerDatosUsuario(firebaseUser.getUid());
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerDatosUsuario(String uid) {
        FirestoreHelper.getUsuario(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        if (usuario != null) {
                            mostrarDatos(usuario);
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void mostrarDatos(Usuario usuario) {
        if (usuario.getNombre_usuario() != null) tvUsername.setText("@" + usuario.getNombre_usuario());
        if (usuario.getNombre() != null) tvFullName.setText(usuario.getNombre());
        if (usuario.getEmail() != null) tvEmail.setText(usuario.getEmail());
        if (usuario.getDescripcion() != null && !usuario.getDescripcion().isEmpty()) {
            tvDescription.setText(usuario.getDescripcion());
        }
        if (usuario.getFecha_registro() != null) {
            tvRegisterDate.setText("Miembro desde: " + usuario.getFecha_registro());
        }

        if (usuario.getFoto_perfil() != null && !usuario.getFoto_perfil().isEmpty()) {
            Glide.with(this)
                    .load(usuario.getFoto_perfil())
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfilePic);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void subirFotoPerfil(Uri imageUri) {
        Glide.with(this).load(imageUri).into(ivProfilePic);

        Toast.makeText(getContext(), "Subiendo foto...", Toast.LENGTH_SHORT).show();

        CloudinaryHelper.uploadImage(imageUri, "perfiles", new CloudinaryHelper.UploadResultCallback() {
            @Override
            public void onSuccess(String url) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    FirestoreHelper.updateUsuarioField(user.getUid(), "foto_perfil", url)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(getContext(), "Foto actualizada", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Error al guardar URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error al subir: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setBtnSignOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}