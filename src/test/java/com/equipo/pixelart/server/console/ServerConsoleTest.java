package com.equipo.pixelart.server.console;

import com.equipo.pixelart.server.app.ServerApp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerConsoleTest {

    private ServerConsole serverConsole;
    private ServerApp mockServer;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
    
        mockServer = mock(ServerApp.class);

        System.setOut(new PrintStream(outContent));
        serverConsole = new ServerConsole(mockServer);
    }

    @AfterEach
    void tearDown() {
       
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testConstructorRegistraComandos() throws Exception {
        
        Field field = ServerConsole.class.getDeclaredField("commands");
        field.setAccessible(true);
        Map<?, ?> commandsMap = (Map<?, ?>) field.get(serverConsole);
        assertNotNull(commandsMap);
        assertEquals(7, commandsMap.size(), "Debe registrar 7 comandos al iniciar");
        assertTrue(commandsMap.containsKey("help"));
        assertTrue(commandsMap.containsKey("exit"));
    }

    @Test
    void testInicioEjecutaComandoConocido() throws InterruptedException {
       
        String input = "help\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        serverConsole.inicio();
        Thread.sleep(100);

        String output = outContent.toString();
        assertTrue(output.contains("server>"), "Debe mostrar el prompt del servidor");
    }

    @Test
    void testInicioComandoDesconocido() throws InterruptedException {
        
        String input = "comando_raro\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        serverConsole.inicio();
        Thread.sleep(100);

        String output = outContent.toString();
        assertTrue(output.contains("Comando no encontrado"), "Debe avisar si el comando no existe");
    }

    @Test
    void testInicioManejoDeExcepcion() throws InterruptedException {

        String input = "\n"; 
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        assertDoesNotThrow(() -> serverConsole.inicio());
        
        Thread.sleep(50);
        
    }
    
    @Test
    void testFlujoCompleto() throws InterruptedException {
       
        String input = "blabla\nhelp\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        serverConsole.inicio();
        Thread.sleep(150);
        
        String output = outContent.toString();
        assertTrue(output.contains("Comando no encontrado")); 
        assertTrue(output.contains("server>")); 
    }
}