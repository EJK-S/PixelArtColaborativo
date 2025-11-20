package com.equipo.pixelart.server.persistence;

import com.equipo.pixelart.server.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    private FileManager fileManager;
    private final String DATA_DIR = "data";
    private final String USERS_FILE = DATA_DIR + "/users.dat";
    private final String CANVAS_FILE = DATA_DIR + "/canvas.dat";

    @BeforeEach
    void setUp() {
        // Aseguramos un estado limpio antes de cada test
        limpiarArchivos();
        fileManager = new FileManager();
    }

    @AfterEach
    void tearDown() {
        // Limpiamos después para no dejar basura en tu disco
    	System.gc(); 
        
        // Damos un respiro al sistema de archivos
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
    
 // Método auxiliar robusto para borrar archivos rebeldes
    private void borrarConReintentos(File file) {
        if (file.exists()) {
            boolean deleted = file.delete();
            // Si falla a la primera, esperamos y reintentamos
            if (!deleted) {
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                file.delete();
            }
        }
    }

    // ==========================================
    // TESTS DE USUARIOS
    // ==========================================

    @Test
    void testGuardarYCargarUsuariosExitoso() {
        // 1. Crear datos
        Map<String, Usuario> usuarios = new HashMap<>();
        // Nota: Usuario debe ser Serializable. Asumo que tu clase Usuario lo es.
        // Si Usuario no es serializable, el test fallará (y tu app también).
        usuarios.put("Anthony", new Usuario("Anthony", "hash123"));

        // 2. Guardar
        fileManager.guardarUsuarios(usuarios);

        // 3. Cargar
        Map<String, Usuario> cargados = fileManager.cargarUsuarios();

        // 4. Verificar
        assertNotNull(cargados);
        assertEquals(1, cargados.size());
        assertTrue(cargados.containsKey("Anthony"));
    }

    @Test
    void testCargarUsuariosArchivoNoExiste() {
        // Aseguramos que no existe
        new File(USERS_FILE).delete();

        Map<String, Usuario> result = fileManager.cargarUsuarios();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Debe devolver mapa vacío si no hay archivo");
    }

    @Test
    void testCargarUsuariosArchivoCorrupto() throws IOException {
        // 1. Crear archivo con basura (texto plano en lugar de objeto serializado)
        File file = new File(USERS_FILE);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("ESTO NO ES UN OBJETO".getBytes());
        }

        // 2. Intentar cargar (Debe saltar al catch y devolver map vacío)
        Map<String, Usuario> result = fileManager.cargarUsuarios();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGuardarUsuariosFalloIO() {
        // TRUCO PARA EL 100%: Provocar error de escritura
        // Creamos un DIRECTORIO con el mismo nombre que el archivo.
        // Java no puede escribir un archivo encima de un directorio -> IOException
        File file = new File(USERS_FILE);
        file.mkdir(); 

        Map<String, Usuario> map = new HashMap<>();
        
        // Esto imprimirá el error en consola, pero no debe lanzar excepción
        assertDoesNotThrow(() -> fileManager.guardarUsuarios(map));
        
        file.delete(); // Limpieza
    }

    // ==========================================
    // TESTS DE CANVAS
    // ==========================================

    @Test
    void testGuardarYCargarCanvasExitoso() {
        // 1. Crear Canvas 2x2
        Color[][] canvas = new Color[2][2];
        canvas[0][0] = Color.RED;
        canvas[0][1] = Color.WHITE;
        canvas[1][0] = Color.BLUE;
        canvas[1][1] = Color.BLACK;

        // 2. Guardar
        fileManager.guardarCanvas(canvas);

        // 3. Cargar
        Color[][] cargado = fileManager.cargarCanvas(2, 2);

        // 4. Verificar
        assertNotNull(cargado);
        assertEquals(Color.RED, cargado[0][0]);
        assertEquals(Color.BLACK, cargado[1][1]);
    }

    @Test
    void testCargarCanvasNoExiste() {
        new File(CANVAS_FILE).delete();

        // Debe devolver un canvas vacío (blanco)
        Color[][] result = fileManager.cargarCanvas(5, 5);

        assertNotNull(result);
        assertEquals(5, result.length);
        assertEquals(Color.WHITE, result[0][0]);
    }

    @Test
    void testCargarCanvasDimensionesIncorrectas() {
        // 1. Guardamos un canvas de 2x2
        Color[][] peque = new Color[2][2];
        fileManager.guardarCanvas(peque);

        // 2. Intentamos cargar pidiendo 10x10
        // El sistema debe detectar el cambio y devolver uno nuevo vacío
        Color[][] result = fileManager.cargarCanvas(10, 10);

        assertEquals(10, result.length);
        assertEquals(10, result[0].length);
        // Si devolvió uno nuevo, debe ser blanco (no null que sería el default de array)
        assertEquals(Color.WHITE, result[0][0]); 
    }

    @Test
    void testCargarCanvasCorrupto() throws IOException {
        // Escribir basura
        File file = new File(CANVAS_FILE);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("BASURA".getBytes());
        }

        // Debe manejar la excepción y devolver canvas vacío
        Color[][] result = fileManager.cargarCanvas(3, 3);

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(Color.WHITE, result[0][0]);
    }
    
    @Test
    void testGuardarCanvasFalloIO() {
        // TRUCO: Crear directorio con nombre del archivo para bloquear escritura
        File file = new File(CANVAS_FILE);
        file.mkdir();

        Color[][] canvas = new Color[2][2];
        assertDoesNotThrow(() -> fileManager.guardarCanvas(canvas));
        
        file.delete();
    }

    // ==========================================
    // OTROS MÉTODOS
    // ==========================================

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
        // Es un stub (no implementado), solo verificamos que no explote
        assertDoesNotThrow(() -> fileManager.exportarCanvasPNG(new Color[2][2], "test.png"));
    }
    
    @Test
    void testConstructorCreaDirectorio() {
        // Borramos el directorio 'data' manualmente
        File dataDir = new File(DATA_DIR);
        if(dataDir.exists()) {
            String[] entries = dataDir.list();
            for(String s: entries){
                new File(dataDir.getPath(),s).delete();
            }
            dataDir.delete();
        }
        assertFalse(dataDir.exists());
        
        // Al instanciar, debe crearlo
        new FileManager();
        
        assertTrue(dataDir.exists());
    }
}