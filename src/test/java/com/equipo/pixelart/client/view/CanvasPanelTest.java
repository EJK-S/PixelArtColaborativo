package com.equipo.pixelart.client.view;

import com.equipo.pixelart.client.model.CanvasModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CanvasPanelTest {

    private CanvasPanel canvasPanel;
    private CanvasModel mockModel;
    private final int PIXEL_SIZE = 10;
    private final int GRID_W = 5;
    private final int GRID_H = 5;

    @BeforeEach
    void setUp() {
        // 1. Mock del Modelo
        mockModel = mock(CanvasModel.class);
        when(mockModel.getWidth()).thenReturn(GRID_W);
        when(mockModel.getHeight()).thenReturn(GRID_H);
        when(mockModel.getPixel(anyInt(), anyInt())).thenReturn(Color.WHITE);

        // 2. Instancia del Panel
        canvasPanel = new CanvasPanel(mockModel, PIXEL_SIZE);
    }

    @Test
    void testConstructors() {
        // Constructor principal
        assertEquals(PIXEL_SIZE, canvasPanel.getPixelSize());
        assertEquals(GRID_W, canvasPanel.getGridWidth());
        
        // Constructor secundario (por defecto pixelSize = 12)
        CanvasPanel defaultPanel = new CanvasPanel(mockModel);
        assertEquals(12, defaultPanel.getPixelSize());
    }

    @Test
    void testSetupPanelProperties() {
        // Verifica que se calcularon bien las dimensiones preferidas
        Dimension dim = canvasPanel.getPreferredSize();
        assertEquals(GRID_W * PIXEL_SIZE, dim.width);
        assertEquals(GRID_H * PIXEL_SIZE, dim.height);
        
        assertNotNull(canvasPanel.getBorder());
        assertEquals(Color.LIGHT_GRAY, canvasPanel.getBackground());
    }

    @Test
    void testShowGrid() {
        // Por defecto es true
        assertTrue(canvasPanel.isShowGrid());

        // Cambiar a false
        canvasPanel.setShowGrid(false);
        assertFalse(canvasPanel.isShowGrid());

        // Cambiar a true
        canvasPanel.setShowGrid(true);
        assertTrue(canvasPanel.isShowGrid());
    }

    // ==============================================
    // TEST DE CONVERSIÓN DE COORDENADAS (Lógica pura)
    // ==============================================

    @Test
    void testPixelToGrid() {
        // Caso ideal: Clic en (15, 15) con pixel 10 -> Celda (1, 1)
        Point click = new Point(15, 15);
        Point grid = canvasPanel.pixelToGrid(click);
        assertEquals(new Point(1, 1), grid);

        // Caso borde: (0,0) -> (0,0)
        assertEquals(new Point(0, 0), canvasPanel.pixelToGrid(new Point(0, 0)));
    }

    @Test
    void testPixelToGridNull() {
        assertNull(canvasPanel.pixelToGrid(null));
    }

    @Test
    void testPixelToGridOutOfBounds() {
        // Negativo
    	
        assertNull(canvasPanel.pixelToGrid(new Point(-5, 5)));
        assertNull(canvasPanel.pixelToGrid(new Point(5, -5)));

        // Fuera del ancho (5 * 10 = 50px ancho total)
        assertNull(canvasPanel.pixelToGrid(new Point(55, 10))); // X se pasa
        assertNull(canvasPanel.pixelToGrid(new Point(10, 55))); // Y se pasa
    }

    @Test
    void testGridToPixel() {
        // Celda (2, 3) * 10px -> (20, 30)
        Point pixel = canvasPanel.gridToPixel(new Point(2, 3));
        assertEquals(new Point(20, 30), pixel);
    }

    @Test
    void testGridToPixelNull() {
        assertNull(canvasPanel.gridToPixel(null));
    }

    // ==============================================
    // TEST DE DIBUJADO (PAINTING) - EL TRUCO
    // ==============================================

    @Test
    void testPaintComponentConGrid() {
        // 1. Crear una imagen vacía en memoria (simula la pantalla)
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
     // --- FIX: Damos tamaño al panel para que Swing se digne a pintarlo ---
        canvasPanel.setSize(GRID_W * PIXEL_SIZE, GRID_H * PIXEL_SIZE); 
        // --------------------------------------------------------------------

        // 2. Forzar el pintado sobre esa imagen
        canvasPanel.setShowGrid(true);
        canvasPanel.paint(g2d); // Esto llama internamente a paintComponent, drawPixels y drawGrid

        // 3. Verificar que interactuó con el modelo
        // Si llamó a getPixel, significa que el bucle de dibujo se ejecutó
        verify(mockModel, atLeastOnce()).getPixel(anyInt(), anyInt());
        
        g2d.dispose();
    }

    @Test
    void testPaintComponentSinGrid() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        canvasPanel.setSize(GRID_W * PIXEL_SIZE, GRID_H * PIXEL_SIZE);
        canvasPanel.setShowGrid(false);
        canvasPanel.paint(g2d); 
        
        // Se dibuja igual, pero el if(showGrid) será falso.
        // Verificamos que siga dibujando píxeles
        verify(mockModel, atLeastOnce()).getPixel(anyInt(), anyInt());
        
        g2d.dispose();
    }

    @Test
    void testExportAsImage() {
        // Este es delicado. createImage() devuelve null si el componente no es "Displayable" 
        // (no está en una ventana).
        // Truco: Lo metemos en un JFrame invisible temporalmente.
        
        JFrame frame = new JFrame();
        frame.add(canvasPanel);
        frame.pack(); // Esto hace que el componente sea "displayable"
        
        Image img = canvasPanel.exportAsImage();
        
        assertNotNull(img, "La imagen exportada no debería ser nula");
        assertEquals(GRID_W * PIXEL_SIZE, img.getWidth(null));
        assertEquals(GRID_H * PIXEL_SIZE, img.getHeight(null));
        
        frame.dispose();
    }
}