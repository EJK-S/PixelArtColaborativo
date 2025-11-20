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
        
        mockServer = mock(ServerApp.class);

        clearCommand = new ClearCommand(mockServer);
    }

    @Test
    void testMetadatos() {
        
        assertEquals("clear", clearCommand.name());
        assertNotNull(clearCommand.help());
        assertFalse(clearCommand.help().isEmpty());
    }

    @Test
    void testExecute() {
        
        clearCommand.execute();
        verify(mockServer, times(1)).clearCanvas();
    }
}