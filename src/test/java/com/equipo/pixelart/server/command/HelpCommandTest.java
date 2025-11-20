package com.equipo.pixelart.server.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelpCommandTest {

    private HelpCommand helpCommand;
    private Map<String, ServerCommand> commandMap;
    
    // Variables para capturar la consola
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // 1. Redirigir System.out a nuestra variable para leer lo que se imprime
        System.setOut(new PrintStream(outContent));

        // 2. Preparamos datos de prueba
        commandMap = new LinkedHashMap<>(); // Usamos LinkedHashMap para orden predecible
        helpCommand = new HelpCommand(commandMap);
    }

    @AfterEach
    void tearDown() {
        // 3. Restaurar la consola original (Muy importante)
        System.setOut(originalOut);
    }

    @Test
    void testMetadatos() {
        assertEquals("help", helpCommand.name());
        assertEquals("Muestra este mensaje", helpCommand.help());
    }

    @Test
    void testExecuteConComandos() {
        // 1. Crear un comando falso para ver si aparece en la lista
        ServerCommand mockCmd = mock(ServerCommand.class);
        when(mockCmd.name()).thenReturn("test_cmd");
        when(mockCmd.help()).thenReturn("ayuda de prueba");
        
        // 2. Agregarlo al mapa
        commandMap.put("test", mockCmd);
        
        // 3. Ejecutar help
        helpCommand.execute();

        // 4. Verificar la salida
        String salida = outContent.toString();
        
        // Debe tener el título
        assertTrue(salida.contains("=== COMANDOS ==="));
        // Debe tener el nombre del comando mock
        assertTrue(salida.contains("test_cmd"));
        // Debe tener la ayuda del comando mock
        assertTrue(salida.contains("ayuda de prueba"));
        // Debe tener el formato " - nombre : ayuda"
        assertTrue(salida.contains(" - test_cmd : ayuda de prueba"));
    }

    @Test
    void testExecuteSinComandos() {
        // Mapa vacío
        helpCommand.execute();

        String salida = outContent.toString();
        
        // Debe imprimir el título aunque no haya comandos
        assertTrue(salida.contains("=== COMANDOS ==="));
        // No debe contener guiones de lista
        assertFalse(salida.contains(" - "));
    }
}