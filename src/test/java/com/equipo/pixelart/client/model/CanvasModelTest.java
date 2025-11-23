package com.equipo.pixelart.client.model;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CanvasModelTest {

    private CanvasModel canvasModel;
    private final int TEST_WIDTH = 10;
    private final int TEST_HEIGHT = 10;

    // 1. DEFINIMOS EL COLOR TRANSPARENTE QUE ESPERAMOS AHORA
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    @BeforeEach
    public void setUp() {
        canvasModel = new CanvasModel(TEST_WIDTH, TEST_HEIGHT);
    }

    @Test
    void testConstructors() {
        assertEquals(TEST_WIDTH, canvasModel.getWidth());
        assertEquals(TEST_HEIGHT, canvasModel.getHeight());
        
        CanvasModel defaultModel = new CanvasModel();
        assertEquals(48, defaultModel.getWidth());
        assertEquals(48, defaultModel.getHeight());
        // El color por defecto (fallback) sigue siendo blanco si te sales del mapa, eso está bien.
        assertEquals(Color.WHITE, defaultModel.getDefaultColor());
    }

    @Test
    void testSetAndGetPixel() {
        canvasModel.setPixel(5, 5, Color.RED);
        assertEquals(Color.RED, canvasModel.getPixel(5, 5));

        // Estos devuelven Color.WHITE porque getDefaultColor() sigue siendo blanco
        // para cuando te sales de los límites. Esto está bien.
        canvasModel.setPixel(-1, 0, Color.BLUE);
        assertEquals(Color.WHITE, canvasModel.getPixel(-1, 0), "No debe permitir pintar fuera (negativo)");

        canvasModel.setPixel(TEST_WIDTH + 1, 0, Color.BLUE);
        assertEquals(Color.WHITE, canvasModel.getPixel(TEST_WIDTH + 1, 0), "No debe permitir pintar fuera (exceso)");
    }

    @Test
    void testClear() {
        canvasModel.setPixel(0, 0, Color.BLACK);
        canvasModel.setPixel(1, 1, Color.BLUE);

        canvasModel.clear();

        // CAMBIO: Ahora al limpiar esperamos TRANSPARENTE, no BLANCO
        assertEquals(TRANSPARENT, canvasModel.getPixel(0, 0));
        assertEquals(TRANSPARENT, canvasModel.getPixel(1, 1));
    }

    @Test
    void testSetPixels() {
        canvasModel.setPixels(null);

        Color[][] smallMatrix = new Color[2][2];
        smallMatrix[0][0] = Color.RED;
        smallMatrix[1][1] = null; 
        
        canvasModel.setPixels(smallMatrix);
        
        assertEquals(Color.RED, canvasModel.getPixel(0, 0));
        
        // CAMBIO: El original ahora es TRANSPARENTE, así que si mandas null, se queda TRANSPARENTE
        assertEquals(TRANSPARENT, canvasModel.getPixel(1, 1), "Si es null en la entrada, no debe cambiar el original");

        Color[][] bigMatrix = new Color[TEST_WIDTH + 5][TEST_HEIGHT + 5];
        bigMatrix[0][0] = Color.GREEN;
        
        canvasModel.setPixels(bigMatrix); 
        assertEquals(Color.GREEN, canvasModel.getPixel(0, 0));
    }

    @Test
    void testGetPixelsReturnsDeepCopy() {
        canvasModel.setPixel(0, 0, Color.RED);
        
        Color[][] externalPixels = canvasModel.getPixels();
        externalPixels[0][0] = Color.BLUE; 

        assertEquals(Color.RED, canvasModel.getPixel(0, 0), "getPixels() debe devolver una copia segura");
    }

    @Test
    void testCopyModel() {
        canvasModel.setPixel(2, 2, Color.MAGENTA);
        
        CanvasModel clon = canvasModel.copy();
        
        assertNotSame(canvasModel, clon); 
        assertEquals(Color.MAGENTA, clon.getPixel(2, 2)); 
        assertEquals(canvasModel.getWidth(), clon.getWidth());
    }

    @Test
    void testUndoBasic() {
        // Guardamos el estado inicial (que es todo TRANSPARENTE)
        canvasModel.saveState();

        canvasModel.setPixel(0, 0, Color.RED);
        assertEquals(Color.RED, canvasModel.getPixel(0, 0));

        assertTrue(canvasModel.canUndo());
        boolean result = canvasModel.undo();

        assertTrue(result);
        
        // CAMBIO: Al deshacer, volvemos al estado inicial, que es TRANSPARENTE
        assertEquals(TRANSPARENT, canvasModel.getPixel(0, 0), "Debe volver a transparente");
    }

    @Test
    void testUndoEmptyStack() {
        assertFalse(canvasModel.canUndo());
        boolean result = canvasModel.undo();
        
        assertFalse(result, "Undo debe retornar false si la pila está vacía");
    }

    @Test
    void testUndoLimit() {
        for (int i = 0; i < 55; i++) {
            canvasModel.setPixel(0, 0, new Color(i, i, i)); 
            canvasModel.saveState();
        }

        int undoCount = 0;
        while (canvasModel.canUndo()) {
            canvasModel.undo();
            undoCount++;
        }

        assertEquals(50, undoCount, "El historial no debe exceder 50 pasos");
    }
    
    @Test
    void testIsValidPosition() {
        assertTrue(canvasModel.isValidPosition(0, 0));
        assertTrue(canvasModel.isValidPosition(TEST_WIDTH - 1, TEST_HEIGHT - 1));
        
        assertFalse(canvasModel.isValidPosition(-1, 0));
        assertFalse(canvasModel.isValidPosition(0, -1));
        assertFalse(canvasModel.isValidPosition(TEST_WIDTH, 0));
        assertFalse(canvasModel.isValidPosition(0, TEST_HEIGHT));
    }
}