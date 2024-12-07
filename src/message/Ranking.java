package message;

import engine.event.Body;

public record Ranking(String userId, int highScore) implements Body {
    public Ranking updateScore(int newScore) {
        return new Ranking(this.userId, newScore);
    }
}
