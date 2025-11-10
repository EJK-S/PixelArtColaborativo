package com.equipo.pixelart.server.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Modelo de Usuario
 * Representa un usuario del sistema con sus credenciales
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordHash;
    private long fechaCreacion;
    private long ultimoAcceso;

    /**
     * Constructor completo
     */
    public Usuario(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fechaCreacion = System.currentTimeMillis();
        this.ultimoAcceso = System.currentTimeMillis();
    }

    /**
     * Constructor por defecto (necesario para serialización)
     */
    public Usuario() {
        this.fechaCreacion = System.currentTimeMillis();
        this.ultimoAcceso = System.currentTimeMillis();
    }

    /**
     * Verifica si la contraseña es correcta
     */
    public boolean verificarPassword(String password) {
        return hashPassword(password).equals(this.passwordHash);
    }

    /**
     * Actualiza el último acceso
     */
    public void actualizarAcceso() {
        this.ultimoAcceso = System.currentTimeMillis();
    }

    /**
     * Hash simple de contraseña (en producción usar BCrypt)
     */
    public static String hashPassword(String password) {
        // Para desarrollo usamos un hash simple
        // En producción deberías usar BCrypt o similar
        int hash = 7;
        for (int i = 0; i < password.length(); i++) {
            hash = hash * 31 + password.charAt(i);
        }
        return Integer.toHexString(hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(username, usuario.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "username='" + username + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", ultimoAcceso=" + ultimoAcceso +
                '}';
    }

    // ==================== GETTERS Y SETTERS ====================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public long getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(long ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}