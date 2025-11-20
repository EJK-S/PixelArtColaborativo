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
        // Instancia real para pruebas visuales
        toolboxPanel = new ToolboxPanel();
        
        // Spy para pruebas de diálogos (bloqueantes)
        spyPanel = spy(new ToolboxPanel());
    }

    @Test
    void testInicializacionUI() {
        // Verificamos que los componentes principales existan
        assertNotNull(toolboxPanel.getPencilButton());
        assertNotNull(toolboxPanel.getEraserButton());
        assertNotNull(toolboxPanel.getPrimaryColorButton());
        assertNotNull(toolboxPanel.getBrushSizeSlider());
        assertNotNull(toolboxPanel.getUndoButton()); // Verificar el botón nuevo
        
        // Verificar valores por defecto
        assertEquals(Color.BLACK, toolboxPanel.getPrimaryColorButton().getBackground());
        assertEquals(Color.WHITE, toolboxPanel.getSecondaryColorButton().getBackground());
        assertEquals(20, toolboxPanel.getColorPresetButtons().length);
    }

    @Test
    void testSetSelectedTool() {
        // 1. Seleccionar Lápiz
        toolboxPanel.setSelectedTool("pencil");
        
        // Verificar que el lápiz tenga borde azul grueso (indicador de selección)
        JButton pencil = toolboxPanel.getPencilButton();
        assertTrue(pencil.getBorder() instanceof LineBorder);
        assertEquals(Color.BLUE, ((LineBorder) pencil.getBorder()).getLineColor());
        assertEquals(3, ((LineBorder) pencil.getBorder()).getThickness());

        // Verificar que otros no tengan borde (o tengan el default vacío)
        JButton eraser = toolboxPanel.getEraserButton();
        assertFalse(eraser.getBorder() instanceof LineBorder);

        // 2. Cambiar a Borrador
        toolboxPanel.setSelectedTool("eraser");
        
        // Lápiz debe perder el borde
        assertFalse(pencil.getBorder() instanceof LineBorder);
        
        // Borrador debe ganar el borde
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

    // ==========================================
    // TEST DE DIÁLOGOS (Usando el Spy)
    // ==========================================

    @Test
    void testShowConfirmDialog() {
        // Burlamos el método protegido interno
        doReturn(JOptionPane.YES_OPTION).when(spyPanel).showConfirmDialogInternal(anyString(), anyString());

        int result = spyPanel.showConfirmDialog("Msg", "Title");

        assertEquals(JOptionPane.YES_OPTION, result);
        verify(spyPanel).showConfirmDialogInternal("Msg", "Title");
    }

    @Test
    void testShowMessageAndError() {
        // Evitamos que salga la ventana real
        doNothing().when(spyPanel).showMessageDialogInternal(anyString(), anyString(), anyInt());

        spyPanel.showMessage("Hola", "Titulo");
        verify(spyPanel).showMessageDialogInternal("Hola", "Titulo", JOptionPane.INFORMATION_MESSAGE);

        spyPanel.showError("Fallo");
        verify(spyPanel).showMessageDialogInternal("Fallo", "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Test
    void testShowSaveDialog_Aprobar() {
        // ESTE ES EL TRUCO MAESTRO PARA JFILECHOOSER
        
        // 1. Creamos un JFileChooser falso (Mock)
        JFileChooser mockChooser = mock(JFileChooser.class);
        File mockFile = new File("C:/test/dibujo.png"); // Archivo simulado

        // 2. Configuramos el Mock para que diga "Sí, el usuario eligió este archivo"
        when(mockChooser.showSaveDialog(any())).thenReturn(JFileChooser.APPROVE_OPTION);
        when(mockChooser.getSelectedFile()).thenReturn(mockFile);

        // 3. Inyectamos el Mock en nuestro Spy
        doReturn(mockChooser).when(spyPanel).createFileChooser();

        // 4. Ejecutamos
        String result = spyPanel.showSaveDialog();

        // 5. Verificamos
        // Nota: getAbsolutePath() depende del SO, pero verificamos que termine en el nombre
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
        // Misma lógica que SaveDialog pero con showOpenDialog
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