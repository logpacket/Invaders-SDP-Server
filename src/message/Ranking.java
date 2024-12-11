package message;

import engine.event.Body;

public record Ranking(String username, int highScore) implements Body { }
