package com.equipo.pixelart.server.network;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.equipo.pixelart.server.service.AuthService;
import com.equipo.pixelart.server.service.CanvasService;

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

    public ClientHandler(Socket socket, AuthService authService,
                         CanvasService canvasService, List<ClientHandler> allClients) {
        this.socket = socket;
        this.authService = authService;
        this.canvasService = canvasService;
        this.allClients = allClients;
        this.authenticated = false;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Nuevo cliente conectado desde: " + socket.getInetAddress());

            
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

    private void processMessage(String message) {
        String[] parts = message.split("\\|");

        if (parts.length == 0) return;

        String command = parts[0];

        try {
            switch (command) {
                case "LOGIN" -> handleLogin(parts);

                case "REGISTER" -> handleRegister(parts);

                case "PIXEL" -> {
                    if (authenticated) handlePixel(parts);
                }

                case "GET_CANVAS" -> {
                    if (authenticated) handleGetCanvas();
                }

                case "CLEAR" -> {
                    if (authenticated) handleClear();
                }

                case "CHAT" -> {
                    if (authenticated) handleChat(parts);
                }

                case "GET_USERS" -> {
                    if (authenticated) handleGetUsers();
                }

                case "DISCONNECT" -> running = false;

                case "UNDO" -> {
                    if (authenticated) handleUndo();
                }

                default -> sendMessage("ERROR|Comando desconocido: " + command);
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
            sendMessage("ERROR|Error procesando mensaje");
        }
    }


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

            
            broadcastMessage("USER_JOINED|" + user, this);

            System.out.println("Usuario autenticado: " + user);
        } else {
            sendMessage("AUTH_FAILED|Usuario o contraseña incorrectos");
        }
    }

 
    private void handleRegister(String[] parts) {
        if (parts.length < 3) {
            sendMessage("AUTH_FAILED|Formato invalido");
            return;
        }

        String user = parts[1];
        String pass = parts[2];

        boolean registered = authService.registrarUsuario(user, pass);

        if (registered) {
            sendMessage("AUTH_SUCCESS|" + user);
            System.out.println("Usuario registrado exitosamente: " + user);


        } else {
            sendMessage("AUTH_FAILED|No se pudo registrar el usuario (posiblemente ya existe)");
        }
    }

 
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
                
                String updateMsg = "PIXEL_UPDATE|" + x + "|" + y + "|" + parts[3];
                broadcastMessage(updateMsg, null);
            } else {
                sendMessage("ERROR|No se pudo actualizar el píxel");
            }

        } catch (NumberFormatException e) {
            sendMessage("ERROR|Error en formato de píxel");
        }
    }

    private void handleGetCanvas() {
        int width = canvasService.getWidth();
        int height = canvasService.getHeight();
        String pixelData = canvasService.getCanvasAsString();

        sendMessage("CANVAS_STATE|" + width + "|" + height + "|" + pixelData);
    }

    private void handleClear() {
        canvasService.clear();
        broadcastMessage("CLEAR_CANVAS", null);
        System.out.println("Canvas limpiado por: " + username);
    }

    private void handleChat(String[] parts) {
        if (parts.length < 2) return;

        String message = parts[1];
        String chatMsg = "CHAT_MESSAGE|" + username + "|" + message;

        broadcastMessage(chatMsg, null);
    }

    private void handleGetUsers() {
        String[] users = authService.obtenerUsuariosConectados();
        String userList = String.join(",", users);

        sendMessage("USER_LIST|" + userList);
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void broadcastMessage(String message, ClientHandler exclude) {
        synchronized (allClients) {
            for (ClientHandler client : allClients) {
                if (client != exclude && client.authenticated) {
                    client.sendMessage(message);
                }
            }
        }
    }

    private void disconnect() {
        running = false;

        if (authenticated && username != null) {
            authService.desconectar(username);
            broadcastMessage("USER_LEFT|" + username, this);
            System.out.println("Usuario desconectado: " + username);
        }

        synchronized (allClients) {
            allClients.remove(this);
        }

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando recursos: " + e.getMessage());
        }

        System.out.println("Cliente desconectado: " + socket.getInetAddress());
    }
    /**
     * Maneja el comando UNDO
     */
    private void handleUndo() {
        
        if (canvasService.undo()) {
            
            String canvasData = canvasService.getCanvasAsString();
            int width = canvasService.getWidth();
            int height = canvasService.getHeight();

            
            String msg = "CANVAS_STATE|" + width + "|" + height + "|" + canvasData;

            for (ClientHandler client : allClients) {
                if (client.isAuthenticated()) {
                    client.sendMessage(msg);
                }
            }

            System.out.println("UNDO aplicado por: " + username);
        }
    }

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