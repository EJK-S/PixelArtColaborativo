package com.equipo.pixelart.server.command;

public interface ServerCommand {
    String name();     
    String help();     
    void execute();    
}
