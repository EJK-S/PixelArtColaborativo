package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClearCommandTest {

    private ClearCommand clearCommand;
    private ServerApp mockServer;

    @BeforeEach
    void setUp() {
        // 1. Creamos el mock del servidor
        mockServer = mock(ServerApp.class);
        
        // 2. Instanciamos el comando inyectando el mock
        clearCommand = new ClearCommand(mockServer);
    }

    @Test
    void testMetadatos() {
        // Verificamos nombre y ayuda
        assertEquals("clear", clearCommand.name());
        assertNotNull(clearCommand.help());
        assertFalse(clearCommand.help().isEmpty());
    }

    @Test
    void testExecute() {
        // 1. Ejecutamos el comando
        clearCommand.execute();

        // 2. VERIFICACIÓN CLAVE:
        // ¿El comando le dijo al servidor que limpiara el canvas?
        verify(mockServer, times(1)).clearCanvas();
    }
}