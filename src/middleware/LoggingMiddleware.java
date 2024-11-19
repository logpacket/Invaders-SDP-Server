package middleware;

import core.Main;
import engine.Session;
import engine.event.EventContext;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingMiddleware implements Consumer<EventContext> {
    Logger logger;

    public LoggingMiddleware() {
        logger = Main.getLogger();
    }

    @Override
    public void accept(EventContext eventContext) {
        Session session = eventContext.session();
        String eventName = eventContext.event().name();
        logger.log(Level.INFO, "Client (" + session.getAddress() + ") send engine.event: " + eventName );
    }
}
