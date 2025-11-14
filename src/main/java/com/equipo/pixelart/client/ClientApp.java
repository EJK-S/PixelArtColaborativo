package com.equipo.pixelart.client;

import com.equipo.pixelart.client.controller.CanvasController;
import com.equipo.pixelart.client.controller.NetworkController;
import com.equipo.pixelart.client.controller.ToolboxController;
import com.equipo.pixelart.client.model.CanvasModel;
import com.equipo.pixelart.client.view.*;

import javax.swing.*;

/**
 * Aplicación Principal del Cliente
 * Punto de entrada y orquestador de todos los componentes
 */
public class ClientApp {

    // Componentes principales
    private MainFrame mainFrame;

    // Model
    private CanvasModel canvasModel;

    // Views
    private ToolboxPanel toolboxPanel;
    private CanvasPanel canvasPanel;
    private ChatPanel chatPanel;
    private UserListPanel userListPanel;

    // Controllers
    private NetworkController networkController;
    private CanvasController canvasController;
    private ToolboxController toolboxController;

    /**
     * Constructor
     */
    public ClientApp() {
        initializeComponents();
        setupControllers();
        setupMainFrame();
        setupEventHandlers();
    }

    /**
     * Inicializa todos los componentes
     */
    private void initializeComponents() {
        System.out.println("Inicializando componentes...");

        // Crear modelo
        canvasModel = new CanvasModel(48, 48); // Canvas de 48x48 píxeles

        // Crear vistas
        canvasPanel = new CanvasPanel(canvasModel, 12); // 12px por píxel
        toolboxPanel = new ToolboxPanel();
        chatPanel = new ChatPanel();
        userListPanel = new UserListPanel();

        // Crear controladores
        networkController = new NetworkController();

        System.out.println("Componentes inicializados correctamente");
    }

    /**
     * Configura las relaciones entre controladores
     */
    private void setupControllers() {
        System.out.println("Configurando controladores...");

        // Crear CanvasController con sus dependencias
        canvasController = new CanvasController(canvasModel, canvasPanel, networkController);

        // Crear ToolboxController
        toolboxController = new ToolboxController(toolboxPanel, canvasController);

        // Conectar NetworkController con CanvasController
        networkController.setCanvasController(canvasController);

        System.out.println("Controladores configurados correctamente");
    }

    /**
     * Configura la ventana principal
     */
    private void setupMainFrame() {
        System.out.println("Configurando ventana principal...");

        // Crear MainFrame
        mainFrame = new MainFrame();

        // Agregar paneles al MainFrame
        mainFrame.setPanels(toolboxPanel, canvasPanel, chatPanel, userListPanel);

        System.out.println("Ventana principal configurada");
    }

    /**
     * Configura todos los manejadores de eventos
     */
    private void setupEventHandlers() {
        System.out.println("Configurando manejadores de eventos...");

        // ========== EVENTOS DE MENÚ ==========

        // Menú: Conectar
        mainFrame.getConnectItem().addActionListener(e -> handleConnect());

        // Menú: Desconectar
        mainFrame.getDisconnectItem().addActionListener(e -> handleDisconnect());

        // Menú: Salir
        mainFrame.getExitItem().addActionListener(e -> System.exit(0));

        // Menú: Limpiar Canvas
        mainFrame.getClearItem().addActionListener(e -> canvasController.clearCanvas());

        // Menú: Toggle Grid
        mainFrame.getToggleGridItem().addActionListener(e -> {
            boolean show = mainFrame.getToggleGridItem().isSelected();
            canvasPanel.setShowGrid(show);
        });

        // Menú: Acerca de
        mainFrame.getAboutItem().addActionListener(e -> mainFrame.showAboutDialog());

        // ========== EVENTOS DE CHAT ==========

        // Chat: Enviar mensaje
        chatPanel.addSendListener(e -> handleSendMessage());

        // ========== EVENTOS DE RED ==========

        // Listener de eventos de red
        networkController.addNetworkListener(new NetworkController.NetworkListener() {

            @Override
            public void onConnectionStatusChanged(boolean connected, String message) {
                mainFrame.updateConnectionStatus(connected, message);

                if (connected) {
                    chatPanel.addSystemMessage("Conectado al servidor");
                    chatPanel.setEnabled(true);
                } else {
                    chatPanel.addSystemMessage("Desconectado del servidor");
                    chatPanel.setEnabled(false);
                    userListPanel.clearUsers();
                }
            }

            @Override
            public void onAuthResult(boolean success, String message) {
                if (success) {
                    String username = networkController.getUsername();
                    mainFrame.updateUsername(username);
                    chatPanel.addSystemMessage("*** Autenticacion exitosa! Bienvenido " + username);
                    JOptionPane.showMessageDialog(mainFrame,
                            "Bienvenido " + username + "!\n\n" +
                                    "Ahora puedes empezar a dibujar en el lienzo colaborativo.",
                            "Login Exitoso",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    chatPanel.addSystemMessage("*** Error de autenticacion: " + message);

                    String errorMsg = message;
                    boolean shouldRetry = true;
                    boolean wasRegistration = message.toLowerCase().contains("ya existe") ||
                            message.toLowerCase().contains("usuario ya registrado");

                    if (message.contains("incorrectos") || message.contains("no existe")) {
                        errorMsg = "Usuario o contrasena incorrectos.\n\n" +
                                "Si no tienes cuenta, haz clic en 'Crear Nueva Cuenta'.";
                    } else if (wasRegistration) {
                        // El servidor rechaza porque ya existe = registro exitoso
                        errorMsg = "Usuario registrado exitosamente!\n\n" +
                                "Tu cuenta ha sido creada.\n" +
                                "Ahora puedes iniciar sesion con tus credenciales.";
                        shouldRetry = false;

                        chatPanel.addSystemMessage("*** Usuario registrado correctamente!");

                        // Desconectar después de registro exitoso
                        networkController.disconnect();

                        // Volver al diálogo de login
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Usuario registrado exitosamente!\n\n" +
                                            "Ahora puedes iniciar sesion con tu usuario y contrasena.",
                                    "Registro Exitoso",
                                    JOptionPane.INFORMATION_MESSAGE);
                            handleConnect(); // Volver a mostrar el diálogo de login
                        });
                        return;
                    } else if (message.contains("ya conectado")) {
                        errorMsg = "Este usuario ya esta conectado.\n\n" +
                                "Por favor cierra la otra sesion primero.";
                    }

                    JOptionPane.showMessageDialog(mainFrame,
                            errorMsg,
                            "Autenticacion Fallida",
                            JOptionPane.ERROR_MESSAGE);

                    // Desconectar y permitir reintentar
                    networkController.disconnect();

                    if (shouldRetry) {
                        // Ofrecer reconectar
                        SwingUtilities.invokeLater(() -> {
                            int retry = JOptionPane.showConfirmDialog(mainFrame,
                                    "Quieres intentar nuevamente?",
                                    "Reintentar Conexion",
                                    JOptionPane.YES_NO_OPTION);

                            if (retry == JOptionPane.YES_OPTION) {
                                handleConnect();
                            }
                        });
                    }
                }
            }

            @Override
            public void onChatMessage(String user, String message) {
                chatPanel.addMessage(user, message);
            }

            @Override
            public void onUserJoined(String user) {
                userListPanel.addUser(user);
                chatPanel.addSystemMessage(user + " se ha unido");
            }

            @Override
            public void onUserLeft(String user) {
                userListPanel.removeUser(user);
                chatPanel.addSystemMessage(user + " se ha desconectado");
            }

            @Override
            public void onUserListReceived(String[] users) {
                userListPanel.setUsers(users);
            }

            @Override
            public void onError(String message) {
                chatPanel.addSystemMessage("ERROR: " + message);
                JOptionPane.showMessageDialog(mainFrame,
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        System.out.println("Manejadores de eventos configurados");
    }

    /**
     * Maneja la conexión al servidor
     */
    private void handleConnect() {
        MainFrame.ConnectionDialog dialog = mainFrame.showConnectionDialog();

        if (!dialog.isAccepted()) {
            return;
        }

        String server = dialog.getServer();
        int port = dialog.getPort();
        String username = dialog.getUsername();
        String password = dialog.getPassword();
        boolean isRegister = dialog.isRegister();

        // Si es registro, solo registrar y volver al diálogo de login
        if (isRegister) {
            handleRegistration(server, port, username, password);
            return;
        }

        // Si es login, conectar y autenticar
        handleLogin(server, port, username, password);
    }

    /**
     * Maneja solo el registro de usuario (sin conectar)
     */
    private void handleRegistration(String server, int port, String username, String password) {
        // Validar campos
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Por favor ingresa usuario y contraseña",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
            handleConnect(); // Volver a mostrar el diálogo
            return;
        }

        // Validar dirección del servidor
        if (server.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Por favor ingresa la dirección del servidor",
                    "Servidor No Especificado",
                    JOptionPane.WARNING_MESSAGE);
            handleConnect(); // Volver a mostrar el diálogo
            return;
        }

        chatPanel.addSystemMessage("===========================================");
        chatPanel.addSystemMessage("Registrando nuevo usuario...");
        chatPanel.addSystemMessage("   Servidor: " + server);
        chatPanel.addSystemMessage("   Puerto: " + port);
        chatPanel.addSystemMessage("   Usuario: " + username);
        chatPanel.addSystemMessage("===========================================");

        // Conectar temporalmente para registrar
        boolean connected = networkController.connect(server, port);

        if (!connected) {
            String errorMsg = """
                No se pudo conectar al servidor.
                
                Verifica:
                ✓ El servidor está ejecutándose (ServerApp)
                ✓ La IP y puerto son correctos
                ✓ Estás en la misma red (para LAN)
                ✓ El firewall no bloquea el puerto
                
                Ejemplos de dirección:
                • localhost (mismo equipo)
                • 127.0.0.1 (mismo equipo)
                • 192.168.1.100 (red local)
                
                El servidor debe mostrar su IP al iniciar.
                Usa el comando 'network' en el servidor.""";

            JOptionPane.showMessageDialog(mainFrame,
                    errorMsg,
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            chatPanel.addSystemMessage("*** Error: No se pudo conectar al servidor");
            handleConnect(); // Volver a mostrar el diálogo
            return;
        }

        // Esperar un momento
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // Ignorar
        }

        // Enviar registro
        chatPanel.addSystemMessage("Conectado! Registrando usuario '" + username + "'...");
        networkController.register(username, password);

        // Esperar respuesta del servidor (el listener de NetworkController manejará el resultado)
        // El usuario será notificado y luego se desconectará
    }

    /**
     * Maneja el login de usuario
     */
    private void handleLogin(String server, int port, String username, String password) {
        // Validar campos
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Por favor ingresa usuario y contraseña",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar dirección del servidor
        if (server.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Por favor ingresa la dirección del servidor",
                    "Servidor No Especificado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar mensaje de conexión
        chatPanel.addSystemMessage("===========================================");
        chatPanel.addSystemMessage("Conectando al servidor...");
        chatPanel.addSystemMessage("   Servidor: " + server);
        chatPanel.addSystemMessage("   Puerto: " + port);
        chatPanel.addSystemMessage("===========================================");

        // Intentar conectar
        boolean connected = networkController.connect(server, port);

        if (!connected) {
            String errorMsg = """
                No se pudo conectar al servidor.
                
                Verifica:
                ✓ El servidor está ejecutándose (ServerApp)
                ✓ La IP y puerto son correctos
                ✓ Estás en la misma red (para LAN)
                ✓ El firewall no bloquea el puerto
                
                Ejemplos de dirección:
                • localhost (mismo equipo)
                • 127.0.0.1 (mismo equipo)
                • 192.168.1.100 (red local)
                
                El servidor debe mostrar su IP al iniciar.
                Usa el comando 'network' en el servidor.""";

            JOptionPane.showMessageDialog(mainFrame,
                    errorMsg,
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            chatPanel.addSystemMessage("*** Error: No se pudo conectar al servidor");
            chatPanel.addSystemMessage("Verifica la IP y que el servidor esté corriendo");
            return;
        }

        // Esperar un momento para que la conexión se establezca
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // Ignorar
        }

        // Intentar autenticar
        chatPanel.addSystemMessage("Conectado! Autenticando como '" + username + "'...");
        networkController.login(username, password);
        chatPanel.addSystemMessage("Iniciando sesion...");
    }

    /**
     * Maneja la desconexión del servidor
     */
    private void handleDisconnect() {
        if (networkController.isConnected()) {
            int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "¿Estás seguro de que quieres desconectar?",
                    "Confirmar Desconexión",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                networkController.disconnect();
            }
        }
    }

    /**
     * Maneja el envío de mensajes de chat
     */
    private void handleSendMessage() {
        String message = chatPanel.getMessageAndClear();

        if (!message.isEmpty()) {
            if (networkController.isConnected()) {
                networkController.sendChatMessage(message);
            } else {
                chatPanel.addSystemMessage("No estás conectado al servidor");
            }
        }

        chatPanel.focusMessageField();
    }

    /**
     * Inicia la aplicación
     */
    public void start() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(true);
            System.out.println("Aplicación iniciada correctamente");

            // Mensaje de bienvenida
            chatPanel.addSystemMessage("=== Bienvenido a Pixel Art Colaborativo ===");
            chatPanel.addSystemMessage("Conecta al servidor usando el menú Archivo > Conectar");

            // Mostrar diálogo de conexión automáticamente
            handleConnect();
        });
    }

    /**
     * Punto de entrada de la aplicación
     */
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel: " + e.getMessage());
        }

        System.out.println("=== PIXEL ART COLABORATIVO - CLIENTE ===");
        System.out.println("Inicializando aplicación...");

        // Crear y arrancar la aplicación
        ClientApp app = new ClientApp();
        app.start();
    }
}