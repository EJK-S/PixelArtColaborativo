package com.equipo.pixelart.server.service;

import com.equipo.pixelart.server.persistence.FileManager;
import java.awt.Color;

/**
 * Servicio del Canvas
 * Gestiona el estado maestro del lienzo compartido
 */
public class CanvasService {

    private final FileManager fileManager;
    private final Color[][] canvas;
    private final int width;
    private final int height;

    // Estadísticas
    private long pixelsModificados = 0;

    /**
     * Constructor
     */
    public CanvasService(FileManager fileManager, int width, int height) {
        this.fileManager = fileManager;
        this.width = width;
        this.height = height;

        // Cargar canvas desde archivo o crear uno nuevo
        this.canvas = fileManager.cargarCanvas(width, height);

        System.out.println("CanvasService inicializado: " + width + "x" + height);
    }

    /**
     * Constructor con dimensiones por defecto (48x48)
     */
    public CanvasService(FileManager fileManager) {
        this(fileManager, 48, 48);
    }

    /**
     * Establece el color de un píxel
     */
    public synchronized boolean setPixel(int x, int y, Color color) {
        if (!isValidPosition(x, y)) {
            System.err.println("Posición inválida: (" + x + ", " + y + ")");
            return false;
        }

        if (color == null) {
            System.err.println("Color nulo");
            return false;
        }

        canvas[x][y] = color;
        pixelsModificados++;

        return true;
    }

    /**
     * Obtiene el color de un píxel
     */
    public synchronized Color getPixel(int x, int y) {
        if (!isValidPosition(x, y)) {
            return Color.WHITE;
        }
        return canvas[x][y];
    }

    /**
     * Valida si una posición está dentro del canvas
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Limpia todo el canvas (lo pone blanco)
     */
    public synchronized void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvas[x][y] = Color.WHITE;
            }
        }
        System.out.println("Canvas limpiado");
    }

    /**
     * Obtiene una copia del canvas completo
     */
    public synchronized Color[][] getCanvasCopy() {
        Color[][] copy = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copy[x][y] = canvas[x][y];
            }
        }
        return copy;
    }

    /**
     * Obtiene el canvas serializado como string
     * Formato: RRGGBB,RRGGBB,RRGGBB...
     */
    public synchronized String getCanvasAsString() {
        StringBuilder sb = new StringBuilder();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = canvas[x][y];
                String colorHex = String.format("%02x%02x%02x",
                        color.getRed(), color.getGreen(), color.getBlue());

                sb.append(colorHex);

                // Agregar coma si no es el último píxel
                if (!(x == width - 1 && y == height - 1)) {
                    sb.append(",");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Guarda el canvas en disco
     */
    public synchronized void guardarCanvas() {
        fileManager.guardarCanvas(canvas);
        System.out.println("Canvas guardado en disco");
    }

    /**
     * Guarda el canvas periódicamente (para auto-guardado)
     */
    public void iniciarAutoGuardado(int intervalSegundos) {
        Thread autoSaveThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(intervalSegundos * 1000L);
                    guardarCanvas();
                } catch (InterruptedException e) {
                    System.out.println("Auto-guardado interrumpido");
                    break;
                }
            }
        });

        autoSaveThread.setDaemon(true);
        autoSaveThread.start();

        System.out.println("Auto-guardado iniciado cada " + intervalSegundos + " segundos");
    }

    /**
     * Obtiene estadísticas del canvas
     */
    public String getEstadisticas() {
        return String.format("Canvas: %dx%d | Píxeles modificados: %d",
                width, height, pixelsModificados);
    }

    // ==================== GETTERS ====================

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getPixelsModificados() {
        return pixelsModificados;
    }

    public Color[][] getCanvas() {
        return canvas;
    }
}