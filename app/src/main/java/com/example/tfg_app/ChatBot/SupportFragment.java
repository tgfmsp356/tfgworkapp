package com.example.tfg_app.ChatBot;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_app.R;
import com.example.tfg_app.adapters.ServiceCardAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupportFragment extends Fragment {

    // ── Tipos de ítem del chat ────────────────────────────────────────────────
    private static final int TYPE_USER    = 0;
    private static final int TYPE_BOT     = 1;
    private static final int TYPE_CARDS   = 2;   // fila de tarjetas de servicios
    private static final int TYPE_LOADING = 3;   // burbuja "escribiendo..."

    // ── Modelo de mensaje ─────────────────────────────────────────────────────
    private static class ChatItem {
        final int    type;
        final String text;        // para TYPE_USER y TYPE_BOT
        final List<AgentApiClient.Servicio> servicios; // para TYPE_CARDS
        final String time;

        // Mensaje de texto
        ChatItem(int type, String text) {
            this.type      = type;
            this.text      = text;
            this.servicios = null;
            this.time      = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        }

        // Tarjetas de servicios
        ChatItem(List<AgentApiClient.Servicio> servicios) {
            this.type      = TYPE_CARDS;
            this.text      = null;
            this.servicios = servicios;
            this.time      = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        }

        // Indicador de carga
        static ChatItem loading() {
            return new ChatItem(TYPE_LOADING, "...");
        }
    }

    // ── Adapter del RecyclerView principal ────────────────────────────────────
    private class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<ChatItem> items;

        ChatAdapter(List<ChatItem> items) { this.items = items; }

        @Override public int getItemViewType(int position) { return items.get(position).type; }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inf = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case TYPE_CARDS:
                    return new CardsVH(inf.inflate(R.layout.item_service_cards_row, parent, false));
                default:
                    return new BubbleVH(inf.inflate(R.layout.item_message, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChatItem item = items.get(position);
            if (holder instanceof BubbleVH) {
                BubbleVH h = (BubbleVH) holder;
                LinearLayout root = (LinearLayout) h.itemView;

                if (item.type == TYPE_LOADING) {
                    root.setGravity(Gravity.START);
                    h.bubbleContainer.setBackgroundResource(R.drawable.bg_bubble_bot);
                    h.tvMessage.setText("Escribiendo...");
                    h.tvMessage.setTextColor(0xFF9E9E9E);
                    h.tvTime.setVisibility(View.GONE);
                } else if (item.type == TYPE_USER) {
                    root.setGravity(Gravity.END);
                    h.bubbleContainer.setBackgroundResource(R.drawable.bg_bubble_user);
                    h.tvMessage.setText(item.text);
                    h.tvMessage.setTextColor(0xFFFFFFFF);
                    h.tvTime.setGravity(Gravity.END);
                    h.tvTime.setText(item.time);
                    h.tvTime.setVisibility(View.VISIBLE);
                } else { // TYPE_BOT
                    root.setGravity(Gravity.START);
                    h.bubbleContainer.setBackgroundResource(R.drawable.bg_bubble_bot);
                    h.tvMessage.setText(item.text);
                    h.tvMessage.setTextColor(0xFF1A1A2E);
                    h.tvTime.setGravity(Gravity.START);
                    h.tvTime.setText(item.time);
                    h.tvTime.setVisibility(View.VISIBLE);
                }
            } else if (holder instanceof CardsVH) {
                CardsVH h = (CardsVH) holder;
                ServiceCardAdapter cardAdapter = new ServiceCardAdapter(item.servicios);
                h.rvCards.setLayoutManager(
                        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                h.rvCards.setAdapter(cardAdapter);
            }
        }

        @Override public int getItemCount() { return items.size(); }
    }

    // ── ViewHolders ───────────────────────────────────────────────────────────
    static class BubbleVH extends RecyclerView.ViewHolder {
        LinearLayout bubbleContainer;
        TextView     tvMessage, tvTime;
        BubbleVH(@NonNull View v) {
            super(v);
            bubbleContainer = v.findViewById(R.id.bubble_container);
            tvMessage       = v.findViewById(R.id.tv_message);
            tvTime          = v.findViewById(R.id.tv_time);
        }
    }

    static class CardsVH extends RecyclerView.ViewHolder {
        RecyclerView rvCards;
        CardsVH(@NonNull View v) {
            super(v);
            rvCards = v.findViewById(R.id.rv_service_cards);
        }
    }

    // ── Estado del fragment ───────────────────────────────────────────────────
    private final List<ChatItem>              chatItems   = new ArrayList<>();
    private       ChatAdapter                 chatAdapter;
    private       RecyclerView                rvMessages;
    private       EditText                    etMessage;
    private       AgentApiClient              apiClient;

    // Historial para el backend (rol + texto)
    private final List<AgentApiClient.MessageIn> historial = new ArrayList<>();

    // Índice del item de carga (-1 si no hay ninguno)
    private int loadingIndex = -1;

    // ── Ciclo de vida ─────────────────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMessages = view.findViewById(R.id.rv_messages);
        etMessage  = view.findViewById(R.id.et_message);
        ImageButton btnSend = view.findViewById(R.id.btn_send);

        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setStackFromEnd(true);
        rvMessages.setLayoutManager(llm);
        chatAdapter = new ChatAdapter(chatItems);
        rvMessages.setAdapter(chatAdapter);

        apiClient = new AgentApiClient();

        // Mensaje de bienvenida (local, sin llamar al backend)
        addItem(new ChatItem(TYPE_BOT,
                "¡Hola! Soy el asistente de WorkApp. Puedo ayudarte a encontrar servicios freelance. ¿Qué necesitas?"));

        btnSend.setOnClickListener(v -> sendMessage());
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) { sendMessage(); return true; }
            return false;
        });
    }

    // ── Envío de mensaje ──────────────────────────────────────────────────────
    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        // 1. Mostrar mensaje del usuario
        addItem(new ChatItem(TYPE_USER, text));
        etMessage.setText("");

        // 2. Guardar en historial para el backend
        historial.add(new AgentApiClient.MessageIn("user", text));

        // 3. Mostrar indicador de carga
        showLoading();

        // 4. Llamar al backend
        apiClient.query(text, historial, new AgentApiClient.AgentCallback() {
            @Override
            public void onSuccess(String mensaje, List<AgentApiClient.Servicio> servicios) {
                hideLoading();

                // Respuesta de texto del LLM
                addItem(new ChatItem(TYPE_BOT, mensaje));
                historial.add(new AgentApiClient.MessageIn("assistant", mensaje));

                // Tarjetas de servicios (si las hay)
                if (servicios != null && !servicios.isEmpty()) {
                    addItem(new ChatItem(servicios));
                }
            }

            @Override
            public void onError(String errorMsg) {
                hideLoading();
                addItem(new ChatItem(TYPE_BOT,
                        "Lo siento, no pude contactar con el servidor. " + errorMsg));
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void addItem(ChatItem item) {
        chatItems.add(item);
        chatAdapter.notifyItemInserted(chatItems.size() - 1);
        rvMessages.smoothScrollToPosition(chatItems.size() - 1);
    }

    private void showLoading() {
        chatItems.add(ChatItem.loading());
        loadingIndex = chatItems.size() - 1;
        chatAdapter.notifyItemInserted(loadingIndex);
        rvMessages.smoothScrollToPosition(loadingIndex);
    }

    private void hideLoading() {
        if (loadingIndex >= 0 && loadingIndex < chatItems.size()) {
            chatItems.remove(loadingIndex);
            chatAdapter.notifyItemRemoved(loadingIndex);
            loadingIndex = -1;
        }
    }
}
