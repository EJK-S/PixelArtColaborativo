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

class CanvasServiceTest {

    private CanvasService canvasService;
    private FileManager mockFileManager;

    private final int TEST_ANCHO = 10;
    private final int TEST_ALTO = 10;
    private Color[][] canvasFalso;

   

    @BeforeEach
    void setUp() {
       
        mockFileManager = mock(FileManager.class);

        
        canvasFalso = new Color[TEST_ANCHO][TEST_ALTO];
        for (int x = 0; x < TEST_ANCHO; x++) {
            for (int y = 0; y < TEST_ALTO; y++) {
                canvasFalso[x][y] = Color.WHITE;
            }
        }
      
        when(mockFileManager.cargarCanvas(TEST_ANCHO, TEST_ALTO)).thenReturn(canvasFalso);
    
        canvasService = new CanvasService(mockFileManager, TEST_ANCHO, TEST_ALTO);
    }

    @Test
    void testConstructorCargaElLienzo() {       
        verify(mockFileManager).cargarCanvas(TEST_ANCHO, TEST_ALTO);
    }

    @Test
    void testSetPixelYGetCanvasAsString() {
        Color[][] lienzoPequeno = { {Color.WHITE, Color.WHITE}, {Color.WHITE, Color.WHITE} };
        when(mockFileManager.cargarCanvas(2, 2)).thenReturn(lienzoPequeno);
        canvasService = new CanvasService(mockFileManager, 2, 2);
   
        canvasService.setPixel(1, 0, Color.RED);   
        canvasService.setPixel(0, 1, Color.GREEN); 
        canvasService.setPixel(1, 1, Color.BLUE);  
                                                 
        String expectedString = "ffffff,ff0000,008000,0000ff";
        assertEquals(expectedString, canvasService.getCanvasAsString());
    }

    @Test
    void testSetPixelFueraDeRango() {       
        assertFalse(canvasService.setPixel(-1, 5, Color.RED), "No debe pintar en X negativo");
        assertFalse(canvasService.setPixel(5, -1, Color.RED), "No debe pintar en Y negativo");
        assertFalse(canvasService.setPixel(TEST_ANCHO, 5, Color.RED), "No debe pintar fuera de X");
        assertFalse(canvasService.setPixel(5, TEST_ALTO, Color.RED), "No debe pintar fuera de Y");
    }

    @Test
    void testClearLimpiaElLienzo() {      
        canvasService.setPixel(5, 5, Color.MAGENTA);        
        canvasService.clear();

        String canvasString = canvasService.getCanvasAsString();
        assertFalse(canvasString.contains("ff00ff"), "El lienzo no debe contener Magenta");
        assertTrue(canvasString.contains("ffffff"), "El lienzo debe contener Blanco");
    }

    @Test
    void testGuardarCanvasLlamaAFileManager() {
   
        canvasService.guardarCanvas();
        verify(mockFileManager).guardarCanvas(any(Color[][].class));
    }
}