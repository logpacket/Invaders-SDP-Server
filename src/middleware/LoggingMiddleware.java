package middleware;

import engine.Session;
import engine.event.EventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class LoggingMiddleware implements Consumer<EventContext> {
    private final Logger logger = LoggerFactory.getLogger(LoggingMiddleware.class);
    @Override
    public void accept(EventContext eventContext) {
        Session session = eventContext.session();
        String eventName = eventContext.event().name();
        if (!eventName.equals("ping"))
            logger.info(
                "Client ({}) send event: {}",
                session.getAddress(), eventName
            );
    }
}
