package com.equipo.pixelart.client.controller;

import org.junit.jupiter.api.Test;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NetworkListenerTest {

    @Test
    void testDefaultMethods() {
        // 1. Creamos una implementación anónima de la interfaz
        // Esto usa las implementaciones 'default' que definiste
        NetworkListener listener = new NetworkListener() {};

        // 2. Llamamos a CADA método para forzar a la JVM a entrar en las llaves {}
        // Usamos assertDoesNotThrow para confirmar que la ejecución es limpia.
        
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