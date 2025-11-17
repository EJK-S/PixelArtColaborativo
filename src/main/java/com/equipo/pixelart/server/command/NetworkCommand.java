package com.equipo.pixelart.server.command;

import com.equipo.pixelart.server.app.ServerApp;

public class NetworkCommand implements ServerCommand {

    private final ServerApp server;

    public NetworkCommand(ServerApp server) {
        this.server = server;
    }

    @Override
    public String name() {
        return "network";
    }

    @Override
    public String help() {
        return "Muestra IPs locales del servidor";
    }

    @Override
    public void execute() {
        server.getLocalIPs().forEach(System.out::println);
    }
}
