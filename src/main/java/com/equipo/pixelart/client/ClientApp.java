package com.equipo.pixelart.client;

import com.equipo.pixelart.client.controller.CanvasController;
import com.equipo.pixelart.client.controller.NetworkController;
import com.equipo.pixelart.client.controller.ToolboxController;
import com.equipo.pixelart.client.model.CanvasModel;
import com.equipo.pixelart.client.view.*;

import javax.swing.*;

/**
 * Aplicación Cliente compacta y optimizada.
 * Mantiene toda la funcionalidad en una sola clase pero organizada.
 */
public class ClientApp {

    private MainFrame mainFrame;

    private CanvasModel canvasModel;

    private ToolboxPanel toolboxPanel;
    private CanvasPanel canvasPanel;
    private ChatPanel chatPanel;
    private UserListPanel userListPanel;

    private NetworkController networkController;
    private CanvasController canvasController;
    private ToolboxController toolboxController;

    public ClientApp() {
        initComponents();
        initControllers();
        initMainFrame();
        initEvents();
    }

    // ============================================================
    //  1. Inicialización de componentes
    // ============================================================
    private void initComponents() {
        canvasModel = new CanvasModel(48, 48);
        canvasPanel = new CanvasPanel(canvasModel, 12);
        toolboxPanel = new ToolboxPanel();
        chatPanel = new ChatPanel();
        userListPanel = new UserListPanel();
        networkController = new NetworkController();
    }

    // ============================================================
    //  2. Inicialización de controladores
    // ============================================================
    private void initControllers() {
        canvasController = new CanvasController(canvasModel, canvasPanel, networkController);
        toolboxController = new ToolboxController(toolboxPanel, canvasController);
        networkController.setCanvasController(canvasController);
    }

    // ============================================================
    //  3. Configurar ventana principal
    // ============================================================
    private void initMainFrame() {
        mainFrame = new MainFrame();
        mainFrame.setPanels(toolboxPanel, canvasPanel, chatPanel, userListPanel);
    }

    // ============================================================
    //  4. Manejadores de eventos compactos
    // ============================================================
    private void initEvents() {
        mainFrame.getConnectItem().addActionListener(e -> handleConnect());
        mainFrame.getDisconnectItem().addActionListener(e -> handleDisconnect());
        mainFrame.getExitItem().addActionListener(e -> System.exit(0));

        mainFrame.getClearItem().addActionListener(e -> canvasController.clearCanvas());
        mainFrame.getToggleGridItem().addActionListener(e ->
                canvasPanel.setShowGrid(mainFrame.getToggleGridItem().isSelected()));
        mainFrame.getAboutItem().addActionListener(e -> mainFrame.showAboutDialog());

        chatPanel.addSendListener(e -> handleSendMessage());

        networkController.addNetworkListener(new NetworkController.NetworkListener() {

            public void onConnectionStatusChanged(boolean connected, String msg) {
                mainFrame.updateConnectionStatus(connected, msg);

                chatPanel.setEnabled(connected);
                chatPanel.addSystemMessage(connected ? "Conectado" : "Desconectado");

                if (!connected) userListPanel.clearUsers();
            }

            public void onAuthResult(boolean ok, String msg) {
                if (ok) handleAuthSuccess();
                else handleAuthFail(msg);
            }

            public void onChatMessage(String user, String txt) {
                chatPanel.addMessage(user, txt);
            }

            public void onUserJoined(String u) {
                userListPanel.addUser(u);
                chatPanel.addSystemMessage(u + " se ha unido");
            }

            public void onUserLeft(String u) {
                userListPanel.removeUser(u);
                chatPanel.addSystemMessage(u + " se ha desconectado");
            }

            public void onUserListReceived(String[] u) {
                userListPanel.setUsers(u);
            }

            public void onError(String m) {
                JOptionPane.showMessageDialog(mainFrame, m, "Error", JOptionPane.ERROR_MESSAGE);
                chatPanel.addSystemMessage("ERROR: " + m);
            }
        });
    }

    // ============================================================
    //  5. Conexión / Registro / Login
    // ============================================================
    private void handleConnect() {
        var dialog = mainFrame.showConnectionDialog();
        if (!dialog.isAccepted()) return;

        String server = dialog.getServer();
        int port = dialog.getPort();
        String user = dialog.getUsername();
        String pass = dialog.getPassword();

        if (dialog.isRegister()) {
            handleRegistration(server, port, user, pass);
        } else {
            handleLogin(server, port, user, pass);
        }
    }

    private void handleRegistration(String s, int p, String u, String pw) {
        if (!validateFields(s, u, pw)) { handleConnect(); return; }

        if (!networkController.connect(s, p)) {
            showConnectionError();
            handleConnect();
            return;
        }

        sleep(200);
        networkController.register(u, pw);
    }

    private void handleLogin(String s, int p, String u, String pw) {
        if (!validateFields(s, u, pw)) return;

        chatPanel.addSystemMessage("Conectando a " + s + ":" + p + "...");

        if (!networkController.connect(s, p)) {
            showConnectionError();
            return;
        }

        sleep(200);
        chatPanel.addSystemMessage("Autenticando...");
        networkController.login(u, pw);
    }

    private boolean validateFields(String server, String user, String pw) {
        if (server.isEmpty() || user.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Completa todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void handleDisconnect() {
        if (networkController.isConnected()) {
            if (JOptionPane.showConfirmDialog(mainFrame,
                    "¿Desconectar?", "Confirmar",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                networkController.disconnect();
        }
    }

    // ============================================================
    //  6. Chat
    // ============================================================
    private void handleSendMessage() {
        String msg = chatPanel.getMessageAndClear();
        if (msg.isEmpty()) return;

        if (networkController.isConnected())
            networkController.sendChatMessage(msg);
        else
            chatPanel.addSystemMessage("No estás conectado.");

        chatPanel.focusMessageField();
    }

    // ============================================================
    //  7. Respuestas de Autenticación
    // ============================================================
    private void handleAuthSuccess() {
        String user = networkController.getUsername();
        mainFrame.updateUsername(user);

        JOptionPane.showMessageDialog(mainFrame,
                "Bienvenido " + user + "!",
                "Login Exitoso",
                JOptionPane.INFORMATION_MESSAGE);

        chatPanel.addSystemMessage("Autenticación exitosa");
    }

    private void handleAuthFail(String msg) {
        JOptionPane.showMessageDialog(mainFrame, msg, "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        networkController.disconnect();

        if (msg.contains("ya existe")) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Usuario registrado exitosamente.\nInicia sesión.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            handleConnect();
        }
    }

    // ============================================================
    //  Utilidades
    // ============================================================
    private void showConnectionError() {
        JOptionPane.showMessageDialog(mainFrame,
                "No se pudo conectar al servidor.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }

    // ============================================================
    //  Arranque
    // ============================================================
    public void start() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(true);
            chatPanel.addSystemMessage("=== Pixel Art Colaborativo ===");
            handleConnect();
        });
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        ClientApp app = new ClientApp();
        app.start();
    }
}
