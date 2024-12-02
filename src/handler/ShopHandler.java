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
        } catch (Exception e) {
            logger.error("Error handling shop event: {}", e.getMessage());
            session.sendEvent(new Error("Error processing shop data"), event.name(), event.id());
        }
    }

    public message.Shop loadShop(Session session, String username) {
        StatelessSession dbSession = session.getDbSession();
        try {
            Query<Shop> query = dbSession.createQuery("FROM Shop WHERE username = :username", Shop.class);
            query.setParameter("username", username);
            Shop shopEntity = query.uniqueResult();

            if (shopEntity != null) {
                return new message.Shop(
                        shopEntity.getUsername(),
                        shopEntity.getCoin(),
                        shopEntity.getBulletLevel(),
                        shopEntity.getShootLevel(),
                        shopEntity.getLivesLevel(),
                        shopEntity.getCoinLevel()
                );
            } else {
                logger.info("No shop data found for username: {}", username);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error loading shop data for username {}: {}", username, e.getMessage());
            return null;
        }
    }

    public void saveShopToServer(Session session, message.Shop shopMessage) {
        StatelessSession dbSession = session.getDbSession();
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
        } catch (Exception e) {
            logger.error("Error saving shop data for username {}: {}", shopMessage.username(), e.getMessage());
        }
    }
}
