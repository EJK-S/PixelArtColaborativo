package com.equipo.pixelart.client.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ToolboxPanelTest {

    private ToolboxPanel toolboxPanel;
    private ToolboxPanel spyPanel;

    @BeforeEach
    void setUp() {
        
        toolboxPanel = new ToolboxPanel();

        spyPanel = spy(new ToolboxPanel());
    }

    @Test
    void testInicializacionUI() {
       
        assertNotNull(toolboxPanel.getPencilButton());
        assertNotNull(toolboxPanel.getEraserButton());
        assertNotNull(toolboxPanel.getPrimaryColorButton());
        assertNotNull(toolboxPanel.getBrushSizeSlider());
        assertNotNull(toolboxPanel.getUndoButton()); 

        assertEquals(Color.BLACK, toolboxPanel.getPrimaryColorButton().getBackground());
        assertEquals(Color.WHITE, toolboxPanel.getSecondaryColorButton().getBackground());
        assertEquals(20, toolboxPanel.getColorPresetButtons().length);
    }

    @Test
    void testSetSelectedTool() {
       
        toolboxPanel.setSelectedTool("pencil");

        JButton pencil = toolboxPanel.getPencilButton();
        assertTrue(pencil.getBorder() instanceof LineBorder);
        assertEquals(Color.BLUE, ((LineBorder) pencil.getBorder()).getLineColor());
        assertEquals(3, ((LineBorder) pencil.getBorder()).getThickness());

        JButton eraser = toolboxPanel.getEraserButton();
        assertFalse(eraser.getBorder() instanceof LineBorder);

        toolboxPanel.setSelectedTool("eraser");
 
        assertFalse(pencil.getBorder() instanceof LineBorder);
  
        assertTrue(eraser.getBorder() instanceof LineBorder);
    }

    @Test
    void testUpdateColorDisplay() {
        toolboxPanel.updateColorDisplay(Color.RED, Color.BLUE);
        
        assertEquals(Color.RED, toolboxPanel.getPrimaryColorButton().getBackground());
        assertEquals(Color.BLUE, toolboxPanel.getSecondaryColorButton().getBackground());
    }

    @Test
    void testUpdateBrushSizeLabel() {
        toolboxPanel.updateBrushSizeLabel(5);
        assertEquals("Tamaño: 5", toolboxPanel.getBrushSizeLabel().getText());
    }

    @Test
    void testShowConfirmDialog() {
     
        doReturn(JOptionPane.YES_OPTION).when(spyPanel).showConfirmDialogInternal(anyString(), anyString());

        int result = spyPanel.showConfirmDialog("Msg", "Title");

        assertEquals(JOptionPane.YES_OPTION, result);
        verify(spyPanel).showConfirmDialogInternal("Msg", "Title");
    }

    @Test
    void testShowMessageAndError() {
       
        doNothing().when(spyPanel).showMessageDialogInternal(anyString(), anyString(), anyInt());

        spyPanel.showMessage("Hola", "Titulo");
        verify(spyPanel).showMessageDialogInternal("Hola", "Titulo", JOptionPane.INFORMATION_MESSAGE);

        spyPanel.showError("Fallo");
        verify(spyPanel).showMessageDialogInternal("Fallo", "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    void testShowSaveDialog_Aprobar() {
   
        JFileChooser mockChooser = mock(JFileChooser.class);
        File mockFile = new File("C:/test/dibujo.png"); 
       
        when(mockChooser.showSaveDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
        when(mockChooser.getSelectedFile()).thenReturn(mockFile);

        doReturn(mockChooser).when(spyPanel).createFileChooser();

        String result = spyPanel.showSaveDialog();

        assertNotNull(result);
        assertTrue(result.endsWith("dibujo.png"));
    }

    @Test
    void testShowSaveDialog_Cancelar() {
        JFileChooser mockChooser = mock(JFileChooser.class);
        when(mockChooser.showSaveDialog(any())).thenReturn(JFileChooser.CANCEL_OPTION);
        
        doReturn(mockChooser).when(spyPanel).createFileChooser();

        String result = spyPanel.showSaveDialog();

        assertNull(result, "Si cancela, debe devolver null");
    }

    @Test
    void testShowLoadDialog() {
        
        JFileChooser mockChooser = mock(JFileChooser.class);
        File mockFile = new File("imagen.png");
        
        when(mockChooser.showOpenDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
        when(mockChooser.getSelectedFile()).thenReturn(mockFile);

        doReturn(mockChooser).when(spyPanel).createFileChooser();

        String result = spyPanel.showLoadDialog();

        assertNotNull(result);
        assertTrue(result.endsWith("imagen.png"));
    }
}