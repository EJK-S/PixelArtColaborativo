package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.model.CanvasModel;
import com.equipo.pixelart.client.view.CanvasPanel;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Controlador del Canvas - Gestiona las interacciones de dibujo del usuario
 * Patrón MVC: Conecta la vista (CanvasPanel) con el modelo (CanvasModel)
 */
public class CanvasController {

    private final CanvasModel model;
    private final CanvasPanel view;
    private final NetworkController networkController;

    // Estado actual de la herramienta
    private Color currentColor;
    private String currentTool; // "pencil", "eraser", "bucket", "eyedropper"
    private int brushSize;

    // Control de arrastre
    private boolean isDragging;
    private Point lastPoint;

    /**
     * Constructor
     */
    public CanvasController(CanvasModel model, CanvasPanel view, NetworkController networkController) {
        this.model = model;
        this.view = view;
        this.networkController = networkController;

        // Valores por defecto
        this.currentColor = Color.BLACK;
        this.currentTool = "pencil";
        this.brushSize = 1;
        this.isDragging = false;

        initializeListeners();
    }

    /**
     * Inicializa los listeners del mouse para el canvas
     */
    private void initializeListeners() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        });

        view.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }

    /**
     * Maneja el evento de presionar el mouse
     */
    private void handleMousePressed(MouseEvent e) {
        Point gridPoint = view.pixelToGrid(e.getPoint());

        if (gridPoint == null) return; // Fuera del canvas

        isDragging = true;
        lastPoint = gridPoint;

        // Aplicar herramienta según el tipo
        if (currentTool.equals("pencil") || currentTool.equals("eraser")) {
            drawPixel(gridPoint);
        }
    }

    /**
     * Maneja el evento de soltar el mouse
     */
    private void handleMouseReleased(MouseEvent e) {
        isDragging = false;
        lastPoint = null;
    }

    /**
     * Maneja el evento de click del mouse
     */
    private void handleMouseClicked(MouseEvent e) {
        Point gridPoint = view.pixelToGrid(e.getPoint());

        if (gridPoint == null) return;

        switch (currentTool) {
            case "bucket":
                floodFill(gridPoint);
                break;
            case "eyedropper":
                pickColor(gridPoint);
                break;
        }
    }

    /**
     * Maneja el evento de arrastrar el mouse
     */
    private void handleMouseDragged(MouseEvent e) {
        if (!isDragging) return;

        Point gridPoint = view.pixelToGrid(e.getPoint());

        if (gridPoint == null) return;

        // Dibujar línea desde el último punto (para evitar huecos)
        if (lastPoint != null && (currentTool.equals("pencil") || currentTool.equals("eraser"))) {
            drawLine(lastPoint, gridPoint);
        }

        lastPoint = gridPoint;
    }

    /**
     * Dibuja un píxel en la posición especificada
     */
    private void drawPixel(Point p) {
        Color colorToUse = currentTool.equals("eraser") ? Color.WHITE : currentColor;

        // Aplicar brush size
        for (int dx = -brushSize + 1; dx < brushSize; dx++) {
            for (int dy = -brushSize + 1; dy < brushSize; dy++) {
                int x = p.x + dx;
                int y = p.y + dy;

                if (model.isValidPosition(x, y)) {
                    // Actualizar modelo local
                    model.setPixel(x, y, colorToUse);

                    // Enviar al servidor
                    networkController.sendPixelUpdate(x, y, colorToUse);
                }
            }
        }

        // Refrescar vista
        view.repaint();
    }

    /**
     * Dibuja una línea entre dos puntos (algoritmo de Bresenham)
     */
    private void drawLine(Point p1, Point p2) {
        int dx = Math.abs(p2.x - p1.x);
        int dy = Math.abs(p2.y - p1.y);
        int sx = p1.x < p2.x ? 1 : -1;
        int sy = p1.y < p2.y ? 1 : -1;
        int err = dx - dy;

        int x = p1.x;
        int y = p1.y;

        while (true) {
            drawPixel(new Point(x, y));

            if (x == p2.x && y == p2.y) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    /**
     * Relleno de área (Flood Fill)
     */
    private void floodFill(Point start) {
        if (!model.isValidPosition(start.x, start.y)) return;

        Color targetColor = model.getPixel(start.x, start.y);

        // No hacer nada si el color es el mismo
        if (targetColor.equals(currentColor)) return;

        floodFillRecursive(start.x, start.y, targetColor);

        view.repaint();
    }

    /**
     * Algoritmo recursivo de flood fill
     */
    private void floodFillRecursive(int x, int y, Color targetColor) {
        if (!model.isValidPosition(x, y)) return;
        if (!model.getPixel(x, y).equals(targetColor)) return;

        // Establecer nuevo color
        model.setPixel(x, y, currentColor);
        networkController.sendPixelUpdate(x, y, currentColor);

        // Llamadas recursivas a los 4 vecinos
        floodFillRecursive(x + 1, y, targetColor);
        floodFillRecursive(x - 1, y, targetColor);
        floodFillRecursive(x, y + 1, targetColor);
        floodFillRecursive(x, y - 1, targetColor);
    }

    /**
     * Toma el color del píxel seleccionado (Eyedropper)
     */
    private void pickColor(Point p) {
        if (!model.isValidPosition(p.x, p.y)) return;

        Color pickedColor = model.getPixel(p.x, p.y);
        setCurrentColor(pickedColor);

        // Cambiar de vuelta a pencil después de tomar el color
        setCurrentTool("pencil");
    }

    /**
     * Actualiza el canvas completo desde el servidor
     */
    public void updateCanvas(Color[][] pixels) {
        model.setPixels(pixels);
        view.repaint();
    }

    /**
     * Actualiza un píxel individual desde el servidor
     */
    public void updatePixelFromServer(int x, int y, Color color) {
        if (model.isValidPosition(x, y)) {
            model.setPixel(x, y, color);
            view.repaint();
        }
    }

    /**
     * Limpia todo el canvas
     */
    public void clearCanvas() {
        model.clear();
        networkController.sendClearCanvas();
        view.repaint();
    }

    // ==================== SETTERS ====================

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setCurrentTool(String tool) {
        this.currentTool = tool;
    }

    public void setBrushSize(int size) {
        this.brushSize = Math.max(1, size);
    }

    // ==================== GETTERS ====================

    public Color getCurrentColor() {
        return currentColor;
    }

    public String getCurrentTool() {
        return currentTool;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public CanvasModel getModel() {
        return model;
    }
}