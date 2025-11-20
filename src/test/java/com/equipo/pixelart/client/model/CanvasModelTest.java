package com.equipo.pixelart.client.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

class CanvasModelTest {

    private CanvasModel canvasModel;
    private final int TEST_WIDTH = 10;
    private final int TEST_HEIGHT = 10;

    @BeforeEach
    void setUp() {
        // Usamos un tamaño pequeño (10x10) para facilitar las pruebas
        canvasModel = new CanvasModel(TEST_WIDTH, TEST_HEIGHT);
    }

    @Test
    void testConstructors() {
        // Test Constructor Principal
        assertEquals(TEST_WIDTH, canvasModel.getWidth());
        assertEquals(TEST_HEIGHT, canvasModel.getHeight());
        
        // Test Constructor Por Defecto (48x48)
        CanvasModel defaultModel = new CanvasModel();
        assertEquals(48, defaultModel.getWidth());
        assertEquals(48, defaultModel.getHeight());
        assertEquals(Color.WHITE, defaultModel.getDefaultColor());
    }

    @Test
    void testSetAndGetPixel() {
        // Caso válido
        canvasModel.setPixel(5, 5, Color.RED);
        assertEquals(Color.RED, canvasModel.getPixel(5, 5));

        // Caso inválido (fuera de límites negativos)
        canvasModel.setPixel(-1, 0, Color.BLUE);
        assertEquals(Color.WHITE, canvasModel.getPixel(-1, 0), "No debe permitir pintar fuera (negativo)");

        // Caso inválido (fuera de límites positivos)
        canvasModel.setPixel(TEST_WIDTH + 1, 0, Color.BLUE);
        assertEquals(Color.WHITE, canvasModel.getPixel(TEST_WIDTH + 1, 0), "No debe permitir pintar fuera (exceso)");
    }

    @Test
    void testClear() {
        // Pintamos algo
        canvasModel.setPixel(0, 0, Color.BLACK);
        canvasModel.setPixel(1, 1, Color.BLUE);

        // Limpiamos
        canvasModel.clear();

        // Verificamos que todo volvió a BLANCO
        assertEquals(Color.WHITE, canvasModel.getPixel(0, 0));
        assertEquals(Color.WHITE, canvasModel.getPixel(1, 1));
    }

    @Test
    void testSetPixels() {
        // 1. Caso NULL (Protección)
        canvasModel.setPixels(null);
        // No debe explotar

        // 2. Caso Matriz más pequeña que el canvas
        Color[][] smallMatrix = new Color[2][2];
        smallMatrix[0][0] = Color.RED;
        smallMatrix[1][1] = null; // Probamos el check de nulidad interno
        
        canvasModel.setPixels(smallMatrix);
        
        assertEquals(Color.RED, canvasModel.getPixel(0, 0));
        assertEquals(Color.WHITE, canvasModel.getPixel(1, 1), "Si es null en la entrada, no debe cambiar el original");

        // 3. Caso Matriz más grande que el canvas (Math.min check)
        Color[][] bigMatrix = new Color[TEST_WIDTH + 5][TEST_HEIGHT + 5];
        bigMatrix[0][0] = Color.GREEN;
        
        canvasModel.setPixels(bigMatrix); // No debe dar IndexOutOfBounds
        assertEquals(Color.GREEN, canvasModel.getPixel(0, 0));
    }

    @Test
    void testGetPixelsReturnsDeepCopy() {
        // Verificamos que getPixels devuelve una COPIA y no la referencia real
        canvasModel.setPixel(0, 0, Color.RED);
        
        Color[][] externalPixels = canvasModel.getPixels();
        externalPixels[0][0] = Color.BLUE; // Modificamos la copia externa
        
        // El modelo original debe seguir siendo ROJO
        assertEquals(Color.RED, canvasModel.getPixel(0, 0), "getPixels() debe devolver una copia segura");
    }

    @Test
    void testCopyModel() {
        canvasModel.setPixel(2, 2, Color.MAGENTA);
        
        CanvasModel clon = canvasModel.copy();
        
        assertNotSame(canvasModel, clon); // Son objetos distintos en memoria
        assertEquals(Color.MAGENTA, clon.getPixel(2, 2)); // Tienen los mismos datos
        assertEquals(canvasModel.getWidth(), clon.getWidth());
    }

    @Test
    void testUndoBasic() {
        // 1. Estado Inicial (Blanco)
        canvasModel.saveState(); // Guardamos estado blanco

        // 2. Pintamos Rojo
        canvasModel.setPixel(0, 0, Color.RED);
        assertEquals(Color.RED, canvasModel.getPixel(0, 0));

        // 3. Deshacemos
        assertTrue(canvasModel.canUndo());
        boolean result = canvasModel.undo();

        assertTrue(result);
        assertEquals(Color.WHITE, canvasModel.getPixel(0, 0), "Debe volver a blanco");
    }

    @Test
    void testUndoEmptyStack() {
        // No hemos guardado nada
        assertFalse(canvasModel.canUndo());
        boolean result = canvasModel.undo();
        
        assertFalse(result, "Undo debe retornar false si la pila está vacía");
    }

    @Test
    void testUndoLimit() {
        // El modelo tiene MAX_UNDO_STEPS = 50.
        // Vamos a guardar 55 estados.
        for (int i = 0; i < 55; i++) {
            canvasModel.setPixel(0, 0, new Color(i, i, i)); // Cambiamos algo
            canvasModel.saveState();
        }

        // Si el límite funciona, deberíamos poder hacer undo 50 veces, no 55.
        int undoCount = 0;
        while (canvasModel.canUndo()) {
            canvasModel.undo();
            undoCount++;
        }

        // Nota: Dependiendo de cómo implementes el límite, podría quedarse en 50 o 51.
        // Tu código dice: if (size > 50) remove(0). Así que el tamaño máximo es 50.
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