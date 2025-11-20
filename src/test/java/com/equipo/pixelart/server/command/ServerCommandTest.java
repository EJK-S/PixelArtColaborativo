package com.equipo.pixelart.server.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServerCommandTest {

    @Test
    void testInterfaceContract() {
       
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
                
            }
        };

        assertEquals("test_cmd", cmd.name());
        assertEquals("test_help", cmd.help());       
        assertDoesNotThrow(cmd::execute);
    }
}