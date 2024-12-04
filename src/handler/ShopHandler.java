package handler;

import engine.Session;
import engine.event.Event;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import entity.Shop;
import message.Error;
import message.Wallet;
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
        Wallet walletMessage = (Wallet) event.body();

        try {
            Shop existingShop = getShopById(dbSession, session.getId());
            Wallet responseWallet;

            if (existingShop == null) {
                Shop newShopEntity = new Shop(walletMessage, session.getId());
                dbSession.insert(newShopEntity);
                responseWallet = new Wallet(
                        newShopEntity.getCoin(),
                        newShopEntity.getBulletLevel(),
                        newShopEntity.getShootLevel(),
                        newShopEntity.getLivesLevel(),
                        newShopEntity.getCoinLevel()
                );
            } else {
                existingShop.updateFromMessage(walletMessage);
                dbSession.update(existingShop);
                responseWallet = new Wallet(
                        existingShop.getCoin(),
                        existingShop.getBulletLevel(),
                        existingShop.getShootLevel(),
                        existingShop.getLivesLevel(),
                        existingShop.getCoinLevel()
                );
            }

            session.sendEvent(responseWallet, event.name(), event.id());
        } catch (Exception e) {
            logger.error("Error handling shop event: {}", e.getMessage());
            session.sendEvent(new Error("Error processing shop data"), event.name(), event.id());
        }
    }

    private Shop getShopById(StatelessSession dbSession, long userId) {
        Query<Shop> query = dbSession.createQuery("FROM Shop WHERE userId = :userId", Shop.class);
        query.setParameter("userId", userId);
        return query.uniqueResult();
    }
}
