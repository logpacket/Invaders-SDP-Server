package engine.event;

import engine.PlayerSession;
import engine.MiddlewareManager;
import message.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class EventDispatcher {
    private final Map<String, EventHandler> handlerByEventType = new HashMap<>();
    private final MiddlewareManager middleware = new MiddlewareManager();
    Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

    public void addHandler(String eventName, EventHandler eventHandler) {
        handlerByEventType.put(eventName, eventHandler);
    }

    public EventHandler getHandler(String eventType) {
        return handlerByEventType.get(eventType);
    }

    public void removeHandler(String eventName) {
        handlerByEventType.remove(eventName);
    }

    public void removeHandlerForEvent(String eventName) {
        handlerByEventType.remove(eventName);
    }

    public void clear() {
        handlerByEventType.clear();
    }

    public void dispatch(PlayerSession playerSession, Event event) {
        String eventName = event.name();
        EventHandler handler = handlerByEventType.get(eventName);
        if (handler != null) {
            EventContext context = new EventContext(playerSession, event);
            try {
                middleware.process(context, () -> handler.handle(context));
            } catch (Exception e) {
                logger.warn("Error in {} handler\n message: {}", eventName, e.getMessage());
                playerSession.sendEvent(new Error(e.getMessage()), eventName, event.id());
            }
        }
    }

    public void useMiddleware(Consumer<EventContext> middlewareFunc) {
        middleware.use(middlewareFunc);
    }

    public void close() {
        clear();
    }
}
