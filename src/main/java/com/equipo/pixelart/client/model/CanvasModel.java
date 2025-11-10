package com.equipo.pixelart.client.model;

import java.awt.Color;

/**
 * Modelo del Canvas - Representa el estado del lienzo de dibujo
 * Almacena una matriz bidimensional de píxeles (colores)
 */
public class CanvasModel {

    private final int width;
    private final int height;
    private Color[][] pixels;

    // Color por defecto (blanco)
    private static final Color DEFAULT_COLOR = Color.WHITE;

    /**
     * Constructor con dimensiones personalizadas
     */
    public CanvasModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Color[width][height];
        clear();
    }

    /**
     * Constructor con dimensiones por defecto (48x48)
     */
    public CanvasModel() {
        this(48, 48);
    }

    /**
     * Limpia todo el canvas al color por defecto
     */
    public void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = DEFAULT_COLOR;
            }
        }
    }

    /**
     * Establece el color de un píxel específico
     */
    public void setPixel(int x, int y, Color color) {
        if (isValidPosition(x, y)) {
            pixels[x][y] = color;
        }
    }

    /**
     * Obtiene el color de un píxel específico
     */
    public Color getPixel(int x, int y) {
        if (isValidPosition(x, y)) {
            return pixels[x][y];
        }
        return DEFAULT_COLOR;
    }

    /**
     * Valida si una posición está dentro del canvas
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Establece todo el canvas desde una matriz de píxeles
     */
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

    /**
     * Obtiene una copia de toda la matriz de píxeles
     */
    public Color[][] getPixels() {
        Color[][] copy = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copy[x][y] = pixels[x][y];
            }
        }
        return copy;
    }

    /**
     * Crea una copia profunda del modelo
     */
    public CanvasModel copy() {
        CanvasModel newModel = new CanvasModel(width, height);
        newModel.setPixels(this.getPixels());
        return newModel;
    }

    // ==================== GETTERS ====================

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