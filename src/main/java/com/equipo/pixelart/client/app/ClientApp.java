package com.equipo.pixelart.client.app;

import com.equipo.pixelart.client.controller.CanvasController;
import com.equipo.pixelart.client.controller.NetworkController;
import com.equipo.pixelart.client.controller.ToolboxController;
import com.equipo.pixelart.client.model.CanvasModel;
import com.equipo.pixelart.client.view.*;

import javax.swing.*;


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
        inicializacionComponentes();
        inicializacionControladores();
        inicialiazarMarcoPrincipal();
        iniciarEventos();
    }

    private void inicializacionComponentes() {
        canvasModel = new CanvasModel(48, 48);
        canvasPanel = new CanvasPanel(canvasModel, 12);
        toolboxPanel = new ToolboxPanel();
        chatPanel = new ChatPanel();
        userListPanel = new UserListPanel();
        networkController = new NetworkController();
    }

    private void inicializacionControladores() {
        canvasController = new CanvasController(canvasModel, canvasPanel, networkController);
        toolboxController = new ToolboxController(toolboxPanel, canvasController);
        toolboxController.init();
        networkController.setCanvasController(canvasController);
    }

    private void inicialiazarMarcoPrincipal() {
        mainFrame = new MainFrame();
        mainFrame.setPanels(toolboxPanel, canvasPanel, chatPanel, userListPanel);
    }

    private void iniciarEventos() {
        mainFrame.getConnectItem().addActionListener(e -> manejarConexion());
        mainFrame.getDisconnectItem().addActionListener(e -> gestionarDesconexion());
        mainFrame.getExitItem().addActionListener(e -> System.exit(0));

        mainFrame.getClearItem().addActionListener(e -> canvasController.clearCanvas());
        mainFrame.getToggleGridItem().addActionListener(e ->
                canvasPanel.setShowGrid(mainFrame.getToggleGridItem().isSelected()));
        mainFrame.getAboutItem().addActionListener(e -> mainFrame.showAboutDialog());

        chatPanel.addSendListener(e -> gestionarEnvioMensaje());

        networkController.addNetworkListener(new NetworkController.NetworkListener() {

            public void onConnectionStatusChanged(boolean connected, String msg) {
                mainFrame.updateConnectionStatus(connected, msg);

                chatPanel.setEnabled(connected);
                chatPanel.addSystemMessage(connected ? "Conectado" : "Desconectado");

                if (!connected) userListPanel.clearUsers();
            }

            public void onAuthResult(boolean ok, String msg) {
                if (ok) procesarAutenticacionExitosa();
                else procesarFalloDeAutenticacion(msg);
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

    private void manejarConexion() {
        var dialog = mainFrame.showConnectionDialog();
        if (!dialog.isAccepted()) return;

        String server = dialog.getServer();
        int port = dialog.getPort();
        String user = dialog.getUsername();
        String pass = dialog.getPassword();

        if (dialog.isRegister()) {
            manejarRegistro(server, port, user, pass);
        } else {
            gestionarLogin(server, port, user, pass);
        }
    }

    private void manejarRegistro(String s, int p, String u, String pw) {
        if (!validarCampos(s, u, pw)) { manejarConexion(); return; }

        if (!networkController.connect(s, p)) {
            mostrarErrorDeConexion();
            manejarConexion();
            return;
        }

        sleep(200);
        networkController.register(u, pw);
    }

    private void gestionarLogin(String s, int p, String u, String pw) {
        if (!validarCampos(s, u, pw)) return;

        chatPanel.addSystemMessage("Conectando a " + s + ":" + p + "...");

        if (!networkController.connect(s, p)) {
            mostrarErrorDeConexion();
            return;
        }

        sleep(200);
        chatPanel.addSystemMessage("Autenticando...");
        networkController.login(u, pw);
    }

    private boolean validarCampos(String server, String user, String pw) {
        if (server.isEmpty() || user.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Completa todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void gestionarDesconexion() {
        if (networkController.isConnected()) {
            if (JOptionPane.showConfirmDialog(mainFrame,
                    "¿Desconectar?", "Confirmar",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                networkController.disconnect();
        }
    }
 
    private void gestionarEnvioMensaje() {
        String msg = chatPanel.getMessageAndClear();
        if (msg.isEmpty()) return;

        if (networkController.isConnected())
            networkController.sendChatMessage(msg);
        else
            chatPanel.addSystemMessage("No estás conectado.");

        chatPanel.focusMessageField();
    }
 
    private void procesarAutenticacionExitosa() {
        String user = networkController.getUsername();
        mainFrame.updateUsername(user);

        JOptionPane.showMessageDialog(mainFrame,
                "Bienvenido " + user + "!",
                "Login Exitoso",
                JOptionPane.INFORMATION_MESSAGE);

        chatPanel.addSystemMessage("Autenticación exitosa");
    }

    private void procesarFalloDeAutenticacion(String msg) {
        JOptionPane.showMessageDialog(mainFrame, msg, "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        networkController.disconnect();

        if (msg.contains("ya existe")) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Usuario registrado exitosamente.\nInicia sesión.",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            manejarConexion();
        }
    }

    private void mostrarErrorDeConexion() {
        JOptionPane.showMessageDialog(mainFrame,
                "No se pudo conectar al servidor.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(true);
            chatPanel.addSystemMessage("=== Pixel Art Colaborativo ===");
            manejarConexion();
        });
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        ClientApp app = new ClientApp();
        app.start();
    }
}
