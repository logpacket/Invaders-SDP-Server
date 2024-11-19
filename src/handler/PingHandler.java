package handler;

import core.Main;
import engine.Session;
import engine.Status;
import engine.event.Event;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.EventType;
import entity.Ping;

import java.util.logging.Level;
import java.util.logging.Logger;

@EventType("ping")
public class PingHandler implements EventHandler {
    private final Logger logger = Main.getLogger();

    @Override
    public void handle(EventContext eventContext) {
        Session session = eventContext.session();
        try {
            session.oos.writeObject(new Event(
                    "ping",
                    new Ping(Status.OK),
                    System.currentTimeMillis()
            ));
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send ping", e);
        }
    }
}
