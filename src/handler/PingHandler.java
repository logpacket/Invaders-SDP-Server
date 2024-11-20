package handler;

import engine.Session;
import engine.Status;
import engine.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Handle("ping")
public class PingHandler implements EventHandler {
    private final Logger logger = LoggerFactory.getLogger(PingHandler.class);

    @Override
    public void handle(EventContext eventContext) {
        Session session = eventContext.session();
        try { session.sendEvent(eventContext.event().body(), "ping", Status.OK); }
        catch (Exception e) {
            logger.warn("Failed to send ping", e);
        }
    }
}
