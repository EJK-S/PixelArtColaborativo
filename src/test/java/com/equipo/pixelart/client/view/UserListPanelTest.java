package com.equipo.pixelart.client.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class UserListPanelTest {

    private UserListPanel userListPanel;

    @BeforeEach
    void setUp() {
        // Instanciamos directamente, es un JPanel simple
        userListPanel = new UserListPanel();
    }

    @Test
    void testInitialization() {
        assertNotNull(userListPanel.getUserList());
        assertNotNull(userListPanel.getListModel());
        assertEquals(0, userListPanel.getUserCount());
        
        // Verificar texto inicial
        assertEquals("0 usuarios", getCountLabelText());
    }

    @Test
    void testAddUser() {
        userListPanel.addUser("Anthony");

        assertEquals(1, userListPanel.getUserCount());
        assertTrue(userListPanel.containsUser("Anthony"));
        
        // Verificar singularización
        assertEquals("1 usuario", getCountLabelText());
    }

    @Test
    void testAddDuplicateUser() {
        userListPanel.addUser("Anthony");
        userListPanel.addUser("Anthony"); // Intento duplicar

        assertEquals(1, userListPanel.getUserCount(), "No debe permitir duplicados");
    }

    @Test
    void testRemoveUser() {
        userListPanel.addUser("Anthony");
        userListPanel.removeUser("Anthony");

        assertEquals(0, userListPanel.getUserCount());
        assertFalse(userListPanel.containsUser("Anthony"));
        assertEquals("0 usuarios", getCountLabelText());
    }

    @Test
    void testSetUsers() {
        // Probamos filtrado de nulos y vacíos
        String[] users = {"Anthony", "", null, "Maria"};

        userListPanel.setUsers(users);

        assertEquals(2, userListPanel.getUserCount()); // Solo Anthony y Maria
        assertTrue(userListPanel.containsUser("Anthony"));
        assertTrue(userListPanel.containsUser("Maria"));
        assertEquals("2 usuarios", getCountLabelText());
    }

    @Test
    void testClearUsers() {
        userListPanel.addUser("Juan");
        userListPanel.clearUsers();

        assertEquals(0, userListPanel.getUserCount());
    }

    @Test
    void testGetSelectedUser() {
        userListPanel.addUser("Pedro");
        
        // Simulamos selección programática
        userListPanel.getUserList().setSelectedIndex(0);

        assertEquals("Pedro", userListPanel.getSelectedUser());
    }
    
    @Test
    void testGetSelectedUserNone() {
        assertNull(userListPanel.getSelectedUser());
    }

    @Test
    void testGetListModel() {
        assertNotNull(userListPanel.getListModel());
        assertTrue(userListPanel.getListModel() instanceof DefaultListModel);
    }

    // ==========================================
    // TEST DEL RENDERER (Clase Interna)
    // ==========================================

    @Test
    void testCellRenderer() {
        JList<String> list = userListPanel.getUserList();
        ListCellRenderer<? super String> renderer = list.getCellRenderer();
        
        // 1. Caso NO seleccionado
        Component cNormal = renderer.getListCellRendererComponent(
                list, "UsuarioTest", 0, false, false
        );
        
        assertTrue(cNormal instanceof JLabel);
        JLabel labelNormal = (JLabel) cNormal;
        
        // Verificar Emoji
        assertEquals("👤 UsuarioTest", labelNormal.getText());
        // Verificar colores normales
        assertEquals(Color.WHITE, labelNormal.getBackground());
        assertEquals(Color.BLACK, labelNormal.getForeground());

        // 2. Caso SELECCIONADO
        Component cSelected = renderer.getListCellRendererComponent(
                list, "UsuarioTest", 0, true, false
        );
        
        JLabel labelSelected = (JLabel) cSelected;
        // Verificar colores de selección (Azul claro)
        assertEquals(new Color(100, 149, 237), labelSelected.getBackground());
        assertEquals(Color.WHITE, labelSelected.getForeground());
    }

    // ==========================================
    // HELPER (Reflection para leer etiqueta privada)
    // ==========================================

    private String getCountLabelText() {
        try {
            Field field = UserListPanel.class.getDeclaredField("countLabel");
            field.setAccessible(true);
            JLabel label = (JLabel) field.get(userListPanel);
            return label.getText();
        } catch (Exception e) {
            fail("No se pudo acceder al countLabel: " + e.getMessage());
            return null;
        }
    }
}