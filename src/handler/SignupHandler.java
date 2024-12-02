package handler;

import engine.event.Event;
import entity.User;
import engine.Session;
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
        Session session = eventContext.session();
        StatelessSession dbSession = session.getDbSession();
        Event event = eventContext.event();
        message.User user = (message.User) event.body();
        try {
            dbSession.insert(new User(user, LocalDateTime.now()));
            session.sendEvent(null, event.name(), event.id());
        } catch (ConstraintViolationException e) {
            session.sendEvent(new Error("Duplicated username"), event.name(), event.id());
        }
    }
}
