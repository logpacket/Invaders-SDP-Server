package message;

import engine.event.Body;

public record GameSettings(int difficulty) implements Body { }
