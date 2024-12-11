package handler;

import engine.PlayerSession;
import engine.event.*;

@Handle("ping")
public class PingHandler implements EventHandler {
    @Override
    public void handle(EventContext eventContext) {
        PlayerSession playerSession = eventContext.playerSession();
        playerSession.sendEvent(eventContext.event().body(), "ping", eventContext.event().id());
    }
}
