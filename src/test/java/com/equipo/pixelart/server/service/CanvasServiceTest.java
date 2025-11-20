package com.equipo.pixelart.server.service;

import com.equipo.pixelart.server.persistence.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CanvasServiceTest {

    private CanvasService canvasService;
    private FileManager mockFileManager;
    private final int TEST_WIDTH = 5;
    private final int TEST_HEIGHT = 5;

    @BeforeEach
    void setUp() {
        
        mockFileManager = mock(FileManager.class);

        Color[][] lienzoVacio = new Color[TEST_WIDTH][TEST_HEIGHT];
       
        for(int x=0; x<TEST_WIDTH; x++) {
            for(int y=0; y<TEST_HEIGHT; y++) {
                lienzoVacio[x][y] = Color.WHITE;
            }
        }

        when(mockFileManager.cargarCanvas(anyInt(), anyInt())).thenReturn(lienzoVacio);

        canvasService = new CanvasService(mockFileManager, TEST_WIDTH, TEST_HEIGHT);
    }

    @Test
    void testConstructores() {
      
        assertEquals(TEST_WIDTH, canvasService.getWidth());
        assertEquals(TEST_HEIGHT, canvasService.getHeight());
        when(mockFileManager.cargarCanvas(48, 48)).thenReturn(new Color[48][48]);
        CanvasService defaultService = new CanvasService(mockFileManager);
        
        assertEquals(48, defaultService.getWidth());
        assertEquals(48, defaultService.getHeight());
    }

    @Test
    void testSetPixelValido() {
       
        boolean resultado = canvasService.setPixel(0, 0, Color.RED);

        assertTrue(resultado);
        assertEquals(Color.RED, canvasService.getPixel(0, 0));
        assertEquals(1, canvasService.getPixelsModificados());
    }

    @Test
    void testSetPixelInvalido() {
        
        assertFalse(canvasService.setPixel(-1, 0, Color.RED));
        assertFalse(canvasService.setPixel(0, 99, Color.RED));
        assertFalse(canvasService.setPixel(0, 0, null));
        assertEquals(0, canvasService.getPixelsModificados());
    }

    @Test
    void testGetPixel() {
        canvasService.setPixel(1, 1, Color.BLUE);
        
        assertEquals(Color.BLUE, canvasService.getPixel(1, 1));
        assertEquals(Color.WHITE, canvasService.getPixel(99, 99));
    }

    @Test
    void testClear() {
        canvasService.setPixel(0, 0, Color.BLACK);
        canvasService.setPixel(1, 1, Color.MAGENTA);

        canvasService.clear();

        assertEquals(Color.WHITE, canvasService.getPixel(0, 0));
        assertEquals(Color.WHITE, canvasService.getPixel(1, 1));
    }

    @Test
    void testGetCanvasCopy() {
        canvasService.setPixel(0, 0, Color.GREEN);
        
        Color[][] copia = canvasService.getCanvasCopy();

        assertEquals(Color.GREEN, copia[0][0]);
 
        copia[0][0] = Color.BLACK;

        assertEquals(Color.GREEN, canvasService.getPixel(0, 0));
    }

    @Test
    void testGuardarCanvas() {
        canvasService.guardarCanvas();
        
        verify(mockFileManager, times(1)).guardarCanvas(any());
    }
    
    @Test
    void testGetCanvasAsString() {
 
        Color[][] matrizPeque = new Color[2][2];
      
        matrizPeque[0][0] = Color.WHITE; 
        matrizPeque[0][1] = Color.RED;   
    
        matrizPeque[1][0] = Color.BLUE;  
        matrizPeque[1][1] = Color.BLACK; 
        
        when(mockFileManager.cargarCanvas(2, 2)).thenReturn(matrizPeque);
        CanvasService miniService = new CanvasService(mockFileManager, 2, 2);
        
        String resultado = miniService.getCanvasAsString();

        String esperado = "ffffff,ff0000,0000ff,000000";
        assertEquals(esperado, resultado);
    }

    @Test
    void testEstadisticas() {
        canvasService.setPixel(0, 0, Color.RED);
        String stats = canvasService.getEstadisticas();
        
        assertTrue(stats.contains("5x5")); 
        assertTrue(stats.contains("1"));  
    }
    
    @Test
    void testGetCanvasDirecto() {
       
        assertNotNull(canvasService.getCanvas());
    }

    @Test
    void testUndoBasico() {

        canvasService.setPixel(0, 0, Color.RED);
        assertEquals(Color.RED, canvasService.getPixel(0, 0));

        boolean undoResult = canvasService.undo();

        assertTrue(undoResult);
        assertEquals(Color.WHITE, canvasService.getPixel(0, 0));
    }

    @Test
    void testUndoSinHistorial() {
       
        boolean undoResult = canvasService.undo();
        assertFalse(undoResult);
    }
    
    @Test
    void testSetPixelMismoColorNoGuardaEstado() {
      
        canvasService.setPixel(0, 0, Color.WHITE);
        
        assertFalse(canvasService.undo());
    }

    @Test
    void testUndoLimiteMaximo() {

        for (int i = 0; i < 55; i++) {

            canvasService.setPixel(0, 0, new Color(i, i, i));
        }

        int undoCount = 0;
        while (canvasService.undo()) {
            undoCount++;
        }

        assertEquals(50, undoCount, "El historial no debe exceder 50 pasos");
    }


    @Test
    void testIniciarAutoGuardado() {      
        assertDoesNotThrow(() -> canvasService.iniciarAutoGuardado(1));
    }
}