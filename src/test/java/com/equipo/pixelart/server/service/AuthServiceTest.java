package com.equipo.pixelart.server.service;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals; 
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.equipo.pixelart.server.persistence.FileManager;

class AuthServiceTest {

    private AuthService authService;
    private FileManager fileManager;

    
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
        
        boolean resultado = authService.registrarUsuario("nuevoUsuario", "pass123");

       
        assertTrue(resultado, "El registro debería ser exitoso");
        assertEquals(1, authService.getTotalUsuarios(), "Debería haber 1 usuario registrado");
    }

    @Test
    void testRegistroUsuarioDuplicado() {
        
        authService.registrarUsuario("usuarioExistente", "pass123");
        boolean resultado = authService.registrarUsuario("usuarioExistente", "pass456"); 

        // 2. Verificación
        assertFalse(resultado, "El registro de un duplicado debería fallar");
        assertEquals(1, authService.getTotalUsuarios(), "Solo debería haber 1 usuario");
    }

    @Test
    void testLoginExitoso() {
        
        authService.registrarUsuario("jean", "pass123");
        
        
        boolean resultado = authService.autenticar("jean", "pass123");
        
        
        assertTrue(resultado, "El login debería ser exitoso");
        assertEquals(1, authService.getUsuariosConectadosCount(), "El usuario debería estar conectado");
    }

    @Test
    void testLoginUsuarioNoExiste() {
       
        boolean resultado = authService.autenticar("noexiste", "pass123");

        assertFalse(resultado, "El login debería fallar si el usuario no existe");
    }

    @Test
    void testLoginPasswordIncorrecta() {
      
        authService.registrarUsuario("adrian", "passCorrecta");
        
        boolean resultado = authService.autenticar("adrian", "passIncorrecta");
        
        assertFalse(resultado, "El login debería fallar si la contraseña es incorrecta");
    }
}