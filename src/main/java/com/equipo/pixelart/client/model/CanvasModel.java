package com.equipo.pixelart.client.model;

import java.awt.Color;


public class CanvasModel {

    private final int width;
    private final int height;
    private Color[][] pixels;

    private static final Color DEFAULT_COLOR = Color.WHITE;

    public CanvasModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Color[width][height];
        clear();
    }

    public CanvasModel() {
        this(48, 48);
    }

    public void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = DEFAULT_COLOR;
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
            for (int y = 0; y < height; y++) {
                copy[x][y] = pixels[x][y];
            }
        }
        return copy;
    }

    public CanvasModel copy() {
        CanvasModel newModel = new CanvasModel(width, height);
        newModel.setPixels(this.getPixels());
        return newModel;
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