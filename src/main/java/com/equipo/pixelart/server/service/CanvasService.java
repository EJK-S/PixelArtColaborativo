package com.equipo.pixelart.server.service;

import java.awt.Color;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.equipo.pixelart.server.persistence.FileManager;

public final class CanvasService {

    private final FileManager fileManager;
    private final Color[][] canvas;
    private final int width;
    private final int height;
    private Stack<Color[][]> undoStack;
    
    
    private static final int MAX_UNDO_STEPS = 50;

    
    private static final Color DEFAULT_BG = new Color(0, 0, 0, 0); 

    private long pixelsModificados = 0;

    public CanvasService(FileManager fileManager, int width, int height) {
        this.fileManager = fileManager;
        this.width = width;
        this.height = height;
        this.undoStack = new Stack<>();

        Color[][] loaded = fileManager.cargarCanvas(width, height);
        if (loaded != null) {
            this.canvas = loaded;
        } else {
            this.canvas = new Color[width][height];
            clear(); 
        }
        
        
        saveState();

        System.out.println("CanvasService inicializado: " + width + "x" + height);
    }

    public CanvasService(FileManager fileManager) {
        this(fileManager, 48, 48);
    }

    public synchronized boolean setPixel(int x, int y, Color color) {
        if (!isValidPosition(x, y)) return false;
        if (color == null) return false;
        
        Color colorActual = canvas[x][y];
        if (colorActual.equals(color)) {
            return false; 
        }
        
        saveState();
        canvas[x][y] = color;
        pixelsModificados++;

        return true;
    }

    public synchronized Color getPixel(int x, int y) {
        if (!isValidPosition(x, y)) {
            return DEFAULT_BG;
        }
        return canvas[x][y];
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public synchronized void clear() {
        
        saveState();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvas[x][y] = DEFAULT_BG;
            }
        }
        System.out.println("Canvas limpiado");
    }

    public synchronized String getCanvasAsString() {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = canvas[x][y];
                String colorHex = String.format("%02x%02x%02x%02x",
                        color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                sb.append(colorHex);
                if (!(x == width - 1 && y == height - 1)) sb.append(",");
            }
        }
        return sb.toString();
    }

    public synchronized void guardarCanvas() {
        fileManager.guardarCanvas(canvas);
        System.out.println("Canvas guardado en disco");
    }

    public void iniciarAutoGuardado(int intervalSegundos) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); 
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try { guardarCanvas(); } catch (Exception e) {
                System.err.println("Error auto-guardado: " + e.getMessage());
            }
        }, intervalSegundos, intervalSegundos, TimeUnit.SECONDS);
    }

    public String getEstadisticas() {
        return String.format("Canvas: %dx%d | Píxeles modificados: %d",
                width, height, pixelsModificados);
    }
    
    
    public void saveState() {
        Color[][] copy = new Color[width][height];
        for (int x = 0; x < width; x++) {
            System.arraycopy(canvas[x], 0, copy[x], 0, height);
        }
        
        undoStack.push(copy);
        
        
        if (undoStack.size() > MAX_UNDO_STEPS) {
            undoStack.remove(0);
        }
    }

    public synchronized boolean undo() {
        if (undoStack.isEmpty()) return false;
        Color[][] prev = undoStack.pop();
        for(int x=0; x<width; x++) System.arraycopy(prev[x], 0, canvas[x], 0, height);
        return true;
    }

    public long getPixelsModificados() {
        return pixelsModificados;
    }

    public synchronized Color[][] getCanvasCopy() {
        Color[][] copy = new Color[width][height];
        for (int x = 0; x < width; x++) {
            System.arraycopy(canvas[x], 0, copy[x], 0, height);
        }
        return copy;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Color[][] getCanvas() { return canvas; }
}