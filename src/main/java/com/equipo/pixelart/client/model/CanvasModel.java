package com.equipo.pixelart.client.model;

import java.awt.Color;
import java.util.Stack;


public class CanvasModel {

    private final int width;
    private final int height;
    private Color[][] pixels;
    private Stack<Color[][]> undoStack;
    private static final int MAX_UNDO_STEPS = 50;

    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

    public CanvasModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Color[width][height];

        this.undoStack = new Stack<>();

        // Inicializar el canvas TOTALMENTE TRANSPARENTE
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = TRANSPARENT_COLOR; // Antes era Color.WHITE
            }
        }
    }

    public CanvasModel() {
        this(48, 48);
    }

    public void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = TRANSPARENT_COLOR; // Antes era Color.WHITE
            }
        }
    }

    public void setPixel(int x, int y, Color color) {
        if (isValidPosition(x, y)) {
            pixels[x][y] = color;
        }
    }

    public Color getPixel(int x, int y) {
        if (isValidPosition(x, y)) {
            return pixels[x][y];
        }
        return DEFAULT_COLOR;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void setPixels(Color[][] newPixels) {
        if (newPixels == null) return;

        int minWidth = Math.min(width, newPixels.length);
        int minHeight = newPixels.length > 0 ? Math.min(height, newPixels[0].length) : 0;

        for (int x = 0; x < minWidth; x++) {
            for (int y = 0; y < minHeight; y++) {
                if (newPixels[x][y] != null) {
                    pixels[x][y] = newPixels[x][y];
                }
            }
        }
    }

    public Color[][] getPixels() {
        Color[][] copy = new Color[width][height];
        for (int x = 0; x < width; x++) {
            System.arraycopy(pixels[x], 0, copy[x], 0, height);
        }
        return copy;
    }

    public CanvasModel copy() {
        CanvasModel newModel = new CanvasModel(width, height);
        newModel.setPixels(this.getPixels());
        return newModel;
    }
    /**
     * Guarda el estado actual en el historial
     */
    public void saveState() {
        // Crear copia del canvas actual
        Color[][] copy = new Color[width][height];
        for (int x = 0; x < width; x++) {
            System.arraycopy(pixels[x], 0, copy[x], 0, height);
        }

        undoStack.push(copy);

        // Limitar el tamaño del historial
        if (undoStack.size() > MAX_UNDO_STEPS) {
            undoStack.remove(0);
        }
    }

    /**
     * Deshace el último cambio
     */
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        Color[][] previousState = undoStack.pop();

        // Restaurar estado anterior
        for (int x = 0; x < width; x++) {
            System.arraycopy(previousState[x], 0, pixels[x], 0, height);
        }

        return true;
    }

    /**
     * Verifica si hay cambios para deshacer
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getDefaultColor() {
        return DEFAULT_COLOR;
    }
}