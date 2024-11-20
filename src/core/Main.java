package core;

import engine.Server;

public final class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
