package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.model.CanvasModel;
import com.equipo.pixelart.client.view.ToolboxPanel;
import com.equipo.pixelart.server.persistence.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ToolboxControllerTest {

    private ToolboxController toolboxController; // Será un SPY
    private ToolboxPanel mockView;
    private CanvasController mockCanvasController;
    private FileManager mockFileManager;

    // Mocks de componentes UI
    private JButton mockPencilBtn, mockEraserBtn, mockBucketBtn, mockEyedropperBtn;
    private JButton mockPrimaryColorBtn, mockSecondaryColorBtn, mockSwapBtn;
    private JButton mockClearBtn, mockUndoBtn, mockSaveBtn, mockLoadBtn;
    private JSlider mockBrushSlider;
    private JButton[] mockColorPresetButtons;

    @BeforeEach
    void setUp() {
        // 1. Mocks básicos
        mockView = mock(ToolboxPanel.class);
        mockCanvasController = mock(CanvasController.class);
        mockFileManager = mock(FileManager.class);

        // Simular modelo del canvas para el guardado
        CanvasModel mockModel = mock(CanvasModel.class);
        when(mockCanvasController.getModel()).thenReturn(mockModel);
        when(mockModel.getPixels()).thenReturn(new Color[2][2]);

        // 2. Inicializar botones mock para que init() no falle
        mockPencilBtn = mock(JButton.class);
        mockEraserBtn = mock(JButton.class);
        mockBucketBtn = mock(JButton.class);
        mockEyedropperBtn = mock(JButton.class);
        mockPrimaryColorBtn = mock(JButton.class);
        mockSecondaryColorBtn = mock(JButton.class);
        mockSwapBtn = mock(JButton.class);
        mockClearBtn = mock(JButton.class);
        mockUndoBtn = mock(JButton.class);
        mockSaveBtn = mock(JButton.class);
        mockLoadBtn = mock(JButton.class);
        mockBrushSlider = mock(JSlider.class);

        // Configurar el View para devolver estos botones
        when(mockView.getPencilButton()).thenReturn(mockPencilBtn);
        when(mockView.getEraserButton()).thenReturn(mockEraserBtn);
        when(mockView.getBucketButton()).thenReturn(mockBucketBtn);
        when(mockView.getEyedropperButton()).thenReturn(mockEyedropperBtn);
        when(mockView.getPrimaryColorButton()).thenReturn(mockPrimaryColorBtn);
        when(mockView.getSecondaryColorButton()).thenReturn(mockSecondaryColorBtn);
        when(mockView.getSwapColorsButton()).thenReturn(mockSwapBtn);
        when(mockView.getClearButton()).thenReturn(mockClearBtn);
        when(mockView.getUndoButton()).thenReturn(mockUndoBtn);
        when(mockView.getSaveButton()).thenReturn(mockSaveBtn);
        when(mockView.getLoadButton()).thenReturn(mockLoadBtn);
        when(mockView.getBrushSizeSlider()).thenReturn(mockBrushSlider);

        // Configurar botones de colores predefinidos
        mockColorPresetButtons = new JButton[ToolboxController.getPresetColors().length];
        for (int i = 0; i < mockColorPresetButtons.length; i++) {
            mockColorPresetButtons[i] = mock(JButton.class);
        }
        when(mockView.getColorPresetButtons()).thenReturn(mockColorPresetButtons);

        // 3. Crear el SPY del Controller
        // Usamos spy() para poder sobrescribir los métodos protected openColorChooser y createFileManager
        toolboxController = spy(new ToolboxController(mockView, mockCanvasController));
    }

    @Test
    void testInitListeners() {
        toolboxController.init();

        // Verificar que se añadieron listeners a las herramientas
        verify(mockPencilBtn).addActionListener(any());
        verify(mockEraserBtn).addActionListener(any());
        verify(mockSaveBtn).addActionListener(any());
        // ... verificar el resto si quieres ser exhaustivo
        
        // Verificar inicialización de estado en el otro controller
        verify(mockCanvasController).setCurrentTool("pencil");
    }

    @Test
    void testSelectTool() {
        toolboxController.init();
        
        // Capturamos el listener del botón Goma (Eraser)
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockEraserBtn).addActionListener(captor.capture());
        
        // Simulamos el clic
        captor.getValue().actionPerformed(null);

        assertEquals("eraser", toolboxController.getCurrentTool());
        verify(mockView).setSelectedTool("eraser");
        verify(mockCanvasController).setCurrentTool("eraser");
    }

    @Test
    void testBrushSizeChange() {
        toolboxController.init();

        // Capturar el listener del slider
        ArgumentCaptor<ChangeListener> captor = ArgumentCaptor.forClass(ChangeListener.class);
        verify(mockBrushSlider).addChangeListener(captor.capture());

        // Configurar el mock del slider
        when(mockBrushSlider.getValueIsAdjusting()).thenReturn(false);
        when(mockBrushSlider.getValue()).thenReturn(10);

        // Simular evento
        javax.swing.event.ChangeEvent event = new javax.swing.event.ChangeEvent(mockBrushSlider);
        captor.getValue().stateChanged(event);

        assertEquals(10, toolboxController.getBrushSize());
        verify(mockView).updateBrushSizeLabel(10);
        verify(mockCanvasController).setBrushSize(10);
    }

    @Test
    void testSwapColors() {
        toolboxController.init();
        
        // Estado inicial
        Color inicialPrimario = toolboxController.getCurrentColor();
        Color inicialSecundario = toolboxController.getSecondaryColor();

        // Ejecutar swap via listener
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSwapBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        // Verificar intercambio
        assertEquals(inicialSecundario, toolboxController.getCurrentColor());
        assertEquals(inicialPrimario, toolboxController.getSecondaryColor());
        verify(mockView).updateColorDisplay(any(), any());
    }

    @Test
    void testChoosePrimaryColor() {
        toolboxController.init();

        // 1. Entrenar al SPY para que NO abra la ventana real, sino que devuelva AZUL
        doReturn(Color.BLUE).when(toolboxController).openColorChooser(anyString(), any());

        // 2. Ejecutar acción
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockPrimaryColorBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        // 3. Verificar
        assertEquals(Color.BLUE, toolboxController.getCurrentColor());
        verify(toolboxController).openColorChooser(contains("Primario"), any());
    }
    
    @Test
    void testChoosePrimaryColorCancelado() {
        toolboxController.init();
        Color colorAntes = toolboxController.getCurrentColor();

        // Simular que el usuario cierra el diálogo (devuelve null)
        doReturn(null).when(toolboxController).openColorChooser(anyString(), any());

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockPrimaryColorBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        // El color no debe cambiar
        assertEquals(colorAntes, toolboxController.getCurrentColor());
    }

    @Test
    void testChooseSecondaryColor() {
        toolboxController.init();
        doReturn(Color.RED).when(toolboxController).openColorChooser(anyString(), any());

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSecondaryColorBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        assertEquals(Color.RED, toolboxController.getSecondaryColor());
    }

    @Test
    void testColorPresets() {
        toolboxController.init();
        
        // Probamos el primer botón de la lista (debería ser BLACK o el primero del array)
        JButton primerBoton = mockColorPresetButtons[0];
        Color primerColor = ToolboxController.getPresetColors()[0];

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(primerBoton).addActionListener(captor.capture());
        
        captor.getValue().actionPerformed(null);
        
        assertEquals(primerColor, toolboxController.getCurrentColor());
    }

    @Test
    void testClearCanvasConfirmed() {
        toolboxController.init();

        // Simular que el usuario dice SI (0 = YES_OPTION)
        when(mockView.showConfirmDialog(anyString(), anyString())).thenReturn(0);

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockClearBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockCanvasController).clearCanvas();
    }
    
    @Test
    void testClearCanvasCancelled() {
        toolboxController.init();
        // Simular que el usuario dice NO (1 = NO_OPTION)
        when(mockView.showConfirmDialog(anyString(), anyString())).thenReturn(1);

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockClearBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockCanvasController, never()).clearCanvas();
    }

    @Test
    void testSaveCanvasSuccess() {
        toolboxController.init();

        // 1. Simular que el usuario elige un archivo
        when(mockView.showSaveDialog()).thenReturn("dibujo.png");
        
        // 2. Entrenar al SPY para que devuelva nuestro mockFileManager
        doReturn(mockFileManager).when(toolboxController).createFileManager();

        // 3. Ejecutar
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSaveBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        // 4. Verificar que se intentó guardar
        verify(mockFileManager).guardarCanvas(any());
        verify(mockView).showMessage(contains("exitosamente"), anyString());
    }

    @Test
    void testSaveCanvasError() {
        toolboxController.init();
        when(mockView.showSaveDialog()).thenReturn("dibujo.png");
        doReturn(mockFileManager).when(toolboxController).createFileManager();

        // Simular error al guardar
        doThrow(new RuntimeException("Disco lleno")).when(mockFileManager).guardarCanvas(any());

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSaveBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockView).showError(contains("Disco lleno"));
    }

    @Test
    void testLoadCanvas() {
        toolboxController.init();
        
        // Simular selección de archivo
        when(mockView.showLoadDialog()).thenReturn("dibujo.png");

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockLoadBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);
        
        // Como la función está "en desarrollo", verificamos el mensaje de info
        verify(mockView).showMessage(contains("en desarrollo"), anyString());
    }
    
    @Test
    void testLoadCanvasError() {
        toolboxController.init();
        when(mockView.showLoadDialog()).thenThrow(new RuntimeException("Error archivo"));

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockLoadBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);
        
        verify(mockView).showError(contains("Error archivo"));
    }

    @Test
    void testUndo() {
        toolboxController.init();
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockUndoBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockCanvasController).undo();
    }
}