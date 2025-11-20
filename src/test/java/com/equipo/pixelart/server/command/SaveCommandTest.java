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
        
        mockServer = mock(ServerApp.class);
  
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
      
        saveCommand.execute();
        verify(mockServer, times(1)).saveCanvas();
    }
}