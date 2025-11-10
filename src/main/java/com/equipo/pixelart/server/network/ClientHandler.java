package com.equipo.pixelart.server.network;

import com.equipo.pixelart.server.service.AuthService;
import com.equipo.pixelart.server.service.CanvasService;

import java.awt.Color;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Manejador de Cliente
 * Gestiona la conexión individual con cada cliente
 * Cada cliente tiene su propio thread
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final AuthService authService;
    private final CanvasService canvasService;
    private final List<ClientHandler> allClients;

    private PrintWriter out;
    private BufferedReader in;

    private String username;
    private boolean authenticated;
    private boolean running;

    /**
     * Constructor
     */
    public ClientHandler(Socket socket, AuthService authService,
                         CanvasService canvasService, List<ClientHandler> allClients) {
        this.socket = socket;
        this.authService = authService;
        this.canvasService = canvasService;
        this.allClients = allClients;
        this.authenticated = false;
        this.running = true;
    }

    /**
     * Thread principal del cliente
     */
    @Override
    public void run() {
        try {
            // Configurar streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Nuevo cliente conectado desde: " + socket.getInetAddress());

            // Procesar mensajes del cliente
            String message;
            while (running && (message = in.readLine()) != null) {
                processMessage(message);
            }

        } catch (IOException e) {
            System.err.println("Error en conexión con cliente: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Procesa un mensaje recibido del cliente
     */
    private void processMessage(String message) {
        String[] parts = message.split("\\|");

        if (parts.length == 0) return;

        String command = parts[0];

        try {
            switch (command) {
                case "LOGIN":
                    handleLogin(parts);
                    break;

                case "REGISTER":
                    handleRegister(parts);
                    break;

                case "PIXEL":
                    if (authenticated) handlePixel(parts);
                    break;

                case "GET_CANVAS":
                    if (authenticated) handleGetCanvas();
                    break;

                case "CLEAR":
                    if (authenticated) handleClear();
                    break;

                case "CHAT":
                    if (authenticated) handleChat(parts);
                    break;

                case "GET_USERS":
                    if (authenticated) handleGetUsers();
                    break;

                case "DISCONNECT":
                    running = false;
                    break;

                default:
                    sendMessage("ERROR|Comando desconocido: " + command);
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
            sendMessage("ERROR|Error procesando mensaje");
        }
    }

    // ==================== MANEJADORES DE COMANDOS ====================

    /**
     * Maneja el login de un usuario
     * LOGIN|username|password
     */
    private void handleLogin(String[] parts) {
        if (parts.length < 3) {
            sendMessage("AUTH_FAILED|Formato inválido");
            return;
        }

        String user = parts[1];
        String pass = parts[2];

        if (authService.autenticar(user, pass)) {
            this.username = user;
            this.authenticated = true;

            sendMessage("AUTH_SUCCESS|" + user);

            // Notificar a todos que un usuario se unió
            broadcastMessage("USER_JOINED|" + user, this);

            System.out.println("Usuario autenticado: " + user);
        } else {
            sendMessage("AUTH_FAILED|Usuario o contraseña incorrectos");
        }
    }

    /**
     * Maneja el registro de un nuevo usuario
     * REGISTER|username|password
     */
    private void handleRegister(String[] parts) {
        if (parts.length < 3) {
            sendMessage("AUTH_FAILED|Formato inválido");
            return;
        }

        String user = parts[1];
        String pass = parts[2];

        if (authService.registrarUsuario(user, pass)) {
            // Auto-login después de registrar
            if (authService.autenticar(user, pass)) {
                this.username = user;
                this.authenticated = true;

                sendMessage("AUTH_SUCCESS|" + user);
                broadcastMessage("USER_JOINED|" + user, this);

                System.out.println("Usuario registrado y autenticado: " + user);
            }
        } else {
            sendMessage("AUTH_FAILED|No se pudo registrar el usuario");
        }
    }

    /**
     * Maneja la actualización de un píxel
     * PIXEL|x|y|colorHex
     */
    private void handlePixel(String[] parts) {
        if (parts.length < 4) {
            sendMessage("ERROR|Formato inválido");
            return;
        }

        try {
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            Color color = Color.decode("#" + parts[3]);

            if (canvasService.setPixel(x, y, color)) {
                // Broadcast a todos los clientes
                String updateMsg = "PIXEL_UPDATE|" + x + "|" + y + "|" + parts[3];
                broadcastMessage(updateMsg, null);
            } else {
                sendMessage("ERROR|No se pudo actualizar el píxel");
            }

        } catch (Exception e) {
            sendMessage("ERROR|Error en formato de píxel");
        }
    }

    /**
     * Envía el estado completo del canvas
     * GET_CANVAS
     */
    private void handleGetCanvas() {
        int width = canvasService.getWidth();
        int height = canvasService.getHeight();
        String pixelData = canvasService.getCanvasAsString();

        sendMessage("CANVAS_STATE|" + width + "|" + height + "|" + pixelData);
    }

    /**
     * Limpia el canvas
     * CLEAR
     */
    private void handleClear() {
        canvasService.clear();
        broadcastMessage("CLEAR_CANVAS", null);
        System.out.println("Canvas limpiado por: " + username);
    }

    /**
     * Maneja un mensaje de chat
     * CHAT|message
     */
    private void handleChat(String[] parts) {
        if (parts.length < 2) return;

        String message = parts[1];
        String chatMsg = "CHAT_MESSAGE|" + username + "|" + message;

        broadcastMessage(chatMsg, null);
    }

    /**
     * Envía la lista de usuarios conectados
     * GET_USERS
     */
    private void handleGetUsers() {
        String[] users = authService.obtenerUsuariosConectados();
        String userList = String.join(",", users);

        sendMessage("USER_LIST|" + userList);
    }

    // ==================== MÉTODOS DE ENVÍO ====================

    /**
     * Envía un mensaje a este cliente
     */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    /**
     * Envía un mensaje a todos los clientes excepto al excluido
     */
    private void broadcastMessage(String message, ClientHandler exclude) {
        synchronized (allClients) {
            for (ClientHandler client : allClients) {
                if (client != exclude && client.authenticated) {
                    client.sendMessage(message);
                }
            }
        }
    }

    /**
     * Desconecta el cliente
     */
    private void disconnect() {
        running = false;

        // Desautenticar usuario
        if (authenticated && username != null) {
            authService.desconectar(username);
            broadcastMessage("USER_LEFT|" + username, this);
            System.out.println("Usuario desconectado: " + username);
        }

        // Remover de la lista de clientes
        synchronized (allClients) {
            allClients.remove(this);
        }

        // Cerrar recursos
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando recursos: " + e.getMessage());
        }

        System.out.println("Cliente desconectado: " + socket.getInetAddress());
    }

    // ==================== GETTERS ====================

    public String getUsername() {
        return username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Socket getSocket() {
        return socket;
    }
}