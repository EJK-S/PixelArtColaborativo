package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.app.ClientApp;
import com.equipo.pixelart.client.view.CanvasPanel;
import com.equipo.pixelart.client.view.MainFrame;
import com.equipo.pixelart.client.view.ToolboxPanel;

public class MenuController {

    private final MainFrame mainFrame;
    private final CanvasController canvasController;
    private final CanvasPanel canvasPanel;
    private final ClientApp clientApp;
    private final ToolboxPanel toolboxPanel;  

    public MenuController(MainFrame mainFrame, CanvasController canvasController,
                          CanvasPanel canvasPanel, ClientApp clientApp,
                          ToolboxPanel toolboxPanel) {  
        this.mainFrame = mainFrame;
        this.canvasController = canvasController;
        this.canvasPanel = canvasPanel;
        this.clientApp = clientApp;
        this.toolboxPanel = toolboxPanel;  
    }

    public void init() {
        mainFrame.getConnectItem().addActionListener(e -> clientApp.manejarConexion());
        mainFrame.getDisconnectItem().addActionListener(e -> clientApp.gestionarDesconexion());
        mainFrame.getExitItem().addActionListener(e -> System.exit(0));

        mainFrame.getClearItem().addActionListener(e -> canvasController.clearCanvas());
        mainFrame.getToggleGridItem().addActionListener(e ->
                canvasPanel.setShowGrid(mainFrame.getToggleGridItem().isSelected()));

        mainFrame.getAboutItem().addActionListener(e -> mainFrame.showAboutDialog());

        
        mainFrame.getUndoItem().addActionListener(e -> canvasController.undo());

        
        toolboxPanel.getUndoButton().addActionListener(e -> canvasController.undo());
    }
}