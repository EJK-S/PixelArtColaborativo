package com.equipo.pixelart.client.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatPanelTest {

    private ChatPanel chatPanel;

    @BeforeEach
    void setUp() {
    
        chatPanel = new ChatPanel();
    }

    @Test
    void testInicializacionUI() {
        assertNotNull(chatPanel.getChatArea());
        assertNotNull(chatPanel.getMessageField());
        assertNotNull(chatPanel.getSendButton());

        assertFalse(chatPanel.getChatArea().isEditable(), "El área de chat no debe ser editable");
        assertEquals("", chatPanel.getChatArea().getText());
    }

    @Test
    void testAddMessageFormato() {
        String user = "Anthony";
        String msg = "Hola Mundo";

        chatPanel.addMessage(user, msg);

        String contenido = chatPanel.getChatArea().getText();

        assertTrue(contenido.contains(user), "Debe contener el usuario");
        assertTrue(contenido.contains(msg), "Debe contener el mensaje");

        assertTrue(contenido.matches("(?s).*?\\[\\d{2}:\\d{2}:\\d{2}].*"), "Debe tener timestamp [HH:mm:ss]");
    }

    @Test
    void testAddSystemMessage() {
        String msg = "Servidor desconectado";
        chatPanel.addSystemMessage(msg);

        String contenido = chatPanel.getChatArea().getText();

        assertTrue(contenido.contains("*** " + msg + " ***"));
    }

    @Test
    void testClearChat() {
        chatPanel.addMessage("User", "Test");
        assertFalse(chatPanel.getChatArea().getText().isEmpty());

        chatPanel.clearChat();

        assertEquals("", chatPanel.getChatArea().getText());
    }

    @Test
    void testGetMessageAndClear() {
        
        JTextField field = chatPanel.getMessageField();
        field.setText("  Mensaje con espacios  "); 

        String resultado = chatPanel.getMessageAndClear();

        assertEquals("Mensaje con espacios", resultado, "Debe hacer trim del texto");
        assertEquals("", field.getText(), "Debe limpiar el campo después de enviar");
    }

    @Test
    void testSetEnabled() {
       
        chatPanel.setEnabled(false);
        assertFalse(chatPanel.getMessageField().isEnabled());
        assertFalse(chatPanel.getSendButton().isEnabled());

      
        chatPanel.setEnabled(true);
        assertTrue(chatPanel.getMessageField().isEnabled());
        assertTrue(chatPanel.getSendButton().isEnabled());
    }

    @Test
    void testFocusMessageField() {

        assertDoesNotThrow(() -> chatPanel.focusMessageField());
    }

    @Test
    void testBotonEnviarDisparaListener() {
        
        ActionListener mockListener = mock(ActionListener.class);
        chatPanel.addSendListener(mockListener);

        chatPanel.getSendButton().doClick();

        verify(mockListener, times(1)).actionPerformed(any());
    }

    @Test
    void testEnterEnTextFieldDisparaBoton() {
   
        ActionListener mockListener = mock(ActionListener.class);
        chatPanel.addSendListener(mockListener);

        JTextField field = chatPanel.getMessageField();
        field.postActionEvent(); 

        verify(mockListener, times(1)).actionPerformed(any());
    }
}