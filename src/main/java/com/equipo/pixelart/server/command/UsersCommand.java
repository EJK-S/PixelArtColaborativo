package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;

public class UsersCommand implements ServerCommand {

    private final ServerApp server;

    public UsersCommand(ServerApp server) {
        this.server = server;
    }

    @Override
    public String name() {
        return "users";
    }

    @Override
    public String help() {
        return "Lista usuarios conectados";
    }

    @Override
    public void execute() {
        server.imprimirUsuarios();
    }
}
