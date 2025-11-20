package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaveCommandTest {

    private SaveCommand saveCommand;
    private ServerApp mockServer;

    @BeforeEach
    void setUp() {
        // 1. Mock del servidor
        mockServer = mock(ServerApp.class);
        
        // 2. Instancia del comando
        saveCommand = new SaveCommand(mockServer);
    }

    @Test
    void testMetadatos() {
        assertEquals("save", saveCommand.name());
        assertNotNull(saveCommand.help());
        assertTrue(saveCommand.help().contains("Guarda"));
    }

    @Test
    void testExecute() {
        // 1. Ejecutar
        saveCommand.execute();

        // 2. Verificar que delegó la acción al servidor
        verify(mockServer, times(1)).saveCanvas();
    }
}