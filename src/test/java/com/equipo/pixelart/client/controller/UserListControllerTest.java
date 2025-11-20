package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.view.UserListPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UserListControllerTest {

    private UserListController userListController;
    private UserListPanel mockPanel;
    private NetworkController mockNetwork;

    @BeforeEach
    void setUp() {
        // 1. Mocks de dependencias
        mockPanel = mock(UserListPanel.class);
        mockNetwork = mock(NetworkController.class);

        // 2. Instanciar la clase a probar
        userListController = new UserListController(mockPanel, mockNetwork);
    }

    @Test
    void testInit() {
        // Ejecutar
        userListController.init();

        // Verificar: ¿Se agregó el controller como listener de la red?
        verify(mockNetwork).addNetworkListener(userListController);
    }

    @Test
    void testOnUserJoined() {
        String usuario = "Anthony";
        
        userListController.onUserJoined(usuario);

        // Verificar que el panel visual agregue al usuario
        verify(mockPanel).addUser(usuario);
    }

    @Test
    void testOnUserLeft() {
        String usuario = "Anthony";

        userListController.onUserLeft(usuario);

        verify(mockPanel).removeUser(usuario);
    }

    @Test
    void testOnUserListReceived() {
        String[] listaUsuarios = {"Anthony", "Juan", "Maria"};

        userListController.onUserListReceived(listaUsuarios);

        verify(mockPanel).setUsers(listaUsuarios);
    }

    @Test
    void testOnConnectionStatusChanged_Desconectado() {
        // Caso: Se perdió la conexión (false)
        userListController.onConnectionStatusChanged(false, "Desconectado");

        // Verificar: Debe limpiar la lista visual
        verify(mockPanel).clearUsers();
    }

    @Test
    void testOnConnectionStatusChanged_Conectado() {
        // Caso: Se conectó (true)
        // El código tiene un 'if (!connected)', así que si es true, NO entra al if.
        userListController.onConnectionStatusChanged(true, "Conectado");

        // Verificar: NO debe limpiar la lista
        verify(mockPanel, never()).clearUsers();
    }
}