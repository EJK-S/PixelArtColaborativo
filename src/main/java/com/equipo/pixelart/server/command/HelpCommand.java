package com.equipo.pixelart.server.command;

import java.util.Map;

public class HelpCommand implements ServerCommand {

    private final Map<String, ServerCommand> cmds;

    public HelpCommand(Map<String, ServerCommand> cmds) {
        this.cmds = cmds;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String help() {
        return "Muestra este mensaje";
    }

    @Override
    public void execute() {
        System.out.println("=== COMANDOS ===");
        cmds.values().forEach(cmd ->
                System.out.println(" - " + cmd.name() + " : " + cmd.help())
        );
        System.out.println();
    }
}
