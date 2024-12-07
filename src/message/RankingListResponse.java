package message;

import engine.event.Body;

import java.util.List;

public record RankingListResponse(List<Ranking> rankings) implements Body {}

