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

class SaveCommandTest {

    private SaveCommand saveCommand;
    private ServerApp mockServer;

    @BeforeEach
    public void setUp() {
        
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