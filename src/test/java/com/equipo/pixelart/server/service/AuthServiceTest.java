package com.equipo.pixelart.server.service;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.equipo.pixelart.server.model.Usuario;
import com.equipo.pixelart.server.persistence.FileManager;

class AuthServiceTest {

    private AuthService authService;
    private FileManager mockFileManager;
    private Map<String, Usuario> baseDatosSimulada;

    @BeforeEach
    public void setUp() {
        
        mockFileManager = mock(FileManager.class);

        baseDatosSimulada = new HashMap<>();
  
        when(mockFileManager.cargarUsuarios()).thenReturn(baseDatosSimulada);

        authService = new AuthService(mockFileManager);
    }


    @Test
    void testRegistrarUsuarioExitoso() {
        boolean resultado = authService.registrarUsuario("Anthony", "password123");

        assertTrue(resultado);
        assertEquals(1, authService.getTotalUsuarios());

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
        assertFalse(authService.registrarUsuario("Anthony", "12")); 
        assertEquals(0, authService.getTotalUsuarios());
    }

    @Test
    void testRegistrarUsuarioFallido_YaExiste() {
        
        authService.registrarUsuario("Anthony", "pass1");

        boolean resultado = authService.registrarUsuario("Anthony", "pass2");

        assertFalse(resultado);
        assertEquals(1, authService.getTotalUsuarios());
    }


    @Test
    void testAutenticarExitoso() {
       
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

        authService.autenticar("Anthony", "secreto");

        boolean resultado = authService.autenticar("Anthony", "secreto");

        assertFalse(resultado, "No debe permitir login doble");
    }


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

    }
    
    @Test
    void testObtenerUsuario() {
        authService.registrarUsuario("Anthony", "pass");
        Usuario u = authService.obtenerUsuario("Anthony");
        
        assertNotNull(u);
        assertEquals("Anthony", u.getUsername());
        
        assertNull(authService.obtenerUsuario("Nadie"));
    }


    @Test
    void testEliminarUsuario() {
        authService.registrarUsuario("Anthony", "pass");
        authService.autenticar("Anthony", "pass"); 

        boolean eliminado = authService.eliminarUsuario("Anthony");

        assertTrue(eliminado);
        assertEquals(0, authService.getTotalUsuarios());
        assertFalse(authService.estaConectado("Anthony")); 
        verify(mockFileManager, atLeastOnce()).guardarUsuarios(anyMap());
    }

    @Test
    void testEliminarUsuarioNoExistente() {
        boolean eliminado = authService.eliminarUsuario("Fantasma");
        assertFalse(eliminado);
    }


    @Test
    void testCambiarPasswordExitoso() {
        authService.registrarUsuario("Anthony", "antigua");

        boolean resultado = authService.cambiarPassword("Anthony", "antigua", "nueva123");

        assertTrue(resultado);

        assertTrue(authService.autenticar("Anthony", "nueva123"));
       
        authService.desconectar("Anthony");
        assertFalse(authService.autenticar("Anthony", "antigua"));
    }

    @Test
    void testCambiarPasswordFallos() {
        authService.registrarUsuario("Anthony", "antigua");

        assertFalse(authService.cambiarPassword("Nadie", "1", "2"));
        assertFalse(authService.cambiarPassword("Anthony", "mal", "nueva"));
        assertFalse(authService.cambiarPassword("Anthony", "antigua", "12"));
        assertFalse(authService.cambiarPassword("Anthony", "antigua", null));
    }
}