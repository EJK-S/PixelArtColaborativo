package com.equipo.pixelart.server.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static final int TEST_PORT = 9999;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));

        mockAuth = mock(AuthService.class);
        mockCanvas = mock(CanvasService.class);
        mockFile = mock(FileManager.class);

        serverApp = new ServerApp(TEST_PORT);

        spyServer = spy(serverApp);

        spyServer.setServices(mockFile, mockAuth, mockCanvas);
 
        doNothing().when(spyServer).exitSystem();
        doNothing().when(spyServer).startConsole();
        doNothing().when(spyServer).initServicios();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        spyServer.stopServer();
    }

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

        when(mockAuth.obtenerUsuariosConectados()).thenReturn(new String[]{});
        spyServer.imprimirUsuarios();
        assertTrue(outContent.toString().contains("No hay usuarios"));

        outContent.reset();

        when(mockAuth.obtenerUsuariosConectados()).thenReturn(new String[]{"Anthony", "Maria"});
        spyServer.imprimirUsuarios();
        String log = outContent.toString();
        assertTrue(log.contains("1. Anthony"));
        assertTrue(log.contains("2. Maria"));
    }

    @Test
    void testSaveAndClearCanvas() {
        ClientHandler mockClient = mock(ClientHandler.class);
        when(mockClient.isAuthenticated()).thenReturn(true);
        Socket mockSocket = mock(Socket.class);
        when(mockClient.getSocket()).thenReturn(mockSocket);

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

    @Test
    void testStopServer_ClientCloseError() throws IOException {
       
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
       
        java.net.ServerSocket badServerSocket = mock(java.net.ServerSocket.class);
        doThrow(new IOException("Error forzado")).when(badServerSocket).close();

        setField(spyServer, "serverSocket", badServerSocket);
        
        spyServer.stopServer();

        verify(badServerSocket).close();
        assertTrue(outContent.toString().contains("Error cerrando servidor"));
    }

    @Test
    void testMain_PuertoInvalido() {
       
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


    @Test
        void testSetFieldException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        setField(spyServer, "campo_imaginario", "valor")
        );
        assertNotNull(exception); 
}

    private void setField(Object target, String name, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

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

        assertEquals(12345, port); 
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

        try (java.net.ServerSocket socketBloqueador = new java.net.ServerSocket(TEST_PORT)) {
        
        assertFalse(socketBloqueador.isClosed(), "El socket de prueba debe estar activo para bloquear el puerto");

        ServerApp badApp = new ServerApp(TEST_PORT);
        ServerApp spyBadApp = spy(badApp);

        doNothing().when(spyBadApp).initServicios();
        doNothing().when(spyBadApp).startConsole();

        spyBadApp.inicio();
    } 
    assertTrue(outContent.toString().contains("No se pudo iniciar el servidor"), 
        "Debe capturar la excepción si el puerto está ocupado");
}
    
    @Test
    void testMain_EjecucionCompleta() {
        
        Thread t = new Thread(() -> {
            try {
                
                String[] args = {"9998"}; 
                ServerApp.main(args);
            } catch (Exception e) {
                
            }
        });
        t.start();

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        t.interrupt();

    }
}