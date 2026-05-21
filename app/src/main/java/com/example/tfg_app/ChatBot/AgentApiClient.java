package com.example.tfg_app.ChatBot;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AgentApiClient {

    // ── Cambia esta IP según dónde corra el backend ──────────────────────────
    // Emulador Android Studio  → 10.0.2.2
    // Dispositivo físico       → IP local de tu PC (ej: 192.168.1.X)
    private static final String BASE_URL = "http://192.168.1.61:8000";
    // ─────────────────────────────────────────────────────────────────────────

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final Handler mainHandler;

    // ── POJOs de request / response ──────────────────────────────────────────

    public static class MessageIn {
        public String role;
        public String content;

        public MessageIn(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    public static class AgentRequest {
        public String mensaje;
        public List<MessageIn> historial;

        public AgentRequest(String mensaje, List<MessageIn> historial) {
            this.mensaje = mensaje;
            this.historial = historial != null ? historial : new ArrayList<>();
        }
    }

    public static class Servicio {
        public String id;
        public String titulo;
        public String descripcion;
        @SerializedName("precio_hora")   public double  precioHora;
        @SerializedName("tiempo_entrega") public int    tiempoEntrega;
        @SerializedName("categoria_id")   public String categoriaId;
        @SerializedName("categoria_nombre") public String categoriaNombre;
        @SerializedName("usuario_nombre") public String usuarioNombre;
        @SerializedName("valoracion_media") public Double valoracionMedia;
        @SerializedName("imagen_url")     public String imagenUrl;
    }

    public static class AgentResponse {
        public String mensaje;
        public List<Servicio> servicios;
    }

    // ── Callback para el llamador ─────────────────────────────────────────────

    public interface AgentCallback {
        void onSuccess(String mensaje, List<Servicio> servicios);
        void onError(String errorMsg);
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    public AgentApiClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)   // el LLM puede tardar
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // ── Método principal ──────────────────────────────────────────────────────

    /**
     * Envía la consulta del usuario al agente LangGraph.
     *
     * @param mensaje   Texto que escribió el usuario.
     * @param historial Lista de mensajes previos del chat (puede ser vacía).
     * @param callback  Se llama en el hilo principal con la respuesta o el error.
     */
    public void query(String mensaje, List<MessageIn> historial, AgentCallback callback) {
        AgentRequest requestBody = new AgentRequest(mensaje, historial);
        String json = gson.toJson(requestBody);

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/agent")
                .post(RequestBody.create(json, JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() ->
                        callback.onError("No se pudo conectar con el servidor: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (response) {
                    String body = response.body() != null ? response.body().string() : "";
                    if (!response.isSuccessful()) {
                        mainHandler.post(() ->
                                callback.onError("Error del servidor (" + response.code() + ")")
                        );
                        return;
                    }
                    AgentResponse agentResponse = gson.fromJson(body, AgentResponse.class);
                    List<Servicio> servicios = agentResponse.servicios != null
                            ? agentResponse.servicios
                            : new ArrayList<>();
                    mainHandler.post(() ->
                            callback.onSuccess(agentResponse.mensaje, servicios)
                    );
                } catch (Exception e) {
                    mainHandler.post(() ->
                            callback.onError("Error al procesar la respuesta: " + e.getMessage())
                    );
                }
            }
        });
    }
}
