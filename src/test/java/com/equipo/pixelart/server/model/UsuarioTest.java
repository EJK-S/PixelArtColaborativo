package com.equipo.pixelart.server.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void testConstructorPrincipal() {
        String user = "Anthony";
        String hash = "hash123";

        Usuario u = new Usuario(user, hash);

        assertEquals(user, u.getUsername());
        assertEquals(hash, u.getPasswordHash());

        assertTrue(u.getFechaCreacion() > 0);
        assertTrue(u.getUltimoAcceso() > 0);

        assertEquals(u.getFechaCreacion(), u.getUltimoAcceso(), 100); 
    }

    @Test
    void testConstructorVacioYSetters() {
        Usuario u = new Usuario();
 
        assertTrue(u.getFechaCreacion() > 0);

        u.setUsername("Nuevo");
        u.setPasswordHash("NuevoHash");
        u.setFechaCreacion(12345L);
        u.setUltimoAcceso(67890L);

        assertEquals("Nuevo", u.getUsername());
        assertEquals("NuevoHash", u.getPasswordHash());
        assertEquals(12345L, u.getFechaCreacion());
        assertEquals(67890L, u.getUltimoAcceso());
    }

    @Test
    void testHashPassword() {
        String pass = "123456";

        String hash1 = Usuario.hashPassword(pass);
        String hash2 = Usuario.hashPassword(pass);
        
        assertNotNull(hash1);
        assertFalse(hash1.isEmpty());
        assertEquals(hash1, hash2);

        String hashDiff = Usuario.hashPassword("12345");
        assertNotEquals(hash1, hashDiff);
    }

    @Test
    void testVerificarPassword() {
        String pass = "mi_secreto";
        
        String hashReal = Usuario.hashPassword(pass);
        
        Usuario u = new Usuario("User", hashReal);

        assertTrue(u.verificarPassword("mi_secreto"));

        assertFalse(u.verificarPassword("otra_cosa"));
        assertFalse(u.verificarPassword("MI_SECRETO")); 
    }

    @Test
    void testActualizarAcceso() throws InterruptedException {
        Usuario u = new Usuario("User", "hash");
        long accesoInicial = u.getUltimoAcceso();

        Thread.sleep(10); 

        u.actualizarAcceso();

        assertTrue(u.getUltimoAcceso() > accesoInicial, 
                   "El tiempo de último acceso debe aumentar");
    }

    @Test
    void testEquals() {
        Usuario u1 = new Usuario("Anthony", "hashA");
        Usuario u2 = new Usuario("Anthony", "hashB"); 
        Usuario u3 = new Usuario("Maria", "hashA");   
        
        assertEquals(u1, u1);

        assertEquals(u1, u2);
        assertEquals(u2, u1);

        assertNotEquals(u1, u3);

        assertNotEquals(u1, null);

        assertNotEquals(u1, "Un String cualquiera");
    }

    @Test
    void testHashCode() {
        Usuario u1 = new Usuario("Anthony", "hashA");
        Usuario u2 = new Usuario("Anthony", "hashB");

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void testToString() {
        Usuario u = new Usuario("TestUser", "hash");
        String texto = u.toString();

        assertTrue(texto.contains("TestUser"));
        assertTrue(texto.contains("fechaCreacion"));
        assertTrue(texto.contains("ultimoAcceso"));
    }
}