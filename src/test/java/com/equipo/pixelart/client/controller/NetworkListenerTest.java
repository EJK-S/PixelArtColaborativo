package com.equipo.pixelart.client.controller;

import org.junit.jupiter.api.Test;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NetworkListenerTest {

    @Test
    void testDefaultMethods() {
   
        NetworkListener listener = new NetworkListener() {};

       
        
        assertDoesNotThrow(() -> {
            listener.onConnectionStatusChanged(true, "Test Message");
            listener.onAuthResult(true, "Login OK");
            listener.onChatMessage("User1", "Hola");
            listener.onUserJoined("User2");
            listener.onUserLeft("User3");
            listener.onUserListReceived(new String[]{"User1", "User2"});
            listener.onError("Error de prueba");
            listener.onPixelUpdateReceived(10, 10, Color.RED);
            listener.onCanvasStateReceived(new Color[2][2]);
            listener.onCanvasCleared();
        });
    }
}