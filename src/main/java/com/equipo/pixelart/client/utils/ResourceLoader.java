package com.equipo.pixelart.client.utils;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException; 
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

public class ResourceLoader {

    
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();

    /**
     * 1. Carga una imagen tal cual viene (sin cambiar tamaño).
     * Este es el método que le faltaba a tu MainFrame.
     */
    public static ImageIcon loadIcon(String path) {
        if (iconCache.containsKey(path)) {
            return iconCache.get(path);
        }

        URL imgUrl = ResourceLoader.class.getResource(path);
        if (imgUrl == null) {
            System.err.println("⚠️ Imagen no encontrada: " + path);
            return null;
        }

        ImageIcon icon = new ImageIcon(imgUrl);
        iconCache.put(path, icon);
        return icon;
    }

    /**
     * 2. Carga una imagen y le cambia el tamaño.
     * Usado por: ToolboxPanel (botones).
     */
    public static ImageIcon loadResizedIcon(String path, int width, int height) {
        ImageIcon original = loadIcon(path); 
        if (original == null) return null;

        Image img = original.getImage();
        Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }

    /**
     * 3. Crea un cursor personalizado.
     * Usado por: CanvasPanel (cuando implementemos los cursores).
     */
    public static Cursor loadCustomCursor(String path, String name) {
        
        ImageIcon icon = loadResizedIcon(path, 32, 32);
        if (icon == null) return Cursor.getDefaultCursor();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        
        return toolkit.createCustomCursor(icon.getImage(), new Point(0, 31), name);
    }

    public static void playSound(String path) {
        new Thread(() -> {
            try {
                
                URL url = ResourceLoader.class.getResource(path);
                if (url == null) {
                    System.err.println("Audio no encontrado: " + path);
                    return;
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                
                
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                
                clip.start();
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                System.err.println("Error reproduciendo audio: " + e.getMessage());
            }
        }).start();
    }
}