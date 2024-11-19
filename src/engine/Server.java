package engine;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.Main;
import engine.event.EventHandler;
import engine.event.EventType;
import org.reflections.Reflections;

public final class Server {
    public static final Map<String, EventHandler> EVENT_HANDLERS = new HashMap<>();
    private final Logger logger;

    public Server() {
        this.logger = Main.getLogger();
        Reflections reflections = new Reflections("handler");

        try {
            for (Class<?> handlerClass : reflections.getTypesAnnotatedWith(EventType.class)){
                final Constructor<?> constructor = handlerClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                EventHandler handler = (EventHandler) constructor.newInstance();
                String eventName = handlerClass.getAnnotation(EventType.class).value();
                EVENT_HANDLERS.put(eventName, handler);
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.log(Level.WARNING, "Could not initialize eventHandler", e);
        }
        startServer();
    }

    public void startServer() {
        try (
            ServerSocket serverSocket = new ServerSocket(1105);
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            while(true) {
                Socket socket = serverSocket.accept();
                executor.submit(new Session(socket));
            }
        }
        catch (IOException e) {
            logger.log(Level.WARNING, "Could not initialize server", e);
        }
    }
}
