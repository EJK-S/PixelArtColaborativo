package com.equipo.pixelart.client.controller;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.equipo.pixelart.client.model.CanvasModel;
import com.equipo.pixelart.client.view.ToolboxPanel;
import com.equipo.pixelart.server.persistence.FileManager;

class ToolboxControllerTest {

    private ToolboxController toolboxController; 
    private ToolboxPanel mockView;
    private CanvasController mockCanvasController;
    private FileManager mockFileManager;

    private JButton mockPencilBtn, mockEraserBtn, mockBucketBtn, mockEyedropperBtn;
    private JButton mockPrimaryColorBtn, mockSecondaryColorBtn, mockSwapBtn;
    private JButton mockClearBtn, mockUndoBtn, mockSaveBtn, mockLoadBtn;
    private JSlider mockBrushSlider;
    private JButton[] mockColorPresetButtons;

    @BeforeEach
    public void setUp() {
       
        mockView = mock(ToolboxPanel.class);
        mockCanvasController = mock(CanvasController.class);
        mockFileManager = mock(FileManager.class);

        CanvasModel mockModel = mock(CanvasModel.class);
        when(mockCanvasController.getModel()).thenReturn(mockModel);
        when(mockModel.getPixels()).thenReturn(new Color[2][2]);

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

        mockColorPresetButtons = new JButton[ToolboxController.getPresetColors().length];
        for (int i = 0; i < mockColorPresetButtons.length; i++) {
            mockColorPresetButtons[i] = mock(JButton.class);
        }
        when(mockView.getColorPresetButtons()).thenReturn(mockColorPresetButtons);

        toolboxController = spy(new ToolboxController(mockView, mockCanvasController));
    }

    @Test
    void testInitListeners() {
        toolboxController.init();

        verify(mockPencilBtn).addActionListener(any());
        verify(mockEraserBtn).addActionListener(any());
        verify(mockSaveBtn).addActionListener(any());

        verify(mockCanvasController).setCurrentTool("pencil");
    }

    @Test
    void testSelectTool() {
        toolboxController.init();

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockEraserBtn).addActionListener(captor.capture());

        captor.getValue().actionPerformed(null);

        assertEquals("eraser", toolboxController.getCurrentTool());
        verify(mockView).setSelectedTool("eraser");
        verify(mockCanvasController).setCurrentTool("eraser");
    }

    @Test
    void testBrushSizeChange() {
        toolboxController.init();

        ArgumentCaptor<ChangeListener> captor = ArgumentCaptor.forClass(ChangeListener.class);
        verify(mockBrushSlider).addChangeListener(captor.capture());

        when(mockBrushSlider.getValueIsAdjusting()).thenReturn(false);
        when(mockBrushSlider.getValue()).thenReturn(10);

        javax.swing.event.ChangeEvent event = new javax.swing.event.ChangeEvent(mockBrushSlider);
        captor.getValue().stateChanged(event);

        assertEquals(10, toolboxController.getBrushSize());
        verify(mockView).updateBrushSizeLabel(10);
        verify(mockCanvasController).setBrushSize(10);
    }

    @Test
    void testSwapColors() {
        toolboxController.init();
   
        Color inicialPrimario = toolboxController.getCurrentColor();
        Color inicialSecundario = toolboxController.getSecondaryColor();

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSwapBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        assertEquals(inicialSecundario, toolboxController.getCurrentColor());
        assertEquals(inicialPrimario, toolboxController.getSecondaryColor());
        verify(mockView).updateColorDisplay(any(), any());
    }

    @Test
    void testChoosePrimaryColor() {
        toolboxController.init();

        doReturn(Color.BLUE).when(toolboxController).openColorChooser(anyString(), any());

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockPrimaryColorBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        assertEquals(Color.BLUE, toolboxController.getCurrentColor());
        verify(toolboxController).openColorChooser(contains("Primario"), any());
    }
    
    @Test
    void testChoosePrimaryColorCancelado() {
        toolboxController.init();
        Color colorAntes = toolboxController.getCurrentColor();

        doReturn(null).when(toolboxController).openColorChooser(anyString(), any());

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockPrimaryColorBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

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

        when(mockView.showConfirmDialog(anyString(), anyString())).thenReturn(0);

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockClearBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockCanvasController).clearCanvas();
    }
    
    @Test
    void testClearCanvasCancelled() {
        toolboxController.init();

        when(mockView.showConfirmDialog(anyString(), anyString())).thenReturn(1);

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockClearBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockCanvasController, never()).clearCanvas();
    }

    @Test
    void testSaveCanvasSuccess() {
        toolboxController.init();

        when(mockView.showSaveDialog()).thenReturn("dibujo.png");
 
        doReturn(mockFileManager).when(toolboxController).createFileManager();

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSaveBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockFileManager).guardarCanvas(any());
        verify(mockView).showMessage(contains("exitosamente"), anyString());
    }

    @Test
    void testSaveCanvasError() {
        toolboxController.init();
        when(mockView.showSaveDialog()).thenReturn("dibujo.png");
        doReturn(mockFileManager).when(toolboxController).createFileManager();

        doThrow(new RuntimeException("Disco lleno")).when(mockFileManager).guardarCanvas(any());

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockSaveBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockView).showError(contains("Disco lleno"));
    }

    @Test
    void testLoadCanvas() {
        toolboxController.init();

        when(mockView.showLoadDialog()).thenReturn("dibujo.png");

        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockLoadBtn).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

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