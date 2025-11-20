package com.equipo.pixelart.client.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainFrameTest {

    private MainFrame mainFrame; 
    private MainFrame realFrame; 

    @BeforeEach
    void setUp() {
      
        realFrame = new MainFrame();
 
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
       
        mainFrame.updateConnectionStatus(true, "Servidor 1");

        assertFalse(mainFrame.getConnectItem().isEnabled());
        assertTrue(mainFrame.getDisconnectItem().isEnabled());

        JLabel statusLabel = (JLabel) getPrivateField("statusLabel");
        assertTrue(statusLabel.getText().contains("Conectado"));
        assertTrue(statusLabel.getText().contains("Servidor 1"));
    }

    @Test
    void testUpdateConnectionStatus_Desconectado() throws Exception {
      
        mainFrame.updateConnectionStatus(false, "Error de red");

        assertTrue(mainFrame.getConnectItem().isEnabled());
        assertFalse(mainFrame.getDisconnectItem().isEnabled());

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

        doNothing().when(mainFrame).openDialog(any());

        ConnectionDialog result = mainFrame.showConnectionDialog();

        assertNotNull(result);

        verify(mainFrame).openDialog(result);
    }

    @Test
    void testShowAboutDialog() {
       
        doNothing().when(mainFrame).showMessageDialog(anyString(), anyString(), anyInt());

        mainFrame.showAboutDialog();

        verify(mainFrame).showMessageDialog(contains("Pixel Art"), eq("Acerca de"), eq(JOptionPane.INFORMATION_MESSAGE));
    }

    @Test
    void testSetPanels() {

        com.equipo.pixelart.client.model.CanvasModel mockModel = mock(com.equipo.pixelart.client.model.CanvasModel.class);
        when(mockModel.getWidth()).thenReturn(10);
        when(mockModel.getHeight()).thenReturn(10);

        CanvasPanel realCanvas = new CanvasPanel(mockModel);
        ChatPanel realChat = new ChatPanel();

        ToolboxPanel realToolbox = mock(ToolboxPanel.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
        UserListPanel realUserList = mock(UserListPanel.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));

        MainFrame cleanFrame = new MainFrame();

        assertDoesNotThrow(() -> 
            cleanFrame.setPanels(realToolbox, realCanvas, realChat, realUserList)
        );

        assertTrue(cleanFrame.getContentPane().getComponentCount() > 0);
        
        cleanFrame.dispose();
    }
    @Test
    void testWindowClosing_Cancelado() {
      
        doReturn(JOptionPane.NO_OPTION).when(mainFrame).showExitConfirmation();

        mainFrame.handleWindowClosing();

       
        verify(mainFrame).showExitConfirmation();
        
        
    }


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