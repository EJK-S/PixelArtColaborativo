package com.equipo.pixelart.client.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.equipo.pixelart.client.utils.ResourceLoader;


public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JMenuItem connectItem;
    private JMenuItem disconnectItem;
    private JMenuItem exitItem;
    private JMenuItem clearItem;
    private JMenuItem undoItem;
    private JMenuItem toggleGridItem;
    private JMenuItem aboutItem;

    private JLabel statusLabel;
    private JLabel userLabel;

    private String username;

    public MainFrame() {
        super("Pixel Art Colaborativo");
        username = "Invitado";
        setupFrame();
    }

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        ImageIcon logo = ResourceLoader.loadIcon("/images/app_logo.png");
        if (logo != null) {
            this.setIconImage(logo.getImage());
        }
        
        setLayout(new BorderLayout(5, 5));
       
        createMenuBar();
        createStatusBar();
       
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.setMnemonic('A');

        connectItem = new JMenuItem("Conectar...");
        connectItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));

        disconnectItem = new JMenuItem("Desconectar");
        disconnectItem.setEnabled(false);

        exitItem = new JMenuItem("Salir");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));

        fileMenu.add(connectItem);
        fileMenu.add(disconnectItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu editMenu = new JMenu("Edición");
        editMenu.setMnemonic('E');

        undoItem = new JMenuItem("Deshacer");
        undoItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));

        clearItem = new JMenuItem("Limpiar Canvas");
        clearItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));

        editMenu.add(undoItem);
        editMenu.add(clearItem);
        
        JMenu viewMenu = new JMenu("Ver");
        viewMenu.setMnemonic('V');

        toggleGridItem = new JCheckBoxMenuItem("Mostrar Grid", true);
        toggleGridItem.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));

        viewMenu.add(toggleGridItem);
        
        JMenu helpMenu = new JMenu("Ayuda");
        helpMenu.setMnemonic('H');

        aboutItem = new JMenuItem("Acerca de...");
        aboutItem.setAccelerator(KeyStroke.getKeyStroke("F1"));

        helpMenu.add(aboutItem);
       
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        statusLabel = new JLabel("Desconectado");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        userLabel = new JLabel("Usuario: " + username);
        userLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(userLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    
    void handleWindowClosing() {
        int confirm = showExitConfirmation();

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    
    protected int showExitConfirmation() {
        return JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que quieres salir?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
    }

    
    public ConnectionDialog showConnectionDialog() {
        ConnectionDialog dialog = new ConnectionDialog(this);
        openDialog(dialog); 
        return dialog;
    }

    
    protected void openDialog(JDialog dialog) {
        dialog.setVisible(true);
    }
    

    public void showAboutDialog() {
        String message = """
                Pixel Art Colaborativo
                
                Versión 1.0
                
                Una aplicación de dibujo colaborativo
                en tiempo real con Java Swing.
                
                © 2024 - Equipo de Desarrollo""";

        
        showMessageDialog(message, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    protected void showMessageDialog(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    public void updateConnectionStatus(boolean connected, String message) {
        if (connected) {
            statusLabel.setText("🟢 Conectado - " + message);
            connectItem.setEnabled(false);
            disconnectItem.setEnabled(true);
        } else {
            statusLabel.setText("🔴 Desconectado - " + message);
            connectItem.setEnabled(true);
            disconnectItem.setEnabled(false);
        }
    }

    public void updateUsername(String username) {
        this.username = username;
        userLabel.setText("👤 Usuario: " + username);
    }

    public void setPanels(ToolboxPanel toolbox, CanvasPanel canvas,
                          ChatPanel chat, UserListPanel userList) {

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(toolbox, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel canvasWrapper = new JPanel();
        canvasWrapper.setBackground(Color.DARK_GRAY);
        canvasWrapper.add(canvas);

        JScrollPane canvasScrollPane = new JScrollPane(canvasWrapper);
        canvasScrollPane.setBackground(Color.DARK_GRAY);
        centerPanel.add(canvasScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.add(chat);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(userList);

        add(rightPanel, BorderLayout.EAST);
        
        revalidate();
        repaint();
    }

    public JMenuItem getConnectItem() { return connectItem; }
    public JMenuItem getDisconnectItem() { return disconnectItem; }
    public JMenuItem getExitItem() { return exitItem; }
    public JMenuItem getClearItem() { return clearItem; }
    public JMenuItem getUndoItem() { return undoItem; }
    public JMenuItem getToggleGridItem() { return toggleGridItem; }
    public JMenuItem getAboutItem() { return aboutItem; }
}