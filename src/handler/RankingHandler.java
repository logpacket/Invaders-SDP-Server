package handler;

import engine.Session;
import engine.event.Event;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import message.Ranking;
import message.RankingListResponse;

import java.util.ArrayList;
import java.util.List;

@Handle("fetchRankings") // 이벤트 타입 지정
public class RankingHandler implements EventHandler {

    private static final List<Ranking> rankings = new ArrayList<>();

    @Override
    public void handle(EventContext eventContext) {
        Session session = eventContext.session();
        Event event = eventContext.event();

        if ("fetchRankings".equals(event.name())) {
            // 클라이언트가 요청한 데이터를 반환
            RankingListResponse response = new RankingListResponse(rankings);
            session.sendEvent(response, event.name(), event.id());
        } else if ("saveRanking".equals(event.name())) {
            // 클라이언트가 보낸 데이터를 저장
            Ranking newRanking = (Ranking) event.body();
            saveOrUpdateRanking(newRanking);
            session.sendEvent(null, event.name(), event.id());
        }
    }

    private void saveOrUpdateRanking(Ranking newRanking) {
        boolean updated = false;
        for (Ranking ranking : rankings) {
            if (ranking.userId().equals(newRanking.userId())) {
                if (newRanking.highScore() > ranking.highScore()) {
                    ranking.updateScore(newRanking.highScore());
                }
                updated = true;
                break;
            }
        }
        if (!updated) {
            rankings.add(newRanking);
        }
    }
}
