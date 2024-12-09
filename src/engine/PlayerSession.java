package engine;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import engine.event.Body;
import engine.event.Event;
import engine.event.EventDispatcher;
import engine.event.EventHandler;
import lombok.Setter;
import lombok.Getter;
import middleware.LoggingMiddleware;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerSession implements Runnable {
    private final Socket socket;
    private final Logger logger = LoggerFactory.getLogger(PlayerSession.class);
    @Getter
    @Setter
    private long id;
    @Getter
    private final StatelessSession statelessSession;
    @Getter
    private final Session statefulSession;
    private EventDispatcher eventDispatcher;
    private ObjectMapper mapper;
    private BufferedReader reader;
    private BufferedWriter writer;


    public PlayerSession(
        Socket socket,
        StatelessSession statelessSession,
        Session statefulSession,
        Map<String, EventHandler> eventHandlers,
        Set<Class<? extends Body>> bodyClasses
    ) {
        this.socket = socket;
        this.statelessSession = statelessSession;
        this.statefulSession = statefulSession;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            mapper = new ObjectMapper();
            mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            mapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

            eventDispatcher = new EventDispatcher();

            eventHandlers.forEach(eventDispatcher::addHandler);
            bodyClasses.forEach(bodyClass -> mapper.registerSubtypes(bodyClass));
        }
        catch (IOException e) {
            logger.warn("Couldn't initialize client session.", e);
        }
    }

    @Override
    public void run() {
        InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        logger.info("Client connected: {}", socketAddress.getAddress());
        eventDispatcher.useMiddleware(new LoggingMiddleware());
        try {
            while(!socket.isClosed()) {
                if (reader.ready()) {
                    Event event = mapper.readValue(reader, Event.class);
                    statelessSession.beginTransaction();
                    statefulSession.beginTransaction();
                    eventDispatcher.dispatch(this, event);
                    statelessSession.getTransaction().commit();
                    statefulSession.getTransaction().commit();
                }
            }
        }
        catch (IOException e) {
            logger.warn("Couldn't handle client request", e);
        }
        catch (Exception e) {
            logger.warn("Unexpected exception: ", e);
        }
        finally {
            logger.info("Client disconnected: {}", socketAddress.getAddress());
            try {
                socket.close();
            } catch (IOException e) {
                logger.warn("Couldn't close client socket", e);
            }
        }
    }

    public String getAddress() {
        return socket.getRemoteSocketAddress().toString();
    }

    public void sendEvent(Body body, String eventName, UUID id) {
        Event event = new Event(eventName, body, id, System.currentTimeMillis());
        try{
            if (!eventName.equals("ping"))
                logger.info("Sending event: {}", eventName);
            mapper.writeValue(writer, event);
        }
        catch (IOException e) {
            logger.warn("Couldn't serialize event to JSON", e);
        }
    }

}
