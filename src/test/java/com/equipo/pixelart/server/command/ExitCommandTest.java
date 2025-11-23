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

class ExitCommandTest {

    private ExitCommand exitCommand;
    private ServerApp mockServer;

    @BeforeEach
    public void setUp() {
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
       
        exitCommand.execute();
        verify(mockServer, times(1)).stopServer();
    }
}