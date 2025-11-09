package com.equipo.pixelart.server.persistence;

import com.equipo.pixelart.server.model.Usuario;
import java.awt.Color;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de Archivos - Maneja la persistencia de datos
 * Guarda usuarios y estado del canvas en archivos
 */
public class FileManager {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.dat";
    private static final String CANVAS_FILE = DATA_DIR + "/canvas.dat";

    /**
     * Constructor - Crea el directorio de datos si no existe
     */
    public FileManager() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("Directorio de datos creado: " + DATA_DIR);
        }
    }

    // ==================== GESTIÓN DE USUARIOS ====================

    /**
     * Carga todos los usuarios desde el archivo
     */
    @SuppressWarnings("unchecked")
    public Map<String, Usuario> cargarUsuarios() {
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            System.out.println("Archivo de usuarios no existe, creando nuevo mapa");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<String, Usuario> usuarios = (Map<String, Usuario>) ois.readObject();
            System.out.println("Usuarios cargados: " + usuarios.size());
            return usuarios;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error cargando usuarios: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Guarda todos los usuarios en el archivo
     */
    public void guardarUsuarios(Map<String, Usuario> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(usuarios);
            System.out.println("Usuarios guardados: " + usuarios.size());
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    // ==================== GESTIÓN DEL CANVAS ====================

    /**
     * Carga el estado del canvas desde el archivo
     */
    public Color[][] cargarCanvas(int width, int height) {
        File file = new File(CANVAS_FILE);

        if (!file.exists()) {
            System.out.println("Archivo de canvas no existe, creando canvas vacío");
            return crearCanvasVacio(width, height);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Color[][] canvas = (Color[][]) ois.readObject();
            System.out.println("Canvas cargado: " + canvas.length + "x" + canvas[0].length);

            // Verificar dimensiones
            if (canvas.length != width || canvas[0].length != height) {
                System.out.println("Dimensiones no coinciden, creando nuevo canvas");
                return crearCanvasVacio(width, height);
            }

            return canvas;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error cargando canvas: " + e.getMessage());
            return crearCanvasVacio(width, height);
        }
    }

    /**
     * Guarda el estado del canvas en el archivo
     */
    public void guardarCanvas(Color[][] canvas) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CANVAS_FILE))) {
            oos.writeObject(canvas);
            System.out.println("Canvas guardado: " + canvas.length + "x" + canvas[0].length);
        } catch (IOException e) {
            System.err.println("Error guardando canvas: " + e.getMessage());
        }
    }

    /**
     * Crea un canvas vacío (blanco)
     */
    private Color[][] crearCanvasVacio(int width, int height) {
        Color[][] canvas = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvas[x][y] = Color.WHITE;
            }
        }
        return canvas;
    }

    /**
     * Exporta el canvas como imagen PNG
     */
    public void exportarCanvasPNG(Color[][] canvas, String filename) {
        // Implementación para exportar a PNG (opcional)
        System.out.println("Exportar a PNG no implementado aún");
    }

    /**
     * Limpia todos los datos persistidos
     */
    public void limpiarDatos() {
        File usersFile = new File(USERS_FILE);
        File canvasFile = new File(CANVAS_FILE);

        if (usersFile.exists()) usersFile.delete();
        if (canvasFile.exists()) canvasFile.delete();

        System.out.println("Datos limpiados");
    }
}