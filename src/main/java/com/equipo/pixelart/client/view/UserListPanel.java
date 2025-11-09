package com.equipo.pixelart.client.view;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de Lista de Usuarios - Muestra los usuarios conectados
 */
public class UserListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private DefaultListModel<String> listModel;
    private JList<String> userList;
    private JScrollPane scrollPane;
    private JLabel countLabel;

    /**
     * Constructor
     */
    public UserListPanel() {
        setupUI();
    }

    /**
     * Configura la interfaz del panel
     */
    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Usuarios Conectados",
                0,
                0,
                new Font("Arial", Font.BOLD, 12)
        ));

        // Modelo de la lista
        listModel = new DefaultListModel<>();

        // Lista de usuarios
        userList = new JList<>(listModel);
        userList.setFont(new Font("Arial", Font.PLAIN, 12));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setBackground(new Color(245, 245, 245));

        // Renderizador personalizado para mostrar iconos
        userList.setCellRenderer(new UserListCellRenderer());

        scrollPane = new JScrollPane(userList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(280, 150));

        add(scrollPane, BorderLayout.CENTER);

        // Label con contador
        countLabel = new JLabel("0 usuarios");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

        add(countLabel, BorderLayout.SOUTH);
    }

    /**
     * Agrega un usuario a la lista
     */
    public void addUser(String username) {
        if (!listModel.contains(username)) {
            listModel.addElement(username);
            updateCount();
        }
    }

    /**
     * Elimina un usuario de la lista
     */
    public void removeUser(String username) {
        listModel.removeElement(username);
        updateCount();
    }

    /**
     * Actualiza toda la lista de usuarios
     */
    public void setUsers(String[] users) {
        listModel.clear();
        for (String user : users) {
            if (user != null && !user.trim().isEmpty()) {
                listModel.addElement(user);
            }
        }
        updateCount();
    }

    /**
     * Limpia la lista
     */
    public void clearUsers() {
        listModel.clear();
        updateCount();
    }

    /**
     * Actualiza el contador de usuarios
     */
    private void updateCount() {
        int count = listModel.getSize();
        countLabel.setText(count + (count == 1 ? " usuario" : " usuarios"));
    }

    /**
     * Obtiene el usuario seleccionado
     */
    public String getSelectedUser() {
        return userList.getSelectedValue();
    }

    /**
     * Verifica si un usuario está en la lista
     */
    public boolean containsUser(String username) {
        return listModel.contains(username);
    }

    /**
     * Obtiene el número de usuarios
     */
    public int getUserCount() {
        return listModel.getSize();
    }

    // ==================== GETTERS ====================

    public JList<String> getUserList() {
        return userList;
    }

    public DefaultListModel<String> getListModel() {
        return listModel;
    }

    /**
     * Renderizador personalizado para las celdas de la lista
     */
    private static class UserListCellRenderer extends DefaultListCellRenderer {
    	private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            // Agregar ícono de usuario
            label.setText("👤 " + value.toString());
            label.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

            // Colores personalizados
            if (isSelected) {
                label.setBackground(new Color(100, 149, 237)); // Azul claro
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }

            return label;
        }
    }
}