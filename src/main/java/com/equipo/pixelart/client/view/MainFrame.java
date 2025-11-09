package com.equipo.pixelart.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana Principal - Contiene toda la interfaz de usuario del cliente
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
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
     * Diálogo de Conexión Mejorado con dos pantallas separadas
     */
    public static class ConnectionDialog extends JDialog {
    	private static final long serialVersionUID = 1L;
        private JTextField serverField;
        private JTextField portField;

        // Panel de Login
        private JPanel loginPanel;
        private JTextField loginUsernameField;
        private JPasswordField loginPasswordField;

        // Panel de Registro
        private JPanel registerPanel;
        private JTextField registerUsernameField;
        private JPasswordField registerPasswordField;
        private JPasswordField registerConfirmPasswordField;

        private JButton loginButton;
        private JButton goToRegisterButton;
        private JButton registerButton;
        private JButton backToLoginButton;
        private JButton cancelButton;
        private JLabel statusLabel;

        private boolean accepted = false;
        private boolean isRegister = false;

        public ConnectionDialog(Frame parent) {
            super(parent, "Pixel Art Colaborativo", true);
            setupDialog();
        }

        private void setupDialog() {
            setSize(450, 450);
            setLocationRelativeTo(getParent());
            setResizable(false);

            // Panel principal
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            // Título
            JLabel titleLabel = new JLabel("🎨 Pixel Art Colaborativo", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(new Color(100, 149, 237));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            // Panel central que contendrá ambas vistas (CardLayout para cambiar entre ellas)
            final JPanel centerContainer = new JPanel(new CardLayout());

            // Crear ambos paneles
            createLoginPanel();
            createRegisterPanel();

            // Agregar ambos paneles al contenedor con nombres
            centerContainer.add(loginPanel, "LOGIN");
            centerContainer.add(registerPanel, "REGISTER");

            mainPanel.add(centerContainer, BorderLayout.CENTER);

            // Status label
            statusLabel = new JLabel(" ", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            statusLabel.setForeground(Color.BLUE);
            statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            mainPanel.add(statusLabel, BorderLayout.SOUTH);

            add(mainPanel);

            // Actualizar los botones para usar CardLayout
          

            goToRegisterButton.addActionListener(e -> showRegisterPanel());
            backToLoginButton.addActionListener(e -> showLoginPanel());

        }

        private void createLoginPanel() {
            loginPanel = new JPanel();
            loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

            // Sección de servidor
            JPanel serverSection = new JPanel(new BorderLayout(5, 5));
            serverSection.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(100, 149, 237), 2),
                    "🖥️ Servidor",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12)
            ));

            JPanel serverFields = new JPanel(new GridLayout(2, 2, 10, 10));
            serverFields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            serverFields.add(new JLabel("Dirección:"));
            serverField = new JTextField("localhost");
            serverField.setFont(new Font("Arial", Font.PLAIN, 12));
            serverFields.add(serverField);

            serverFields.add(new JLabel("Puerto:"));
            portField = new JTextField("12345");
            portField.setFont(new Font("Arial", Font.PLAIN, 12));
            serverFields.add(portField);

            serverSection.add(serverFields, BorderLayout.CENTER);
            loginPanel.add(serverSection);
            loginPanel.add(Box.createVerticalStrut(15));

            // Sección de login
            JPanel loginSection = new JPanel(new BorderLayout(5, 5));
            loginSection.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(100, 149, 237), 2),
                    "🔐 Iniciar Sesión",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12)
            ));

            JPanel loginFields = new JPanel(new GridLayout(2, 2, 10, 10));
            loginFields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            loginFields.add(new JLabel("Usuario:"));
            loginUsernameField = new JTextField();
            loginUsernameField.setFont(new Font("Arial", Font.PLAIN, 14));
            loginFields.add(loginUsernameField);

            loginFields.add(new JLabel("Contraseña:"));
            loginPasswordField = new JPasswordField();
            loginPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
            loginPasswordField.addActionListener(e -> handleLogin());
            loginFields.add(loginPasswordField);

            loginSection.add(loginFields, BorderLayout.CENTER);
            loginPanel.add(loginSection);
            loginPanel.add(Box.createVerticalStrut(15));

            // Botones
            JPanel loginButtons = new JPanel(new GridLayout(3, 1, 0, 10));

            loginButton = new JButton("🔑 Iniciar Sesión");
            loginButton.setFont(new Font("Arial", Font.BOLD, 14));
            loginButton.setBackground(new Color(100, 149, 237));
            loginButton.setForeground(Color.WHITE);
            loginButton.setFocusPainted(false);
            loginButton.addActionListener(e -> handleLogin());

            goToRegisterButton = new JButton("📝 Crear Nueva Cuenta");
            goToRegisterButton.setFont(new Font("Arial", Font.PLAIN, 13));
            goToRegisterButton.setBackground(new Color(60, 179, 113));
            goToRegisterButton.setForeground(Color.WHITE);
            goToRegisterButton.setFocusPainted(false);
            // El listener se agrega en setupDialog()

            cancelButton = new JButton("❌ Cancelar");
            cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
            cancelButton.setFocusPainted(false);
            cancelButton.addActionListener(e -> dispose());

            loginButtons.add(loginButton);
            loginButtons.add(goToRegisterButton);
            loginButtons.add(cancelButton);

            loginPanel.add(loginButtons);
        }

        private void createRegisterPanel() {
            registerPanel = new JPanel();
            registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));

            // Sección de registro
            JPanel registerSection = new JPanel(new BorderLayout(5, 5));
            registerSection.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
                    "📝 Registro de Nuevo Usuario",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12)
            ));

            JPanel registerFields = new JPanel(new GridLayout(3, 2, 10, 15));
            registerFields.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

            JLabel regUserLabel = new JLabel("Usuario:");
            regUserLabel.setFont(new Font("Arial", Font.BOLD, 12));
            registerFields.add(regUserLabel);
            registerUsernameField = new JTextField();
            registerUsernameField.setFont(new Font("Arial", Font.PLAIN, 14));
            registerFields.add(registerUsernameField);

            JLabel regPassLabel = new JLabel("Contraseña:");
            regPassLabel.setFont(new Font("Arial", Font.BOLD, 12));
            registerFields.add(regPassLabel);
            registerPasswordField = new JPasswordField();
            registerPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
            registerFields.add(registerPasswordField);

            JLabel regConfirmLabel = new JLabel("Confirmar:");
            regConfirmLabel.setFont(new Font("Arial", Font.BOLD, 12));
            registerFields.add(regConfirmLabel);
            registerConfirmPasswordField = new JPasswordField();
            registerConfirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
            registerConfirmPasswordField.addActionListener(e -> handleRegister());
            registerFields.add(registerConfirmPasswordField);

            registerSection.add(registerFields, BorderLayout.CENTER);

            // Info panel
            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel infoLabel = new JLabel("<html><center>💡 <b>Requisitos:</b><br>" +
                    "• Mínimo 3 caracteres<br>" +
                    "• Las contraseñas deben coincidir</center></html>");
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            infoLabel.setForeground(new Color(100, 100, 100));
            infoPanel.add(infoLabel, BorderLayout.CENTER);
            registerSection.add(infoPanel, BorderLayout.SOUTH);

            registerPanel.add(registerSection);
            registerPanel.add(Box.createVerticalStrut(20));

            // Botones de registro
            JPanel registerButtons = new JPanel(new GridLayout(2, 1, 0, 10));

            registerButton = new JButton("✅ Registrar y Conectar");
            registerButton.setFont(new Font("Arial", Font.BOLD, 14));
            registerButton.setBackground(new Color(60, 179, 113));
            registerButton.setForeground(Color.WHITE);
            registerButton.setFocusPainted(false);
            registerButton.addActionListener(e -> handleRegister());

            backToLoginButton = new JButton("← Volver a Iniciar Sesión");
            backToLoginButton.setFont(new Font("Arial", Font.PLAIN, 12));
            backToLoginButton.setFocusPainted(false);
            // El listener se agrega en setupDialog()

            registerButtons.add(registerButton);
            registerButtons.add(backToLoginButton);

            registerPanel.add(registerButtons);
        }

        private void showLoginPanel() {   	
            loginPanel.setVisible(true);
            registerPanel.setVisible(false);
            statusLabel.setText(" ");
            statusLabel.setForeground(Color.BLUE);
            loginUsernameField.requestFocus();
        }

        private void showRegisterPanel() {
            loginPanel.setVisible(false);
            registerPanel.setVisible(true);
            statusLabel.setText("💡 Crea tu cuenta para empezar a dibujar");
            statusLabel.setForeground(new Color(60, 179, 113));
            registerUsernameField.requestFocus();
        }

        private void handleLogin() {
            String user = loginUsernameField.getText().trim();
            String pass = new String(loginPasswordField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                statusLabel.setText("⚠️ Por favor completa usuario y contraseña");
                statusLabel.setForeground(Color.RED);
                return;
            }

            isRegister = false;
            accepted = true;
            statusLabel.setText("✓ Conectando...");
            statusLabel.setForeground(new Color(0, 128, 0));
            setVisible(false);
        }

        private void handleRegister() {
            String user = registerUsernameField.getText().trim();
            String pass = new String(registerPasswordField.getPassword());
            String confirm = new String(registerConfirmPasswordField.getPassword());

            if (user.isEmpty()) {
                statusLabel.setText("⚠️ El campo usuario está vacío");
                statusLabel.setForeground(Color.RED);
                registerUsernameField.requestFocus();
                return;
            }

            if (pass.isEmpty()) {
                statusLabel.setText("⚠️ El campo contraseña está vacío");
                statusLabel.setForeground(Color.RED);
                registerPasswordField.requestFocus();
                return;
            }

            if (confirm.isEmpty()) {
                statusLabel.setText("⚠️ Debes confirmar tu contraseña");
                statusLabel.setForeground(Color.RED);
                registerConfirmPasswordField.requestFocus();
                return;
            }

            if (pass.length() < 3) {
                statusLabel.setText("⚠️ La contraseña debe tener al menos 3 caracteres");
                statusLabel.setForeground(Color.RED);
                registerPasswordField.requestFocus();
                return;
            }

            if (!pass.equals(confirm)) {
                statusLabel.setText("⚠️ Las contraseñas no coinciden");
                statusLabel.setForeground(Color.RED);
                registerConfirmPasswordField.setText("");
                registerConfirmPasswordField.requestFocus();
                return;
            }

            isRegister = true;
            accepted = true;
            statusLabel.setText("✓ Registrando usuario...");
            statusLabel.setForeground(new Color(0, 128, 0));
            setVisible(false);
        }

        public boolean isAccepted() { return accepted; }
        public boolean isRegister() { return isRegister; }
        public String getServer() { return serverField.getText().trim(); }
        public int getPort() {
            try {
                return Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException e) {
                return 12345;
            }
        }
        public String getUsername() {
            return isRegister ? registerUsernameField.getText().trim() : loginUsernameField.getText().trim();
        }
        public String getPassword() {
            return isRegister ? new String(registerPasswordField.getPassword()) : new String(loginPasswordField.getPassword());
        }
    }
}