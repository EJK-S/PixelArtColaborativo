package com.equipo.pixelart.server.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.equipo.pixelart.server.app.ServerApp;

class UsersCommandTest {

    private UsersCommand usersCommand;
    private ServerApp mockServer;

    @BeforeEach
    public void setUp() {
        
        mockServer = mock(ServerApp.class);

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
        
        usersCommand.execute();
        verify(mockServer, times(1)).imprimirUsuarios();
    }
}