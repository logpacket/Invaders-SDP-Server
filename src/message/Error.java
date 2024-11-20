package message;

import engine.event.Body;

public record Error(String error) implements Body { }
