package handler;

import engine.PlayerSession;
import engine.event.Event;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import entity.Ranking;
import entity.User;
import message.HighScore;
import message.RankingList;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

import java.util.List;

@Handle("ranking") // 이벤트 타입 지정
public class RankingHandler implements EventHandler {

    @Override
    public void handle(EventContext eventContext) {
        PlayerSession playerSession = eventContext.playerSession();
        Session session = playerSession.getStatefulSession();
        Event event = eventContext.event();

        if (event.body() instanceof HighScore(int score)) {
            User user = session.get(User.class, playerSession.getId());
            session.persist(new Ranking(user, score));
            playerSession.sendEvent(null, event.name(), event.id());
        }
        else if (event.body() == null) {
            List<Ranking> rankings = session
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
            playerSession.sendEvent(response, event.name(), event.id());
        }
    }
}
