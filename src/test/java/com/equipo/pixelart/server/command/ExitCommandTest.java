package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExitCommandTest {

    private ExitCommand exitCommand;
    private ServerApp mockServer;

    @BeforeEach
    void setUp() {
        mockServer = mock(ServerApp.class);
        exitCommand = new ExitCommand(mockServer);
    }

    @Test
    void testMetadatos() {
        assertEquals("exit", exitCommand.name());
        assertNotNull(exitCommand.help());
        assertTrue(exitCommand.help().contains("Detiene"));
    }

    @Test
    void testExecute() {
        // Ejecutar el comando
        exitCommand.execute();

        // Verificar que se llamó al método de detener el servidor
        verify(mockServer, times(1)).stopServer();
    }
}