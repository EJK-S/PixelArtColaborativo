package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatsCommandTest {

    private StatsCommand statsCommand;
    private ServerApp mockServer;

    @BeforeEach
    void setUp() {
        // 1. Mock del servidor
        mockServer = mock(ServerApp.class);
        
        // 2. Instancia del comando
        statsCommand = new StatsCommand(mockServer);
    }

    @Test
    void testMetadatos() {
        assertEquals("stats", statsCommand.name());
        assertNotNull(statsCommand.help());
        assertTrue(statsCommand.help().contains("estadísticas"));
    }

    @Test
    void testExecute() {
        // 1. Ejecutar
        statsCommand.execute();

        // 2. Verificar que delegó la acción al método correcto del servidor
        verify(mockServer, times(1)).imprimirEstadisticas();
    }
}