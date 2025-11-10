package com.equipo.pixelart.client.model;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas Unitarias para el CanvasModel.
 * Verifica que la lógica de píxeles (el "grid") funcione.
 */
class CanvasModelTest {

    private CanvasModel canvasModel;
    private final int ANCHO = 48;
    private final int ALTO = 48;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        // Crear un nuevo modelo de lienzo antes de cada prueba
        canvasModel = new CanvasModel(ANCHO, ALTO);
    }

    @Test
    void testDimensionesConstructor() {
        assertEquals(ANCHO, canvasModel.getWidth(), "El ancho debe ser " + ANCHO);
        assertEquals(ALTO, canvasModel.getHeight(), "El alto debe ser " + ALTO);
    }

    @Test
    void testClearPoneColorDefault() {
        // 1. Poner un píxel de un color
        canvasModel.setPixel(10, 10, Color.RED);
        
        // 2. Limpiar el lienzo
        canvasModel.clear();
        
        // 3. Comprobar que el píxel volvió al color por defecto (Blanco)
        assertEquals(Color.WHITE, canvasModel.getPixel(10, 10), "El píxel (10,10) debería ser blanco después de 'clear'");
    }

    @Test
    void testSetAndGetPixel() {
        // Probar poner un píxel y leerlo
        canvasModel.setPixel(5, 5, Color.BLUE);
        assertEquals(Color.BLUE, canvasModel.getPixel(5, 5), "El píxel (5,5) debería ser AZUL");
    }

    @Test
    void testSetPixelFueraDeRango() {
        // Probar poner un píxel fuera de los límites (no debe fallar)
        assertDoesNotThrow(() -> canvasModel.setPixel(-1, -1, Color.RED), "Poner un píxel en (-1,-1) no debe lanzar una excepción");
        assertDoesNotThrow(() -> canvasModel.setPixel(ANCHO, ALTO, Color.RED), "Poner un píxel fuera de rango no debe lanzar una excepción");
    }

    @Test
    void testIsValidPosition() {
        assertTrue(canvasModel.isValidPosition(0, 0), "El píxel (0,0) es válido");
        assertTrue(canvasModel.isValidPosition(47, 47), "El píxel (47,47) es válido");
        assertFalse(canvasModel.isValidPosition(-1, 0), "El píxel (-1,0) NO es válido");
        assertFalse(canvasModel.isValidPosition(0, -1), "El píxel (0,-1) NO es válido");
        assertFalse(canvasModel.isValidPosition(ANCHO, ALTO), "El píxel (48,48) NO es válido");
    }
}