package com.equipo.pixelart.server.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import com.equipo.pixelart.server.app.ServerApp;
import com.equipo.pixelart.server.command.ClearCommand;
import com.equipo.pixelart.server.command.ExitCommand;
import com.equipo.pixelart.server.command.HelpCommand;
import com.equipo.pixelart.server.command.NetworkCommand;
import com.equipo.pixelart.server.command.SaveCommand;
import com.equipo.pixelart.server.command.ServerCommand;
import com.equipo.pixelart.server.command.StatsCommand;
import com.equipo.pixelart.server.command.UsersCommand;

public class ServerConsole {

    private final Map<String, ServerCommand> commands = new LinkedHashMap<>();

    public ServerConsole(ServerApp server) {
        register(new StatsCommand(server));
        register(new UsersCommand(server));
        register(new NetworkCommand(server));
        register(new SaveCommand(server));
        register(new ClearCommand(server));
        register(new ExitCommand(server));
        register(new HelpCommand(commands));
    }

    private void register(ServerCommand cmd) {
        commands.put(cmd.name(), cmd);
    }

    public void inicio() {
        Thread t = new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            commands.get("help").execute();

            while (true) {
                System.out.print("server> ");
                try {
                    String line = br.readLine();
                    if (line == null) break;

                    ServerCommand cmd = commands.get(line.trim());
                    if (cmd != null) cmd.execute();
                    else System.out.println("Comando no encontrado.");
                } catch (IOException ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }
}
