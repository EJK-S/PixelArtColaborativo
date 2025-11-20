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

        mockPanel = mock(UserListPanel.class);
        mockNetwork = mock(NetworkController.class);

        userListController = new UserListController(mockPanel, mockNetwork);
    }

    @Test
    void testInit() {

        userListController.init();

        verify(mockNetwork).addNetworkListener(userListController);
    }

    @Test
    void testOnUserJoined() {
        String usuario = "Anthony";
        
        userListController.onUserJoined(usuario);

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

        userListController.onConnectionStatusChanged(false, "Desconectado");

        verify(mockPanel).clearUsers();
    }

    @Test
    void testOnConnectionStatusChanged_Conectado() {

        userListController.onConnectionStatusChanged(true, "Conectado");

        verify(mockPanel, never()).clearUsers();
    }
}