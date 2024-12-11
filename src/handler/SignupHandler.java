package handler;

import engine.event.Event;
import entity.User;
import engine.PlayerSession;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import message.Error;
import org.hibernate.StatelessSession;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDateTime;

@Handle("signup")
public class SignupHandler implements EventHandler {
    @Override
    public void handle(EventContext eventContext) {
        PlayerSession playerSession = eventContext.playerSession();
        StatelessSession dbSession = playerSession.getStatelessSession();
        Event event = eventContext.event();
        message.User user = (message.User) event.body();
        try {
            dbSession.insert(new User(user, LocalDateTime.now()));
            playerSession.sendEvent(null, event.name(), event.id());
        } catch (ConstraintViolationException e) {
            playerSession.sendEvent(new Error("Duplicated username"), event.name(), event.id());
        }
    }
}
