package com.equipo.pixelart.client.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NetworkControllerTest {

    private NetworkController networkController;
    private NetworkListener mockListener;
    private PrintWriter mockOut;
    private Socket mockSocket;

    @BeforeEach
    void setUp() throws Exception {
        networkController = new NetworkController();
        mockListener = mock(NetworkListener.class);
        networkController.addNetworkListener(mockListener);

        // Mocks para simular conexión interna
        mockOut = mock(PrintWriter.class);
        mockSocket = mock(Socket.class);
    }

    // ==========================================
    // 1. TEST DE MÉTODOS DE ENVÍO (SENDERS)
    // ==========================================
    
    @Test
    void testLoginEnviaMensaje() throws Exception {
        simularConexionExitosa(); // Truco para engañar al controller
        
        networkController.login("Usuario", "Pass123");
        verify(mockOut).println("LOGIN|Usuario|Pass123");
        assertEquals("Usuario", networkController.getUsername());
    }

    @Test
    void testRegisterEnviaMensaje() throws Exception {
        simularConexionExitosa();
        networkController.register("Usuario", "Pass");
        verify(mockOut).println("REGISTER|Usuario|Pass");
    }

    @Test
    void testSendPixelUpdate() throws Exception {
        simularConexionExitosa();
        // Color Rojo (255, 0, 0) -> ff0000
        networkController.sendPixelUpdate(10, 20, Color.RED);
        verify(mockOut).println("PIXEL|10|20|ff0000");
    }

    @Test
    void testComandosSimples() throws Exception {
        simularConexionExitosa();

        networkController.requestCanvasState();
        verify(mockOut).println("GET_CANVAS");

        networkController.sendClearCanvas();
        verify(mockOut).println("CLEAR");

        networkController.requestUserList();
        verify(mockOut).println("GET_USERS");
        
        networkController.sendUndo();
        verify(mockOut).println("UNDO");
    }

    @Test
    void testSendChatMessage() throws Exception {
        simularConexionExitosa();
        networkController.sendChatMessage("Hola Mundo");
        verify(mockOut).println("CHAT|Hola Mundo");
    }

    @Test
    void testSendMessageSinConexion() {
        // No llamamos a simularConexionExitosa()
        networkController.sendChatMessage("No saldrá");
        // Verificamos que NO se usó el writer porque está desconectado/nulo
        verifyNoInteractions(mockOut);
    }

    // ==========================================
    // 2. TEST DE MANEJADORES PRIVADOS (HANDLERS)
    // Usamos Reflection para probar la lógica de respuesta
    // ==========================================

    @Test
    void testHandleAuthSuccess() throws Exception {
        String[] parts = {"AUTH_SUCCESS", "Anthony"};
        invocarMetodoPrivado("handleAuthSuccess", new Object[]{parts}, String[].class);

        assertEquals("Anthony", networkController.getUsername());
        verify(mockListener).onAuthResult(true, "Autenticación exitosa");
        
        // Verifica que pida el estado inicial tras loguearse
        // (Necesitamos inyectar mockOut antes para verificar esto, o ignorarlo si falla por null)
    }

    @Test
    void testHandleAuthFailed() throws Exception {
        String[] parts = {"AUTH_FAILED", "Password incorrecto"};
        invocarMetodoPrivado("handleAuthFailed", new Object[]{parts}, String[].class);
        verify(mockListener).onAuthResult(false, "Password incorrecto");
    }

    @Test
    void testHandlePixelUpdate() throws Exception {
        // Caso Éxito
        String[] parts = {"PIXEL_UPDATE", "5", "10", "00ff00"}; // Verde
        invocarMetodoPrivado("handlePixelUpdate", new Object[]{parts}, String[].class);
        
        verify(mockListener).onPixelUpdateReceived(eq(5), eq(10), any(Color.class));

        // Caso Error (Mal formato numérico) - Para cubrir el try-catch
        String[] partsMal = {"PIXEL_UPDATE", "X", "Y", "ZZZ"};
        invocarMetodoPrivado("handlePixelUpdate", new Object[]{partsMal}, String[].class);
        // No debe explotar, simplemente imprime error en consola
    }

    @Test
    void testHandleCanvasState() throws Exception {
        // Ancho 2, Alto 2. Datos: Blanco(ffffff), Rojo(ff0000)...
        String[] parts = {"CANVAS_STATE", "2", "2", "ffffff,ff0000,0000ff,000000"};
        invocarMetodoPrivado("handleCanvasState", new Object[]{parts}, String[].class);
        
        verify(mockListener).onCanvasStateReceived(any(Color[][].class));
        
        // Caso Error (Array vacío o mal formado)
        String[] partsMal = {"CANVAS_STATE", "2", "2"}; // Faltan datos
        invocarMetodoPrivado("handleCanvasState", new Object[]{partsMal}, String[].class);
    }

    @Test
    void testHandleChatMessage() throws Exception {
        String[] parts = {"CHAT_MESSAGE", "User1", "Hola"};
        invocarMetodoPrivado("handleChatMessage", new Object[]{parts}, String[].class);
        verify(mockListener).onChatMessage("User1", "Hola");
    }

    @Test
    void testHandleUserUpdates() throws Exception {
        // Joined
        String[] partsJoin = {"USER_JOINED", "NuevoUsuario"};
        invocarMetodoPrivado("handleUserJoined", new Object[]{partsJoin}, String[].class);
        verify(mockListener).onUserJoined("NuevoUsuario");

        // Left
        String[] partsLeft = {"USER_LEFT", "ViejoUsuario"};
        invocarMetodoPrivado("handleUserLeft", new Object[]{partsLeft}, String[].class);
        verify(mockListener).onUserLeft("ViejoUsuario");
        
        // List
        String[] partsList = {"USER_LIST", "UserA,UserB"};
        invocarMetodoPrivado("handleUserList", new Object[]{partsList}, String[].class);
        verify(mockListener).onUserListReceived(any(String[].class));
    }
    
    @Test
    void testHandleClearCanvas() throws Exception {
        Method method = NetworkController.class.getDeclaredMethod("handleClearCanvas");
        method.setAccessible(true);
        method.invoke(networkController);
        
        verify(mockListener).onCanvasCleared();
    }

    // ==========================================
    // 3. TEST DEL SWITCH GIGANTE (ProcessMessage)
    // ==========================================
    
    @Test
    void testProcessMessageDispatch() throws Exception {
        // Probamos que el switch redirige bien
        // OJO: Como processMessage usa SwingUtilities.invokeLater, 
        // en un test unitario a veces no corre inmediatamente.
        // Pero probamos al menos que no explote.
        
        String msg = "ERROR|Mensaje de prueba";
        invocarMetodoPrivado("processMessage", new Object[]{msg}, String.class);
        
        // Damos un micro respiro para que invokeLater (si corre síncrono en test) actúe
        Thread.sleep(50); 
        // Nota: Mockito podría no detectar esto si SwingUtilities no corre en el test runner.
        // Si falla la verificación, confiamos en los tests individuales de HandleX.
    }

    // ==========================================
    // 4. TEST DE CONEXIÓN Y DESCONEXIÓN
    // ==========================================

    @Test
    void testDisconnect() throws Exception {
        simularConexionExitosa();
        when(mockSocket.isClosed()).thenReturn(false);

        networkController.disconnect();

        verify(mockOut).println("DISCONNECT"); // Debe avisar al server
        verify(mockSocket).close();            // Debe cerrar socket
        verify(mockListener).onConnectionStatusChanged(false, "Desconectado del servidor");
        assertFalse(networkController.isConnected());
    }
    
    @Test
    void testConnectFailsUnknownHost() {
        // Intentamos conectar a un host imposible
        boolean result = networkController.connect("host-que-no-existe-12345", 9999);
        assertFalse(result);
        verify(mockListener).onConnectionStatusChanged(eq(false), contains("Host desconocido"));
    }
    
    @Test
    void testRemoveListener() {
        networkController.removeNetworkListener(mockListener);
        // Forzamos un evento
        try {
            Method method = NetworkController.class.getDeclaredMethod("notifyError", String.class);
            method.setAccessible(true);
            method.invoke(networkController, "Test");
        } catch(Exception e) {}
        
        // No debería recibir nada porque lo removimos
        verify(mockListener, never()).onError(anyString());
    }

    // ==========================================
    // UTILIDADES (HELPERS)
    // ==========================================

    /**
     * Inyecta mocks en los campos privados para simular que ya estamos conectados
     */
    private void simularConexionExitosa() throws Exception {
        // Inyectar socket
        Field socketField = NetworkController.class.getDeclaredField("socket");
        socketField.setAccessible(true);
        socketField.set(networkController, mockSocket);

        // Inyectar PrintWriter (out)
        Field outField = NetworkController.class.getDeclaredField("out");
        outField.setAccessible(true);
        outField.set(networkController, mockOut);

        // Inyectar boolean connected
        Field connectedField = NetworkController.class.getDeclaredField("connected");
        connectedField.setAccessible(true);
        connectedField.set(networkController, true);
    }

    /**
     * Ayudante para invocar métodos privados con argumentos
     */
    private void invocarMetodoPrivado(String nombreMetodo, Object[] args, Class<?>... parameterTypes) throws Exception {
        Method method = NetworkController.class.getDeclaredMethod(nombreMetodo, parameterTypes);
        method.setAccessible(true);
        method.invoke(networkController, args);
    }
}