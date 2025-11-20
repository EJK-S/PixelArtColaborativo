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
        // Al ser un panel ligero, podemos instanciarlo directamente
        chatPanel = new ChatPanel();
    }

    @Test
    void testInicializacionUI() {
        assertNotNull(chatPanel.getChatArea());
        assertNotNull(chatPanel.getMessageField());
        assertNotNull(chatPanel.getSendButton());

        // Verificar propiedades iniciales
        assertFalse(chatPanel.getChatArea().isEditable(), "El área de chat no debe ser editable");
        assertEquals("", chatPanel.getChatArea().getText());
    }

    @Test
    void testAddMessageFormato() {
        String user = "Anthony";
        String msg = "Hola Mundo";

        chatPanel.addMessage(user, msg);

        String contenido = chatPanel.getChatArea().getText();

        // Verificamos que contenga las partes clave, ya que la hora cambia
        assertTrue(contenido.contains(user), "Debe contener el usuario");
        assertTrue(contenido.contains(msg), "Debe contener el mensaje");
        // Verificamos el formato de los corchetes de la hora [...]
        assertTrue(contenido.matches("(?s).*?\\[\\d{2}:\\d{2}:\\d{2}].*"), "Debe tener timestamp [HH:mm:ss]");
    }

    @Test
    void testAddSystemMessage() {
        String msg = "Servidor desconectado";
        chatPanel.addSystemMessage(msg);

        String contenido = chatPanel.getChatArea().getText();

        // Verificamos el formato de sistema (*** mensaje ***)
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
        // Escribimos en el campo de texto (simulado)
        JTextField field = chatPanel.getMessageField();
        field.setText("  Mensaje con espacios  "); // Probamos el trim()

        String resultado = chatPanel.getMessageAndClear();

        assertEquals("Mensaje con espacios", resultado, "Debe hacer trim del texto");
        assertEquals("", field.getText(), "Debe limpiar el campo después de enviar");
    }

    @Test
    void testSetEnabled() {
        // Desactivar
        chatPanel.setEnabled(false);
        assertFalse(chatPanel.getMessageField().isEnabled());
        assertFalse(chatPanel.getSendButton().isEnabled());

        // Activar
        chatPanel.setEnabled(true);
        assertTrue(chatPanel.getMessageField().isEnabled());
        assertTrue(chatPanel.getSendButton().isEnabled());
    }

    @Test
    void testFocusMessageField() {
        // Simplemente verificamos que no lance excepción.
        // Para probar el foco real se necesita una ventana visible,
        // pero para cobertura esto es suficiente.
        assertDoesNotThrow(() -> chatPanel.focusMessageField());
    }

    // ==========================================
    // TEST DE INTERACCIÓN (LISTENERS)
    // ==========================================

    @Test
    void testBotonEnviarDisparaListener() {
        // 1. Crear un Listener espía
        ActionListener mockListener = mock(ActionListener.class);
        chatPanel.addSendListener(mockListener);

        // 2. Simular clic en el botón "Enviar"
        chatPanel.getSendButton().doClick();

        // 3. Verificar que el listener se activó
        verify(mockListener, times(1)).actionPerformed(any());
    }

    @Test
    void testEnterEnTextFieldDisparaBoton() {
        // Este es el truco para probar la lambda: e -> sendButton.doClick()
        
        // 1. Espiamos el botón para ver si recibe el clic
        ActionListener mockListener = mock(ActionListener.class);
        chatPanel.addSendListener(mockListener);

        // 2. Disparamos un "Enter" en la caja de texto
        JTextField field = chatPanel.getMessageField();
        field.postActionEvent(); // Simula presionar Enter

        // 3. El Enter debe haber llamado a sendButton.doClick(),
        // lo cual a su vez llama a nuestro listener.
        verify(mockListener, times(1)).actionPerformed(any());
    }
}