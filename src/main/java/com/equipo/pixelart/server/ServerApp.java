package com.equipo.pixelart.server;

import com.equipo.pixelart.server.network.ClientHandler;
import com.equipo.pixelart.server.persistence.FileManager;
import com.equipo.pixelart.server.service.AuthService;
import com.equipo.pixelart.server.service.CanvasService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Aplicación del Servidor
 * Punto de entrada del servidor - Gestiona conexiones y servicios
 * Soporta conexiones LAN (Red Local)
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
    private int port;

    /**
     * Constructor
     */
    public ServerApp(int port) {
        this.port = port;
        this.clients = new CopyOnWriteArrayList<>();
        this.running = false;
    }

    /**
     * Obtiene todas las direcciones IP locales del servidor
     */
    private List<String> getLocalIPAddresses() {
        List<String> ipAddresses = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Ignorar interfaces deshabilitadas o loopback
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // Solo IPv4 y no loopback
                    if (addr.getHostAddress().indexOf(':') == -1 && !addr.isLoopbackAddress()) {
                        ipAddresses.add(addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error obteniendo direcciones IP: " + e.getMessage());
        }

        return ipAddresses;
    }

    /**
     * Muestra las direcciones IP disponibles
     */
    private void displayNetworkInfo() {
        List<String> ipAddresses = getLocalIPAddresses();

        System.out.println("===========================================");
        System.out.println("  INFORMACION DE RED DEL SERVIDOR");
        System.out.println("===========================================");
        System.out.println();

        if (ipAddresses.isEmpty()) {
            System.out.println("*** No se detectaron direcciones IP (solo localhost)");
            System.out.println();
            System.out.println("Direccion para conectar:");
            System.out.println("  - localhost:" + port + " (solo este equipo)");
        } else {
            System.out.println("*** Servidor escuchando en multiples interfaces:");
            System.out.println();

            System.out.println("Direcciones para conectar:");
            System.out.println("  - localhost:" + port + " (este equipo)");
            System.out.println("  - 127.0.0.1:" + port + " (este equipo)");

            for (String ip : ipAddresses) {
                System.out.println("  - " + ip + ":" + port + " *** (LAN - RED LOCAL)");
            }
        }

        System.out.println();
        System.out.println("INSTRUCCIONES PARA CLIENTES EN RED LOCAL:");
        System.out.println("  1. En el cliente, usar una de las IPs de arriba");
        System.out.println("  2. Puerto: " + port);
        System.out.println("  3. Asegurate de que el firewall permita el puerto " + port);
        System.out.println();
        System.out.println("CONFIGURACION DE FIREWALL:");
        System.out.println("  Windows: Permitir puerto " + port + " en Windows Defender");
        System.out.println("  Linux: sudo ufw allow " + port);
        System.out.println("  macOS: Verificar en Preferencias del Sistema > Seguridad");
        System.out.println();
        System.out.println("===========================================");
        System.out.println();
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

            // Crear ServerSocket vinculado a todas las interfaces (0.0.0.0)
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
            running = true;

            System.out.println("===========================================");
            System.out.println("  SERVIDOR PIXEL ART COLABORATIVO");
            System.out.println("===========================================");
            System.out.println();
            System.out.println("Servidor iniciado exitosamente");
            System.out.println("Canvas: " + CANVAS_WIDTH + "x" + CANVAS_HEIGHT);
            System.out.println("Auto-guardado: cada " + AUTO_SAVE_INTERVAL + " segundos");
            System.out.println();

            // Mostrar información de red
            displayNetworkInfo();

            System.out.println("Esperando conexiones de clientes...");
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
            System.err.println("*** Error iniciando servidor: " + e.getMessage());
            System.err.println();
            System.err.println("Posibles causas:");
            System.err.println("  - El puerto " + port + " ya esta en uso");
            System.err.println("  - No tienes permisos para abrir el puerto");
            System.err.println("  - El firewall esta bloqueando la conexion");
            System.err.println();
            System.err.println("Soluciones:");
            System.err.println("  - Cierra otras aplicaciones usando el puerto " + port);
            System.err.println("  - Ejecuta con permisos de administrador");
            System.err.println("  - Configura el firewall para permitir el puerto " + port);
            System.err.println("  - Intenta con otro puerto: java -cp bin com.equipo.pixelart.server.ServerApp 8080");
        }
    }

    /**
     * Maneja un nuevo cliente conectado
     */
    private void handleNewClient(Socket clientSocket) {
        String clientIP = clientSocket.getInetAddress().getHostAddress();
        String clientHost = clientSocket.getInetAddress().getHostName();

        System.out.println("===========================================");
        System.out.println("NUEVA CONEXION");
        System.out.println("  IP: " + clientIP);
        System.out.println("  Host: " + clientHost);
        System.out.println("  Puerto remoto: " + clientSocket.getPort());
        System.out.println("===========================================");

        ClientHandler handler = new ClientHandler(
                clientSocket,
                authService,
                canvasService,
                clients
        );

        clients.add(handler);

        Thread clientThread = new Thread(handler);
        clientThread.start();

        System.out.println("Cliente agregado. Total clientes: " + clients.size());
        mostrarEstadisticas();
    }

    /**
     * Inicia el thread de comandos de consola
     */
    private void startConsoleThread() {
        Thread consoleThread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("COMANDOS DISPONIBLES:");
            System.out.println("  stats    - Mostrar estadisticas");
            System.out.println("  users    - Listar usuarios conectados");
            System.out.println("  network  - Mostrar info de red");
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
            case "stats":
                mostrarEstadisticas();
                break;

            case "users":
                listarUsuarios();
                break;

            case "network":
                displayNetworkInfo();
                break;

            case "save":
                guardarDatos();
                break;

            case "clear":
                limpiarCanvas();
                break;

            case "help":
                mostrarAyuda();
                break;

            case "exit":
                detener();
                break;

            case "":
                // Ignorar líneas vacías
                break;

            default:
                System.out.println("*** Comando desconocido: " + command);
                System.out.println("Escribe 'help' para ver los comandos disponibles");
        }
    }

    /**
     * Muestra estadísticas del servidor
     */
    private void mostrarEstadisticas() {
        System.out.println();
        System.out.println("=== ESTADISTICAS DEL SERVIDOR ===");
        System.out.println("Puerto: " + port);
        System.out.println("Clientes conectados: " + clients.size());
        System.out.println("Usuarios autenticados: " + authService.getUsuariosConectadosCount());
        System.out.println("Total usuarios registrados: " + authService.getTotalUsuarios());
        System.out.println(canvasService.getEstadisticas());
        System.out.println("=================================");
        System.out.println();
    }

    /**
     * Lista los usuarios conectados
     */
    private void listarUsuarios() {
        String[] usuarios = authService.obtenerUsuariosConectados();

        System.out.println();
        System.out.println("=== USUARIOS CONECTADOS ===");
        if (usuarios.length == 0) {
            System.out.println("No hay usuarios conectados");
        } else {
            for (int i = 0; i < usuarios.length; i++) {
                System.out.println((i + 1) + ". " + usuarios[i]);
            }
        }
        System.out.println("Total: " + usuarios.length);
        System.out.println("===========================");
        System.out.println();
    }

    /**
     * Guarda los datos (usuarios y canvas)
     */
    private void guardarDatos() {
        System.out.println("Guardando datos...");
        canvasService.guardarCanvas();
        System.out.println("Datos guardados correctamente");
        System.out.println();
    }

    /**
     * Limpia el canvas
     */
    private void limpiarCanvas() {
        System.out.print("Estas seguro de limpiar el canvas? (s/n): ");
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

                System.out.println("Canvas limpiado");
                System.out.println();
            } else {
                System.out.println("Operacion cancelada");
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Muestra la ayuda
     */
    private void mostrarAyuda() {
        System.out.println();
        System.out.println("=== AYUDA - COMANDOS DISPONIBLES ===");
        System.out.println("stats    - Muestra estadisticas del servidor");
        System.out.println("users    - Lista los usuarios conectados");
        System.out.println("network  - Muestra informacion de red (IPs)");
        System.out.println("save     - Guarda manualmente el canvas");
        System.out.println("clear    - Limpia el canvas (con confirmacion)");
        System.out.println("help     - Muestra esta ayuda");
        System.out.println("exit     - Detiene el servidor ordenadamente");
        System.out.println("====================================");
        System.out.println();
    }

    /**
     * Detiene el servidor ordenadamente
     */
    private void detener() {
        System.out.println();
        System.out.println("Deteniendo servidor...");

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
        System.out.println("Hasta pronto!");
        System.exit(0);
    }

    /**
     * Punto de entrada del servidor
     */
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  INICIANDO SERVIDOR PIXEL ART");
        System.out.println("===========================================");
        System.out.println();

        // Determinar puerto
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                System.out.println("Puerto personalizado: " + port);
            } catch (NumberFormatException e) {
                System.err.println("Puerto invalido, usando puerto por defecto: " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        } else {
            System.out.println("Usando puerto por defecto: " + DEFAULT_PORT);
        }

        System.out.println();

        // Crear y arrancar servidor
        ServerApp server = new ServerApp(port);

        // Agregar shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("Señal de interrupcion recibida");
            server.detener();
        }));

        // Iniciar servidor
        server.start();
    }
}