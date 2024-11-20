package message;

import engine.event.Body;

public record User(String username, String password) implements Body { }
