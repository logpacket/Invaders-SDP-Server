package engine;

import core.Main;
import engine.event.Event;
import engine.event.EventDispatcher;
import middleware.LoggingMiddleware;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Session implements Runnable {
    private final Socket socket;
    private final Logger logger = Main.getLogger();
    private final Random random = new Random();
    private final long id = random.nextLong();
    private ObjectInputStream ois;
    public ObjectOutputStream oos;


    public Session(Socket socket) {
        this.socket = socket;
        try {
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            logger.log(Level.WARNING, "Couldn't initialize client session.", e);
        }
    }

    @Override
    public void run() {
        InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        logger.log(Level.INFO, "Client connected: {}", socketAddress.getAddress());
        EventDispatcher eventDispatcher = new EventDispatcher();
        Server.EVENT_HANDLERS.forEach(eventDispatcher::addHandler);
        eventDispatcher.useMiddleware(new LoggingMiddleware());
        try {
            while(socket.isConnected()) {
                Event event = (Event) ois.readObject();
                eventDispatcher.dispatch(this, event);
            }
        }
        catch (ClassNotFoundException | IOException e) {
            logger.log(Level.WARNING, "Couldn't handle client request", e);
        }
    }

    public String getAddress() {
        return socket.getRemoteSocketAddress().toString();
    }

    public long getId() { return id; }
}
