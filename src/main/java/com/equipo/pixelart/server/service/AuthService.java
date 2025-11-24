package com.equipo.pixelart.server.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.equipo.pixelart.server.model.Usuario;
import com.equipo.pixelart.server.persistence.FileManager;


public class AuthService {

    private final FileManager fileManager;
    private final Map<String, Usuario> usuarios;
    private final Set<String> usuariosConectados;

  
    public AuthService(FileManager fileManager) {
        this.fileManager = fileManager;
        this.usuarios = fileManager.cargarUsuarios();
        this.usuariosConectados = new HashSet<>();

        System.out.println("AuthService inicializado con " + usuarios.size() + " usuarios");
    }

  
    public synchronized boolean registrarUsuario(String username, String password) {
        // Validaciones
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Registro fallido: username vacio");
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

        String passwordHash = Usuario.hashPassword(password);
        Usuario nuevoUsuario = new Usuario(username, passwordHash);

        usuarios.put(username, nuevoUsuario);
        fileManager.guardarUsuarios(usuarios);

        System.out.println("*** Usuario registrado exitosamente: " + username);
        return true;
    }

  
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

        usuario.actualizarAcceso();
        fileManager.guardarUsuarios(usuarios);

        usuariosConectados.add(username);

        System.out.println("Login exitoso: " + username);
        return true;
    }

  
    public synchronized void desconectar(String username) {
        if (username != null && usuariosConectados.contains(username)) {
            usuariosConectados.remove(username);
            System.out.println("Usuario desconectado: " + username);
        }
    }

  
    public synchronized String[] obtenerUsuariosConectados() {
        return usuariosConectados.toArray(String[]::new);
    }

   
    public synchronized boolean estaConectado(String username) {
        return usuariosConectados.contains(username);
    }

  
    public int getTotalUsuarios() {
        return usuarios.size();
    }

   
    public int getUsuariosConectadosCount() {
        return usuariosConectados.size();
    }

   
    public Usuario obtenerUsuario(String username) {
        return usuarios.get(username);
    }

    
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