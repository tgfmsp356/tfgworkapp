package com.example.tfg_app.utils;

import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class CloudinaryHelper {

    /**
     * Callback simple para informar del resultado de la subida.
     */
    public interface UploadResultCallback {
        void onSuccess(String url);
        void onError(String errorMessage);
    }

    /**
     * Sube una imagen a Cloudinary dentro de la carpeta especificada.
     *
     * @param imageUri URI de la imagen seleccionada (de la galería).
     * @param folder   Carpeta en Cloudinary (ej: "perfiles", "anuncios").
     * @param callback Devuelve la URL pública si va bien, o un mensaje de error.
     */
    public static void uploadImage(Uri imageUri, String folder, UploadResultCallback callback) {
        MediaManager.get()
                .upload(imageUri)
                .option("folder", folder)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // Empieza la subida (podríamos mostrar un loading aquí)
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Progreso (opcional para barra de progreso)
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = (String) resultData.get("secure_url");
                        callback.onSuccess(url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onError(error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        callback.onError("Subida reprogramada: " + error.getDescription());
                    }
                })
                .dispatch();
    }
}