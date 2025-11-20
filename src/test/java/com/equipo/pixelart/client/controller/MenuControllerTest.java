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
    
    
    private MainFrame mockMainFrame;
    private CanvasController mockCanvasController;
    private CanvasPanel mockCanvasPanel;
    private ClientApp mockClientApp;
    private ToolboxPanel mockToolboxPanel;

    
    private JMenuItem mockConnectItem;
    private JMenuItem mockDisconnectItem;
    private JMenuItem mockExitItem;
    private JMenuItem mockClearItem;
    private JCheckBoxMenuItem mockToggleGridItem; 
    private JMenuItem mockAboutItem;
    private JMenuItem mockUndoItem;
    private JButton mockUndoButton;

    @BeforeEach
    void setUp() {
        
        mockMainFrame = mock(MainFrame.class);
        mockCanvasController = mock(CanvasController.class);
        mockCanvasPanel = mock(CanvasPanel.class);
        mockClientApp = mock(ClientApp.class);
        mockToolboxPanel = mock(ToolboxPanel.class);

        
        mockConnectItem = mock(JMenuItem.class);
        mockDisconnectItem = mock(JMenuItem.class);
        mockExitItem = mock(JMenuItem.class);
        mockClearItem = mock(JMenuItem.class);
        mockToggleGridItem = mock(JCheckBoxMenuItem.class); 
        mockAboutItem = mock(JMenuItem.class);
        mockUndoItem = mock(JMenuItem.class);
        mockUndoButton = mock(JButton.class);


        when(mockMainFrame.getConnectItem()).thenReturn(mockConnectItem);
        when(mockMainFrame.getDisconnectItem()).thenReturn(mockDisconnectItem);
        when(mockMainFrame.getExitItem()).thenReturn(mockExitItem);
        when(mockMainFrame.getClearItem()).thenReturn(mockClearItem);
        when(mockMainFrame.getToggleGridItem()).thenReturn(mockToggleGridItem);
        when(mockMainFrame.getAboutItem()).thenReturn(mockAboutItem);
        when(mockMainFrame.getUndoItem()).thenReturn(mockUndoItem);
        
        
        when(mockToolboxPanel.getUndoButton()).thenReturn(mockUndoButton);

        
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
        
        menuController.init();

        
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

        
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);
        verify(mockConnectItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null); 
        verify(mockClientApp).manejarConexion();

        
        verify(mockDisconnectItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null); 
        verify(mockClientApp).gestionarDesconexion();
    }

    @Test
    void testAccionesDelCanvas() {
        menuController.init();
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);

        
        verify(mockClearItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);
        verify(mockCanvasController).clearCanvas();

        
        verify(mockUndoItem).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);
        verify(mockCanvasController).undo();

        
        verify(mockUndoButton).addActionListener(captor.capture());
        captor.getValue().actionPerformed(null);
        verify(mockCanvasController, times(2)).undo(); 
    }

    @Test
    void testToggleGrid() {
        menuController.init();
        ArgumentCaptor<ActionListener> captor = ArgumentCaptor.forClass(ActionListener.class);

       
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
    
    
}