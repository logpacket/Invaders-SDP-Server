package handler;

import engine.Session;
import engine.event.*;

@Handle("ping")
public class PingHandler implements EventHandler {
    @Override
    public void handle(EventContext eventContext) {
        Session session = eventContext.session();
        session.sendEvent(eventContext.event().body(), "ping", eventContext.event().id());
    }
}
