package engine.event;

import engine.PlayerSession;

public record EventContext(PlayerSession playerSession, Event event) { }