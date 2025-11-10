package com.equipo.pixelart.server.service;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals; // Asegúrate de importar tu clase Usuario
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.equipo.pixelart.server.persistence.FileManager;

/**
 * Pruebas Unitarias/Integración para AuthService.
 * Nota: Esta prueba SÍ escribirá un archivo "data/users.dat" real.
 */
class AuthServiceTest {

    private AuthService authService;
    private FileManager fileManager;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        // Antes de CADA prueba, borramos los datos antiguos
        // para asegurar que la prueba sea "limpia".
        new File("data/users.dat").delete(); 
        new File("data/canvas.dat").delete();

        // Creamos los servicios reales
        fileManager = new FileManager();
        authService = new AuthService(fileManager);
    }

    @Test
    void testRegistroExitoso() {
        // 1. Acción
        boolean resultado = authService.registrarUsuario("nuevoUsuario", "pass123");

        // 2. Verificación
        assertTrue(resultado, "El registro debería ser exitoso");
        assertEquals(1, authService.getTotalUsuarios(), "Debería haber 1 usuario registrado");
    }

    @Test
    void testRegistroUsuarioDuplicado() {
        // 1. Acción
        authService.registrarUsuario("usuarioExistente", "pass123"); // Registrarlo la 1ra vez
        boolean resultado = authService.registrarUsuario("usuarioExistente", "pass456"); // Intentar de nuevo

        // 2. Verificación
        assertFalse(resultado, "El registro de un duplicado debería fallar");
        assertEquals(1, authService.getTotalUsuarios(), "Solo debería haber 1 usuario");
    }

    @Test
    void testLoginExitoso() {
        // 1. Preparación
        authService.registrarUsuario("jean", "pass123");
        
        // 2. Acción
        boolean resultado = authService.autenticar("jean", "pass123");
        
        // 3. Verificación
        assertTrue(resultado, "El login debería ser exitoso");
        assertEquals(1, authService.getUsuariosConectadosCount(), "El usuario debería estar conectado");
    }

    @Test
    void testLoginUsuarioNoExiste() {
        // 1. Acción
        boolean resultado = authService.autenticar("noexiste", "pass123");
        
        // 2. Verificación
        assertFalse(resultado, "El login debería fallar si el usuario no existe");
    }

    @Test
    void testLoginPasswordIncorrecta() {
        // 1. Preparación
        authService.registrarUsuario("adrian", "passCorrecta");
        
        // 2. Acción
        boolean resultado = authService.autenticar("adrian", "passIncorrecta");
        
        // 3. Verificación
        assertFalse(resultado, "El login debería fallar si la contraseña es incorrecta");
    }
}