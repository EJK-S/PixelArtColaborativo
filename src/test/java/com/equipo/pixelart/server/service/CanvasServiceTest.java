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
        // 1. Crear el Mock del FileManager
        mockFileManager = mock(FileManager.class);

        // 2. Preparar la matriz que devolverá el mock cuando se cargue
        Color[][] lienzoVacio = new Color[TEST_WIDTH][TEST_HEIGHT];
        // Inicializamos con BLANCO para tener un estado conocido
        for(int x=0; x<TEST_WIDTH; x++) {
            for(int y=0; y<TEST_HEIGHT; y++) {
                lienzoVacio[x][y] = Color.WHITE;
            }
        }

        // Importante: Entrenar al mock ANTES de crear el servicio
        when(mockFileManager.cargarCanvas(anyInt(), anyInt())).thenReturn(lienzoVacio);

        // 3. Instanciar el servicio
        canvasService = new CanvasService(mockFileManager, TEST_WIDTH, TEST_HEIGHT);
    }

    @Test
    void testConstructores() {
        // Test constructor principal (ya probado en setUp)
        assertEquals(TEST_WIDTH, canvasService.getWidth());
        assertEquals(TEST_HEIGHT, canvasService.getHeight());

        // Test constructor por defecto (48x48)
        // Necesitamos entrenar al mock para que devuelva un array de 48x48
        when(mockFileManager.cargarCanvas(48, 48)).thenReturn(new Color[48][48]);
        CanvasService defaultService = new CanvasService(mockFileManager);
        
        assertEquals(48, defaultService.getWidth());
        assertEquals(48, defaultService.getHeight());
    }

    @Test
    void testSetPixelValido() {
        // Pintamos rojo en (0,0)
        boolean resultado = canvasService.setPixel(0, 0, Color.RED);

        assertTrue(resultado);
        assertEquals(Color.RED, canvasService.getPixel(0, 0));
        assertEquals(1, canvasService.getPixelsModificados());
    }

    @Test
    void testSetPixelInvalido() {
        // Fuera de rango
        assertFalse(canvasService.setPixel(-1, 0, Color.RED));
        assertFalse(canvasService.setPixel(0, 99, Color.RED));
        
        // Color nulo
        assertFalse(canvasService.setPixel(0, 0, null));

        // El contador no debe aumentar
        assertEquals(0, canvasService.getPixelsModificados());
    }

    @Test
    void testGetPixel() {
        canvasService.setPixel(1, 1, Color.BLUE);
        
        assertEquals(Color.BLUE, canvasService.getPixel(1, 1));
        
        // Fuera de rango devuelve BLANCO por defecto (según tu lógica)
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
        
        // Verificamos que es una copia y tiene el dato
        assertEquals(Color.GREEN, copia[0][0]);
        
        // Modificamos la copia
        copia[0][0] = Color.BLACK;
        
        // El original NO debe cambiar
        assertEquals(Color.GREEN, canvasService.getPixel(0, 0));
    }

    @Test
    void testGuardarCanvas() {
        canvasService.guardarCanvas();
        // Verificamos que el servicio llamó al fileManager
        verify(mockFileManager, times(1)).guardarCanvas(any());
    }
    
    @Test
    void testGetCanvasAsString() {
        // Tu lógica recorre X (ancho) y luego Y (alto) -> Column-Major
        // Matriz 2x2 para facilitar el test
        Color[][] matrizPeque = new Color[2][2];
        // Columna 0
        matrizPeque[0][0] = Color.WHITE; // ffffff
        matrizPeque[0][1] = Color.RED;   // ff0000
        // Columna 1
        matrizPeque[1][0] = Color.BLUE;  // 0000ff
        matrizPeque[1][1] = Color.BLACK; // 000000
        
        when(mockFileManager.cargarCanvas(2, 2)).thenReturn(matrizPeque);
        CanvasService miniService = new CanvasService(mockFileManager, 2, 2);
        
        String resultado = miniService.getCanvasAsString();
        
        // Esperado: (0,0),(0,1),(1,0),(1,1)
        String esperado = "ffffff,ff0000,0000ff,000000";
        assertEquals(esperado, resultado);
    }

    @Test
    void testEstadisticas() {
        canvasService.setPixel(0, 0, Color.RED);
        String stats = canvasService.getEstadisticas();
        
        assertTrue(stats.contains("5x5")); // Dimensiones
        assertTrue(stats.contains("1"));   // Pixeles modificados
    }
    
    @Test
    void testGetCanvasDirecto() {
        // Para cubrir el getter simple getCanvas()
        assertNotNull(canvasService.getCanvas());
    }

    // ==========================================
    // TEST DE UNDO (DESHACER)
    // ==========================================

    @Test
    void testUndoBasico() {
        // 1. Estado inicial (Blanco)
        
        // 2. Pintar Rojo (Esto guarda el estado BLANCO en el historial)
        canvasService.setPixel(0, 0, Color.RED);
        assertEquals(Color.RED, canvasService.getPixel(0, 0));

        // 3. Deshacer
        boolean undoResult = canvasService.undo();

        assertTrue(undoResult);
        assertEquals(Color.WHITE, canvasService.getPixel(0, 0));
    }

    @Test
    void testUndoSinHistorial() {
        // No hemos pintado nada
        boolean undoResult = canvasService.undo();
        assertFalse(undoResult);
    }
    
    @Test
    void testSetPixelMismoColorNoGuardaEstado() {
        // El pixel ya es BLANCO. Lo pintamos de BLANCO.
        canvasService.setPixel(0, 0, Color.WHITE);
        
        // Como no hubo cambio real, no debió guardarse nada en el stack
        assertFalse(canvasService.undo());
    }

    @Test
    void testUndoLimiteMaximo() {
        // MAX_UNDO_STEPS es 50.
        // Vamos a pintar 55 veces con colores distintos.
        for (int i = 0; i < 55; i++) {
            // Usamos colores aleatorios o distintos para forzar el guardado
            canvasService.setPixel(0, 0, new Color(i, i, i));
        }

        // Intentamos deshacer. Deberíamos poder deshacer 50 veces como máximo.
        int undoCount = 0;
        while (canvasService.undo()) {
            undoCount++;
        }

        assertEquals(50, undoCount, "El historial no debe exceder 50 pasos");
    }

    // ==========================================
    // TEST DE HILOS (AUTO-GUARDADO)
    // ==========================================

    @Test
    void testIniciarAutoGuardado() {
        // Este test es delicado. Inicia un hilo infinito.
        // JUnit matará el hilo daemon cuando termine el test.
        // Solo verificamos que no lance excepción al iniciarse.
        
        assertDoesNotThrow(() -> canvasService.iniciarAutoGuardado(1));
        
        // No podemos verificar fácilmente que el hilo se ejecutó X veces sin Thread.sleep
        // lo cual haría el test muy lento. Para cobertura, invocarlo es suficiente.
    }
}