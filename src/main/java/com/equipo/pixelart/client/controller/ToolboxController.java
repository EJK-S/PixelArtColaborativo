package com.equipo.pixelart.client.controller;

import com.equipo.pixelart.client.view.ToolboxPanel;
import com.equipo.pixelart.server.persistence.FileManager;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JSlider;


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
            new Color(128, 0, 0),      // Marrón oscuro
            new Color(139, 69, 19),    // Marrón
            new Color(160, 82, 45),    // Sienna
            new Color(210, 180, 140),  // Tan
            new Color(192, 192, 192),  // Plata
            new Color(128, 128, 128),  // Gris
            new Color(0, 128, 0),      // Verde oscuro
            new Color(0, 0, 128),      // Azul marino
            new Color(128, 0, 128),    // Púrpura
            new Color(255, 215, 0)     // Dorado
    };

  
    private String currentTool;
    private Color currentColor;
    private Color secondaryColor; // Para el botón derecho del mouse
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
        // Botones de herramientas
        view.getPencilButton().addActionListener(e -> selectTool(TOOL_PENCIL));
        view.getEraserButton().addActionListener(e -> selectTool(TOOL_ERASER));
        view.getBucketButton().addActionListener(e -> selectTool(TOOL_BUCKET));
        view.getEyedropperButton().addActionListener(e -> selectTool(TOOL_EYEDROPPER));

        // Selector de color primario
        view.getPrimaryColorButton().addActionListener(e -> choosePrimaryColor());

        // Selector de color secundario
        view.getSecondaryColorButton().addActionListener(e -> chooseSecondaryColor());

        // Botón de intercambio de colores
        view.getSwapColorsButton().addActionListener(e -> swapColors());

        // Slider de tamaño de pincel
        view.getBrushSizeSlider().addChangeListener(e -> {
            JSlider slider = (JSlider) e.getSource();
            if (!slider.getValueIsAdjusting()) {
                changeBrushSize(slider.getValue());
            }
        });

        // Botones de colores predefinidos
        initializeColorPresets();

        // Botón de limpiar canvas
        view.getClearButton().addActionListener(e -> clearCanvas());

        // Botón de guardar
        view.getSaveButton().addActionListener(e -> saveCanvas());

        // Botón de cargar
        view.getLoadButton().addActionListener(e -> loadCanvas());
    }

    /**
     * Inicializa los botones de colores predefinidos
     */
    private void initializeColorPresets() {
        JButton[] colorButtons = view.getColorPresetButtons();

        for (int i = 0; i < colorButtons.length && i < PRESET_COLORS.length; i++) {
            final Color color = PRESET_COLORS[i];
            colorButtons[i].setBackground(color);
            colorButtons[i].addActionListener(e -> selectPresetColor(color));
        }
    }

    /**
     * Selecciona una herramienta
     */
    private void selectTool(String tool) {
        currentTool = tool;

        // Actualizar UI
        view.setSelectedTool(tool);

        // Actualizar canvas controller
        canvasController.setCurrentTool(tool);

        System.out.println("Herramienta seleccionada: " + tool);
    }

    /**
     * Abre el selector de color primario
     */
    private void choosePrimaryColor() {
        Color newColor = JColorChooser.showDialog(
                view,
                "Seleccionar Color Primario",
                currentColor
        );

        if (newColor != null) {
            setCurrentColor(newColor);
        }
    }

    /**
     * Abre el selector de color secundario
     */
    private void chooseSecondaryColor() {
        Color newColor = JColorChooser.showDialog(
                view,
                "Seleccionar Color Secundario",
                secondaryColor
        );

        if (newColor != null) {
            setSecondaryColor(newColor);
        }
    }

    /**
     * Intercambia los colores primario y secundario
     */
    private void swapColors() {
        Color temp = currentColor;
        currentColor = secondaryColor;
        secondaryColor = temp;

        // Actualizar UI
        view.updateColorDisplay(currentColor, secondaryColor);

        // Actualizar canvas controller
        canvasController.setCurrentColor(currentColor);

        System.out.println("Colores intercambiados");
    }

    /**
     * Selecciona un color predefinido
     */
    private void selectPresetColor(Color color) {
        setCurrentColor(color);
    }

    /**
     * Cambia el tamaño del pincel
     */
    private void changeBrushSize(int size) {
        brushSize = size;

        // Actualizar label en la UI
        view.updateBrushSizeLabel(size);

        // Actualizar canvas controller
        canvasController.setBrushSize(size);

        System.out.println("Tamaño de pincel: " + size);
    }

    /**
     * Limpia el canvas
     */
    private void clearCanvas() {
        int confirm = view.showConfirmDialog(
                "¿Estás seguro de que quieres limpiar el canvas?",
                "Confirmar"
        );

        if (confirm == 0) { // YES_OPTION
            canvasController.clearCanvas();
            System.out.println("Canvas limpiado");
        }
    }

    /**
     * Guarda el canvas actual
     */
    private void saveCanvas() {
        try {
            String filename = view.showSaveDialog();
            FileManager fileManager = new FileManager();


            if (filename != null) {
                // Obtener el modelo del canvas
                Color[][] pixels = canvasController.getModel().getPixels();

                // Guardar en archivo (implementar FileManager)
                 fileManager.guardarCanvas(pixels);

                view.showMessage("Canvas guardado exitosamente", "Éxito");
                System.out.println("Canvas guardado: " + filename);
            }
        } catch (Exception e) {
            view.showError("Error al guardar: " + e.getMessage());
            System.err.println("Error guardando canvas: " + e.getMessage());
        }
    }

    /**
     * Carga un canvas desde archivo
     */
    private void loadCanvas() {
        try {
            String filename = view.showLoadDialog();

            if (filename != null) {
                // Cargar desde archivo (implementar FileManager)
                // Color[][] pixels = fileManager.loadCanvas(filename);

                // Por ahora, mostrar mensaje
                view.showMessage("Función de carga en desarrollo", "Info");
                System.out.println("Cargando canvas: " + filename);
            }
        } catch (Exception e) {
            view.showError("Error al cargar: " + e.getMessage());
            System.err.println("Error cargando canvas: " + e.getMessage());
        }
    }

    /**
     * Establece el color actual
     */
    public void setCurrentColor(Color color) {
        this.currentColor = color;

        // Actualizar UI
        view.updateColorDisplay(currentColor, secondaryColor);

        // Actualizar canvas controller
        canvasController.setCurrentColor(color);

        System.out.println("Color seleccionado: " + colorToString(color));
    }

    /**
     * Establece el color secundario
     */
    public void setSecondaryColor(Color color) {
        this.secondaryColor = color;

        // Actualizar UI
        view.updateColorDisplay(currentColor, secondaryColor);
    }

    /**
     * Actualiza el canvas controller con los valores actuales
     */
    private  void updateCanvasController() {
        canvasController.setCurrentTool(currentTool);
        canvasController.setCurrentColor(currentColor);
        canvasController.setBrushSize(brushSize);
    }

    /**
     * Convierte un color a string legible
     */
    private String colorToString(Color color) {
        return String.format("RGB(%d, %d, %d)",
                color.getRed(), color.getGreen(), color.getBlue());
    }

    // ==================== GETTERS ====================

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