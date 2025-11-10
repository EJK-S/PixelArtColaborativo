package com.equipo.pixelart.client.controller;

import java.awt.Color;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NetworkControllerTest {

    private NetworkController networkController;
    private NetworkController.NetworkListener listenerMock;

        @SuppressWarnings("unused")

    @BeforeEach
    void setUp() {
        networkController = new NetworkController();
        listenerMock = mock(NetworkController.NetworkListener.class);
        networkController.addNetworkListener(listenerMock);
    }

    @Test
    void testInicialmenteNoConectado() {
        assertFalse(networkController.isConnected(), "Debe iniciar desconectado");
    }

    @Test
    void testNotificaConexionExitosa() throws Exception {
        
        networkController.getClass()
            .getDeclaredMethod("notifyConnectionStatus", boolean.class, String.class)
            .invoke(networkController, true, "Conectado");

        verify(listenerMock).onConnectionStatusChanged(true, "Conectado");
    }

    @Test
    void testLoginEnvíaMensajeCorrecto() throws Exception {
       
        PrintWriter mockOut = mock(PrintWriter.class);

        
        var field = NetworkController.class.getDeclaredField("out");
        field.setAccessible(true);
        field.set(networkController, mockOut);

        var connectedField = NetworkController.class.getDeclaredField("connected");
        connectedField.setAccessible(true);
        connectedField.set(networkController, true);

        networkController.login("User", "1234");

        verify(mockOut).println("LOGIN|User|1234");
    }

    @Test
    void testSendPixelUpdateEnvíaMensajeCorrecto() throws Exception {
        PrintWriter mockOut = mock(PrintWriter.class);

        var field = NetworkController.class.getDeclaredField("out");
        field.setAccessible(true);
        field.set(networkController, mockOut);

        var connectedField = NetworkController.class.getDeclaredField("connected");
        connectedField.setAccessible(true);
        connectedField.set(networkController, true);

        networkController.sendPixelUpdate(2, 3, Color.RED);

        verify(mockOut).println("PIXEL|2|3|ff0000");
    }

    @Test
    void testHandleAuthSuccessActualizaEstado() throws Exception {
        
        String[] parts = {"AUTH_SUCCESS", "User"};

        var method = NetworkController.class
                .getDeclaredMethod("handleAuthSuccess", String[].class);
        method.setAccessible(true);
        method.invoke(networkController, (Object) parts);

        verify(listenerMock).onAuthResult(true, "Autenticación exitosa");
        assertEquals("User", networkController.getUsername());
    }

    @Test
    void testHandleErrorNotificaListener() throws Exception {
        String[] parts = {"ERROR", "Algo salió mal"};

        var method = NetworkController.class
                .getDeclaredMethod("handleError", String[].class);
        method.setAccessible(true);
        method.invoke(networkController, (Object) parts);

        verify(listenerMock).onError("Algo salió mal");
    }
}

