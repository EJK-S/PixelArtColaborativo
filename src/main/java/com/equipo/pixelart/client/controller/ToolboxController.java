package com.equipo.pixelart.client.controller;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JSlider;

import com.equipo.pixelart.client.view.ToolboxPanel;
import com.equipo.pixelart.server.persistence.FileManager;


public class ToolboxController {

    private final ToolboxPanel view;
    private final CanvasController canvasController;

   
    private static final String TOOL_PENCIL = "pencil";
    private static final String TOOL_ERASER = "eraser";
    private static final String TOOL_BUCKET = "bucket";
    private static final String TOOL_EYEDROPPER = "eyedropper";


    private static final Color[] PRESET_COLORS = {
            Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
            Color.YELLOW, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.CYAN,
            new Color(128, 0, 0),      
            new Color(139, 69, 19),    
            new Color(160, 82, 45),    
            new Color(210, 180, 140),  
            new Color(192, 192, 192),  
            new Color(128, 128, 128),  
            new Color(0, 128, 0),      
            new Color(0, 0, 128),      
            new Color(128, 0, 128),    
            new Color(255, 215, 0)     
    };

  
    private String currentTool;
    private Color currentColor;
    private Color secondaryColor; 
    private int brushSize;

   
    public ToolboxController(ToolboxPanel view, CanvasController canvasController) {
        this.view = view;
        this.canvasController = canvasController;

        
        this.currentTool = TOOL_PENCIL;
        this.currentColor = Color.BLACK;
        this.secondaryColor = Color.WHITE;
        this.brushSize = 1;

        
    }
    
    public void init() {
    	initializeListeners();
        updateCanvasController();
    }

   
    private  void initializeListeners() {
        
        view.getPencilButton().addActionListener(e -> selectTool(TOOL_PENCIL));
        view.getEraserButton().addActionListener(e -> selectTool(TOOL_ERASER));
        view.getBucketButton().addActionListener(e -> selectTool(TOOL_BUCKET));
        view.getEyedropperButton().addActionListener(e -> selectTool(TOOL_EYEDROPPER));

        
        view.getPrimaryColorButton().addActionListener(e -> choosePrimaryColor());

        
        view.getSecondaryColorButton().addActionListener(e -> chooseSecondaryColor());

        
        view.getSwapColorsButton().addActionListener(e -> swapColors());

        
        view.getBrushSizeSlider().addChangeListener(e -> {
            JSlider slider = (JSlider) e.getSource();
            if (!slider.getValueIsAdjusting()) {
                changeBrushSize(slider.getValue());
            }
        });

        
        initializeColorPresets();

        
        view.getClearButton().addActionListener(e -> clearCanvas());

        
        view.getUndoButton().addActionListener(e -> undo());

        
        view.getSaveButton().addActionListener(e -> saveCanvas());

        
        view.getLoadButton().addActionListener(e -> loadCanvas());
    }

  
    private void initializeColorPresets() {
        JButton[] colorButtons = view.getColorPresetButtons();

        for (int i = 0; i < colorButtons.length && i < PRESET_COLORS.length; i++) {
            final Color color = PRESET_COLORS[i];
            colorButtons[i].setBackground(color);
            colorButtons[i].addActionListener(e -> selectPresetColor(color));
        }
    }

    
    private void selectTool(String tool) {
        currentTool = tool;

        
        view.setSelectedTool(tool);

        
        canvasController.setCurrentTool(tool);

        System.out.println("Herramienta seleccionada: " + tool);
    }
 
    private void choosePrimaryColor() {
        
        Color newColor = openColorChooser("Seleccionar Color Primario", currentColor);
        if (newColor != null) {
            setCurrentColor(newColor);
        }
    }

    
    private void chooseSecondaryColor() {
        
        Color newColor = openColorChooser("Seleccionar Color Secundario", secondaryColor);
        if (newColor != null) {
            setSecondaryColor(newColor);
        }
    }

    
    protected Color openColorChooser(String title, Color initialColor) {
        return JColorChooser.showDialog(view, title, initialColor);
    }
  
    private void swapColors() {
        Color temp = currentColor;
        currentColor = secondaryColor;
        secondaryColor = temp;

        
        view.updateColorDisplay(currentColor, secondaryColor);

        
        canvasController.setCurrentColor(currentColor);

        System.out.println("Colores intercambiados");
    }
    
    private void selectPresetColor(Color color) {
        setCurrentColor(color);
    }

    private void changeBrushSize(int size) {
        brushSize = size;

        
        view.updateBrushSizeLabel(size);

        
        canvasController.setBrushSize(size);

        System.out.println("Tamaño de pincel: " + size);
    }
    
    private void clearCanvas() {
        int confirm = view.showConfirmDialog(
                "¿Estás seguro de que quieres limpiar el canvas?",
                "Confirmar"
        );

        if (confirm == 0) { 
            canvasController.clearCanvas();
            System.out.println("Canvas limpiado");
        }
    }
  
    private void saveCanvas() {
        try {
            String filename = view.showSaveDialog();
            
            if (filename != null) {
                
                FileManager fileManager = createFileManager(); 
                
                Color[][] pixels = canvasController.getModel().getPixels();
                fileManager.guardarCanvas(pixels); 
                
                view.showMessage("Canvas guardado exitosamente", "Éxito");
                System.out.println("Canvas guardado: " + filename);
            }
        } catch (Exception e) {
            view.showError("Error al guardar: " + e.getMessage());
            System.err.println("Error guardando canvas: " + e.getMessage());
        }
    }
    
    protected FileManager createFileManager() {
        return new FileManager();
    }
  
    private void loadCanvas() {
        try {
            String filename = view.showLoadDialog();

            if (filename != null) {
                
                

                
                view.showMessage("Función de carga en desarrollo", "Info");
                System.out.println("Cargando canvas: " + filename);
            }
        } catch (Exception e) {
            view.showError("Error al cargar: " + e.getMessage());
            System.err.println("Error cargando canvas: " + e.getMessage());
        }
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;

        
        view.updateColorDisplay(currentColor, secondaryColor);

        
        canvasController.setCurrentColor(color);

        System.out.println("Color seleccionado: " + colorToString(color));
    }
 
    public void setSecondaryColor(Color color) {
        this.secondaryColor = color;

        
        view.updateColorDisplay(currentColor, secondaryColor);
    }

    /**
     * Deshace el último cambio
     */
    private void undo() {
        canvasController.undo();
    }


    private  void updateCanvasController() {
        canvasController.setCurrentTool(currentTool);
        canvasController.setCurrentColor(currentColor);
        canvasController.setBrushSize(brushSize);
    }

    private String colorToString(Color color) {
        return String.format("RGB(%d, %d, %d)",
                color.getRed(), color.getGreen(), color.getBlue());
    }
 
    public String getCurrentTool() {
        return currentTool;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public static Color[] getPresetColors() {
        return PRESET_COLORS;
    }
}