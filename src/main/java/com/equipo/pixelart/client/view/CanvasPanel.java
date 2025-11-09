package com.equipo.pixelart.client.view;

import com.equipo.pixelart.client.model.CanvasModel;
import javax.swing.*;
import java.awt.*;

/**
 * Panel del Canvas - Vista principal donde se dibuja el pixel art
 */
public class CanvasPanel extends JPanel {

    private final CanvasModel model;
    private final int pixelSize; // Tamaño de cada píxel en pantalla
    private final int gridWidth;
    private final int gridHeight;

    private boolean showGrid;

    /**
     * Constructor
     */
    public CanvasPanel(CanvasModel model, int pixelSize) {
        this.model = model;
        this.pixelSize = pixelSize;
        this.gridWidth = model.getWidth();
        this.gridHeight = model.getHeight();
        this.showGrid = true;

        setupPanel();
    }

    /**
     * Constructor con tamaño de píxel por defecto (12px)
     */
    public CanvasPanel(CanvasModel model) {
        this(model, 12);
    }

    /**
     * Configura las propiedades del panel
     */
    private void setupPanel() {
        int width = gridWidth * pixelSize;
        int height = gridHeight * pixelSize;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    }

    /**
     * Dibuja el canvas
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Antialiasing para mejor calidad visual
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        // Dibujar píxeles
        drawPixels(g2d);

        // Dibujar grid si está activado
        if (showGrid) {
            drawGrid(g2d);
        }
    }

    /**
     * Dibuja todos los píxeles del canvas
     */
    private void drawPixels(Graphics2D g2d) {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Color pixelColor = model.getPixel(x, y);
                g2d.setColor(pixelColor);

                int screenX = x * pixelSize;
                int screenY = y * pixelSize;

                g2d.fillRect(screenX, screenY, pixelSize, pixelSize);
            }
        }
    }

    /**
     * Dibuja la cuadrícula
     */
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(200, 200, 200, 100)); // Gris semi-transparente

        // Líneas verticales
        for (int x = 0; x <= gridWidth; x++) {
            int screenX = x * pixelSize;
            g2d.drawLine(screenX, 0, screenX, gridHeight * pixelSize);
        }

        // Líneas horizontales
        for (int y = 0; y <= gridHeight; y++) {
            int screenY = y * pixelSize;
            g2d.drawLine(0, screenY, gridWidth * pixelSize, screenY);
        }
    }

    /**
     * Convierte coordenadas de pantalla a coordenadas de grid
     */
    public Point pixelToGrid(Point screenPoint) {
        if (screenPoint == null) return null;

        int gridX = screenPoint.x / pixelSize;
        int gridY = screenPoint.y / pixelSize;

        // Validar que está dentro del canvas
        if (gridX >= 0 && gridX < gridWidth && gridY >= 0 && gridY < gridHeight) {
            return new Point(gridX, gridY);
        }

        return null;
    }

    /**
     * Convierte coordenadas de grid a coordenadas de pantalla
     */
    public Point gridToPixel(Point gridPoint) {
        if (gridPoint == null) return null;

        int screenX = gridPoint.x * pixelSize;
        int screenY = gridPoint.y * pixelSize;

        return new Point(screenX, screenY);
    }

    /**
     * Activa o desactiva la visualización del grid
     */
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    /**
     * Exporta el canvas como imagen
     */
    public Image exportAsImage() {
        int width = gridWidth * pixelSize;
        int height = gridHeight * pixelSize;

        Image img = createImage(width, height);
        Graphics g = img.getGraphics();
        paint(g);
        g.dispose();

        return img;
    }

    // ==================== GETTERS ====================

    public boolean isShowGrid() {
        return showGrid;
    }

    public int getPixelSize() {
        return pixelSize;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }
}