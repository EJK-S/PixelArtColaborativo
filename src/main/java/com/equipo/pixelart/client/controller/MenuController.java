package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.app.ClientApp;
import com.equipo.pixelart.client.view.CanvasPanel;
import com.equipo.pixelart.client.view.MainFrame;


public class MenuController {

    private final MainFrame mainFrame;
    private final CanvasController canvasController;
    private final CanvasPanel canvasPanel;
    private final ClientApp clientApp; 

    public MenuController(MainFrame mainFrame, CanvasController canvasController,
                          CanvasPanel canvasPanel, ClientApp clientApp) {
        this.mainFrame = mainFrame;
        this.canvasController = canvasController;
        this.canvasPanel = canvasPanel;
        this.clientApp = clientApp;
    }

    public void init() {
        mainFrame.getConnectItem().addActionListener(e -> clientApp.manejarConexion());
        mainFrame.getDisconnectItem().addActionListener(e -> clientApp.gestionarDesconexion());
        mainFrame.getExitItem().addActionListener(e -> System.exit(0));

        mainFrame.getClearItem().addActionListener(e -> canvasController.clearCanvas());
        mainFrame.getToggleGridItem().addActionListener(e ->
                canvasPanel.setShowGrid(mainFrame.getToggleGridItem().isSelected()));
        
        mainFrame.getAboutItem().addActionListener(e -> mainFrame.showAboutDialog());
    }
}