package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.app.ClientApp;
import com.equipo.pixelart.client.view.CanvasPanel;
import com.equipo.pixelart.client.view.MainFrame;
import com.equipo.pixelart.client.view.ToolboxPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.*;
import java.awt.event.ActionListener;

import static org.mockito.Mockito.*;

class MenuControllerTest {

    private MenuController menuController;
    
    // Mocks de las clases principales
    private MainFrame mockMainFrame;
    private CanvasController mockCanvasController;
    private CanvasPanel mockCanvasPanel;
    private ClientApp mockClientApp;
    private ToolboxPanel mockToolboxPanel;

    // Mocks de los componentes visuales (Items del menú y botones)
    private JMenuItem mockConnectItem;
    private JMenuItem mockDisconnectItem;
    private JMenuItem mockExitItem;
    private JMenuItem mockClearItem;
    private JCheckBoxMenuItem mockToggleGridItem; // Ojo: suele ser CheckBoxMenuItem para toggle
    private JMenuItem mockAboutItem;
    private JMenuItem mockUndoItem;
    private JButton mockUndoButton;

    @BeforeEach
    void setUp() {
        // 1. Crear los Mocks de dependencias
        mockMainFrame = mock(MainFrame.class);
        mockCanvasController = mock(CanvasController.class);
        mockCanvasPanel = mock(CanvasPanel.class);
        mockClientApp = mock(ClientApp.class);
        mockToolboxPanel = mock(ToolboxPanel.class);

        // 2. Crear los Mocks de los componentes de la UI
        mockConnectItem = mock(JMenuItem.class);
        mockDisconnectItem = mock(JMenuItem.class);
        mockExitItem = mock(JMenuItem.class);
        mockClearItem = mock(JMenuItem.class);
        mockToggleGridItem = mock(JCheckBoxMenuItem.class); // Asumo que es CheckBox
        mockAboutItem = mock(JMenuItem.class);
        mockUndoItem = mock(JMenuItem.class);
        mockUndoButton = mock(JButton.class);

        // 3. Entrenar al MainFrame para que devuelva nuestros componentes falsos
        when(mockMainFrame.getConnectItem()).thenReturn(mockConnectItem);
        when(mockMainFrame.getDisconnectItem()).thenReturn(mockDisconnectItem);
        when(mockMainFrame.getExitItem()).thenReturn(mockExitItem);
        when(mockMainFrame.getClearItem()).thenReturn(mockClearItem);
        when(mockMainFrame.getToggleGridItem()).thenReturn(mockToggleGridItem);
        when(mockMainFrame.getAboutItem()).thenReturn(mockAboutItem);
        when(mockMainFrame.getUndoItem()).thenReturn(mockUndoItem);
        
        // 4. Entrenar al ToolboxPanel
        when(mockToolboxPanel.getUndoButton()).thenReturn(mockUndoButton);

        // 5. Inicializar el controlador
        menuController = new MenuController(
                mockMainFrame, 
                mockCanvasController, 
                mockCanvasPanel, 
                mockClientApp, 
                mockToolboxPanel
        );
    }

    @Test
    void testInitConfiguraTodosLosListeners() {
        // Ejecutamos el método que queremos probar
        menuController.init();

        // Verificamos que se haya llamado a addActionListener en TODOS los elementos
        verify(mockConnectItem).addActionListener(any());
        verify(mockDisconnectItem).addActionListener(any());
        verify(mockExitItem).addActionListener(any());
        verify(mockClearItem).addActionListener(any());
        verify(mockToggleGridItem).addActionListener(any());
        verify(mockAboutItem).addActionListener(any());
        verify(mockUndoItem).addActionListener(any());
        verify(mockUndoButton).addActionListener(any());
    }

    @Test
    void testConectarYDesconectar() {
        menuController.init();

        // Capturamos el listener de CONECTAR
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockConnectItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null); // Simulamos el clic
        verify(mockClientApp).manejarConexion();

        // Capturamos el listener de DESCONECTAR
        verify(mockDisconnectItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null); // Simulamos el clic
        verify(mockClientApp).gestionarDesconexion();
    }

    @Test
    void testAccionesDelCanvas() {
        menuController.init();
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);

        // 1. Limpiar Canvas
        verify(mockClearItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);
        verify(mockCanvasController).clearCanvas();

        // 2. Undo desde Menú
        verify(mockUndoItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);
        verify(mockCanvasController).undo();

        // 3. Undo desde Botón (Toolbox)
        verify(mockUndoButton).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);
        verify(mockCanvasController, times(2)).undo(); // Debe haberse llamado 2 veces en total
    }

    @Test
    void testToggleGrid() {
        menuController.init();
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);

        // Simulamos que el checkbox está seleccionado (TRUE)
        when(mockToggleGridItem.isSelected()).thenReturn(true);

        verify(mockToggleGridItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockCanvasPanel).setShowGrid(true);
    }

    @Test
    void testAboutDialog() {
        menuController.init();
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);

        verify(mockAboutItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);

        verify(mockMainFrame).showAboutDialog();
    }
    
    // NOTA: No testeamos el CLICK de "ExitItem" (System.exit) porque
    // eso cerraría la máquina virtual de Java y detendría el test.
    // Con verificar que se agregó el listener en el primer test es suficiente por ahora.
}