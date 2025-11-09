package com.equipo.pixelart.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana Principal - Contiene toda la interfaz de usuario del cliente
 */
public class MainFrame extends JFrame {

    // Paneles principales
    private ToolboxPanel toolboxPanel;
    private CanvasPanel canvasPanel;
    private ChatPanel chatPanel;
    private UserListPanel userListPanel;

    // Barra de menú
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;

    // Items de menú
    private JMenuItem connectItem;
    private JMenuItem disconnectItem;
    private JMenuItem exitItem;
    private JMenuItem clearItem;
    private JMenuItem toggleGridItem;
    private JMenuItem aboutItem;

    // Barra de estado
    private JPanel statusBar;
    private JLabel statusLabel;
    private JLabel userLabel;

    private String username;

    /**
     * Constructor
     */
    public MainFrame() {
        super("Pixel Art Colaborativo");
        username = "Invitado";
        setupFrame();
    }

    /**
     * Configura la ventana principal
     */
    private void setupFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Configurar layout principal
        setLayout(new BorderLayout(5, 5));

        // Crear menú
        createMenuBar();

        // Crear paneles (se asignarán desde ClientApp)
        createMainLayout();

        // Crear barra de estado
        createStatusBar();

        // Listener de cierre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    /**
     * Crea el layout principal (vacío, se llenará con setPanels)
     */
    private void createMainLayout() {
        // El layout se configurará cuando se llame a setPanels()
    }

    /**
     * Crea la barra de menú
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();

        // Menú Archivo
        fileMenu = new JMenu("Archivo");
        fileMenu.setMnemonic('A');

        connectItem = new JMenuItem("Conectar...");
        connectItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));

        disconnectItem = new JMenuItem("Desconectar");
        disconnectItem.setEnabled(false);

        exitItem = new JMenuItem("Salir");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));

        fileMenu.add(connectItem);
        fileMenu.add(disconnectItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Menú Edición
        editMenu = new JMenu("Edición");
        editMenu.setMnemonic('E');

        clearItem = new JMenuItem("Limpiar Canvas");
        clearItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));

        editMenu.add(clearItem);

        // Menú Ver
        viewMenu = new JMenu("Ver");
        viewMenu.setMnemonic('V');

        toggleGridItem = new JCheckBoxMenuItem("Mostrar Grid", true);
        toggleGridItem.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));

        viewMenu.add(toggleGridItem);

        // Menú Ayuda
        helpMenu = new JMenu("Ayuda");
        helpMenu.setMnemonic('H');

        aboutItem = new JMenuItem("Acerca de...");
        aboutItem.setAccelerator(KeyStroke.getKeyStroke("F1"));

        helpMenu.add(aboutItem);

        // Agregar menús a la barra
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Crea la barra de estado
     */
    private void createStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        statusLabel = new JLabel("Desconectado");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        userLabel = new JLabel("Usuario: " + username);
        userLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(userLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Maneja el cierre de la ventana
     */
    private void handleWindowClosing() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que quieres salir?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Muestra el diálogo de conexión
     */
    public ConnectionDialog showConnectionDialog() {
        ConnectionDialog dialog = new ConnectionDialog(this);
        dialog.setVisible(true);
        return dialog;
    }

    /**
     * Muestra el diálogo "Acerca de"
     */
    public void showAboutDialog() {
        String message = "Pixel Art Colaborativo\n\n" +
                "Versión 1.0\n\n" +
                "Una aplicación de dibujo colaborativo\n" +
                "en tiempo real con Java Swing.\n\n" +
                "© 2024 - Equipo de Desarrollo";

        JOptionPane.showMessageDialog(
                this,
                message,
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Actualiza el estado de la conexión en la barra de estado
     */
    public void updateConnectionStatus(boolean connected, String message) {
        if (connected) {
            statusLabel.setText("🟢 Conectado - " + message);
            connectItem.setEnabled(false);
            disconnectItem.setEnabled(true);
        } else {
            statusLabel.setText("🔴 Desconectado - " + message);
            connectItem.setEnabled(true);
            disconnectItem.setEnabled(false);
        }
    }

    /**
     * Actualiza el nombre de usuario en la barra de estado
     */
    public void updateUsername(String username) {
        this.username = username;
        userLabel.setText("👤 Usuario: " + username);
    }

    /**
     * Establece los paneles principales
     */
    public void setPanels(ToolboxPanel toolbox, CanvasPanel canvas,
                          ChatPanel chat, UserListPanel userList) {
        this.toolboxPanel = toolbox;
        this.canvasPanel = canvas;
        this.chatPanel = chat;
        this.userListPanel = userList;

        // Panel izquierdo (Toolbox)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(toolbox, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Panel central (Canvas)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Wrapper con scroll para el canvas
        JPanel canvasWrapper = new JPanel();
        canvasWrapper.setBackground(Color.DARK_GRAY);
        canvasWrapper.add(canvas);

        JScrollPane canvasScrollPane = new JScrollPane(canvasWrapper);
        canvasScrollPane.setBackground(Color.DARK_GRAY);
        centerPanel.add(canvasScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Panel derecho (Chat y UserList)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.add(chat);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(userList);

        add(rightPanel, BorderLayout.EAST);

        // Refrescar el layout
        revalidate();
        repaint();
    }

    // ==================== GETTERS ====================

    public ToolboxPanel getToolboxPanel() { return toolboxPanel; }
    public CanvasPanel getCanvasPanel() { return canvasPanel; }
    public ChatPanel getChatPanel() { return chatPanel; }
    public UserListPanel getUserListPanel() { return userListPanel; }

    public JMenuItem getConnectItem() { return connectItem; }
    public JMenuItem getDisconnectItem() { return disconnectItem; }
    public JMenuItem getExitItem() { return exitItem; }
    public JMenuItem getClearItem() { return clearItem; }
    public JMenuItem getToggleGridItem() { return toggleGridItem; }
    public JMenuItem getAboutItem() { return aboutItem; }

    /**
     * Diálogo de Conexión
     */
    public static class ConnectionDialog extends JDialog {
        private JTextField serverField;
        private JTextField portField;
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JButton loginButton;
        private JButton registerButton;
        private JButton cancelButton;

        private boolean accepted = false;

        public ConnectionDialog(Frame parent) {
            super(parent, "Conectar al Servidor", true);
            setupDialog();
        }

        private void setupDialog() {
            setSize(350, 250);
            setLocationRelativeTo(getParent());
            setLayout(new BorderLayout(10, 10));

            // Panel de campos
            JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
            fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            fieldsPanel.add(new JLabel("Servidor:"));
            serverField = new JTextField("localhost");
            fieldsPanel.add(serverField);

            fieldsPanel.add(new JLabel("Puerto:"));
            portField = new JTextField("12345");
            fieldsPanel.add(portField);

            fieldsPanel.add(new JLabel("Usuario:"));
            usernameField = new JTextField();
            fieldsPanel.add(usernameField);

            fieldsPanel.add(new JLabel("Contraseña:"));
            passwordField = new JPasswordField();
            fieldsPanel.add(passwordField);

            add(fieldsPanel, BorderLayout.CENTER);

            // Panel de botones
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            loginButton = new JButton("Login");
            loginButton.addActionListener(e -> {
                accepted = true;
                setVisible(false);
            });

            registerButton = new JButton("Registrar");
            registerButton.addActionListener(e -> {
                accepted = true;
                setVisible(false);
            });

            cancelButton = new JButton("Cancelar");
            cancelButton.addActionListener(e -> setVisible(false));

            buttonsPanel.add(loginButton);
            buttonsPanel.add(registerButton);
            buttonsPanel.add(cancelButton);

            add(buttonsPanel, BorderLayout.SOUTH);
        }

        public boolean isAccepted() { return accepted; }
        public String getServer() { return serverField.getText(); }
        public int getPort() { return Integer.parseInt(portField.getText()); }
        public String getUsername() { return usernameField.getText(); }
        public String getPassword() { return new String(passwordField.getPassword()); }
    }
}