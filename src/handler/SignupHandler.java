package handler;

import engine.Status;
import entity.User;
import engine.Session;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import message.Error;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Handle("signup")
public class SignupHandler implements EventHandler {
    Logger logger = LoggerFactory.getLogger(SignupHandler.class);

    @Override
    public void handle(EventContext eventContext) {
        Session session = eventContext.session();
        StatelessSession dbSession = session.getDbSession();
        message.User user = (message.User) eventContext.event().body();

        try {
            dbSession.insert(new User(user, LocalDateTime.now()));
            session.sendEvent(null, "signup", Status.OK);
        }
        catch (Exception e) {
            logger.warn("Error in inserting user {}", e.getMessage());
            session.sendEvent(new Error(e.getMessage()), "signup", Status.DB_FAILED);
        }
    }
}
