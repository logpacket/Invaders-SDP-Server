package message;

import engine.event.Body;

public record Shop(String username, int coin, int bulletLevel, int shootLevel, int livesLevel, int coinLevel) implements Body { }
