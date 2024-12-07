package message;

import engine.event.Body;

public record HighScore(int score) implements Body { }
