package handler;

import engine.Session;
import engine.event.Event;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import entity.Ranking;
import message.HighScore;
import message.RankingList;
import org.hibernate.StatelessSession;

import java.util.List;

@Handle("ranking") // 이벤트 타입 지정
public class RankingHandler implements EventHandler {

    @Override
    public void handle(EventContext eventContext) {
        Session session = eventContext.session();
        StatelessSession dbSession = session.getDbSession();
        Event event = eventContext.event();

        if (event.body() instanceof HighScore highScore) {
            dbSession.upsert(new Ranking(session.getUser(), highScore.score()));
            session.sendEvent(null, event.name(), event.id());
        }
        else if (event.body() == null) {
            List<Ranking> rankings = dbSession
                .createSelectionQuery("FROM Ranking ORDER BY highScore LIMIT 10", Ranking.class)
                .getResultList();

            RankingList response = new RankingList(
                rankings.stream().map(
                    ranking -> new message.Ranking(
                        ranking.getUser().getUsername(),
                        ranking.getHighScore()
                    )
                ).toList()
            );
            session.sendEvent(response, event.name(), event.id());
        }
    }
}
