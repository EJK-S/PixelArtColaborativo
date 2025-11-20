package com.equipo.pixelart.server.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void testConstructorPrincipal() {
        String user = "Anthony";
        String hash = "hash123";

        Usuario u = new Usuario(user, hash);

        assertEquals(user, u.getUsername());
        assertEquals(hash, u.getPasswordHash());
        
        // Verificamos que los timestamps se inicializaron
        assertTrue(u.getFechaCreacion() > 0);
        assertTrue(u.getUltimoAcceso() > 0);
        
        // La fecha de creación y último acceso deben ser casi iguales al crearse
        assertEquals(u.getFechaCreacion(), u.getUltimoAcceso(), 100); // delta de 100ms
    }

    @Test
    void testConstructorVacioYSetters() {
        Usuario u = new Usuario();
        
        // Los timestamps deben inicializarse incluso en el constructor vacío
        assertTrue(u.getFechaCreacion() > 0);
        
        // Probamos Setters
        u.setUsername("Nuevo");
        u.setPasswordHash("NuevoHash");
        u.setFechaCreacion(12345L);
        u.setUltimoAcceso(67890L);

        // Probamos Getters
        assertEquals("Nuevo", u.getUsername());
        assertEquals("NuevoHash", u.getPasswordHash());
        assertEquals(12345L, u.getFechaCreacion());
        assertEquals(67890L, u.getUltimoAcceso());
    }

    @Test
    void testHashPassword() {
        String pass = "123456";
        
        // 1. Determinismo: El hash de "123456" siempre debe ser el mismo
        String hash1 = Usuario.hashPassword(pass);
        String hash2 = Usuario.hashPassword(pass);
        
        assertNotNull(hash1);
        assertFalse(hash1.isEmpty());
        assertEquals(hash1, hash2);

        // 2. Diferencia: El hash de "123456" no debe ser igual al de "12345"
        String hashDiff = Usuario.hashPassword("12345");
        assertNotEquals(hash1, hashDiff);
    }

    @Test
    void testVerificarPassword() {
        String pass = "mi_secreto";
        // Simulamos el flujo real: primero hasheamos
        String hashReal = Usuario.hashPassword(pass);
        
        Usuario u = new Usuario("User", hashReal);

        // Caso Exitoso
        assertTrue(u.verificarPassword("mi_secreto"));

        // Caso Fallido
        assertFalse(u.verificarPassword("otra_cosa"));
        assertFalse(u.verificarPassword("MI_SECRETO")); // Case sensitive
    }

    @Test
    void testActualizarAcceso() throws InterruptedException {
        Usuario u = new Usuario("User", "hash");
        long accesoInicial = u.getUltimoAcceso();

        // Esperamos un poquito para asegurar que el reloj del sistema avance
        Thread.sleep(10); 

        u.actualizarAcceso();

        assertTrue(u.getUltimoAcceso() > accesoInicial, 
                   "El tiempo de último acceso debe aumentar");
    }

    // ==========================================
    // TEST DE CONTRATO EQUALS & HASHCODE
    // ==========================================

    @Test
    void testEquals() {
        Usuario u1 = new Usuario("Anthony", "hashA");
        Usuario u2 = new Usuario("Anthony", "hashB"); // Mismo user, diferente hash
        Usuario u3 = new Usuario("Maria", "hashA");   // Diferente user

        // 1. Reflexivo (consigo mismo)
        assertEquals(u1, u1);

        // 2. Simétrico (u1 == u2) -> (u2 == u1)
        // Tu equals solo mira el 'username', así que u1 y u2 son iguales
        assertEquals(u1, u2);
        assertEquals(u2, u1);

        // 3. Diferente objeto
        assertNotEquals(u1, u3);

        // 4. Nulo
        assertNotEquals(u1, null);

        // 5. Otro tipo de clase
        assertNotEquals(u1, "Un String cualquiera");
    }

    @Test
    void testHashCode() {
        Usuario u1 = new Usuario("Anthony", "hashA");
        Usuario u2 = new Usuario("Anthony", "hashB");

        // Contrato: Si equals es true, hashCode debe ser igual
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