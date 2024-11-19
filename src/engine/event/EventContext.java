package engine.event;

import engine.Session;

public record EventContext(Session session, Event event) { }