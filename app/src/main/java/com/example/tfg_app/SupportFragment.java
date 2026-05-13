package com.example.tfg_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupportFragment extends Fragment {


    private static class Message {
        static final int TYPE_USER = 0;
        static final int TYPE_BOT  = 1;

        final String text;
        final int type;
        final String time;

        Message(String text, int type) {
            this.text = text;
            this.type = type;
            this.time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        }
    }


    private static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

        private final List<Message> messages;

        MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Message msg = messages.get(position);
            h.tvMessage.setText(msg.text);
            h.tvTime.setText(msg.time);

            LinearLayout root = (LinearLayout) h.itemView;

            if (msg.type == Message.TYPE_USER) {
                // Burbuja usuario → derecha, fondo primario
                root.setGravity(Gravity.END);
                h.bubbleContainer.setBackgroundResource(R.drawable.bg_bubble_user);
                h.tvMessage.setTextColor(0xFFFFFFFF);
                h.tvTime.setGravity(Gravity.END);
            } else {
                // Burbuja bot → izquierda, fondo gris claro
                root.setGravity(Gravity.START);
                h.bubbleContainer.setBackgroundResource(R.drawable.bg_bubble_bot);
                h.tvMessage.setTextColor(0xFF1A1A2E);
                h.tvTime.setGravity(Gravity.START);
            }
        }

        @Override
        public int getItemCount() { return messages.size(); }

        static class VH extends RecyclerView.ViewHolder {
            LinearLayout bubbleContainer;
            TextView     tvMessage;
            TextView     tvTime;

            VH(@NonNull View itemView) {
                super(itemView);
                bubbleContainer = itemView.findViewById(R.id.bubble_container);
                tvMessage       = itemView.findViewById(R.id.tv_message);
                tvTime          = itemView.findViewById(R.id.tv_time);
            }
        }
    }

    // ── Fragment ─────────────────────────────────────────────────────────────

    private final List<Message>  messages = new ArrayList<>();
    private       MessageAdapter adapter;
    private       RecyclerView   rvMessages;
    private       EditText       etMessage;

    /** Respuestas automáticas simples del bot */
    private static final String[] BOT_RESPONSES = {
            "¡Hola! Estoy aquí para ayudarte. ¿En qué puedo asistirte?",
            "Entiendo tu consulta. Déjame buscarte la mejor solución.",
            "Gracias por tu mensaje. Un agente revisará tu caso pronto.",
            "¿Podrías darme más detalles sobre el problema?",
            "Perfecto, he registrado tu solicitud. Te responderemos en breve."
    };
    private int botResponseIndex = 0;

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

        // RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setStackFromEnd(true);   // mensajes nuevos aparecen abajo
        rvMessages.setLayoutManager(llm);
        adapter = new MessageAdapter(messages);
        rvMessages.setAdapter(adapter);

        // Mensaje de bienvenida del bot
        addBotMessage("¡Hola! 👋 Soy el asistente de soporte. ¿En qué puedo ayudarte hoy?");

        // Botón enviar
        btnSend.setOnClickListener(v -> sendMessage());

        // Tecla "Enviar" del teclado
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        // Añadir mensaje del usuario
        addMessage(new Message(text, Message.TYPE_USER));
        etMessage.setText("");

        // Simular respuesta del bot con un pequeño delay
        new Handler(Looper.getMainLooper()).postDelayed(this::simulateBotResponse, 800);
    }

    private void simulateBotResponse() {
        String response = BOT_RESPONSES[botResponseIndex % BOT_RESPONSES.length];
        botResponseIndex++;
        addBotMessage(response);
    }

    private void addBotMessage(String text) {
        addMessage(new Message(text, Message.TYPE_BOT));
    }

    private void addMessage(Message msg) {
        messages.add(msg);
        adapter.notifyItemInserted(messages.size() - 1);
        rvMessages.smoothScrollToPosition(messages.size() - 1);
    }
}
