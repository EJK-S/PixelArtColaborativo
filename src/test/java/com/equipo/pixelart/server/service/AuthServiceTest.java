package com.equipo.pixelart.server.service;

import com.equipo.pixelart.server.model.Usuario;
import com.equipo.pixelart.server.persistence.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private FileManager mockFileManager;
    private Map<String, Usuario> baseDatosSimulada;

    @BeforeEach
    void setUp() {
        // 1. Crear el Mock
        mockFileManager = mock(FileManager.class);

        // 2. Preparar datos iniciales (Base de datos vacía)
        baseDatosSimulada = new HashMap<>();
        
        // 3. ENTRENAR AL MOCK ANTES DE INSTANCIAR EL SERVICIO
        // Esto es crucial: el constructor llama a cargarUsuarios()
        when(mockFileManager.cargarUsuarios()).thenReturn(baseDatosSimulada);

        // 4. Instanciar el servicio
        authService = new AuthService(mockFileManager);
    }

    // ==========================================
    // TEST DE REGISTRO
    // ==========================================

    @Test
    void testRegistrarUsuarioExitoso() {
        boolean resultado = authService.registrarUsuario("Anthony", "password123");

        assertTrue(resultado);
        assertEquals(1, authService.getTotalUsuarios());
        
        // Verificar que se intentó guardar en disco
        verify(mockFileManager).guardarUsuarios(anyMap());
    }

    @Test
    void testRegistrarUsuarioFallido_UsernameInvalido() {
        assertFalse(authService.registrarUsuario(null, "123"));
        assertFalse(authService.registrarUsuario("", "123"));
        assertFalse(authService.registrarUsuario("   ", "123"));
        
        assertEquals(0, authService.getTotalUsuarios());
    }

    @Test
    void testRegistrarUsuarioFallido_PasswordCorta() {
        assertFalse(authService.registrarUsuario("Anthony", "12")); // Menos de 3 chars
        assertEquals(0, authService.getTotalUsuarios());
    }

    @Test
    void testRegistrarUsuarioFallido_YaExiste() {
        // Primero registramos uno
        authService.registrarUsuario("Anthony", "pass1");
        
        // Intentamos registrar el mismo
        boolean resultado = authService.registrarUsuario("Anthony", "pass2");

        assertFalse(resultado);
        assertEquals(1, authService.getTotalUsuarios());
    }

    // ==========================================
    // TEST DE AUTENTICACIÓN (LOGIN)
    // ==========================================

    @Test
    void testAutenticarExitoso() {
        // Setup: Usuario debe existir
        authService.registrarUsuario("Anthony", "secreto");

        boolean resultado = authService.autenticar("Anthony", "secreto");

        assertTrue(resultado);
        assertTrue(authService.estaConectado("Anthony"));
        assertEquals(1, authService.getUsuariosConectadosCount());
    }

    @Test
    void testAutenticarFallido_UsuarioNoExiste() {
        boolean resultado = authService.autenticar("Fantasma", "123");
        assertFalse(resultado);
    }

    @Test
    void testAutenticarFallido_PasswordIncorrecta() {
        authService.registrarUsuario("Anthony", "secreto");

        boolean resultado = authService.autenticar("Anthony", "incorrecta");

        assertFalse(resultado);
        assertFalse(authService.estaConectado("Anthony"));
    }

    @Test
    void testAutenticarFallido_YaConectado() {
        authService.registrarUsuario("Anthony", "secreto");

        // Primer login: Éxito
        authService.autenticar("Anthony", "secreto");
        
        // Segundo login: Fallo
        boolean resultado = authService.autenticar("Anthony", "secreto");

        assertFalse(resultado, "No debe permitir login doble");
    }

    // ==========================================
    // TEST DE DESCONEXIÓN Y ESTADO
    // ==========================================

    @Test
    void testDesconectar() {
        authService.registrarUsuario("Anthony", "pass");
        authService.autenticar("Anthony", "pass");
        assertTrue(authService.estaConectado("Anthony"));

        authService.desconectar("Anthony");

        assertFalse(authService.estaConectado("Anthony"));
        assertEquals(0, authService.getUsuariosConectadosCount());
    }

    @Test
    void testDesconectarUsuarioNoConectado() {
        // No debe lanzar excepción
        assertDoesNotThrow(() -> authService.desconectar("Nadie"));
        assertDoesNotThrow(() -> authService.desconectar(null));
    }
    
    @Test
    void testObtenerUsuariosConectados() {
        authService.registrarUsuario("U1", "pass");
        authService.registrarUsuario("U2", "pass");
        
        authService.autenticar("U1", "pass");
        authService.autenticar("U2", "pass");

        String[] conectados = authService.obtenerUsuariosConectados();
        
        assertEquals(2, conectados.length);
        // No podemos asegurar el orden en un Set, así que verificamos contenido
        // (Esto asume que los nombres están en el array)
    }
    
    @Test
    void testObtenerUsuario() {
        authService.registrarUsuario("Anthony", "pass");
        Usuario u = authService.obtenerUsuario("Anthony");
        
        assertNotNull(u);
        assertEquals("Anthony", u.getUsername());
        
        assertNull(authService.obtenerUsuario("Nadie"));
    }

    // ==========================================
    // TEST DE ELIMINACIÓN
    // ==========================================

    @Test
    void testEliminarUsuario() {
        authService.registrarUsuario("Anthony", "pass");
        authService.autenticar("Anthony", "pass"); // Lo conectamos también

        boolean eliminado = authService.eliminarUsuario("Anthony");

        assertTrue(eliminado);
        assertEquals(0, authService.getTotalUsuarios());
        assertFalse(authService.estaConectado("Anthony")); // Debe desconectarlo también
        verify(mockFileManager, atLeastOnce()).guardarUsuarios(anyMap());
    }

    @Test
    void testEliminarUsuarioNoExistente() {
        boolean eliminado = authService.eliminarUsuario("Fantasma");
        assertFalse(eliminado);
    }

    // ==========================================
    // TEST DE CAMBIO DE PASSWORD
    // ==========================================

    @Test
    void testCambiarPasswordExitoso() {
        authService.registrarUsuario("Anthony", "antigua");

        boolean resultado = authService.cambiarPassword("Anthony", "antigua", "nueva123");

        assertTrue(resultado);
        
        // Verificar que la nueva funciona
        assertTrue(authService.autenticar("Anthony", "nueva123"));
        // Verificar que la vieja ya no funciona
        authService.desconectar("Anthony"); // Necesario para probar login de nuevo
        assertFalse(authService.autenticar("Anthony", "antigua"));
    }

    @Test
    void testCambiarPasswordFallos() {
        authService.registrarUsuario("Anthony", "antigua");

        // 1. Usuario no existe
        assertFalse(authService.cambiarPassword("Nadie", "1", "2"));

        // 2. Password antigua incorrecta
        assertFalse(authService.cambiarPassword("Anthony", "mal", "nueva"));

        // 3. Nueva password inválida (corta o null)
        assertFalse(authService.cambiarPassword("Anthony", "antigua", "12"));
        assertFalse(authService.cambiarPassword("Anthony", "antigua", null));
    }
}