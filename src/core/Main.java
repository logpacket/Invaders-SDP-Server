package core;

import engine.MinimalFormatter;
import engine.Server;

import java.io.IOException;
import java.util.logging.*;

public final class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            LOGGER.setUseParentHandlers(false);
            Handler fileHandler = new FileHandler("log");
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new MinimalFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(consoleHandler);
            LOGGER.setLevel(Level.ALL);
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not initialize logger", e);
        }

        Server server = new Server();
        server.startServer();
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
