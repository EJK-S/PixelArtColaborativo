package com.equipo.pixelart.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

/**
 * Panel de Herramientas - Contiene todas las herramientas de dibujo
 */
public class ToolboxPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    // Botones de herramientas
    private JButton pencilButton;
    private JButton eraserButton;
    private JButton bucketButton;
    private JButton eyedropperButton;

    // Selectores de color
    private JButton primaryColorButton;
    private JButton secondaryColorButton;
    private JButton swapColorsButton;

    // Botones de colores predefinidos
    private JButton[] colorPresetButtons;
    private static final int COLOR_PRESET_COUNT = 20;

    // Control de tamaño de pincel
    private JSlider brushSizeSlider;
    private JLabel brushSizeLabel;

    // Botones de acción
    private JButton clearButton;
    private JButton saveButton;
    private JButton loadButton;

    /**
     * Constructor
     */
    public ToolboxPanel() {
        setupUI();
    }

    /**
     * Configura la interfaz del panel
     */
    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Herramientas",
                0,
                0,
                new Font("Arial", Font.BOLD, 12)
        ));
        setPreferredSize(new Dimension(280, 600));

        // Agregar secciones
        add(createToolsSection());
        add(Box.createVerticalStrut(10));
        add(createColorSection());
        add(Box.createVerticalStrut(10));
        add(createColorPresetsSection());
        add(Box.createVerticalStrut(10));
        add(createBrushSizeSection());
        add(Box.createVerticalStrut(10));
        add(createActionsSection());
        add(Box.createVerticalGlue());
    }

    /**
     * Crea la sección de herramientas
     */
    private JPanel createToolsSection() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Herramientas"));

        pencilButton = createToolButton("✏️ Lápiz", "pencil");
        eraserButton = createToolButton("🧹 Borrador", "eraser");
        bucketButton = createToolButton("🪣 Balde", "bucket");
        eyedropperButton = createToolButton("💧 Gotero", "eyedropper");

        panel.add(pencilButton);
        panel.add(eraserButton);
        panel.add(bucketButton);
        panel.add(eyedropperButton);

        return panel;
    }

    /**
     * Crea un botón de herramienta
     */
    private JButton createToolButton(String text, String name) {
        JButton button = new JButton(text);
        button.setName(name);
        button.setFont(new Font("Arial", Font.PLAIN, 11));
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Crea la sección de colores primario y secundario
     */
    private JPanel createColorSection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Colores"));

        // Panel de colores
        JPanel colorPanel = new JPanel(new GridLayout(1, 2, 5, 5));

        primaryColorButton = new JButton();
        primaryColorButton.setPreferredSize(new Dimension(60, 60));
        primaryColorButton.setBackground(Color.BLACK);
        primaryColorButton.setToolTipText("Color Primario");
        primaryColorButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        secondaryColorButton = new JButton();
        secondaryColorButton.setPreferredSize(new Dimension(60, 60));
        secondaryColorButton.setBackground(Color.WHITE);
        secondaryColorButton.setToolTipText("Color Secundario");
        secondaryColorButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        colorPanel.add(primaryColorButton);
        colorPanel.add(secondaryColorButton);

        // Botón de intercambio
        swapColorsButton = new JButton("⇄ Intercambiar");
        swapColorsButton.setFont(new Font("Arial", Font.PLAIN, 10));

        panel.add(colorPanel, BorderLayout.CENTER);
        panel.add(swapColorsButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea la sección de colores predefinidos
     */
    private JPanel createColorPresetsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Paleta"));

        JPanel gridPanel = new JPanel(new GridLayout(4, 5, 3, 3));
        colorPresetButtons = new JButton[COLOR_PRESET_COUNT];

        for (int i = 0; i < COLOR_PRESET_COUNT; i++) {
            JButton colorButton = new JButton();
            colorButton.setPreferredSize(new Dimension(30, 30));
            colorButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            colorButton.setFocusPainted(false);
            colorPresetButtons[i] = colorButton;
            gridPanel.add(colorButton);
        }

        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea la sección de tamaño de pincel
     */
    private JPanel createBrushSizeSection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Tamaño del Pincel"));

        brushSizeLabel = new JLabel("Tamaño: 1", SwingConstants.CENTER);
        brushSizeLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        brushSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        brushSizeSlider.setMajorTickSpacing(3);
        brushSizeSlider.setMinorTickSpacing(1);
        brushSizeSlider.setPaintTicks(true);
        brushSizeSlider.setPaintLabels(true);

        panel.add(brushSizeLabel, BorderLayout.NORTH);
        panel.add(brushSizeSlider, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea la sección de acciones
     */
    private JPanel createActionsSection() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones"));

        clearButton = new JButton("🗑️ Limpiar Canvas");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 11));

        saveButton = new JButton("💾 Guardar");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 11));

        loadButton = new JButton("📂 Cargar");
        loadButton.setFont(new Font("Arial", Font.PLAIN, 11));

        panel.add(clearButton);
        panel.add(saveButton);
        panel.add(loadButton);

        return panel;
    }

    /**
     * Actualiza la visualización de colores
     */
    public void updateColorDisplay(Color primary, Color secondary) {
        primaryColorButton.setBackground(primary);
        secondaryColorButton.setBackground(secondary);
    }

    /**
     * Actualiza el label del tamaño de pincel
     */
    public void updateBrushSizeLabel(int size) {
        brushSizeLabel.setText("Tamaño: " + size);
    }

    /**
     * Marca una herramienta como seleccionada
     */
    public void setSelectedTool(String toolName) {
        // Resetear todos los botones
        pencilButton.setBorder(BorderFactory.createEmptyBorder());
        eraserButton.setBorder(BorderFactory.createEmptyBorder());
        bucketButton.setBorder(BorderFactory.createEmptyBorder());
        eyedropperButton.setBorder(BorderFactory.createEmptyBorder());

        // Marcar el seleccionado
        JButton selectedButton = null;
        switch (toolName) {
            case "pencil" -> selectedButton = pencilButton;
            case "eraser" -> selectedButton = eraserButton;
            case "bucket" -> selectedButton = bucketButton;
            case "eyedropper" -> selectedButton = eyedropperButton;
        }

        if (selectedButton != null) {
            selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        }
    }

    /**
     * Muestra un diálogo de confirmación
     */
    public int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Muestra un diálogo de guardar archivo
     */
    public String showSaveDialog() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * Muestra un diálogo de cargar archivo
     */
    public String showLoadDialog() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /**
     * Muestra un mensaje
     */
    public void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un error
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // ==================== GETTERS ====================

    public JButton getPencilButton() { return pencilButton; }
    public JButton getEraserButton() { return eraserButton; }
    public JButton getBucketButton() { return bucketButton; }
    public JButton getEyedropperButton() { return eyedropperButton; }
    public JButton getPrimaryColorButton() { return primaryColorButton; }
    public JButton getSecondaryColorButton() { return secondaryColorButton; }
    public JButton getSwapColorsButton() { return swapColorsButton; }
    public JButton[] getColorPresetButtons() { return colorPresetButtons; }
    public JSlider getBrushSizeSlider() { return brushSizeSlider; }
    public JLabel getBrushSizeLabel() { return brushSizeLabel; }
    public JButton getClearButton() { return clearButton; }
    public JButton getSaveButton() { return saveButton; }
    public JButton getLoadButton() { return loadButton; }
}