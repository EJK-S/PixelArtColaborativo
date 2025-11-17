package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;

public class ClearCommand implements ServerCommand {

    private final ServerApp server;

    public ClearCommand(ServerApp server) {
        this.server = server;
    }

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public String help() {
        return "Limpia el canvas";
    }

    @Override
    public void execute() {
        server.clearCanvas();
    }
}
