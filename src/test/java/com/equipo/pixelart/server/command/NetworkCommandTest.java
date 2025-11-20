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

    // Para capturar la consola
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // 1. Redirigir consola
        System.setOut(new PrintStream(outContent));

        // 2. Mock del servidor
        mockServer = mock(ServerApp.class);
        networkCommand = new NetworkCommand(mockServer);
    }

    @AfterEach
    void tearDown() {
        // Restaurar consola original
        System.setOut(originalOut);
    }

    @Test
    void testMetadatos() {
        assertEquals("network", networkCommand.name());
        assertTrue(networkCommand.help().contains("IPs"));
    }

    @Test
    void testExecuteConIPs() {
        // 1. Entrenar al mock para que devuelva una lista de IPs falsa
        List<String> ipsFalsas = Arrays.asList("192.168.1.50", "127.0.0.1");
        when(mockServer.getLocalIPs()).thenReturn(ipsFalsas);

        // 2. Ejecutar
        networkCommand.execute();

        // 3. Verificar salida
        String salida = outContent.toString();
        assertTrue(salida.contains("192.168.1.50"));
        assertTrue(salida.contains("127.0.0.1"));
        
        // Verificar que llamó al método del servidor
        verify(mockServer).getLocalIPs();
    }

    @Test
    void testExecuteSinIPs() {
        // Caso lista vacía
        when(mockServer.getLocalIPs()).thenReturn(Collections.emptyList());

        networkCommand.execute();

        String salida = outContent.toString();
        // No debe haber impreso nada relevante (o estar vacío)
        assertEquals("", salida.trim());
    }
}