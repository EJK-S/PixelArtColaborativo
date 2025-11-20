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
       
        mockServer = mock(ServerApp.class);

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
       
        statsCommand.execute();

        verify(mockServer, times(1)).imprimirEstadisticas();
    }
}