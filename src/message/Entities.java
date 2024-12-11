package message;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import engine.event.Body;

@JsonSerialize(using = EntitiesSerializer.class)
public record Entities(String entities) implements Body {}
