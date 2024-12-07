package message;

import engine.event.Body;

import java.util.List;

public record RankingList(List<Ranking> rankings) implements Body {}

