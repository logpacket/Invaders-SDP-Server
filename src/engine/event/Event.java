package engine.event;

import engine.Status;

public record Event (
    String name,
    Body body,
    Status status,
    long timestamp
) { }
