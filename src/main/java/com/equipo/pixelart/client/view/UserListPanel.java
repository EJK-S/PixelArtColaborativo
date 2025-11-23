package com.equipo.pixelart.client.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class UserListPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;
    private DefaultTableModel tableModel;
    private JTable userTable;
    private JLabel countLabel;

    public UserListPanel() {
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Usuarios Conectados",
                0,
                0,
                new Font("Arial", Font.BOLD, 12)
        ));

        // Crear modelo de tabla con columnas
        String[] columnNames = {"", "Usuario", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer tabla no editable
            }
        };

        // Crear tabla
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setBackground(new Color(245, 245, 245));
        userTable.setRowHeight(28);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));

        // Configurar ancho de columnas
        userTable.getColumnModel().getColumn(0).setPreferredWidth(30);  // Icono
        userTable.getColumnModel().getColumn(0).setMaxWidth(30);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Usuario
        userTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Estado

        // Centrar contenido de las columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Renderer personalizado para la columna de usuario
        userTable.getColumnModel().getColumn(1).setCellRenderer(new UserCellRenderer());

        // Renderer personalizado para la columna de estado
        userTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        // Configurar header
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 11));
        header.setBackground(new Color(230, 230, 230));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(280, 150));

        add(scrollPane, BorderLayout.CENTER);

        countLabel = new JLabel("0 usuarios");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

        add(countLabel, BorderLayout.SOUTH);
    }

    public void addUser(String username) {
        addUser(username, "Activo");
    }

    public void addUser(String username, String status) {
        if (!containsUser(username)) {
            tableModel.addRow(new Object[]{"👤", username, status});
            updateCount();
        }
    }

    public void removeUser(String username) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(username)) {
                tableModel.removeRow(i);
                updateCount();
                break;
            }
        }
    }

    public void setUsers(String[] users) {
        tableModel.setRowCount(0); // Limpiar tabla
        for (String user : users) {
            if (user != null && !user.trim().isEmpty()) {
                tableModel.addRow(new Object[]{"👤", user, "Activo"});
            }
        }
        updateCount();
    }

    public void clearUsers() {
        tableModel.setRowCount(0);
        updateCount();
    }

    public void updateUserStatus(String username, String status) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(username)) {
                tableModel.setValueAt(status, i, 2);
                return;
            }
        }
    }

    private void updateCount() {
        int count = tableModel.getRowCount();
        countLabel.setText(count + (count == 1 ? " usuario" : " usuarios"));
    }

    public String getSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (String) tableModel.getValueAt(selectedRow, 1);
        }
        return null;
    }

    public boolean containsUser(String username) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(username)) {
                return true;
            }
        }
        return false;
    }

    public int getUserCount() {
        return tableModel.getRowCount();
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            users.add((String) tableModel.getValueAt(i, 1));
        }
        return users;
    }

    public JTable getUserTable() {
        return userTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    // Métodos eliminados - ya no son necesarios con JTable
    // Si necesitas acceso directo a la tabla, usa getUserTable()
    // Si necesitas acceso al modelo, usa getTableModel()

    // Renderer personalizado para la columna de usuario
    private static class UserCellRenderer extends DefaultTableCellRenderer {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

            if (isSelected) {
                setBackground(new Color(100, 149, 237));
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }

            setFont(new Font("Arial", Font.PLAIN, 12));

            return this;
        }
    }

    // Renderer personalizado para la columna de estado
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Arial", Font.PLAIN, 10));

            String status = (String) value;

            Color backgroundColor;
            Color foregroundColor;

            if (isSelected) {
                backgroundColor = new Color(100, 149, 237);
                foregroundColor = Color.WHITE;
            } else {
                backgroundColor = Color.WHITE;

                // Colorear según el estado
                foregroundColor = switch (status) {
                    case "Activo" -> new Color(34, 139, 34); // Verde
                    case "Inactivo" -> new Color(169, 169, 169); // Gris
                    case "Dibujando" -> new Color(30, 144, 255); // Azul
                    default -> Color.BLACK;
                };
            }

            setBackground(backgroundColor);
            setForeground(foregroundColor);

            return this;
        }
    }
}