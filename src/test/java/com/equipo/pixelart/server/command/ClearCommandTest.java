package com.equipo.pixelart.server.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.equipo.pixelart.server.app.ServerApp;

class ClearCommandTest {

    private ClearCommand clearCommand;
    private ServerApp mockServer;

    @BeforeEach
    public void setUp() {
        
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