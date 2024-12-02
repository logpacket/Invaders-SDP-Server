package message;

import engine.event.Body;

public record Error(String message) implements Body { }
