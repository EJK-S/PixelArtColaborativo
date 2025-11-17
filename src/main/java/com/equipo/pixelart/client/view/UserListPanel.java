package com.equipo.pixelart.client.view;

import javax.swing.*;
import java.awt.*;

public class UserListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private DefaultListModel<String> listModel;
    private JList<String> userList;
    private JScrollPane scrollPane;
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

        listModel = new DefaultListModel<>();

        userList = new JList<>(listModel);
        userList.setFont(new Font("Arial", Font.PLAIN, 12));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setBackground(new Color(245, 245, 245));

        userList.setCellRenderer(new UserListCellRenderer());

        scrollPane = new JScrollPane(userList);
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
        if (!listModel.contains(username)) {
            listModel.addElement(username);
            updateCount();
        }
    }

    public void removeUser(String username) {
        listModel.removeElement(username);
        updateCount();
    }

    public void setUsers(String[] users) {
        listModel.clear();
        for (String user : users) {
            if (user != null && !user.trim().isEmpty()) {
                listModel.addElement(user);
            }
        }
        updateCount();
    }

    public void clearUsers() {
        listModel.clear();
        updateCount();
    }

    private void updateCount() {
        int count = listModel.getSize();
        countLabel.setText(count + (count == 1 ? " usuario" : " usuarios"));
    }

    public String getSelectedUser() {
        return userList.getSelectedValue();
    }

    public boolean containsUser(String username) {
        return listModel.contains(username);
    }

    public int getUserCount() {
        return listModel.getSize();
    }


    public JList<String> getUserList() {
        return userList;
    }

    public DefaultListModel<String> getListModel() {
        return listModel;
    }

    private static class UserListCellRenderer extends DefaultListCellRenderer {
    	private static final long serialVersionUID = 1L;
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setText("👤 " + value.toString());
            label.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

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