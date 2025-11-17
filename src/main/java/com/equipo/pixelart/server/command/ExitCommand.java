package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;

public class ExitCommand implements ServerCommand {

    private final ServerApp server;

    public ExitCommand(ServerApp server) {
        this.server = server;
    }

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public String help() {
        return "Detiene el servidor correctamente";
    }

    @Override
    public void execute() {
        server.stopServer();
    }
}
