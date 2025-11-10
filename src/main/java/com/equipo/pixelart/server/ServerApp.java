package com.equipo.pixelart.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.equipo.pixelart.server.network.ClientHandler;
import com.equipo.pixelart.server.persistence.FileManager;
import com.equipo.pixelart.server.service.AuthService;
import com.equipo.pixelart.server.service.CanvasService;

/**
 * Aplicación del Servidor
 * Punto de entrada del servidor - Gestiona conexiones y servicios
 */
public class ServerApp {

    private static final int DEFAULT_PORT = 12345;
    private static final int CANVAS_WIDTH = 48;
    private static final int CANVAS_HEIGHT = 48;
    private static final int AUTO_SAVE_INTERVAL = 60; // segundos

    private ServerSocket serverSocket;
    private final List<ClientHandler> clients;

    // Servicios
    private FileManager fileManager;
    private AuthService authService;
    private CanvasService canvasService;

    private boolean running;
    private final int port;

    /**
     * Constructor
     */
    public ServerApp(int port) {
        this.port = port;
        this.clients = new CopyOnWriteArrayList<>();
        this.running = false;
    }

    /**
     * Inicializa todos los servicios del servidor
     */
    private void initializeServices() {
        System.out.println("Inicializando servicios...");

        // FileManager
        fileManager = new FileManager();

        // AuthService
        authService = new AuthService(fileManager);

        // CanvasService
        canvasService = new CanvasService(fileManager, CANVAS_WIDTH, CANVAS_HEIGHT);
        canvasService.iniciarAutoGuardado(AUTO_SAVE_INTERVAL);

        System.out.println("Servicios inicializados correctamente");
    }

    /**
     * Inicia el servidor
     */
    public void start() {
        try {
            // Inicializar servicios
            initializeServices();

            // Crear ServerSocket
            serverSocket = new ServerSocket(port);
            running = true;

            System.out.println("===========================================");
            System.out.println("    SERVIDOR PIXEL ART COLABORATIVO");
            System.out.println("===========================================");
            System.out.println("Servidor iniciado en el puerto: " + port);
            System.out.println("Canvas: " + CANVAS_WIDTH + "x" + CANVAS_HEIGHT);
            System.out.println();
            System.out.println("💡 IMPORTANTE:");
            System.out.println("   Los clientes deben conectarse a:");
            System.out.println("   - Servidor: localhost (o tu IP local)");
            System.out.println("   - Puerto: " + port);
            System.out.println();
            System.out.println("Esperando conexiones de clientes...");
            System.out.println("===========================================");
            System.out.println();

            // Iniciar thread para comandos de consola
            startConsoleThread();

            // Aceptar clientes
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleNewClient(clientSocket);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error aceptando cliente: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error iniciando servidor: " + e.getMessage());
        }
    }

    /**
     * Maneja un nuevo cliente conectado
     */
    private void handleNewClient(Socket clientSocket) {
        ClientHandler handler = new ClientHandler(
                clientSocket,
                authService,
                canvasService,
                clients
        );

        clients.add(handler);

        Thread clientThread = new Thread(handler);
        clientThread.start();

        System.out.println("Cliente conectado. Total clientes: " + clients.size());
        mostrarEstadisticas();
    }

    /**
     * Inicia el thread de comandos de consola
     */
    private void startConsoleThread() {
        Thread consoleThread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Comandos disponibles:");
            System.out.println("  stats    - Mostrar estadísticas");
            System.out.println("  users    - Listar usuarios conectados");
            System.out.println("  save     - Guardar canvas");
            System.out.println("  clear    - Limpiar canvas");
            System.out.println("  help     - Mostrar ayuda");
            System.out.println("  exit     - Detener servidor");
            System.out.println();

            while (running) {
                try {
                    System.out.print("server> ");
                    String command = reader.readLine();

                    if (command != null) {
                        processCommand(command.trim().toLowerCase());
                    }

                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error leyendo comando: " + e.getMessage());
                    }
                }
            }
        });

        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    /**
     * Procesa comandos de consola
     */
    private void processCommand(String command) {
        switch (command) {
            case "stats" -> mostrarEstadisticas();
            case "users" -> listarUsuarios();
            case "save" -> guardarDatos();
            case "clear" -> limpiarCanvas();
            case "help" -> mostrarAyuda();
            case "exit" -> detener();
            case "" -> {
                // Ignorar líneas vacías
            }
            default -> {
                System.out.println("Comando desconocido: " + command);
                System.out.println("Escribe 'help' para ver los comandos disponibles");
            }
        }
    }

    /**
     * Muestra estadísticas del servidor
     */
    private void mostrarEstadisticas() {
        System.out.println("\n=== ESTADÍSTICAS DEL SERVIDOR ===");
        System.out.println("Puerto: " + port);
        System.out.println("Clientes conectados: " + clients.size());
        System.out.println("Usuarios autenticados: " + authService.getUsuariosConectadosCount());
        System.out.println("Total usuarios registrados: " + authService.getTotalUsuarios());
        System.out.println(canvasService.getEstadisticas());
        System.out.println("================================\n");
    }

    /**
     * Lista los usuarios conectados
     */
    private void listarUsuarios() {
        String[] usuarios = authService.obtenerUsuariosConectados();

        System.out.println("\n=== USUARIOS CONECTADOS ===");
        if (usuarios.length == 0) {
            System.out.println("No hay usuarios conectados");
        } else {
            for (int i = 0; i < usuarios.length; i++) {
                System.out.println((i + 1) + ". " + usuarios[i]);
            }
        }
        System.out.println("Total: " + usuarios.length);
        System.out.println("===========================\n");
    }

    /**
     * Guarda los datos (usuarios y canvas)
     */
    private void guardarDatos() {
        System.out.println("Guardando datos...");
        canvasService.guardarCanvas();
        System.out.println("Datos guardados correctamente\n");
    }

    /**
     * Limpia el canvas
     */
    private void limpiarCanvas() {
        System.out.print("¿Estás seguro de limpiar el canvas? (s/n): ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String respuesta = reader.readLine();

            if (respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si")) {
                canvasService.clear();

                // Notificar a todos los clientes
                for (ClientHandler client : clients) {
                    if (client.isAuthenticated()) {
                        client.sendMessage("CLEAR_CANVAS");
                    }
                }

                System.out.println("Canvas limpiado\n");
            } else {
                System.out.println("Operación cancelada\n");
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Muestra la ayuda
     */
    private void mostrarAyuda() {
        System.out.println("\n=== AYUDA - COMANDOS DISPONIBLES ===");
        System.out.println("stats    - Muestra estadísticas del servidor");
        System.out.println("users    - Lista los usuarios conectados");
        System.out.println("save     - Guarda manualmente el canvas y usuarios");
        System.out.println("clear    - Limpia el canvas (con confirmación)");
        System.out.println("help     - Muestra esta ayuda");
        System.out.println("exit     - Detiene el servidor ordenadamente");
        System.out.println("====================================\n");
    }

    /**
     * Detiene el servidor ordenadamente
     */
    private void detener() {
        System.out.println("\nDeteniendo servidor...");

        running = false;

        // Guardar datos
        guardarDatos();

        // Desconectar todos los clientes
        System.out.println("Desconectando clientes...");
        for (ClientHandler client : clients) {
            try {
                client.sendMessage("ERROR|Servidor detenido");
                client.getSocket().close();
            } catch (IOException e) {
                // Ignorar
            }
        }
        clients.clear();

        // Cerrar ServerSocket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando ServerSocket: " + e.getMessage());
        }

        System.out.println("Servidor detenido correctamente");
        System.exit(0);
    }

    /**
     * Punto de entrada del servidor
     */
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  INICIANDO SERVIDOR PIXEL ART");
        System.out.println("===========================================\n");

        // Determinar puerto
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Puerto inválido, usando puerto por defecto: " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        // Crear y arrancar servidor
        ServerApp server = new ServerApp(port);

        // Agregar shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nSeñal de interrupción recibida");
            server.detener();
        }));

        // Iniciar servidor
        server.start();
    }
}