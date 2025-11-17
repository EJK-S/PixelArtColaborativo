package com.equipo.pixelart.client.controller;

import java.awt.Color;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class NetworkController {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;

    private CanvasController canvasController;

    private List<NetworkListener> listeners;

    private Thread receiverThread;

    private String username;

    public NetworkController() {
        this.connected = false;
        this.listeners = new ArrayList<>();
    }
  
    public boolean connect(String host, int port) {
        try {
            System.out.println("🔗 Intentando conectar a " + host + ":" + port);

            // Configurar timeout de conexión (5 segundos)
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(host, port), 5000);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            connected = true;
          
            startReceiver();

            System.out.println("✅ Conectado exitosamente a " + host + ":" + port);
            notifyConnectionStatus(true, "Conectado a " + host);

            return true;

        } catch (java.net.SocketTimeoutException e) {
            System.err.println("❌ Timeout de conexión: El servidor no respondió");
            notifyConnectionStatus(false, "Timeout: El servidor no responde");
            return false;
        } catch (java.net.UnknownHostException e) {
            System.err.println("❌ Host desconocido: " + host);
            notifyConnectionStatus(false, "Host desconocido: " + host);
            return false;
        } catch (java.net.ConnectException e) {
            System.err.println("❌ Conexión rechazada: " + e.getMessage());
            notifyConnectionStatus(false, "Conexión rechazada. ¿Está el servidor corriendo?");
            return false;
        } catch (IOException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            notifyConnectionStatus(false, "Error: " + e.getMessage());
            return false;
        }
    }
 
    public void disconnect() {
        if (!connected) return;

        try {
            sendMessage("DISCONNECT");

            connected = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            notifyConnectionStatus(false, "Desconectado del servidor");

        } catch (IOException e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
    }

    private void startReceiver() {
        receiverThread = new Thread(() -> {
            try {
                String message;
                while (connected && (message = in.readLine()) != null) {
                    processMessage(message);
                }
            } catch (IOException e) {
                if (connected) {
                    notifyConnectionStatus(false, "Conexión perdida: " + e.getMessage());
                    connected = false;
                }
            }
        });

        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    private void processMessage(String message) {
        String[] parts = message.split("\\|");

        if (parts.length == 0) return;

        String command = parts[0];

        SwingUtilities.invokeLater(() -> {
            try {
                switch (command) {
                    case "AUTH_SUCCESS":
                        handleAuthSuccess(parts);
                        break;

                    case "AUTH_FAILED":
                        handleAuthFailed(parts);
                        break;

                    case "PIXEL_UPDATE":
                        handlePixelUpdate(parts);
                        break;

                    case "CANVAS_STATE":
                        handleCanvasState(parts);
                        break;

                    case "CLEAR_CANVAS":
                        handleClearCanvas();
                        break;

                    case "CHAT_MESSAGE":
                        handleChatMessage(parts);
                        break;

                    case "USER_JOINED":
                        handleUserJoined(parts);
                        break;

                    case "USER_LEFT":
                        handleUserLeft(parts);
                        break;

                    case "USER_LIST":
                        handleUserList(parts);
                        break;

                    case "ERROR":
                        handleError(parts);
                        break;

                    default:
                        System.out.println("Comando desconocido: " + command);
                }
            } catch (Exception e) {
                System.err.println("Error procesando mensaje: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void sendMessage(String message) {
        if (!connected || out == null) {
            System.err.println("No conectado al servidor");
            return;
        }
        out.println(message);
    }

    public void login(String username, String password) {
        this.username = username;
        sendMessage("LOGIN|" + username + "|" + password);
        System.out.println("Enviando LOGIN para: " + username);
    }

    public void register(String username, String password) {
        this.username = username;
        sendMessage("REGISTER|" + username + "|" + password);
        System.out.println("Enviando REGISTER para: " + username);
    }

    public void sendPixelUpdate(int x, int y, Color color) {
        String colorHex = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        sendMessage("PIXEL|" + x + "|" + y + "|" + colorHex);
    }

    public void requestCanvasState() {
        sendMessage("GET_CANVAS");
    }

    public void sendClearCanvas() {
        sendMessage("CLEAR");
    }

    public void sendChatMessage(String message) {
        sendMessage("CHAT|" + message);
    }

    public void requestUserList() {
        sendMessage("GET_USERS");
    }

    // ==================== MANEJAR RESPUESTAS ====================

    private void handleAuthSuccess(String[] parts) {
        // AUTH_SUCCESS|username
        if (parts.length > 1) {
            username = parts[1];
        }
        notifyAuthResult(true, "Autenticación exitosa");

        requestCanvasState();
        requestUserList();
    }

    private void handleAuthFailed(String[] parts) {
        // AUTH_FAILED|reason
        String reason = parts.length > 1 ? parts[1] : "Error desconocido";
        notifyAuthResult(false, reason);
    }

    private void handlePixelUpdate(String[] parts) {
        // PIXEL_UPDATE|x|y|colorHex
        if (parts.length < 4) return;

        try {
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            Color color = Color.decode("#" + parts[3]);

            if (canvasController != null) {
                canvasController.updatePixelFromServer(x, y, color);
            }
        } catch (Exception e) {
            System.err.println("Error en PIXEL_UPDATE: " + e.getMessage());
        }
    }

    private void handleCanvasState(String[] parts) {
        // CANVAS_STATE|width|height|pixelData
        if (parts.length < 4) return;

        try {
            int width = Integer.parseInt(parts[1]);
            int height = Integer.parseInt(parts[2]);
            String pixelData = parts[3];

            // Decodificar pixelData (formato: RRGGBB,RRGGBB,...)
            String[] pixels = pixelData.split(",");
            Color[][] canvas = new Color[width][height];

            int index = 0;
            for (int x = 0; x < width && index < pixels.length; x++) {
                for (int y = 0; y < height && index < pixels.length; y++) {
                    try {
                        canvas[x][y] = Color.decode("#" + pixels[index]);
                    } catch (Exception e) {
                        canvas[x][y] = Color.WHITE;
                    }
                    index++;
                }
            }

            if (canvasController != null) {
                canvasController.updateCanvas(canvas);
            }

        } catch (Exception e) {
            System.err.println("Error en CANVAS_STATE: " + e.getMessage());
        }
    }

    private void handleClearCanvas() {
        if (canvasController != null) {
            canvasController.getModel().clear();
        }
    }

    private void handleChatMessage(String[] parts) {
        
        if (parts.length < 3) return;

        String user = parts[1];
        String message = parts[2];

        notifyChatMessage(user, message);
    }

    private void handleUserJoined(String[] parts) {
        
        if (parts.length < 2) return;

        String user = parts[1];
        notifyUserJoined(user);
    }

    private void handleUserLeft(String[] parts) {
      
        if (parts.length < 2) return;

        String user = parts[1];
        notifyUserLeft(user);
    }

    private void handleUserList(String[] parts) {
      
        if (parts.length < 2) return;

        String[] users = parts[1].split(",");
        notifyUserList(users);
    }

    private void handleError(String[] parts) {
        
        String errorMsg = parts.length > 1 ? parts[1] : "Error desconocido";
        notifyError(errorMsg);
    }

    public void addNetworkListener(NetworkListener listener) {
        listeners.add(listener);
    }

    public void removeNetworkListener(NetworkListener listener) {
        listeners.remove(listener);
    }

    private void notifyConnectionStatus(boolean connected, String message) {
        for (NetworkListener listener : listeners) {
            listener.onConnectionStatusChanged(connected, message);
        }
    }

    private void notifyAuthResult(boolean success, String message) {
        for (NetworkListener listener : listeners) {
            listener.onAuthResult(success, message);
        }
    }

    private void notifyChatMessage(String user, String message) {
        for (NetworkListener listener : listeners) {
            listener.onChatMessage(user, message);
        }
    }

    private void notifyUserJoined(String user) {
        for (NetworkListener listener : listeners) {
            listener.onUserJoined(user);
        }
    }

    private void notifyUserLeft(String user) {
        for (NetworkListener listener : listeners) {
            listener.onUserLeft(user);
        }
    }

    private void notifyUserList(String[] users) {
        for (NetworkListener listener : listeners) {
            listener.onUserListReceived(users);
        }
    }

    private void notifyError(String message) {
        for (NetworkListener listener : listeners) {
            listener.onError(message);
        }
    }

    public void setCanvasController(CanvasController canvasController) {
        this.canvasController = canvasController;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getUsername() {
        return username;
    }

    public interface NetworkListener {
        default void onConnectionStatusChanged(boolean connected, String message) {}
        default void onAuthResult(boolean success, String message) {}
        default void onChatMessage(String user, String message) {}
        default void onUserJoined(String user) {}
        default void onUserLeft(String user) {}
        default void onUserListReceived(String[] users) {}
        default void onError(String message) {}
    }
}