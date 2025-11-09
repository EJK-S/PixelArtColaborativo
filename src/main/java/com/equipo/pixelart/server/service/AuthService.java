package com.equipo.pixelart.server.service;

import com.equipo.pixelart.server.model.Usuario;
import com.equipo.pixelart.server.persistence.FileManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Servicio de Autenticación
 * Gestiona usuarios, login y registro
 */
public class AuthService {

    private final FileManager fileManager;
    private final Map<String, Usuario> usuarios;
    private final Set<String> usuariosConectados;

    /**
     * Constructor
     */
    public AuthService(FileManager fileManager) {
        this.fileManager = fileManager;
        this.usuarios = fileManager.cargarUsuarios();
        this.usuariosConectados = new HashSet<>();

        System.out.println("AuthService inicializado con " + usuarios.size() + " usuarios");
    }

    /**
     * Registra un nuevo usuario
     */
    public synchronized boolean registrarUsuario(String username, String password) {
        // Validaciones
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Registro fallido: username vacío");
            return false;
        }

        if (password == null || password.length() < 3) {
            System.out.println("Registro fallido: password muy corta");
            return false;
        }

        if (usuarios.containsKey(username)) {
            System.out.println("Registro fallido: usuario ya existe - " + username);
            return false;
        }

        // Crear usuario
        String passwordHash = Usuario.hashPassword(password);
        Usuario nuevoUsuario = new Usuario(username, passwordHash);

        usuarios.put(username, nuevoUsuario);
        fileManager.guardarUsuarios(usuarios);

        System.out.println("Usuario registrado: " + username);
        return true;
    }

    /**
     * Intenta autenticar un usuario
     */
    public synchronized boolean autenticar(String username, String password) {
        Usuario usuario = usuarios.get(username);

        if (usuario == null) {
            System.out.println("Login fallido: usuario no existe - " + username);
            return false;
        }

        if (!usuario.verificarPassword(password)) {
            System.out.println("Login fallido: password incorrecta - " + username);
            return false;
        }

        if (usuariosConectados.contains(username)) {
            System.out.println("Login fallido: usuario ya conectado - " + username);
            return false;
        }

        // Actualizar último acceso
        usuario.actualizarAcceso();
        fileManager.guardarUsuarios(usuarios);

        // Marcar como conectado
        usuariosConectados.add(username);

        System.out.println("Login exitoso: " + username);
        return true;
    }

    /**
     * Marca un usuario como desconectado
     */
    public synchronized void desconectar(String username) {
        if (username != null && usuariosConectados.contains(username)) {
            usuariosConectados.remove(username);
            System.out.println("Usuario desconectado: " + username);
        }
    }

    /**
     * Obtiene la lista de usuarios conectados
     */
    public synchronized String[] obtenerUsuariosConectados() {
        return usuariosConectados.toArray(new String[0]);
    }

    /**
     * Verifica si un usuario está conectado
     */
    public synchronized boolean estaConectado(String username) {
        return usuariosConectados.contains(username);
    }

    /**
     * Obtiene el número total de usuarios registrados
     */
    public int getTotalUsuarios() {
        return usuarios.size();
    }

    /**
     * Obtiene el número de usuarios conectados
     */
    public int getUsuariosConectadosCount() {
        return usuariosConectados.size();
    }

    /**
     * Obtiene un usuario por su nombre
     */
    public Usuario obtenerUsuario(String username) {
        return usuarios.get(username);
    }

    /**
     * Elimina un usuario (admin)
     */
    public synchronized boolean eliminarUsuario(String username) {
        if (usuarios.containsKey(username)) {
            usuarios.remove(username);
            usuariosConectados.remove(username);
            fileManager.guardarUsuarios(usuarios);
            System.out.println("Usuario eliminado: " + username);
            return true;
        }
        return false;
    }

    /**
     * Cambia la contraseña de un usuario
     */
    public synchronized boolean cambiarPassword(String username, String oldPassword, String newPassword) {
        Usuario usuario = usuarios.get(username);

        if (usuario == null) {
            return false;
        }

        if (!usuario.verificarPassword(oldPassword)) {
            return false;
        }

        if (newPassword == null || newPassword.length() < 3) {
            return false;
        }

        String newHash = Usuario.hashPassword(newPassword);
        usuario.setPasswordHash(newHash);
        fileManager.guardarUsuarios(usuarios);

        System.out.println("Password cambiada para: " + username);
        return true;
    }
}