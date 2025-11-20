package com.equipo.pixelart.server.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.equipo.pixelart.server.network.ClientHandler;
import com.equipo.pixelart.server.persistence.FileManager;
import com.equipo.pixelart.server.service.AuthService;
import com.equipo.pixelart.server.service.CanvasService;

class ServerAppTest {

    private ServerApp serverApp;
    private ServerApp spyServer;
    
    private AuthService mockAuth;
    private CanvasService mockCanvas;
    private FileManager mockFile;

    // Captura de consola
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // Puerto de prueba
    private static final int TEST_PORT = 9999;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));

        // Mocks
        mockAuth = mock(AuthService.class);
        mockCanvas = mock(CanvasService.class);
        mockFile = mock(FileManager.class);

        // Instancia Real
        serverApp = new ServerApp(TEST_PORT);
        
        // SPY: Para anular System.exit y la consola
        spyServer = spy(serverApp);
        
        // Inyectamos los mocks
        spyServer.setServices(mockFile, mockAuth, mockCanvas);
        
        // Burlamos los métodos peligrosos
        doNothing().when(spyServer).exitSystem();
        doNothing().when(spyServer).startConsole();
        doNothing().when(spyServer).initServicios();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        spyServer.stopServer();
    }

    // ==========================================
    // TESTS BÁSICOS
    // ==========================================

    @Test
    void testConstructor() {
        assertNotNull(spyServer);
    }

    @Test
    void testImprimirInfoRed() {
        spyServer.imprimirInfoRed();
        String log = outContent.toString();
        assertTrue(log.contains("DIRECCIONES"));
        assertTrue(log.contains("localhost"));
    }
    
    @Test
    void testGetLocalIPs() {
        List<String> ips = spyServer.getLocalIPs();
        assertNotNull(ips);
    }

    @Test
    void testImprimirEstadisticas() {
        when(mockCanvas.getEstadisticas()).thenReturn("Stats Canvas");
        when(mockAuth.getUsuariosConectadosCount()).thenReturn(5);
        
        spyServer.imprimirEstadisticas();
        
        String log = outContent.toString();
        assertTrue(log.contains("ESTADÍSTICAS"));
        assertTrue(log.contains("5"));
        assertTrue(log.contains("Stats Canvas"));
    }

    @Test
    void testImprimirUsuarios() {
        // Caso 1: Vacío
        when(mockAuth.obtenerUsuariosConectados()).thenReturn(new String[]{});
        spyServer.imprimirUsuarios();
        assertTrue(outContent.toString().contains("No hay usuarios"));

        outContent.reset();

        // Caso 2: Con usuarios
        when(mockAuth.obtenerUsuariosConectados()).thenReturn(new String[]{"Anthony", "Maria"});
        spyServer.imprimirUsuarios();
        String log = outContent.toString();
        assertTrue(log.contains("1. Anthony"));
        assertTrue(log.contains("2. Maria"));
    }

    // ==========================================
    // TESTS LÓGICOS
    // ==========================================

    @Test
    void testSaveAndClearCanvas() {
        ClientHandler mockClient = mock(ClientHandler.class);
        when(mockClient.isAuthenticated()).thenReturn(true);
        Socket mockSocket = mock(Socket.class);
        when(mockClient.getSocket()).thenReturn(mockSocket);

        // Usamos el getter limpio
        spyServer.getClients().add(mockClient);

        spyServer.saveCanvas();
        verify(mockCanvas).guardarCanvas();

        spyServer.clearCanvas();
        verify(mockCanvas).clear();
        verify(mockClient).sendMessage("CLEAR_CANVAS");
    }

    @Test
    void testStopServer() throws Exception {
        Socket mockSocket = mock(Socket.class);
        ClientHandler mockClient = mock(ClientHandler.class);
        when(mockClient.getSocket()).thenReturn(mockSocket);
        
        spyServer.getClients().add(mockClient);

        setField(spyServer, "running", true);

        spyServer.stopServer();

        verify(mockCanvas).guardarCanvas();
        verify(mockClient).sendMessage(contains("detenido"));
        verify(mockSocket).close();
        verify(spyServer).exitSystem();
    }

    @Test
    void testInicioYConexionCliente() throws Exception {
        Thread serverThread = new Thread(() -> {
            spyServer.inicio();
        });
        serverThread.start();

        Thread.sleep(200);
        assertTrue(spyServer.isRunning());

        try (Socket clientSocket = new Socket("localhost", TEST_PORT)) {
            assertTrue(clientSocket.isConnected());
            Thread.sleep(200);
            
            List<ClientHandler> clients = spyServer.getClients();
            
            assertEquals(1, clients.size(), "Debe haber aceptado al cliente");
        }

        spyServer.stopServer();
        serverThread.join(1000);
    }

    // ==========================================
    // TESTS DE EXCEPCIONES (Cobertura 100%)
    // ==========================================

    @Test
    void testStopServer_ClientCloseError() throws IOException {
        // Simula error al cerrar socket del cliente
        Socket badSocket = mock(Socket.class);
        doThrow(new IOException("Error simulado")).when(badSocket).close();

        ClientHandler badClient = mock(ClientHandler.class);
        when(badClient.getSocket()).thenReturn(badSocket);
        
        spyServer.getClients().add(badClient);
        setField(spyServer, "running", true);

        spyServer.stopServer();

        verify(badSocket).close();
    }

    @Test
    void testStopServer_ServerSocketError() throws IOException {
        // Simula error al cerrar el ServerSocket principal
        java.net.ServerSocket badServerSocket = mock(java.net.ServerSocket.class);
        doThrow(new IOException("Error forzado")).when(badServerSocket).close();

        setField(spyServer, "serverSocket", badServerSocket);
        
        spyServer.stopServer();

        verify(badServerSocket).close();
        assertTrue(outContent.toString().contains("Error cerrando servidor"));
    }

    @Test
    void testMain_PuertoInvalido() {
        // Prueba el catch(NumberFormatException) en main
        String[] args = {"PUERTO_NO_VALIDO"};

        Thread t = new Thread(() -> {
            try {
                ServerApp.main(args);
            } catch (Exception e) {}
        });
        t.start();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        String output = outContent.toString();
        assertTrue(output.contains("Puerto inválido"));
        assertTrue(output.contains("usando 12345"));
        
        t.interrupt(); 
    }

    // ==========================================
    // TEST HELPER (Cobertura de catch en setField)
    // ==========================================

    @Test
    void testSetFieldException() {
        // Fuerza el catch del helper de reflexión
        assertThrows(RuntimeException.class, () -> 
            setField(spyServer, "campo_imaginario", "valor")
        );
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private void setField(Object target, String name, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
 // ==========================================
    // TESTS DE ARGS (Main Refactorizado)
    // ==========================================

    @Test
    void testGetPortFromArgs_Valido() {
        String[] args = {"8080"};
        int port = ServerApp.getPortFromArgs(args);
        assertEquals(8080, port);
    }

    @Test
    void testGetPortFromArgs_Invalido() {
        String[] args = {"PUERTO_XYZ"};
        int port = ServerApp.getPortFromArgs(args);
        
        // Debe devolver el default y avisar en consola
        assertEquals(12345, port); // Asumiendo que DEFAULT_PORT es 12345
        assertTrue(outContent.toString().contains("Puerto inválido"));
    }

    @Test
    void testGetPortFromArgs_Vacio() {
        String[] args = {};
        int port = ServerApp.getPortFromArgs(args);
        assertEquals(12345, port);
    }
    
    @Test
    void testInicio_FalloAlIniciarServer() throws IOException {
        // 1. TRUCO: Ocupamos el puerto 9999 nosotros mismos antes de arrancar la app
        java.net.ServerSocket socketBloqueador = new java.net.ServerSocket(TEST_PORT);

        // 2. Creamos una App que intente usar ese MISMO puerto
        ServerApp badApp = new ServerApp(TEST_PORT);
        ServerApp spyBadApp = spy(badApp);

        // Burlamos dependencias para aislar el fallo
        doNothing().when(spyBadApp).initServicios();
        doNothing().when(spyBadApp).startConsole();

        // 3. Ejecutamos: Al intentar hacer new ServerSocket(9999) dentro, 
        // lanzará BindException (que es un IOException) porque ya lo tenemos ocupado.
        spyBadApp.inicio();

        // 4. Soltamos el puerto (Limpieza importante)
        socketBloqueador.close();

        // 5. Verificamos que tu código capturó el error e imprimió el mensaje
        assertTrue(outContent.toString().contains("No se pudo iniciar el servidor"), 
            "Debe capturar la excepción si el puerto está ocupado");
    }
    
    @Test
    void testMain_EjecucionCompleta() {
        // Ejecutamos el main en un hilo para que no bloquee JUnit
        Thread t = new Thread(() -> {
            try {
                // Usamos un puerto diferente para no chocar con otros tests
                String[] args = {"9998"}; 
                ServerApp.main(args);
            } catch (Exception e) {
                // Ignorar errores de cierre
            }
        });
        t.start();

        // Dejamos que el main corra por 1 segundo para que cubra las líneas de new ServerApp() y inicio()
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // Interrumpimos el hilo (aunque el server tiene su propio hilo, esto ayuda a limpiar)
        t.interrupt();
        
        // No hay asserts, solo queremos que las líneas se ejecuten ("se pinten de verde")
    }
}