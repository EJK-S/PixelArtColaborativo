package com.equipo.pixelart.server.app;

import com.equipo.pixelart.server.console.ServerConsole;
import com.equipo.pixelart.server.network.ClientHandler;
import com.equipo.pixelart.server.persistence.FileManager;
import com.equipo.pixelart.server.service.AuthService;
import com.equipo.pixelart.server.service.CanvasService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerApp {

    private static final int DEFAULT_PORT = 12345;
    private static final int WIDTH = 48;
    private static final int HEIGHT = 48;
    private static final int AUTO_SAVE = 60;

    private final int port;
    private boolean running;

    private ServerSocket serverSocket;
    private FileManager fileManager;
    private AuthService authService;
    private CanvasService canvasService;

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public ServerApp(int port) {
        this.port = port;	
    }

    protected void initServicios() {
        log("Inicializando servicios...");

        fileManager = new FileManager();
        authService = new AuthService(fileManager);
        canvasService = new CanvasService(fileManager, WIDTH, HEIGHT);

        canvasService.iniciarAutoGuardado(AUTO_SAVE);
        log("Servicios inicializados\n");
    }

    private void imprimirBanner() {
        System.out.println("""
                =====================================
                  SERVIDOR PIXEL ART COLABORATIVO
                =====================================

                Canvas: %dx%d
                Auto-guardado: %d segundos

                """.formatted(WIDTH, HEIGHT, AUTO_SAVE));
    }

    public List<String> getLocalIPs() {
        List<String> ips = new ArrayList<>();
        try {
            var nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                var ni = nets.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;

                var addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    var ip = addrs.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getAddress().length == 4) {
                        ips.add(ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            log("No se pudieron obtener IPs: " + e.getMessage());
        }
        return ips;
    }

    public void imprimirInfoRed() {
        System.out.println("=== DIRECCIONES PARA CONECTAR ===");
        System.out.println(" - localhost:" + port);
        System.out.println(" - 127.0.0.1:" + port);

        getLocalIPs().forEach(ip -> System.out.println(" - " + ip + ":" + port + "  (LAN)"));
        System.out.println();
    }

    public void inicio() {
        try {
            initServicios();

            serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
            running = true;

            imprimirBanner();
            imprimirInfoRed();

        
            startConsole();

            log("Esperando conexiones...\n");

            ejecutarBuclePrincipal(); 

        } catch (IOException e) {
            log("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
    
    protected void ejecutarBuclePrincipal() {
        while (running) {
            try {
                aceptarCliente(serverSocket.accept());
            } catch (IOException e) {
                if (running) log("Error aceptando cliente: " + e.getMessage());
                // Opcional: Romper el bucle en el test para que no sea infinito
                if (e.getMessage().contains("TEST_EXIT")) running = false; 
            }
        }
    }
    
    protected void startConsole() {
        new ServerConsole(this).inicio();
    }

    private void aceptarCliente(Socket sock) {
        String ip = sock.getInetAddress().getHostAddress();
        log("Nuevo cliente conectado: " + ip);

        ClientHandler handler = new ClientHandler(sock, authService, canvasService, clients);
        clients.add(handler);

        new Thread(handler).start();

        log("Clientes actuales: " + clients.size() + "\n");
    }

  

    public void imprimirEstadisticas() {
        System.out.println("""
                === ESTADÍSTICAS ===
                Puerto: %d
                Clientes: %d
                Autenticados: %d
                Registrados: %d
                %s
                =====================
                """.formatted(
                port,
                clients.size(),
                authService.getUsuariosConectadosCount(),
                authService.getTotalUsuarios(),
                canvasService.getEstadisticas()
        ));
    }

    public void imprimirUsuarios() {
        String[] list = authService.obtenerUsuariosConectados();
        System.out.println("=== USUARIOS ===");

        if (list.length == 0) System.out.println("No hay usuarios conectados");
        else {
            for (int i = 0; i < list.length; i++)
                System.out.println((i + 1) + ". " + list[i]);
        }

        System.out.println();
    }

    public void saveCanvas() {
        canvasService.guardarCanvas();
        log("Canvas guardado.");
    }

    public void clearCanvas() {
        canvasService.clear();
        for (ClientHandler c : clients)
            if (c.isAuthenticated()) c.sendMessage("CLEAR_CANVAS");

        log("Canvas limpiado.");
    }

    public void stopServer() {
        log("Deteniendo servidor...");

        running = false;
        if (canvasService != null) saveCanvas();

        for (ClientHandler c : clients) {
            try {
                c.sendMessage("ERROR|Servidor detenido");
                c.getSocket().close();
            } catch (IOException e) {
                log("Error cerrando cliente: " + e.getMessage());
            }
        }

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log("Error cerrando servidor: " + e.getMessage());
        }

        log("Servidor detenido.");
        exitSystem();
    }
    
    protected void exitSystem() {
        System.exit(0);
    }
    
    public void setServices(FileManager fm, AuthService as, CanvasService cs) {
        this.fileManager = fm;
        this.authService = as;
        this.canvasService = cs;
    }
    
    public List<ClientHandler> getClients() {
        return clients;
    }
    
    public boolean isRunning() { return running; }

    private void log(String msg) {
        System.out.println("[SERVER] " + msg);
    }
    
    public static int getPortFromArgs(String[] args) {
        if (args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Puerto inválido, usando " + DEFAULT_PORT);
            }
        }
        return DEFAULT_PORT;
    }

    public static void main(String[] args) {
        

        int port = getPortFromArgs(args);

        ServerApp server = new ServerApp(port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
        server.inicio();
    }
    
    
    
}
