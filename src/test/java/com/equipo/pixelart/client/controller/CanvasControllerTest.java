package com.equipo.pixelart.client.controller;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.equipo.pixelart.client.model.CanvasModel;
import com.equipo.pixelart.client.view.CanvasPanel;

public class CanvasControllerTest {

    private CanvasModel model;
    private CanvasPanel view;
    private NetworkController network;
    private CanvasController controller;

        @SuppressWarnings("unused")

    @BeforeEach
    void setUp() {
        
        model = mock(CanvasModel.class);
        view = mock(CanvasPanel.class);
        network = mock(NetworkController.class);

        
        doNothing().when(view).repaint();

        
        controller = new CanvasController(model, view, network);
    }

    @Test
    void testValoresIniciales() {
        assertEquals(Color.BLACK, controller.getCurrentColor(), "Color inicial debe ser negro");
        assertEquals("pencil", controller.getCurrentTool(), "Herramienta inicial debe ser 'pencil'");
        assertEquals(1, controller.getBrushSize(), "Tamaño inicial del pincel debe ser 1");
    }

    @Test
    void testSettersCambianEstado() {
        controller.setCurrentColor(Color.RED);
        controller.setCurrentTool("eraser");
        controller.setBrushSize(3);

        assertEquals(Color.RED, controller.getCurrentColor());
        assertEquals("eraser", controller.getCurrentTool());
        assertEquals(3, controller.getBrushSize());
    }

    @Test
    void testUpdateCanvasActualizaModeloYVista() {
        Color[][] pixels = new Color[2][2];
        controller.updateCanvas(pixels);

        verify(model).setPixels(pixels);
        verify(view).repaint();
    }

    @Test
    void testUpdatePixelFromServer_Valido() {
        when(model.isValidPosition(1, 1)).thenReturn(true);

        controller.updatePixelFromServer(1, 1, Color.BLUE);

        verify(model).setPixel(1, 1, Color.BLUE);
        verify(view).repaint();
    }

    @Test
    void testUpdatePixelFromServer_Invalido() {
        when(model.isValidPosition(1, 1)).thenReturn(false);

        controller.updatePixelFromServer(1, 1, Color.BLUE);

        verify(model, never()).setPixel(anyInt(), anyInt(), any());
        verify(view, never()).repaint();
    }

    @Test
    void testClearCanvas() {
        controller.clearCanvas();

        verify(model).clear();
        verify(network).sendClearCanvas();
        verify(view).repaint();
    }

    @Test
    void testSetBrushSize_NoMenorQueUno() {
        controller.setBrushSize(-5);
        assertEquals(1, controller.getBrushSize(), "El tamaño mínimo debe ser 1");
    }

}
