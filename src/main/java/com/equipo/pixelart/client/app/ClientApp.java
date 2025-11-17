package com.equipo.pixelart.client.app;

import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.equipo.pixelart.client.controller.CanvasController;
import com.equipo.pixelart.client.controller.ChatController;
import com.equipo.pixelart.client.controller.MenuController;
import com.equipo.pixelart.client.controller.NetworkController;
import com.equipo.pixelart.client.controller.NetworkListener;
import com.equipo.pixelart.client.controller.ToolboxController;
import com.equipo.pixelart.client.controller.UserListController;
import com.equipo.pixelart.client.model.CanvasModel;
import com.equipo.pixelart.client.view.CanvasPanel;
import com.equipo.pixelart.client.view.ChatPanel;
import com.equipo.pixelart.client.view.MainFrame;
import com.equipo.pixelart.client.view.ToolboxPanel;
import com.equipo.pixelart.client.view.UserListPanel;


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
    private ChatController chatController;
    private UserListController userListController;
    private MenuController menuController;

    public ClientApp() {
        inicializacionComponentes();
        inicialiazarMarcoPrincipal();
        inicializacionControladores();        
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
        chatController = new ChatController(chatPanel, networkController);
        userListController = new UserListController(userListPanel, networkController);
        userListController.init();
        menuController = new MenuController(mainFrame, canvasController, canvasPanel, this);
    }

    private void inicialiazarMarcoPrincipal() {
        mainFrame = new MainFrame();
        mainFrame.setPanels(toolboxPanel, canvasPanel, chatPanel, userListPanel);
    }

    private void iniciarEventos() {
       
        chatController.init();
        userListController.init();
        menuController.init();
        
        networkController.addNetworkListener(new NetworkListener() {

            public void onConnectionStatusChanged(boolean connected, String msg) {
            	mainFrame.updateConnectionStatus(connected, msg);            
                
            }

            public void onAuthResult(boolean ok, String msg) {
                if (ok) procesarAutenticacionExitosa();
                else procesarFalloDeAutenticacion(msg);
            }

            public void onUserListReceived(String[] u) {
                userListPanel.setUsers(u);
            }

            public void onError(String m) {
                JOptionPane.showMessageDialog(mainFrame, m, "Error", JOptionPane.ERROR_MESSAGE);
            }
            @Override
            public void onPixelUpdateReceived(int x, int y, Color color) {
                canvasController.updatePixelFromServer(x, y, color);
            }

            @Override
            public void onCanvasStateReceived(Color[][] canvas) {
                canvasController.updateCanvas(canvas);
            }

            @Override
            public void onCanvasCleared() {
                canvasController.getModel().clear();
            }
        });
    }

    public void manejarConexion() {
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
        chatController.addSystemMessage("Autenticando...");
        networkController.login(u, pw);
    }

    private boolean validarCampos(String server, String user, String pw) {
        if (server.isEmpty() || user.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Completa todos los campos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public void gestionarDesconexion() {
        if (networkController.isConnected()) {
            if (JOptionPane.showConfirmDialog(mainFrame,
                    "¿Desconectar?", "Confirmar",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                networkController.disconnect();
        }
    }

    private void procesarAutenticacionExitosa() {
        String user = networkController.getUsername();
        mainFrame.updateUsername(user);

        JOptionPane.showMessageDialog(mainFrame,
                "Bienvenido " + user + "!",
                "Login Exitoso",
                JOptionPane.INFORMATION_MESSAGE);

        chatController.addSystemMessage("Autenticación exitosa");
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
            chatController.addSystemMessage("=== Pixel Art Colaborativo ===");
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
