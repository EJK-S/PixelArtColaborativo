package com.equipo.pixelart.server.persistence;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.equipo.pixelart.server.model.Usuario;

class FileManagerTest {

    private FileManager fileManager;
    
    // Ya no hardcodeamos "data", lo leeremos dinámicamente
    private String realDataDir;
    private String realUsersFile;
    private String realCanvasFile;

    @BeforeEach
    public void setUp() throws Exception {
        // Leemos las rutas reales usando Reflexión (porque son privadas en FileManager)
        realDataDir = getPrivateConstant("DATA_DIR");
        realUsersFile = getPrivateConstant("USERS_FILE");
        realCanvasFile = getPrivateConstant("CANVAS_FILE");

        limpiarArchivos();
        fileManager = new FileManager();
    }

    @AfterEach
    public void tearDown() {
        System.gc(); 
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        limpiarArchivos();
    }

    private void limpiarArchivos() {
        if (realUsersFile != null) borrarConReintentos(new File(realUsersFile));
        if (realCanvasFile != null) borrarConReintentos(new File(realCanvasFile));
        
        if (realDataDir != null) {
            File dir = new File(realDataDir);
            if (dir.exists() && dir.list() != null && dir.list().length == 0) {
                dir.delete();
            }
        }
    }

    private void borrarConReintentos(File file) {
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                file.delete();
            }
        }
    }

    @Test
    void testGuardarYCargarUsuariosExitoso() {
        Map<String, Usuario> usuarios = new HashMap<>();
        usuarios.put("Anthony", new Usuario("Anthony", "hash123"));

        fileManager.guardarUsuarios(usuarios);

        Map<String, Usuario> cargados = fileManager.cargarUsuarios();

        assertNotNull(cargados);
        assertEquals(1, cargados.size());
        assertTrue(cargados.containsKey("Anthony"));
    }

    @Test
    void testCargarUsuariosArchivoNoExiste() {
        new File(realUsersFile).delete();

        Map<String, Usuario> result = fileManager.cargarUsuarios();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Debe devolver mapa vacío si no hay archivo");
    }

    @Test
    void testCargarUsuariosArchivoCorrupto() throws IOException {
        File file = new File(realUsersFile);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("ESTO NO ES UN OBJETO".getBytes());
        }

        Map<String, Usuario> result = fileManager.cargarUsuarios();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGuardarUsuariosFalloIO() {
        File file = new File(realUsersFile);
        file.mkdir(); // Convertimos el archivo en carpeta para provocar error

        Map<String, Usuario> map = new HashMap<>();

        assertDoesNotThrow(() -> fileManager.guardarUsuarios(map));
        
        file.delete(); 
    }

    @Test
    void testGuardarYCargarCanvasExitoso() {
        Color[][] canvas = new Color[2][2];
        canvas[0][0] = Color.RED;
        canvas[0][1] = new Color(0,0,0,0); // Transparente
        canvas[1][0] = Color.BLUE;
        canvas[1][1] = Color.BLACK;

        fileManager.guardarCanvas(canvas);

        Color[][] cargado = fileManager.cargarCanvas(2, 2);

        assertNotNull(cargado);
        assertEquals(Color.RED, cargado[0][0]);
        assertEquals(0, cargado[0][1].getAlpha()); // Verificamos transparencia
    }

    @Test
    void testCargarCanvasNoExiste() {
        new File(realCanvasFile).delete();

        Color[][] result = fileManager.cargarCanvas(5, 5);

        assertNotNull(result);
        assertEquals(5, result.length);
        // CAMBIO: Ahora esperamos Transparente (Alpha 0), no WHITE
        assertEquals(0, result[0][0].getAlpha(), "Debe iniciar transparente");
    }

    @Test
    void testCargarCanvasDimensionesIncorrectas() {
        Color[][] peque = new Color[2][2];
        fileManager.guardarCanvas(peque);

        Color[][] result = fileManager.cargarCanvas(10, 10);

        assertEquals(10, result.length);
        assertEquals(10, result[0].length);
        // CAMBIO: Esperamos Transparente
        assertEquals(0, result[0][0].getAlpha()); 
    }

    @Test
    void testCargarCanvasCorrupto() throws IOException {
        File file = new File(realCanvasFile);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("BASURA".getBytes());
        }

        Color[][] result = fileManager.cargarCanvas(3, 3);

        assertNotNull(result);
        assertEquals(3, result.length);
        // CAMBIO: Esperamos Transparente
        assertEquals(0, result[0][0].getAlpha());
    }
    
    @Test
    void testGuardarCanvasFalloIO() {
        File file = new File(realCanvasFile);
        file.mkdir();

        Color[][] canvas = new Color[2][2];
        assertDoesNotThrow(() -> fileManager.guardarCanvas(canvas));
        
        file.delete();
    }

    @Test
    void testLimpiarDatos() throws IOException {
        // Usamos las rutas reales
        new File(realDataDir).mkdirs();
        new File(realUsersFile).createNewFile();
        new File(realCanvasFile).createNewFile();

        assertTrue(new File(realUsersFile).exists());

        fileManager.limpiarDatos();

        assertFalse(new File(realUsersFile).exists());
        assertFalse(new File(realCanvasFile).exists());
    }

    // Eliminado testExportarCanvasPNG porque el método ya no existe

    @Test
    void testConstructorCreaDirectorio() {
        File dataDir = new File(realDataDir);
        
        // Limpieza previa manual para el test
        if(dataDir.exists()) {
            String[] entries = dataDir.list();
            if (entries != null) {
                for(String s: entries){
                    new File(dataDir.getPath(), s).delete(); 
                }
            }
            dataDir.delete();
        }
        assertFalse(dataDir.exists());
        
        FileManager fm = new FileManager(); 
        assertNotNull(fm, "El FileManager debería haberse instanciado correctamente");
        
        assertTrue(dataDir.exists());
    }

    // --- MÉTODO DE AYUDA PARA LEER CONSTANTES PRIVADAS ---
    private String getPrivateConstant(String fieldName) throws Exception {
        Field field = FileManager.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (String) field.get(null);
    }
}