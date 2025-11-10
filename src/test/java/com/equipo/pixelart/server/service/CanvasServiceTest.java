package com.equipo.pixelart.server.service;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.equipo.pixelart.server.persistence.FileManager;

/**
 * Pruebas Unitarias para CanvasService.
 * Prueba la lógica del "lienzo maestro" del servidor.
 */
class CanvasServiceTest {

    private CanvasService canvasService;
    private FileManager mockFileManager;

    private final int TEST_ANCHO = 10;
    private final int TEST_ALTO = 10;
    private Color[][] canvasFalso;

    @SuppressWarnings("unused")

    @BeforeEach
    void setUp() {
        // 1. Crear el "Mock" (simulador) de FileManager
        mockFileManager = mock(FileManager.class);

        // 2. Crear un lienzo falso que el mock devolverá
        // Lo llenamos de blanco por defecto
        canvasFalso = new Color[TEST_ANCHO][TEST_ALTO];
        for (int x = 0; x < TEST_ANCHO; x++) {
            for (int y = 0; y < TEST_ALTO; y++) {
                canvasFalso[x][y] = Color.WHITE;
            }
        }

        // 3. Programar el Mock:
        // "Cuando llamen a cargarCanvas(), devuelve mi lienzo falso"
        when(mockFileManager.cargarCanvas(TEST_ANCHO, TEST_ALTO)).thenReturn(canvasFalso);

        // 4. Crear el CanvasService REAL, pasándole el Mock
        // El CanvasService creerá que está hablando con un FileManager real.
        canvasService = new CanvasService(mockFileManager, TEST_ANCHO, TEST_ALTO);
    }

    @Test
    void testConstructorCargaElLienzo() {
        // Verifica que el constructor llamó a mockFileManager.cargarCanvas()
        // Esto prueba que el servicio se inicializa correctamente.
        verify(mockFileManager).cargarCanvas(TEST_ANCHO, TEST_ALTO);
    }

    @Test
    void testSetPixelYGetCanvasAsString() {
        // 1. Preparación: Crear un lienzo 2x2 para que la prueba sea simple
        Color[][] lienzoPequeno = { {Color.WHITE, Color.WHITE}, {Color.WHITE, Color.WHITE} };
        when(mockFileManager.cargarCanvas(2, 2)).thenReturn(lienzoPequeno);
        canvasService = new CanvasService(mockFileManager, 2, 2);

        // 2. Acción: Pintar 3 píxeles
        canvasService.setPixel(1, 0, Color.RED);   // ff0000
        canvasService.setPixel(0, 1, Color.GREEN); // 008000
        canvasService.setPixel(1, 1, Color.BLUE);  // 0000ff
                                               // (0,0) se queda BLANCO (ffffff)

        // 3. Verificación:
        // ¿El método que convierte a String funciona?
        // El formato es RRGGBB,RRGGBB,RRGGBB,RRGGBB (x0y0, x1y0, x0y1, x1y1)
        String expectedString = "ffffff,ff0000,008000,0000ff";
        assertEquals(expectedString, canvasService.getCanvasAsString());
    }

    @Test
    void testSetPixelFueraDeRango() {
        // Acción y Verificación
        // Probar que el servidor no se rompe si recibe coordenadas malas
        assertFalse(canvasService.setPixel(-1, 5, Color.RED), "No debe pintar en X negativo");
        assertFalse(canvasService.setPixel(5, -1, Color.RED), "No debe pintar en Y negativo");
        assertFalse(canvasService.setPixel(TEST_ANCHO, 5, Color.RED), "No debe pintar fuera de X");
        assertFalse(canvasService.setPixel(5, TEST_ALTO, Color.RED), "No debe pintar fuera de Y");
    }

    @Test
    void testClearLimpiaElLienzo() {
        // 1. Preparación: Pintar un píxel
        canvasService.setPixel(5, 5, Color.MAGENTA);

        // 2. Acción: Limpiar el lienzo
        canvasService.clear();

        // 3. Verificación:
        // El píxel (5,5) ya no debe ser magenta, debe ser blanco.
        // Usamos getCanvasAsString() para verificar el estado interno.
        String canvasString = canvasService.getCanvasAsString();
        assertFalse(canvasString.contains("ff00ff"), "El lienzo no debe contener Magenta");
        assertTrue(canvasString.contains("ffffff"), "El lienzo debe contener Blanco");
    }

    @Test
    void testGuardarCanvasLlamaAFileManager() {
        // 1. Acción:
        canvasService.guardarCanvas();

        // 2. Verificación:
        // ¿Se llamó al método guardarCanvas() del mock?
        // Usamos any() para verificar que le pasó *un* array de Color[][].
        verify(mockFileManager).guardarCanvas(any(Color[][].class));
    }
}