package com.equipo.pixelart.server.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelpCommandTest {

    private HelpCommand helpCommand;
    private Map<String, ServerCommand> commandMap;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        
        System.setOut(new PrintStream(outContent));

        commandMap = new LinkedHashMap<>(); 
        helpCommand = new HelpCommand(commandMap);
    }

    @AfterEach
    public void tearDown() {
        
        System.setOut(originalOut);
    }

    @Test
    void testMetadatos() {
        assertEquals("help", helpCommand.name());
        assertEquals("Muestra este mensaje", helpCommand.help());
    }

    @Test
    void testExecuteConComandos() {
        
        ServerCommand mockCmd = mock(ServerCommand.class);
        when(mockCmd.name()).thenReturn("test_cmd");
        when(mockCmd.help()).thenReturn("ayuda de prueba");

        commandMap.put("test", mockCmd);

        helpCommand.execute();

        String salida = outContent.toString();
  
        assertTrue(salida.contains("=== COMANDOS ==="));
       
        assertTrue(salida.contains("test_cmd"));
        
        assertTrue(salida.contains("ayuda de prueba"));
       
        assertTrue(salida.contains(" - test_cmd : ayuda de prueba"));
    }

    @Test
    void testExecuteSinComandos() {
        
        helpCommand.execute();

        String salida = outContent.toString();

        assertTrue(salida.contains("=== COMANDOS ==="));
        
        assertFalse(salida.contains(" - "));
    }
}