package com.equipo.pixelart.server.persistence;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.equipo.pixelart.server.model.Usuario;

public class FileManager {

    private static final String DATA_DIR = "data_V2";
    private static final String USERS_FILE = DATA_DIR + "/users.dat";
    private static final String CANVAS_FILE = DATA_DIR + "/canvas.dat";

    public FileManager() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("Directorio de datos creado: " + DATA_DIR);
        }
    }

    public Map<String, Usuario> cargarUsuarios() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            
            Map<String, Usuario> usuarios = new HashMap<>();

            if (obj instanceof Map<?, ?> rawMap) {
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    if (entry.getKey() instanceof String key && entry.getValue() instanceof Usuario usuario) {
                        usuarios.put(key, usuario);
                    }
                }
            }
            return usuarios;
            
        } catch (Exception e) {
            System.err.println("Error cargando usuarios: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public void guardarUsuarios(Map<String, Usuario> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public Color[][] cargarCanvas(int width, int height) {
        File file = new File(CANVAS_FILE);
        if (!file.exists()) {
            System.out.println("Archivo de canvas no existe, creando canvas vacío");
            return crearCanvasVacio(width, height);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Color[][] canvas = (Color[][]) ois.readObject();
            if (canvas.length != width || canvas[0].length != height) {
                System.out.println("Dimensiones no coinciden, creando nuevo canvas");
                return crearCanvasVacio(width, height);
            }
            return canvas;
        } catch (Exception e) {
            System.err.println("Error cargando canvas: " + e.getMessage());
            return crearCanvasVacio(width, height);
        }
    }

    public void guardarCanvas(Color[][] canvas) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CANVAS_FILE))) {
            oos.writeObject(canvas);
        } catch (IOException e) {
            System.err.println("Error guardando canvas: " + e.getMessage());
        }
    }

    private Color[][] crearCanvasVacio(int width, int height) {
        Color[][] canvas = new Color[width][height];
        Color transparent = new Color(0, 0, 0, 0); 
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                
                canvas[x][y] = transparent; 
            }
        }
        return canvas;
    }

    public void limpiarDatos() {
        File users = new File(USERS_FILE);
        File canvas = new File(CANVAS_FILE);

        if (users.exists()) {
            users.delete();
        }
        if (canvas.exists()) {
            canvas.delete();
        }
        System.out.println("Datos de persistencia eliminados (Limpieza).");
    }
}