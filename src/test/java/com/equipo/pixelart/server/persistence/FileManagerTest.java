package com.equipo.pixelart.server.persistence;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private final String DATA_DIR = "data";
    private final String USERS_FILE = DATA_DIR + "/users.dat";
    private final String CANVAS_FILE = DATA_DIR + "/canvas.dat";

    @BeforeEach
    public void setUp() {

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
    	borrarConReintentos(new File(USERS_FILE));
        borrarConReintentos(new File(CANVAS_FILE));
        
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.list().length == 0) {
            dir.delete();
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
        
        new File(USERS_FILE).delete();

        Map<String, Usuario> result = fileManager.cargarUsuarios();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Debe devolver mapa vacío si no hay archivo");
    }

    @Test
    void testCargarUsuariosArchivoCorrupto() throws IOException {
        
        File file = new File(USERS_FILE);
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

        File file = new File(USERS_FILE);
        file.mkdir(); 

        Map<String, Usuario> map = new HashMap<>();

        assertDoesNotThrow(() -> fileManager.guardarUsuarios(map));
        
        file.delete(); 
    }

    @Test
    void testGuardarYCargarCanvasExitoso() {
      
        Color[][] canvas = new Color[2][2];
        canvas[0][0] = Color.RED;
        canvas[0][1] = Color.WHITE;
        canvas[1][0] = Color.BLUE;
        canvas[1][1] = Color.BLACK;

        fileManager.guardarCanvas(canvas);

        Color[][] cargado = fileManager.cargarCanvas(2, 2);

        assertNotNull(cargado);
        assertEquals(Color.RED, cargado[0][0]);
        assertEquals(Color.BLACK, cargado[1][1]);
    }

    @Test
    void testCargarCanvasNoExiste() {
        new File(CANVAS_FILE).delete();

        Color[][] result = fileManager.cargarCanvas(5, 5);

        assertNotNull(result);
        assertEquals(5, result.length);
        assertEquals(Color.WHITE, result[0][0]);
    }

    @Test
    void testCargarCanvasDimensionesIncorrectas() {
      
        Color[][] peque = new Color[2][2];
        fileManager.guardarCanvas(peque);

        Color[][] result = fileManager.cargarCanvas(10, 10);

        assertEquals(10, result.length);
        assertEquals(10, result[0].length);
        assertEquals(Color.WHITE, result[0][0]); 
    }

    @Test
    void testCargarCanvasCorrupto() throws IOException {
       
        File file = new File(CANVAS_FILE);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("BASURA".getBytes());
        }

        Color[][] result = fileManager.cargarCanvas(3, 3);

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(Color.WHITE, result[0][0]);
    }
    
    @Test
    void testGuardarCanvasFalloIO() {
        
        File file = new File(CANVAS_FILE);
        file.mkdir();

        Color[][] canvas = new Color[2][2];
        assertDoesNotThrow(() -> fileManager.guardarCanvas(canvas));
        
        file.delete();
    }

    @Test
    void testLimpiarDatos() throws IOException {
        // Crear archivos dummy
        new File(DATA_DIR).mkdirs();
        new File(USERS_FILE).createNewFile();
        new File(CANVAS_FILE).createNewFile();

        assertTrue(new File(USERS_FILE).exists());

        fileManager.limpiarDatos();

        assertFalse(new File(USERS_FILE).exists());
        assertFalse(new File(CANVAS_FILE).exists());
    }

    @Test
    void testExportarCanvasPNG() {
        
        assertDoesNotThrow(() -> fileManager.exportarCanvasPNG(new Color[2][2], "test.png"));
    }
    
    @Test
    void testConstructorCreaDirectorio() {
    
        File dataDir = new File(DATA_DIR);
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
}