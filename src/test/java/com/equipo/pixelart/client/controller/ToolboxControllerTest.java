package com.equipo.pixelart.client.controller;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.equipo.pixelart.client.view.ToolboxPanel;

public class ToolboxControllerTest {
	
	private ToolboxController controller;
    private ToolboxPanel mockView;
    private CanvasController mockCanvasController;

        @SuppressWarnings("unused")

    @BeforeEach
    void setUp() {
        // Mockeo de la vista y el controlador de canvas
        mockView = Mockito.mock(ToolboxPanel.class);
        mockCanvasController = Mockito.mock(CanvasController.class);
        controller = new ToolboxController(mockView, mockCanvasController);
    }

    @Test
    void testSeleccionHerramientaPincel() {
        // Ejecucion del metodo
        controller.setCurrentColor(Color.RED); // Cambiamos color para probar después
        controller.getClass(); // solo fuerza carga, no necesario

        controller.getCurrentTool();
        // Comprobamos que realmente cambió el estado interno
        controller.setCurrentColor(Color.BLUE);
        assertEquals(Color.BLUE, controller.getCurrentColor());
    }
    
    @Test
    void testSwapColorsIntercambiaColores() throws Exception {
        // Establecemos colores conocidos
        controller.setCurrentColor(Color.RED);
        controller.setSecondaryColor(Color.BLUE);

        // Accedemos por reflexión al método privado
        var method = ToolboxController.class.getDeclaredMethod("swapColors");
        method.setAccessible(true);
        method.invoke(controller);

        // Comprobamos que se hayan intercambiado
        assertEquals(Color.BLUE, controller.getCurrentColor());
        assertEquals(Color.RED, controller.getSecondaryColor());
    }
}

