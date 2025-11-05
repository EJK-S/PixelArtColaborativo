package com.equipo.pixelart.server.persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Importa el "plano" Usuario desde su ubicación correcta
import com.equipo.pixelart.server.model.Usuario;

/**
 * Esta clase es el "obrero". Su único trabajo es leer y escribir
 * en el archivo usuarios.txt, convirtiendo texto a Objetos Usuario y viceversa.
 */
public class FileManager {
    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    
    public static List<Usuario> cargarUsuariosDesdeArchivo() {
        List<Usuario> usuarios = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Usuario usuario = parsearLineaUsuario(linea);
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
            // Mensaje para la consola del SERVIDOR
            System.out.println("Usuarios cargados exitosamente: " + usuarios.size() + " usuarios.");
        } catch (IOException e) {
            // Mensaje para la consola del SERVIDOR
            System.out.println("No se encontró archivo de usuarios. Se creará uno nuevo.");
        }
        
        return usuarios;
    }
    
    public static void guardarUsuariosEnArchivo(List<Usuario> usuarios) {
        try (FileWriter writer = new FileWriter(ARCHIVO_USUARIOS)) {
            for (Usuario user : usuarios) {
                writer.write(convertirUsuarioATexto(user) + "\n");
            }
            System.out.println("Datos guardados exitosamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }
    
    /**
     * Convierte una línea de texto (ej: "EJK;123;pepe,juan") en un Objeto Usuario.
     */
    private static Usuario parsearLineaUsuario(String linea) {
        try {
            String[] tokens = linea.split(";", -1); // -1 para incluir campos vacíos
            
            // Formato: Username;Password;Amigos
            if (tokens.length < 3) {
                System.err.println("Error: Formato de archivo incorrecto.");
                return null;
            }
            
            Usuario user = new Usuario();
            user.setUsername(tokens[0]);
            user.setPassword(tokens[1]);
            
            // Cargar amigos (Token 2)
            if (tokens.length > 2 && !tokens[2].isEmpty()) {
                String[] amigos = tokens[2].split(",");
                for (String amigo : amigos) {
                    if (!amigo.isEmpty()) {
                        user.getAmigos().add(amigo);
                    }
                }
            }
            
            return user;
            
        } catch (Exception e) {
            System.err.println("Error al parsear línea de usuario: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convierte un Objeto Usuario en una línea de texto (ej: "EJK;123;pepe,juan").
     */
    private static String convertirUsuarioATexto(Usuario user) {
        StringBuilder sb = new StringBuilder();
        
        // Datos básicos
        sb.append(user.getUsername()).append(";");
        sb.append(user.getPassword()).append(";");
        
        // Amigos
        // Esto une la lista de amigos con comas (ej: "pepe,juan,ana")
        String amigosCSV = String.join(",", user.getAmigos());
        sb.append(amigosCSV);
        
        return sb.toString();
    }
    
    public static void limpiarArchivoCorrupto() {
        try {
            java.io.File archivo = new java.io.File(ARCHIVO_USUARIOS);
            if (archivo.exists()) {
                if (archivo.delete()) {
                    System.out.println("Archivo corrupto eliminado.");
                }
            }
            
            // Crear archivo nuevo vacío
            try (FileWriter writer = new FileWriter(ARCHIVO_USUARIOS)) {
                writer.write("");
            }
            
            System.out.println("Archivo limpio creado exitosamente.");
            
        } catch (IOException e) {
            System.err.println("Error al limpiar archivo: " + e.getMessage());
        }
    }
}