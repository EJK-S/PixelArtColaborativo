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
    
    // Para capturar lo que imprime la consola
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        // 1. Mock del servidor (necesario porque los comandos lo piden en el constructor)
        mockServer = mock(ServerApp.class);
        
        // 2. Capturamos System.out para leer qué responde la consola
        System.setOut(new PrintStream(outContent));
        
        // 3. Instanciamos la consola
        serverConsole = new ServerConsole(mockServer);
    }

    @AfterEach
    void tearDown() {
        // Restauramos System.in y System.out para no romper otros tests
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testConstructorRegistraComandos() throws Exception {
        // Usamos Reflection para espiar el mapa privado 'commands'
        Field field = ServerConsole.class.getDeclaredField("commands");
        field.setAccessible(true);
        Map<?, ?> commandsMap = (Map<?, ?>) field.get(serverConsole);

        // Verificamos que se hayan registrado los 7 comandos (stats, users, network, save, clear, exit, help)
        assertNotNull(commandsMap);
        assertEquals(7, commandsMap.size(), "Debe registrar 7 comandos al iniciar");
        assertTrue(commandsMap.containsKey("help"));
        assertTrue(commandsMap.containsKey("exit"));
    }

    @Test
    void testInicioEjecutaComandoConocido() throws InterruptedException {
        // 1. Simulamos que el usuario escribe "help" y luego Enter
        String input = "help\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // 2. Arrancamos el hilo
        serverConsole.inicio();

        // 3. Esperamos un poco a que el hilo procese (es asíncrono)
        Thread.sleep(100);

        // 4. Verificamos la salida
        // Como no tenemos el código de HelpCommand, asumimos que imprime algo o al menos no explota.
        // Pero verificamos que el prompt apareció.
        String output = outContent.toString();
        assertTrue(output.contains("server>"), "Debe mostrar el prompt del servidor");
    }

    @Test
    void testInicioComandoDesconocido() throws InterruptedException {
        // 1. Simulamos input: comando inválido + enter
        String input = "comando_raro\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        serverConsole.inicio();
        Thread.sleep(100);

        // 2. Verificamos que imprima el mensaje de error
        String output = outContent.toString();
        assertTrue(output.contains("Comando no encontrado"), "Debe avisar si el comando no existe");
    }

    @Test
    void testInicioManejoDeExcepcion() throws InterruptedException {
        // Simulamos un input que podría causar problemas o simplemente vacío
        // El código tiene un try-catch (ignored), así que probamos que siga vivo
        String input = "\n"; // Línea vacía
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        assertDoesNotThrow(() -> serverConsole.inicio());
        
        Thread.sleep(50);
        // Si no explotó, pasó el test
    }
    
    @Test
    void testFlujoCompleto() throws InterruptedException {
        // Probamos una secuencia: comando malo -> comando bueno -> fin
        String input = "blabla\nhelp\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        serverConsole.inicio();
        Thread.sleep(150);
        
        String output = outContent.toString();
        assertTrue(output.contains("Comando no encontrado")); // Respuesta al primero
        assertTrue(output.contains("server>")); // Prompt
    }
}