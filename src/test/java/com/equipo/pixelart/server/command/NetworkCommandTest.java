package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NetworkCommandTest {

    private NetworkCommand networkCommand;
    private ServerApp mockServer;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        
        System.setOut(new PrintStream(outContent));

        mockServer = mock(ServerApp.class);
        networkCommand = new NetworkCommand(mockServer);
    }

    @AfterEach
    void tearDown() {
        
        System.setOut(originalOut);
    }

    @Test
    void testMetadatos() {
        assertEquals("network", networkCommand.name());
        assertTrue(networkCommand.help().contains("IPs"));
    }

    @Test
    void testExecuteConIPs() {
       
        List<String> ipsFalsas = Arrays.asList("192.168.1.50", "127.0.0.1");
        when(mockServer.getLocalIPs()).thenReturn(ipsFalsas);

        networkCommand.execute();

        String salida = outContent.toString();
        assertTrue(salida.contains("192.168.1.50"));
        assertTrue(salida.contains("127.0.0.1"));

        verify(mockServer).getLocalIPs();
    }

    @Test
    void testExecuteSinIPs() {
        
        when(mockServer.getLocalIPs()).thenReturn(Collections.emptyList());

        networkCommand.execute();

        String salida = outContent.toString();
        
        assertEquals("", salida.trim());
    }
}