package com.equipo.pixelart.server.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServerCommandTest {

    @Test
    void testInterfaceContract() {
        // 1. Creamos una implementación "tonta" de la interfaz al vuelo
        ServerCommand cmd = new ServerCommand() {
            @Override
            public String name() {
                return "test_cmd";
            }

            @Override
            public String help() {
                return "test_help";
            }

            @Override
            public void execute() {
                // No hace nada
            }
        };

        // 2. Verificamos que la interfaz define correctamente los métodos
        assertEquals("test_cmd", cmd.name());
        assertEquals("test_help", cmd.help());
        
        // 3. Verificamos que se puede ejecutar sin error
        assertDoesNotThrow(cmd::execute);
    }
}