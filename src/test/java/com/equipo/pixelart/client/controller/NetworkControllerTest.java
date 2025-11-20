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

       
        mockOut = mock(PrintWriter.class);
        mockSocket = mock(Socket.class);
    }

    @Test
    void testLoginEnviaMensaje() throws Exception {
        simularConexionExitosa(); 
        
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
        
        networkController.sendChatMessage("No saldrá");
       
        verifyNoInteractions(mockOut);
    }

   

    @Test
    void testHandleAuthSuccess() throws Exception {
        String[] parts = {"AUTH_SUCCESS", "Anthony"};
        invocarMetodoPrivado("handleAuthSuccess", new Object[]{parts}, String[].class);

        assertEquals("Anthony", networkController.getUsername());
        verify(mockListener).onAuthResult(true, "Autenticación exitosa");
        
    
    }

    @Test
    void testHandleAuthFailed() throws Exception {
        String[] parts = {"AUTH_FAILED", "Password incorrecto"};
        invocarMetodoPrivado("handleAuthFailed", new Object[]{parts}, String[].class);
        verify(mockListener).onAuthResult(false, "Password incorrecto");
    }

    @Test
    void testHandlePixelUpdate() throws Exception {
        
        String[] parts = {"PIXEL_UPDATE", "5", "10", "00ff00"}; 
        invocarMetodoPrivado("handlePixelUpdate", new Object[]{parts}, String[].class);
        
        verify(mockListener).onPixelUpdateReceived(eq(5), eq(10), any(Color.class));

        
        String[] partsMal = {"PIXEL_UPDATE", "X", "Y", "ZZZ"};
        invocarMetodoPrivado("handlePixelUpdate", new Object[]{partsMal}, String[].class);
        
    }

    @Test
    void testHandleCanvasState() throws Exception {
        
        String[] parts = {"CANVAS_STATE", "2", "2", "ffffff,ff0000,0000ff,000000"};
        invocarMetodoPrivado("handleCanvasState", new Object[]{parts}, String[].class);
        
        verify(mockListener).onCanvasStateReceived(any(Color[][].class));
        
        
        String[] partsMal = {"CANVAS_STATE", "2", "2"}; 
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
        
        String[] partsJoin = {"USER_JOINED", "NuevoUsuario"};
        invocarMetodoPrivado("handleUserJoined", new Object[]{partsJoin}, String[].class);
        verify(mockListener).onUserJoined("NuevoUsuario");

        String[] partsLeft = {"USER_LEFT", "ViejoUsuario"};
        invocarMetodoPrivado("handleUserLeft", new Object[]{partsLeft}, String[].class);
        verify(mockListener).onUserLeft("ViejoUsuario");
 
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

    @Test
    void testProcessMessageDispatch() throws Exception {

        
        String msg = "ERROR|Mensaje de prueba";
        invocarMetodoPrivado("processMessage", new Object[]{msg}, String.class);
        Thread.sleep(50); 

    }

    @Test
    void testDisconnect() throws Exception {
        simularConexionExitosa();
        when(mockSocket.isClosed()).thenReturn(false);

        networkController.disconnect();

        verify(mockOut).println("DISCONNECT"); 
        verify(mockSocket).close();            
        verify(mockListener).onConnectionStatusChanged(false, "Desconectado del servidor");
        assertFalse(networkController.isConnected());
    }
    
    @Test
    void testConnectFailsUnknownHost() {
        
        boolean result = networkController.connect("host-que-no-existe-12345", 9999);
        assertFalse(result);
        verify(mockListener).onConnectionStatusChanged(eq(false), contains("Host desconocido"));
    }
    
    @Test
    void testRemoveListener() {
        networkController.removeNetworkListener(mockListener);
       
        try {
            Method method = NetworkController.class.getDeclaredMethod("notifyError", String.class);
            method.setAccessible(true);
            method.invoke(networkController, "Test");
        } catch(Exception e) {}
        
       
        verify(mockListener, never()).onError(anyString());
    }

    private void simularConexionExitosa() throws Exception {
      
        Field socketField = NetworkController.class.getDeclaredField("socket");
        socketField.setAccessible(true);
        socketField.set(networkController, mockSocket);

        Field outField = NetworkController.class.getDeclaredField("out");
        outField.setAccessible(true);
        outField.set(networkController, mockOut);

        Field connectedField = NetworkController.class.getDeclaredField("connected");
        connectedField.setAccessible(true);
        connectedField.set(networkController, true);
    }

    private void invocarMetodoPrivado(String nombreMetodo, Object[] args, Class<?>... parameterTypes) throws Exception {
        Method method = NetworkController.class.getDeclaredMethod(nombreMetodo, parameterTypes);
        method.setAccessible(true);
        method.invoke(networkController, args);
    }
}