package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;

public class SaveCommand implements ServerCommand {

    private final ServerApp server;

    public SaveCommand(ServerApp server) {
        this.server = server;
    }

    @Override
    public String name() {
        return "save";
    }

    @Override
    public String help() {
        return "Guarda el canvas manualmente";
    }

   @Override
    public void execute() {
        server.saveCanvas();
    }
}
