package engine;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import engine.event.Body;
import engine.event.EventHandler;
import engine.event.Handle;
import jakarta.persistence.Entity;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Server {
    private final Map<String, EventHandler> eventHandlers = new HashMap<>();
    private Set<Class<? extends Body>> bodyClasses;
    private final Logger logger = LoggerFactory.getLogger(Server.class);

    public Server() {
        Reflections handlerReflections = new Reflections("handler");
        Reflections bodyReflections = new Reflections("message");

        try {
            for (Class<?> handlerClass : handlerReflections.getTypesAnnotatedWith(Handle.class)){
                final Constructor<?> constructor = handlerClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                EventHandler handler = (EventHandler) constructor.newInstance();
                String eventName = handlerClass.getAnnotation(Handle.class).value();
                eventHandlers.put(eventName, handler);
            }
            bodyClasses = bodyReflections.getSubTypesOf(Body.class);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.warn("Could not initialize eventHandler", e);
        }
        startServer();
    }

    public void startServer() {
        Reflections entityReflections = new Reflections("entity");
        Set<Class<?>> entityClasses = entityReflections.getTypesAnnotatedWith(Entity.class);
        try (
            ServerSocket serverSocket = new ServerSocket(1105);
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder().build();
            SessionFactory sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClasses(entityClasses.toArray(Class[]::new))
                    .buildMetadata()
                    .buildSessionFactory()
        ) {
            logger.info("Server started");
            while(serverSocket.isBound()) {
                Socket socket = serverSocket.accept();
                executor.submit(new Session(socket, sessionFactory.openStatelessSession(), eventHandlers, bodyClasses));
            }
        }
        catch (IOException e) {
            logger.warn("Could not initialize server", e);
        }
    }

}
