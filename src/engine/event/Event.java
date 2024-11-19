package engine.event;

import java.io.Serializable;

public record Event (
    String name,
    Record body,
    long timestamp
) implements Serializable { }
