package message;

import engine.event.Body;

public record Ping(long sendTimestamp) implements Body { }
