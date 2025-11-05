// En: com.equipo.pixelart.server.model.Usuario.java

package com.equipo.pixelart.server.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    // 1. Atributos que SÍ necesitamos
    private String username;
    private String password;
    private List<String> amigos;

    // --- (El resto de campos como games, puntuaciones, estado, etc.
    // ---  se han eliminado porque no pertenecen a este proyecto) ---


    // 2. Constructor (¡Importante!)
    // Inicializa la lista de amigos para evitar errores
    public Usuario() {
        this.amigos = new ArrayList<>();
        
        // --- (El resto de inicializaciones de games, puntuaciones, etc.
        // ---  se han eliminado) ---
    }
    
    // 3. Constructor sobrecargado
    public Usuario(String username, String password) {
        this(); // Llama al constructor de arriba para inicializar la lista
        this.username = username;
        this.password = password;
    }

    // 4. Getters y Setters que SÍ necesitamos
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<String> amigos) {
        this.amigos = amigos;
    }

    // --- (El resto de Getters y Setters de games, puntuaciones, etc.
    // ---  se han eliminado) ---
}