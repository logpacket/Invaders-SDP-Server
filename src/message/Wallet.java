package message;

import engine.event.Body;

public record Wallet(int coin, int bulletLevel, int shootLevel, int livesLevel, int coinLevel) implements Body { }
