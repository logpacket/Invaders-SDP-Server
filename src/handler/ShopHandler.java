package handler;

import engine.Session;
import engine.event.Event;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import entity.Shop;
import jakarta.persistence.NoResultException;
import message.Error;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Handle("shop")
public class ShopHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ShopHandler.class);

    @Override
    public void handle(EventContext eventContext) {
        Session session = eventContext.session();
        StatelessSession dbSession = session.getDbSession();
        Event event = eventContext.event();
        message.Shop shopMessage = (message.Shop) event.body();

        try {
            Query<Shop> query = dbSession.createQuery("FROM Shop WHERE username = :username", Shop.class);
            query.setParameter("username", shopMessage.username());
            Shop existingShop = query.uniqueResult();

            if (existingShop == null) {
                Shop newShopEntity = new Shop(shopMessage);
                dbSession.insert(newShopEntity);
                logger.info("New Shop created: {}", newShopEntity);
            } else {
                existingShop.updateFromMessage(shopMessage);
                dbSession.update(existingShop);
                logger.info("Shop updated: {}", existingShop);
            }

            session.sendEvent(null, event.name(), event.id());
        } catch (NoResultException e) {
            logger.warn("No Shop found for username: {}", shopMessage.username());
            session.sendEvent(new Error("No Shop data found"), event.name(), event.id());
        } catch (Exception e) {
            logger.error("Error handling shop event: {}", e.getMessage());
            session.sendEvent(new Error("Error processing shop data"), event.name(), event.id());
        }
    }
}
