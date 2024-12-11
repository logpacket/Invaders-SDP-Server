package handler;

import engine.PlayerSession;
import engine.event.Event;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import entity.User;
import jakarta.persistence.NoResultException;
import message.Error;
import org.hibernate.StatelessSession;

@Handle("login")
public class LoginHandler implements EventHandler {

    @Override
    public void handle(EventContext eventContext) {
        PlayerSession playerSession = eventContext.playerSession();
        StatelessSession dbSession = playerSession.getStatelessSession();
        Event event = eventContext.event();
        message.User userData = (message.User) event.body();

        try {
            User user = dbSession.createSelectionQuery("FROM User WHERE username = :username AND password = :password", User.class)
                    .setParameter("username", userData.username())
                    .setParameter("password", userData.password())
                    .getSingleResult();

            playerSession.setId(user.getId());
            playerSession.sendEvent(null, event.name(), event.id());
        }
        catch (NoResultException e) {
            playerSession.sendEvent(new Error("Invalid Username or Password"), event.name(), event.id());
        }
    }
}
