package com.equipo.pixelart.client.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.equipo.pixelart.client.model.CanvasModel;


public class CanvasPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private final CanvasModel model;
    private final int pixelSize; 
    private final int gridWidth;
    private final int gridHeight;

    private boolean showGrid;

    public CanvasPanel(CanvasModel model, int pixelSize) {
        this.model = model;
        this.pixelSize = pixelSize;
        this.gridWidth = model.getWidth();
        this.gridHeight = model.getHeight();
        this.showGrid = true;

        setupPanel();
    }

    public CanvasPanel(CanvasModel model) {
        this(model, 12);
    }

    private void setupPanel() {
        int width = gridWidth * pixelSize;
        int height = gridHeight * pixelSize;

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.LIGHT_GRAY);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Antialiasing OFF para que el pixel art se vea nítido (crucial)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        // 1. DIBUJAR FONDO DE AJEDREZ (NUEVO)
        drawCheckerboard(g2d);

        // 2. Dibujar los píxeles del usuario encima
        drawPixels(g2d);

        // 3. Dibujar la rejilla (grid) si está activa
        if (showGrid) {
            drawGrid(g2d);
        }
    }

    private void drawPixels(Graphics2D g2d) {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Color pixelColor = model.getPixel(x, y);

                // Solo ignoramos si es null (por seguridad)
                if (pixelColor == null) continue;

                // --- BORRAMOS EL IF QUE IGNORABA EL BLANCO ---
                // Ya no hay "if (pixelColor.equals(Color.WHITE))..."
                // ---------------------------------------------

                g2d.setColor(pixelColor);
                int screenX = x * pixelSize;
                int screenY = y * pixelSize;

                g2d.fillRect(screenX, screenY, pixelSize, pixelSize);
            }
        }
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(200, 200, 200, 100)); // Gris semi-transparente

        
        for (int x = 0; x <= gridWidth; x++) {
            int screenX = x * pixelSize;
            g2d.drawLine(screenX, 0, screenX, gridHeight * pixelSize);
        }

      
        for (int y = 0; y <= gridHeight; y++) {
            int screenY = y * pixelSize;
            g2d.drawLine(0, screenY, gridWidth * pixelSize, screenY);
        }
    }

 // En CanvasPanel.java

    public Point pixelToGrid(Point screenPoint) {
        if (screenPoint == null) return null;

        // --- FIX: Validar negativos ANTES de dividir ---
        // Esto evita que -5 se convierta en 0
        if (screenPoint.x < 0 || screenPoint.y < 0) {
            return null;
        }
        // -----------------------------------------------

        int gridX = screenPoint.x / pixelSize;
        int gridY = screenPoint.y / pixelSize;

        // Validar que está dentro del canvas (Límite derecho e inferior)
        if (gridX >= 0 && gridX < gridWidth && gridY >= 0 && gridY < gridHeight) {
            return new Point(gridX, gridY);
        }

        return null;
    }

    public Point gridToPixel(Point gridPoint) {
        if (gridPoint == null) return null;

        int screenX = gridPoint.x * pixelSize;
        int screenY = gridPoint.y * pixelSize;

        return new Point(screenX, screenY);
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    public Image exportAsImage() {
        int width = gridWidth * pixelSize;
        int height = gridHeight * pixelSize;

        Image img = createImage(width, height);
        Graphics g = img.getGraphics();
        paint(g);
        g.dispose();

        return img;
    }


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

    public void setToolCursor(String toolName) {
    Cursor cursor;
    
            // Rutas relativas a src/main/resources
            cursor = switch (toolName) {
                case "eraser" -> com.equipo.pixelart.client.utils.ResourceLoader.loadCustomCursor("/images/cursor_eraser.png", "Eraser");
                case "bucket" -> com.equipo.pixelart.client.utils.ResourceLoader.loadCustomCursor("/images/cursor_bucket.png", "Bucket");
                case "eyedropper" -> Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                default -> Cursor.getDefaultCursor();
            }; // Carga el cursor del borrador
            // Si tienes cursor de gotero, úsalo, si no, usa una mano o cruz
            // "pencil" y otros
            // O usa un cursor_pencil.png si lo tienes
    
    this.setCursor(cursor);
}
private void drawCheckerboard(Graphics2D g2d) {
        // CAMBIO: Colores muy sutiles y agradables
        Color c1 = Color.WHITE;
        Color c2 = new Color(240, 240, 240); // Un gris casi blanco (muy suave)

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if ((x + y) % 2 == 0) {
                    g2d.setColor(c1);
                } else {
                    g2d.setColor(c2);
                }
                g2d.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            }
        }
    }
}