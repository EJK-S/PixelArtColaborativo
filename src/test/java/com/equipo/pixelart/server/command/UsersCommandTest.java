package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersCommandTest {

    private UsersCommand usersCommand;
    private ServerApp mockServer;

    @BeforeEach
    void setUp() {
        // 1. Mock del servidor
        mockServer = mock(ServerApp.class);
        
        // 2. Instancia del comando
        usersCommand = new UsersCommand(mockServer);
    }

    @Test
    void testMetadatos() {
        assertEquals("users", usersCommand.name());
        assertNotNull(usersCommand.help());
        assertTrue(usersCommand.help().contains("conectados"));
    }

    @Test
    void testExecute() {
        // 1. Ejecutar
        usersCommand.execute();

        // 2. Verificar que delegó la acción al servidor
        verify(mockServer, times(1)).imprimirUsuarios();
    }
}