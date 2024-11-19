package engine.event;

import engine.Session;
import engine.MiddlewareManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class EventDispatcher {
    private final Map<String, EventHandler> handlerByEventType = new HashMap<>();
    private final MiddlewareManager middleware = new MiddlewareManager();

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

    public void dispatch(Session session, Event event) {
        EventHandler handler = handlerByEventType.get(event.name());
        if (handler != null) {
            EventContext context = new EventContext(session, event);
            middleware.process(context, () -> handler.handle(context));
        }
    }

    public void useMiddleware(Consumer<EventContext> middlewareFunc) {
        middleware.use(middlewareFunc);
    }

    public void close() {
        clear();
    }
}
