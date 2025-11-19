package com.equipo.pixelart.server.service;

import com.equipo.pixelart.server.persistence.FileManager;
import java.awt.Color;
import java.util.Stack;


public class CanvasService {

    private final FileManager fileManager;
    private final Color[][] canvas;
    private final int width;
    private final int height;
    private Stack<Color[][]> undoStack;
    private static final int MAX_UNDO_STEPS = 50;

    private long pixelsModificados = 0;

    public CanvasService(FileManager fileManager, int width, int height) {
        this.fileManager = fileManager;
        this.width = width;
        this.height = height;
        this.undoStack = new Stack<>();

        this.canvas = fileManager.cargarCanvas(width, height);

        System.out.println("CanvasService inicializado: " + width + "x" + height);
    }

    public CanvasService(FileManager fileManager) {
        this(fileManager, 48, 48);
    }

    public synchronized boolean setPixel(int x, int y, Color color) {
        if (!isValidPosition(x, y)) {
            System.err.println("Posición inválida: (" + x + ", " + y + ")");
            return false;
        }

        if (color == null) {
            System.err.println("Color nulo");
            return false;
        }
        // Guardar estado antes del cambio (solo si cambió el color)
        if (!canvas[x][y].equals(color)) {
            saveState();
        }

        canvas[x][y] = color;
        pixelsModificados++;

        return true;
    }

    public synchronized Color getPixel(int x, int y) {
        if (!isValidPosition(x, y)) {
            return Color.WHITE;
        }
        return canvas[x][y];
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public synchronized void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvas[x][y] = Color.WHITE;
            }
        }
        System.out.println("Canvas limpiado");
    }

    public synchronized Color[][] getCanvasCopy() {
        Color[][] copy = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copy[x][y] = canvas[x][y];
            }
        }
        return copy;
    }

    public synchronized String getCanvasAsString() {
        StringBuilder sb = new StringBuilder();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = canvas[x][y];
                String colorHex = String.format("%02x%02x%02x",
                        color.getRed(), color.getGreen(), color.getBlue());

                sb.append(colorHex);

                
                if (!(x == width - 1 && y == height - 1)) {
                    sb.append(",");
                }
            }
        }

        return sb.toString();
    }

    public synchronized void guardarCanvas() {
        fileManager.guardarCanvas(canvas);
        System.out.println("Canvas guardado en disco");
    }

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

    public String getEstadisticas() {
        return String.format("Canvas: %dx%d | Píxeles modificados: %d",
                width, height, pixelsModificados);
    }
    /**
     * Guarda el estado actual en el historial
     */
    private synchronized void saveState() {
        Color[][] copy = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copy[x][y] = canvas[x][y];
            }
        }

        undoStack.push(copy);

        if (undoStack.size() > MAX_UNDO_STEPS) {
            undoStack.remove(0);
        }
    }

    /**
     * Deshace el último cambio
     */
    public synchronized boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        Color[][] previousState = undoStack.pop();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvas[x][y] = previousState[x][y];
            }
        }

        return true;
    }


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