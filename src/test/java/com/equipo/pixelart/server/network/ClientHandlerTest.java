package com.equipo.pixelart.server.network;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.equipo.pixelart.server.service.AuthService;
import com.equipo.pixelart.server.service.CanvasService;

class ClientHandlerTest {

    private ClientHandler clientHandler;
    private Socket mockSocket;
    private AuthService mockAuthService;
    private CanvasService mockCanvasService;
    private List<ClientHandler> clientList;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    public void setUp() throws IOException {
        
        mockSocket = mock(Socket.class);
        mockAuthService = mock(AuthService.class);
        mockCanvasService = mock(CanvasService.class);
        clientList = new ArrayList<>(); 

        outputStreamCaptor = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStreamCaptor);

        clientHandler = new ClientHandler(mockSocket, mockAuthService, mockCanvasService, clientList);
        clientList.add(clientHandler); 
    }

    private void ejecutarHandlerConComandos(String comandos) throws IOException {
       
        ByteArrayInputStream input = new ByteArrayInputStream(comandos.getBytes());
        when(mockSocket.getInputStream()).thenReturn(input);

        clientHandler.run();
    }

    @Test
    void testLoginExitoso() throws IOException {
        when(mockAuthService.autenticar("Anthony", "123")).thenReturn(true);

        ejecutarHandlerConComandos("LOGIN|Anthony|123\nDISCONNECT");

        assertTrue(clientHandler.isAuthenticated());
        assertEquals("Anthony", clientHandler.getUsername());

        String respuesta = outputStreamCaptor.toString();
        assertTrue(respuesta.contains("AUTH_SUCCESS|Anthony"));
    }

    @Test
    void testLoginFallido() throws IOException {
        when(mockAuthService.autenticar("Anthony", "mal")).thenReturn(false);

        ejecutarHandlerConComandos("LOGIN|Anthony|mal\nDISCONNECT");

        assertFalse(clientHandler.isAuthenticated());
        assertTrue(outputStreamCaptor.toString().contains("AUTH_FAILED"));
    }
    
    @Test
    void testLoginFormatoInvalido() throws IOException {
        ejecutarHandlerConComandos("LOGIN|SoloUser\nDISCONNECT");
        assertTrue(outputStreamCaptor.toString().contains("AUTH_FAILED"));
    }

    @Test
    void testRegisterExitoso() throws IOException {
        when(mockAuthService.registrarUsuario("Nuevo", "pass")).thenReturn(true);

        ejecutarHandlerConComandos("REGISTER|Nuevo|pass\nDISCONNECT");

        assertTrue(outputStreamCaptor.toString().contains("AUTH_SUCCESS"));
    }

    @Test
    void testRegisterFallido() throws IOException {
        when(mockAuthService.registrarUsuario("Existe", "pass")).thenReturn(false);

        ejecutarHandlerConComandos("REGISTER|Existe|pass\nDISCONNECT");
        assertTrue(outputStreamCaptor.toString().contains("AUTH_FAILED"));
    }

    @Test
    void testPixelUpdate() throws IOException {
      
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        when(mockCanvasService.setPixel(anyInt(), anyInt(), any())).thenReturn(true);

        ejecutarHandlerConComandos("LOGIN|User|Pass\nPIXEL|10|10|ff0000\nDISCONNECT");

        verify(mockCanvasService).setPixel(eq(10), eq(10), any(Color.class));

        assertTrue(outputStreamCaptor.toString().contains("PIXEL_UPDATE|10|10|ff0000"));
    }

    @Test
    void testPixelSinAutenticacion() throws IOException {
        
        ejecutarHandlerConComandos("PIXEL|10|10|ff0000\nDISCONNECT");

        verify(mockCanvasService, never()).setPixel(anyInt(), anyInt(), any());
    }
    
    @Test
    void testPixelErrorFormato() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        
        ejecutarHandlerConComandos("LOGIN|User|Pass\nPIXEL|XX|10|ff0000\nDISCONNECT");
        
        assertTrue(outputStreamCaptor.toString().contains("ERROR"));
    }
    
    @Test
    void testPixelFalloLogico() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
       
        when(mockCanvasService.setPixel(anyInt(), anyInt(), any())).thenReturn(false);
        
        ejecutarHandlerConComandos("LOGIN|User|Pass\nPIXEL|1|1|ff0000\nDISCONNECT");
        
        assertTrue(outputStreamCaptor.toString().contains("ERROR"));
    }

    @Test
    void testGetCanvas() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        when(mockCanvasService.getWidth()).thenReturn(100);
        when(mockCanvasService.getHeight()).thenReturn(100);
        when(mockCanvasService.getCanvasAsString()).thenReturn("fake_data");

        ejecutarHandlerConComandos("LOGIN|User|Pass\nGET_CANVAS\nDISCONNECT");

        assertTrue(outputStreamCaptor.toString().contains("CANVAS_STATE|100|100|fake_data"));
    }

    @Test
    void testClear() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);

        ejecutarHandlerConComandos("LOGIN|User|Pass\nCLEAR\nDISCONNECT");

        verify(mockCanvasService).clear();
        assertTrue(outputStreamCaptor.toString().contains("CLEAR_CANVAS"));
    }
    
    @Test
    void testChat() throws Exception { 
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        
        
        ClientHandler otroCliente = mock(ClientHandler.class);
        
       
        java.lang.reflect.Field authField = ClientHandler.class.getDeclaredField("authenticated");
        authField.setAccessible(true);
        authField.set(otroCliente, true); 

        
        clientList.add(otroCliente);

        
        ejecutarHandlerConComandos("LOGIN|User|Pass\nCHAT|Hola Mundo\nDISCONNECT");
        
       
        verify(otroCliente).sendMessage(contains("CHAT_MESSAGE|User|Hola Mundo"));
    }

    @Test
    void testGetUsers() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        when(mockAuthService.obtenerUsuariosConectados()).thenReturn(new String[]{"User", "Otro"});

        ejecutarHandlerConComandos("LOGIN|User|Pass\nGET_USERS\nDISCONNECT");

        assertTrue(outputStreamCaptor.toString().contains("USER_LIST|User,Otro"));
    }

    @Test
    void testUndo() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        when(mockCanvasService.undo()).thenReturn(true);
        when(mockCanvasService.getCanvasAsString()).thenReturn("data_undo");

        ejecutarHandlerConComandos("LOGIN|User|Pass\nUNDO\nDISCONNECT");

        verify(mockCanvasService).undo();
        assertTrue(outputStreamCaptor.toString().contains("CANVAS_STATE"));
    }

    @Test
    void testComandoDesconocido() throws IOException {
        ejecutarHandlerConComandos("COMANDO_RARO\nDISCONNECT");
        assertTrue(outputStreamCaptor.toString().contains("Comando desconocido"));
    }
    
    @Test
    void testEmptyMessage() throws IOException {

        ejecutarHandlerConComandos("\nDISCONNECT"); 

    }

    @Test
    void testIOExceptionEnRun() throws IOException {
        
        when(mockSocket.getInputStream()).thenThrow(new IOException("Error de red simulado"));

        clientHandler.run();

        verify(mockSocket).close();
    }
    
    @Test
    void testDisconnectLimpiaRecursos() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        
        ejecutarHandlerConComandos("LOGIN|User|Pass\nDISCONNECT");
 
        assertFalse(clientList.contains(clientHandler));
      
        verify(mockAuthService).desconectar("User");
        
        verify(mockSocket, atLeastOnce()).close();
    }
    
    @Test
    void testExceptionEnProcessMessage() throws IOException {
        
        when(mockAuthService.autenticar(any(), any())).thenThrow(new RuntimeException("Crash"));
        
        ejecutarHandlerConComandos("LOGIN|A|B\nDISCONNECT");

        assertTrue(outputStreamCaptor.toString().contains("ERROR|Error procesando mensaje"));
    }
    
    @Test
    void testGetters() {
        assertEquals(mockSocket, clientHandler.getSocket());
        assertNull(clientHandler.getUsername()); 
    }
}