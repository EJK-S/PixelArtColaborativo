package com.equipo.pixelart.client.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainFrameTest {

    private MainFrame mainFrame; // Será un SPY
    private MainFrame realFrame; // Instancia real para setup

    @BeforeEach
    void setUp() {
        // Creamos una instancia real primero
        realFrame = new MainFrame();
        
        // Creamos un SPY sobre ella para poder burlar los diálogos
        mainFrame = spy(realFrame);
    }

    @Test
    void testInicializacion() {
        assertNotNull(mainFrame.getConnectItem());
        assertNotNull(mainFrame.getDisconnectItem());
        assertEquals("Invitado", getPrivateUsername(mainFrame));
    }

    @Test
    void testUpdateConnectionStatus_Conectado() throws Exception {
        // Ejecutar
        mainFrame.updateConnectionStatus(true, "Servidor 1");

        // Verificar estado de menús
        assertFalse(mainFrame.getConnectItem().isEnabled());
        assertTrue(mainFrame.getDisconnectItem().isEnabled());

        // Verificar etiqueta privada (statusLabel) usando Reflection
        JLabel statusLabel = (JLabel) getPrivateField("statusLabel");
        assertTrue(statusLabel.getText().contains("Conectado"));
        assertTrue(statusLabel.getText().contains("Servidor 1"));
    }

    @Test
    void testUpdateConnectionStatus_Desconectado() throws Exception {
        // Ejecutar
        mainFrame.updateConnectionStatus(false, "Error de red");

        // Verificar estado de menús
        assertTrue(mainFrame.getConnectItem().isEnabled());
        assertFalse(mainFrame.getDisconnectItem().isEnabled());

        // Verificar etiqueta
        JLabel statusLabel = (JLabel) getPrivateField("statusLabel");
        assertTrue(statusLabel.getText().contains("Desconectado"));
        assertTrue(statusLabel.getText().contains("Error de red"));
    }

    @Test
    void testUpdateUsername() throws Exception {
        mainFrame.updateUsername("Anthony");

        JLabel userLabel = (JLabel) getPrivateField("userLabel");
        assertTrue(userLabel.getText().contains("Anthony"));
    }

    @Test
    void testShowConnectionDialog() {
        // Evitamos que se bloquee con setVisible(true)
        // Burlamos el nuevo método protegido 'openDialog'
        doNothing().when(mainFrame).openDialog(any());

        ConnectionDialog result = mainFrame.showConnectionDialog();

        assertNotNull(result);
        
        // Verificamos que se llamó a openDialog
        verify(mainFrame).openDialog(result);
    }

    @Test
    void testShowAboutDialog() {
        // Evitamos que salga el JOptionPane real
        doNothing().when(mainFrame).showMessageDialog(anyString(), anyString(), anyInt());

        mainFrame.showAboutDialog();

        // Verificamos que intentó mostrar el mensaje con el texto correcto
        verify(mainFrame).showMessageDialog(contains("Pixel Art"), eq("Acerca de"), eq(JOptionPane.INFORMATION_MESSAGE));
    }

    @Test
    void testSetPanels() {
        // 1. Preparamos dependencias para poder hacer 'new' de los paneles
        // CanvasPanel necesita un modelo, así que le damos uno falso
        com.equipo.pixelart.client.model.CanvasModel mockModel = mock(com.equipo.pixelart.client.model.CanvasModel.class);
        when(mockModel.getWidth()).thenReturn(10);
        when(mockModel.getHeight()).thenReturn(10);

        // 2. Creamos INSTANCIAS REALES (dummies) en lugar de Mocks vacíos
        // Esto inicializa el 'TreeLock' y hace feliz a Swing.
        CanvasPanel realCanvas = new CanvasPanel(mockModel);
        ChatPanel realChat = new ChatPanel();
        
        // Nota: Si ToolboxPanel o UserListPanel te piden argumentos en el constructor,
        // pásales 'null' o un mock. Asumo aquí que tienen constructor vacío o simple.
        // Si te marca error en el 'new', ajusta los parámetros.
        ToolboxPanel realToolbox = mock(ToolboxPanel.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
        UserListPanel realUserList = mock(UserListPanel.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
        
        // SI LO DE ARRIBA FALLA PORQUE NO TIENEN CONSTRUCTOR VACÍO:
        // Intenta hacer: new ToolboxPanel(null) o lo que pida su constructor.

        // 3. Instancia limpia del Frame
        MainFrame cleanFrame = new MainFrame();

        // 4. Ejecutar ensamblaje
        assertDoesNotThrow(() -> 
            cleanFrame.setPanels(realToolbox, realCanvas, realChat, realUserList)
        );

        // 5. Verificar
        assertTrue(cleanFrame.getContentPane().getComponentCount() > 0);
        
        cleanFrame.dispose();
    }
    @Test
    void testWindowClosing_Cancelado() {
        // 1. Preparamos el Spy para que NO abra la ventanita real
        doReturn(JOptionPane.NO_OPTION).when(mainFrame).showExitConfirmation();

        // 2. LLAMADA DIRECTA (El cambio clave)
        // Al llamar al método directamente sobre la variable 'mainFrame' (que es el Spy),
        // aseguramos que la ejecución pase por Mockito.
        mainFrame.handleWindowClosing();

        // 3. Verificamos
        verify(mainFrame).showExitConfirmation();
        
        // Y el test sigue vivo (no hizo System.exit)
    }

    // ==========================================
    // HELPERS (REFLECTION & EVENTS)
    // ==========================================

    private Object getPrivateField(String fieldName) throws Exception {
        Field field = MainFrame.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(mainFrame);
    }

    private String getPrivateUsername(MainFrame frame) {
        try {
            Field field = MainFrame.class.getDeclaredField("username");
            field.setAccessible(true);
            return (String) field.get(frame);
        } catch (Exception e) {
            return null;
        }
    }
}