package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;

public class StatsCommand implements ServerCommand {

    private final ServerApp server;

    public StatsCommand(ServerApp server) {
        this.server = server;
    }

    @Override
    public String name() {
        return "stats";
    }

    @Override
    public String help() {
        return "Muestra estadísticas del servidor";
    }

    @Override
    public void execute() {
        server.imprimirEstadisticas();
    }
}
