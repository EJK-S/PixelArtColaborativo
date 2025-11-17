package com.equipo.pixelart.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

   
    private JMenuItem connectItem;
    private JMenuItem disconnectItem;
    private JMenuItem exitItem;
    private JMenuItem clearItem;
    private JMenuItem toggleGridItem;
    private JMenuItem aboutItem;

   
    private JLabel statusLabel;
    private JLabel userLabel;

    private String username;

    public MainFrame() {
        super("Pixel Art Colaborativo");
        username = "Invitado";
        setupFrame();
    }

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(5, 5));
       
        createMenuBar();
      
        createStatusBar();
       
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        
        JMenu fileMenu = new JMenu("Archivo");
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

        
        JMenu editMenu = new JMenu("Edición");
        editMenu.setMnemonic('E');

        clearItem = new JMenuItem("Limpiar Canvas");
        clearItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));

        editMenu.add(clearItem);

        
        JMenu viewMenu = new JMenu("Ver");
        viewMenu.setMnemonic('V');

        toggleGridItem = new JCheckBoxMenuItem("Mostrar Grid", true);
        toggleGridItem.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));

        viewMenu.add(toggleGridItem);

        
        JMenu helpMenu = new JMenu("Ayuda");
        helpMenu.setMnemonic('H');

        aboutItem = new JMenuItem("Acerca de...");
        aboutItem.setAccelerator(KeyStroke.getKeyStroke("F1"));

        helpMenu.add(aboutItem);

       
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        statusLabel = new JLabel("Desconectado");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        userLabel = new JLabel("Usuario: " + username);
        userLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(userLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

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

    public ConnectionDialog showConnectionDialog() {
        ConnectionDialog dialog = new ConnectionDialog(this);
        dialog.setVisible(true);
        return dialog;
    }

    public void showAboutDialog() {
        String message = """
                Pixel Art Colaborativo
                
                Versión 1.0
                
                Una aplicación de dibujo colaborativo
                en tiempo real con Java Swing.
                
                © 2024 - Equipo de Desarrollo""";

        JOptionPane.showMessageDialog(
                this,
                message,
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

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

    public void updateUsername(String username) {
        this.username = username;
        userLabel.setText("👤 Usuario: " + username);
    }

    public void setPanels(ToolboxPanel toolbox, CanvasPanel canvas,
                          ChatPanel chat, UserListPanel userList) {

        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(toolbox, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        
        JPanel canvasWrapper = new JPanel();
        canvasWrapper.setBackground(Color.DARK_GRAY);
        canvasWrapper.add(canvas);

        JScrollPane canvasScrollPane = new JScrollPane(canvasWrapper);
        canvasScrollPane.setBackground(Color.DARK_GRAY);
        centerPanel.add(canvasScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.add(chat);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(userList);

        add(rightPanel, BorderLayout.EAST);

        
        revalidate();
        repaint();
    }


    public JMenuItem getConnectItem() { return connectItem; }
    public JMenuItem getDisconnectItem() { return disconnectItem; }
    public JMenuItem getExitItem() { return exitItem; }
    public JMenuItem getClearItem() { return clearItem; }
    public JMenuItem getToggleGridItem() { return toggleGridItem; }
    public JMenuItem getAboutItem() { return aboutItem; }

    public static class ConnectionDialog extends JDialog {
        private static final long serialVersionUID = 1L;

        private JTextField serverField;
        private JTextField portField;
      
        private JPanel loginPanel;
        private JTextField loginUsernameField;
        private JPasswordField loginPasswordField;
    
        private JPanel registerPanel;
        private JTextField registerUsernameField;
        private JPasswordField registerPasswordField;
        private JPasswordField registerConfirmPasswordField;

        private JButton loginButton;
        private JButton goToRegisterButton;
        private JButton registerButton;
        private JButton backToLoginButton;
        private JLabel statusLabel;

        private boolean accepted = false;
        private boolean isRegister = false;

        public ConnectionDialog(Frame parent) {
            super(parent, "Pixel Art Colaborativo", true);

            
            serverField = new JTextField("localhost");
            serverField.setFont(new Font("Arial", Font.PLAIN, 12));
            portField = new JTextField("12345");
            portField.setFont(new Font("Arial", Font.PLAIN, 12));

            
            statusLabel = new JLabel(" ", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            statusLabel.setForeground(Color.BLUE);
            statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            createLoginPanel();
            createRegisterPanel();
           
            setupDialog();
        }

        private void setupDialog() {
            setSize(500, 550);
            setLocationRelativeTo(getParent());
            setResizable(false);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            JLabel titleLabel = new JLabel("Pixel Art Colaborativo", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(new Color(100, 149, 237));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            final JPanel centerContainer = new JPanel(new CardLayout());

            centerContainer.add(loginPanel, "LOGIN");
            centerContainer.add(registerPanel, "REGISTER");

            mainPanel.add(centerContainer, BorderLayout.CENTER);
            mainPanel.add(statusLabel, BorderLayout.SOUTH);

            add(mainPanel);

            goToRegisterButton.addActionListener(e -> {
                CardLayout cl = (CardLayout) centerContainer.getLayout();
                cl.show(centerContainer, "REGISTER");
                statusLabel.setText("Crea tu cuenta para empezar a dibujar");
                statusLabel.setForeground(new Color(60, 179, 113));
                registerUsernameField.requestFocus();
            });

            backToLoginButton.addActionListener(e -> {
                CardLayout cl = (CardLayout) centerContainer.getLayout();
                cl.show(centerContainer, "LOGIN");
                statusLabel.setText(" ");
                statusLabel.setForeground(Color.BLUE);
                loginUsernameField.requestFocus();
            });
        }

        private void createLoginPanel() {
            loginPanel = new JPanel();
            loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

            JPanel serverSection = new JPanel(new BorderLayout(5, 5));
            serverSection.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(100, 149, 237), 2),
                    "Configuracion del Servidor",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12)
            ));

            JPanel serverFields = new JPanel(new GridLayout(2, 2, 10, 10));
            serverFields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            serverFields.add(new JLabel("Direccion IP:"));
            serverFields.add(serverField);

            serverFields.add(new JLabel("Puerto:"));
            serverFields.add(portField);

            serverSection.add(serverFields, BorderLayout.CENTER);

            JPanel helpPanel = new JPanel(new BorderLayout());
            helpPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            JLabel helpLabel = new JLabel("<html><small><b>Ejemplos:</b><br>" +
                    "• <b>localhost</b> (mismo equipo)<br>" +
                    "• <b>192.168.1.100</b> (red local)<br>" +
                    "Pide la IP al host del servidor (comando: network)</small></html>");
            helpLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            helpLabel.setForeground(new Color(100, 100, 100));
            helpPanel.add(helpLabel, BorderLayout.CENTER);
            serverSection.add(helpPanel, BorderLayout.SOUTH);

            loginPanel.add(serverSection);
            loginPanel.add(Box.createVerticalStrut(15));

            JPanel loginSection = new JPanel(new BorderLayout(5, 5));
            loginSection.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(100, 149, 237), 2),
                    "Iniciar Sesion",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12)
            ));

            JPanel loginFields = new JPanel(new GridLayout(2, 2, 10, 10));
            loginFields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            loginFields.add(new JLabel("Usuario:"));
            loginUsernameField = new JTextField();
            loginUsernameField.setFont(new Font("Arial", Font.PLAIN, 14));
            loginFields.add(loginUsernameField);

            loginFields.add(new JLabel("Contrasena:"));
            loginPasswordField = new JPasswordField();
            loginPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
            loginPasswordField.addActionListener(e -> handleLogin());
            loginFields.add(loginPasswordField);

            loginSection.add(loginFields, BorderLayout.CENTER);
            loginPanel.add(loginSection);
            loginPanel.add(Box.createVerticalStrut(20));

            JPanel loginButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

            loginButton = new JButton("Iniciar Sesion");
            loginButton.setFont(new Font("Arial", Font.BOLD, 13));
            loginButton.setBackground(new Color(100, 149, 237));
            loginButton.setForeground(Color.BLACK);
            loginButton.setFocusPainted(false);
            loginButton.setPreferredSize(new Dimension(180, 40));
            loginButton.addActionListener(e -> handleLogin());
            loginButtonsPanel.add(loginButton);

            goToRegisterButton = new JButton("Crear Cuenta");
            goToRegisterButton.setFont(new Font("Arial", Font.PLAIN, 13));
            goToRegisterButton.setBackground(new Color(60, 179, 113));
            goToRegisterButton.setForeground(Color.BLACK);
            goToRegisterButton.setFocusPainted(false);
            goToRegisterButton.setPreferredSize(new Dimension(180, 40));
            loginButtonsPanel.add(goToRegisterButton);

            loginPanel.add(loginButtonsPanel);
            loginPanel.add(Box.createVerticalStrut(10));

            JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton cancelButton = new JButton("Cancelar");
            cancelButton.setFont(new Font("Arial", Font.PLAIN, 11));
            cancelButton.setFocusPainted(false);
            cancelButton.setPreferredSize(new Dimension(120, 30));
            cancelButton.addActionListener(e -> dispose());
            cancelPanel.add(cancelButton);

            loginPanel.add(cancelPanel);
        }

        private void createRegisterPanel() {
            registerPanel = new JPanel();
            registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));

            JPanel registerSection = new JPanel(new BorderLayout(5, 5));
            registerSection.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
                    "Registro de Nuevo Usuario",
                    0, 0,
                    new Font("Arial", Font.BOLD, 12)
            ));

            JPanel registerFields = new JPanel(new GridLayout(3, 2, 10, 15));
            registerFields.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

            JLabel regUserLabel = new JLabel("Usuario:");
            regUserLabel.setFont(new Font("Arial", Font.BOLD, 12));
            registerFields.add(regUserLabel);
            registerUsernameField = new JTextField(12);
            registerUsernameField.setFont(new Font("Arial", Font.PLAIN, 14));
            registerFields.add(registerUsernameField);

            JLabel regPassLabel = new JLabel("Contraseña:");
            regPassLabel.setFont(new Font("Arial", Font.BOLD, 12));
            registerFields.add(regPassLabel);
            registerPasswordField = new JPasswordField(12);
            registerPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
            registerFields.add(registerPasswordField);

            JLabel regConfirmLabel = new JLabel("Confirmar:");
            regConfirmLabel.setFont(new Font("Arial", Font.BOLD, 12));
            registerFields.add(regConfirmLabel);
            registerConfirmPasswordField = new JPasswordField(12);
            registerConfirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
            registerConfirmPasswordField.addActionListener(e -> handleRegister());
            registerFields.add(registerConfirmPasswordField);

            registerSection.add(registerFields, BorderLayout.CENTER);

            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JLabel infoLabel = new JLabel("<html><center><b>Requisitos:</b><br>" +
                    "Minimo 3 caracteres<br>" +
                    "Las contrasenas deben coincidir</center></html>");
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            infoLabel.setForeground(new Color(100, 100, 100));
            infoPanel.add(infoLabel, BorderLayout.CENTER);
            registerSection.add(infoPanel, BorderLayout.SOUTH);

            registerPanel.add(registerSection);
            registerPanel.add(Box.createVerticalStrut(20));

            JPanel registerButtons = new JPanel(new GridLayout(2, 1, 0, 10));

            registerButton = new JButton("Registrar y Conectar");
            registerButton.setFont(new Font("Arial", Font.BOLD, 14));
            registerButton.setBackground(new Color(60, 179, 113));
            registerButton.setForeground(Color.WHITE);
            registerButton.setFocusPainted(false);
            registerButton.addActionListener(e -> handleRegister());

            backToLoginButton = new JButton("Volver a Iniciar Sesion");
            backToLoginButton.setFont(new Font("Arial", Font.PLAIN, 12));
            backToLoginButton.setFocusPainted(false);

            registerButtons.add(registerButton);
            registerButtons.add(backToLoginButton);

            registerPanel.add(registerButtons);
        }

        private void handleLogin() {
            String user = loginUsernameField.getText().trim();
            String pass = new String(loginPasswordField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                statusLabel.setText("*** Por favor completa usuario y contrasena");
                statusLabel.setForeground(Color.RED);
                return;
            }

            isRegister = false;
            accepted = true;
            statusLabel.setText("Conectando...");
            statusLabel.setForeground(new Color(0, 128, 0));
            setVisible(false);
        }

        private void handleRegister() {
            String user = registerUsernameField.getText().trim();
            String pass = new String(registerPasswordField.getPassword());
            String confirm = new String(registerConfirmPasswordField.getPassword());

            if (user.isEmpty()) {
                statusLabel.setText("*** El campo usuario esta vacio");
                statusLabel.setForeground(Color.RED);
                registerUsernameField.requestFocus();
                return;
            }

            if (pass.isEmpty()) {
                statusLabel.setText("*** El campo contrasena esta vacio");
                statusLabel.setForeground(Color.RED);
                registerPasswordField.requestFocus();
                return;
            }

            if (confirm.isEmpty()) {
                statusLabel.setText("*** Debes confirmar tu contrasena");
                statusLabel.setForeground(Color.RED);
                registerConfirmPasswordField.requestFocus();
                return;
            }

            if (pass.length() < 3) {
                statusLabel.setText("*** La contrasena debe tener al menos 3 caracteres");
                statusLabel.setForeground(Color.RED);
                registerPasswordField.requestFocus();
                return;
            }

            if (!pass.equals(confirm)) {
                statusLabel.setText("*** Las contrasenas no coinciden");
                statusLabel.setForeground(Color.RED);
                registerConfirmPasswordField.setText("");
                registerConfirmPasswordField.requestFocus();
                return;
            }

            isRegister = true;
            accepted = true;

            // Cerrar el diálogo para que el ClientApp procese el registro
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