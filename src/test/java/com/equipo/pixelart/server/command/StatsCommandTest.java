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

class StatsCommandTest {

    private StatsCommand statsCommand;
    private ServerApp mockServer;

    @BeforeEach
    public void setUp() {
       
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