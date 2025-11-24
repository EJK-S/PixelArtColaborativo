package com.equipo.pixelart.client.view;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserListPanelTest {

    private UserListPanel userListPanel;

    @BeforeEach
    public void setUp() {
        userListPanel = new UserListPanel();
    }

    @Test
    void testInitialization() {
        
        assertNotNull(userListPanel.getUserTable(), "La tabla no debe ser null");
        assertNotNull(userListPanel.getTableModel(), "El modelo no debe ser null");
        
        
        assertEquals(0, userListPanel.getUserCount());
        
        
        assertEquals(3, userListPanel.getTableModel().getColumnCount());
        assertEquals("Usuario", userListPanel.getTableModel().getColumnName(1));
    }

    @Test
    void testAddUser() {
        userListPanel.addUser("Anthony");

        assertEquals(1, userListPanel.getUserCount());
        assertTrue(userListPanel.containsUser("Anthony"));
        
        
        DefaultTableModel model = userListPanel.getTableModel();
        assertEquals("Anthony", model.getValueAt(0, 1)); 
        assertEquals("Activo", model.getValueAt(0, 2), "El estado por defecto debe ser Activo");
    }

    @Test
    void testAddDuplicateUser() {
        userListPanel.addUser("Anthony");
        userListPanel.addUser("Anthony"); 

        assertEquals(1, userListPanel.getUserCount(), "No debe permitir usuarios duplicados");
    }

    @Test
    void testRemoveUser() {
        userListPanel.addUser("Anthony");
        userListPanel.removeUser("Anthony");

        assertEquals(0, userListPanel.getUserCount());
        assertFalse(userListPanel.containsUser("Anthony"));
    }

    @Test
    void testSetUsers() {
        String[] users = {"Anthony", "", null, "Maria"};

        userListPanel.setUsers(users);

        assertEquals(2, userListPanel.getUserCount(), "Debe ignorar nulos y vacíos"); 
        assertTrue(userListPanel.containsUser("Anthony"));
        assertTrue(userListPanel.containsUser("Maria"));
        
        
        userListPanel.setUsers(new String[]{"Pedro"});
        assertEquals(1, userListPanel.getUserCount());
        assertTrue(userListPanel.containsUser("Pedro"));
        assertFalse(userListPanel.containsUser("Anthony"));
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
        JTable table = userListPanel.getUserTable();

        
        table.setRowSelectionInterval(0, 0);

        assertEquals("Pedro", userListPanel.getSelectedUser());
    }
    
    @Test
    void testGetSelectedUserNone() {
        assertNull(userListPanel.getSelectedUser());
    }

    @Test
    void testUpdateUserStatus() {
        userListPanel.addUser("Mario", "Activo");
        
        
        userListPanel.updateUserStatus("Mario", "Dibujando");
        
        DefaultTableModel model = userListPanel.getTableModel();
        assertEquals("Dibujando", model.getValueAt(0, 2), "El estado debería actualizarse en la columna 2");
    }
}