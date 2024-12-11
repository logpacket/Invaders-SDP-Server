package handler;

import engine.PlayerSession;
import engine.event.Event;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import message.Entities;
import message.GameSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Handle("game")
public class GameHandler implements EventHandler {
    private final List<Queue<EventContext>> matchQueues = new ArrayList<>();
    private final Map<PlayerSession, PlayerSession> matchedMap = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(GameHandler.class);

    public GameHandler() {
        for (int i =0; i<3; i++) {
            matchQueues.add(new ConcurrentLinkedQueue<>());
        }
    }

    @Override
    public void handle(EventContext eventContext) {
        PlayerSession session = eventContext.playerSession();
        Event event = eventContext.event();

        switch (event.body()) {
            case GameSettings(int difficulty) -> matching(eventContext, difficulty);
            case Entities _ -> syncEntities(session, event);
            default -> logger.warn("Invalid body type {}", event.body());
        }
    }

    private void matching(EventContext eventContext,  int difficulty) {
        Queue<EventContext> matchQueue = matchQueues.get(difficulty);
        if (!matchQueue.isEmpty()) {
            EventContext rivalContext = matchQueue.poll();

            Event event = eventContext.event();
            Event matchedEvent = rivalContext.event();

            PlayerSession session = eventContext.playerSession();
            PlayerSession matchedSession = rivalContext.playerSession();

            matchedMap.put(session, matchedSession);
            matchedMap.put(matchedSession, session);

            session.sendEvent(null, event.name(), event.id());
            matchedSession.sendEvent(null, matchedEvent.name(), matchedEvent.id());
        }
        else matchQueue.add(eventContext);
    }

    private void syncEntities(PlayerSession session, Event event) {
        matchedMap.get(session).sendEvent(event.body(), event.name(), event.id());
    }
}
