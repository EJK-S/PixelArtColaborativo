package com.equipo.pixelart.server.network;

import com.equipo.pixelart.server.service.AuthService;
import com.equipo.pixelart.server.service.CanvasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClientHandlerTest {

    private ClientHandler clientHandler;
    private Socket mockSocket;
    private AuthService mockAuthService;
    private CanvasService mockCanvasService;
    private List<ClientHandler> clientList;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() throws IOException {
        // 1. Mocks principales
        mockSocket = mock(Socket.class);
        mockAuthService = mock(AuthService.class);
        mockCanvasService = mock(CanvasService.class);
        clientList = new ArrayList<>(); // Lista real para probar añadir/quitar

        // 2. Preparamos la "boca" falsa para capturar lo que el servidor responde
        outputStreamCaptor = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStreamCaptor);

        // 3. Instanciamos el handler (Aun no corremos el run)
        clientHandler = new ClientHandler(mockSocket, mockAuthService, mockCanvasService, clientList);
        clientList.add(clientHandler); // Se agrega a sí mismo como si el Server lo hubiera hecho
    }

    /**
     * Ayudante: Ejecuta el método run() inyectando comandos específicos
     */
    private void ejecutarHandlerConComandos(String comandos) throws IOException {
        // Inyectamos los comandos en el InputStream del socket
        ByteArrayInputStream input = new ByteArrayInputStream(comandos.getBytes());
        when(mockSocket.getInputStream()).thenReturn(input);

        // Ejecutamos el run(). Como el input stream es finito, el while terminará cuando lea null
        // o cuando lea DISCONNECT.
        clientHandler.run();
    }

    // ==========================================
    // TESTS DE LOGIN Y REGISTRO
    // ==========================================

    @Test
    void testLoginExitoso() throws IOException {
        when(mockAuthService.autenticar("Anthony", "123")).thenReturn(true);

        // Enviamos LOGIN y luego DISCONNECT para salir del bucle
        ejecutarHandlerConComandos("LOGIN|Anthony|123\nDISCONNECT");

        // Verificaciones
        assertTrue(clientHandler.isAuthenticated());
        assertEquals("Anthony", clientHandler.getUsername());
        
        // Verificamos que respondió AUTH_SUCCESS
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

        // OJO: Según tu código, registrarse NO autentica automáticamente (no pone authenticated=true)
        // Solo devuelve AUTH_SUCCESS
        assertTrue(outputStreamCaptor.toString().contains("AUTH_SUCCESS"));
    }

    @Test
    void testRegisterFallido() throws IOException {
        when(mockAuthService.registrarUsuario("Existe", "pass")).thenReturn(false);

        ejecutarHandlerConComandos("REGISTER|Existe|pass\nDISCONNECT");
        assertTrue(outputStreamCaptor.toString().contains("AUTH_FAILED"));
    }

    // ==========================================
    // TESTS DE LÓGICA DE JUEGO (Requieren Auth)
    // ==========================================

    @Test
    void testPixelUpdate() throws IOException {
        // Simulamos que ya estamos logueados
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        when(mockCanvasService.setPixel(anyInt(), anyInt(), any())).thenReturn(true);

        // Flujo: Login -> Pintar Pixel -> Salir
        ejecutarHandlerConComandos("LOGIN|User|Pass\nPIXEL|10|10|ff0000\nDISCONNECT");

        verify(mockCanvasService).setPixel(eq(10), eq(10), any(Color.class));
        
        // Verificar que mandó broadcast (se busca en la salida)
        assertTrue(outputStreamCaptor.toString().contains("PIXEL_UPDATE|10|10|ff0000"));
    }

    @Test
    void testPixelSinAutenticacion() throws IOException {
        // Intentamos pintar sin login
        ejecutarHandlerConComandos("PIXEL|10|10|ff0000\nDISCONNECT");
        
        // No debe llamar al servicio
        verify(mockCanvasService, never()).setPixel(anyInt(), anyInt(), any());
    }
    
    @Test
    void testPixelErrorFormato() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        // Coordenada X no es un número
        ejecutarHandlerConComandos("LOGIN|User|Pass\nPIXEL|XX|10|ff0000\nDISCONNECT");
        
        assertTrue(outputStreamCaptor.toString().contains("ERROR"));
    }
    
    @Test
    void testPixelFalloLogico() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        // El servicio dice false (ej. fuera de rango)
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

    // ==========================================
    // TESTS DE EXCEPCIONES Y CIERRE
    // ==========================================

    @Test
    void testComandoDesconocido() throws IOException {
        ejecutarHandlerConComandos("COMANDO_RARO\nDISCONNECT");
        assertTrue(outputStreamCaptor.toString().contains("Comando desconocido"));
    }
    
    @Test
    void testEmptyMessage() throws IOException {
        // Mensaje vacío (simulando doble pipe o error de red)
        // split("\\|") en una linea vacia da array vacio o de 1 elemento vacio
        ejecutarHandlerConComandos("\nDISCONNECT"); 
        // No debe explotar
    }

    @Test
    void testIOExceptionEnRun() throws IOException {
        // Simulamos que el socket explota al intentar crear los streams
        when(mockSocket.getInputStream()).thenThrow(new IOException("Error de red simulado"));

        // Ejecutamos directamente run (no el helper)
        clientHandler.run();

        // Debe imprimir error y llamar a disconnect()
        // Verificamos que se intentó cerrar el socket
        verify(mockSocket).close();
    }
    
    @Test
    void testDisconnectLimpiaRecursos() throws IOException {
        when(mockAuthService.autenticar("User", "Pass")).thenReturn(true);
        
        ejecutarHandlerConComandos("LOGIN|User|Pass\nDISCONNECT");
        
        // Verificar que se removió de la lista
        assertFalse(clientList.contains(clientHandler));
        // Verificar que se llamó al servicio de auth
        verify(mockAuthService).desconectar("User");
        // Verificar cierre de socket
        verify(mockSocket, atLeastOnce()).close();
    }
    
    @Test
    void testExceptionEnProcessMessage() throws IOException {
        // Provocamos un NullPointerException interno
        when(mockAuthService.autenticar(any(), any())).thenThrow(new RuntimeException("Crash"));
        
        ejecutarHandlerConComandos("LOGIN|A|B\nDISCONNECT");
        
        // Debe capturar la excepción y mandar ERROR
        assertTrue(outputStreamCaptor.toString().contains("ERROR|Error procesando mensaje"));
    }
    
    @Test
    void testGetters() {
        assertEquals(mockSocket, clientHandler.getSocket());
        assertNull(clientHandler.getUsername()); // Antes de login
    }
}