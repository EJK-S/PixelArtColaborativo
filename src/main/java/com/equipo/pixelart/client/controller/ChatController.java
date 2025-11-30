package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.utils.ResourceLoader;
import com.equipo.pixelart.client.view.ChatPanel;

public class ChatController implements NetworkListener {

    private final ChatPanel chatPanel;
    private final NetworkController networkController;

    public ChatController(ChatPanel chatPanel, NetworkController networkController) {
        this.chatPanel = chatPanel;
        this.networkController = networkController;
    }

    public void init() {
        
        chatPanel.addSendListener(e -> gestionarEnvioMensaje());

        
        networkController.addNetworkListener(this);
    }

    private void gestionarEnvioMensaje() {
        String msg = chatPanel.getMessageAndClear();
        if (msg.isEmpty()) {
            return;
        }

        if (networkController.isConnected()) {
            networkController.sendChatMessage(msg);
        } else {
            chatPanel.addSystemMessage("No estás conectado.");
        }
        chatPanel.focusMessageField();
    }
    
    public void addSystemMessage(String message) {
        chatPanel.addSystemMessage(message);
    }

    @Override
    public void onChatMessage(String user, String txt) {
        chatPanel.addMessage(user, txt);
        
        
        ResourceLoader.playSound("/sounds/message.wav");
    }

    @Override
    public void onConnectionStatusChanged(boolean connected, String msg) {
        chatPanel.setEnabled(connected);
        chatPanel.addSystemMessage(connected ? "Conectado" : "Desconectado");
    }

    @Override
    public void onUserJoined(String u) {
        chatPanel.addSystemMessage(u + " se ha unido");
        
        
        ResourceLoader.playSound("/sounds/chat.wav");
    }

    @Override
    public void onUserLeft(String u) {
        chatPanel.addSystemMessage(u + " se ha desconectado");
        
        
        ResourceLoader.playSound("/sounds/disconnect.wav");
    }

    @Override
    public void onError(String m) {
        chatPanel.addSystemMessage("ERROR: " + m);
    }
}