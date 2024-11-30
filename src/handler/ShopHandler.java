package handler;

import engine.Status;
import entity.Shop;
import engine.Session;
import engine.event.EventContext;
import engine.event.EventHandler;
import engine.event.Handle;
import message.Error;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Handle("shop")
public class ShopHandler implements EventHandler {
    Logger logger = LoggerFactory.getLogger(ShopHandler.class);

    @Override
    public void handle(EventContext eventContext) {
        Session session = eventContext.session();
        StatelessSession dbSession = session.getDbSession();

        // message.Shop 객체를 가져옵니다.
        message.Shop shopMessage = (message.Shop) eventContext.event().body();

        try {
            // username을 기준으로 Shop 데이터를 조회합니다.
            Query<Shop> query = dbSession.createQuery("FROM Shop WHERE username = :username", Shop.class);
            query.setParameter("username", shopMessage.username());
            Shop existingShop = query.uniqueResult();

            if (existingShop == null) {
                // 새로운 Shop 객체를 생성하고 DB에 저장합니다.
                Shop newShopEntity = new Shop(shopMessage);
                dbSession.insert(newShopEntity);
                logger.info("New Shop data saved: {}", newShopEntity);
            } else {
                // 기존 Shop 객체를 업데이트합니다.
                existingShop.updateFromMessage(shopMessage);
                dbSession.update(existingShop);
                logger.info("Shop data updated: {}", existingShop);
            }

            // 성공적인 처리 후 응답을 보냅니다.
            session.sendEvent(null, "shop", Status.OK);
        } catch (Exception e) {
            logger.warn("Error in processing shop data: {}", e.getMessage());
            session.sendEvent(new Error(e.getMessage()), "shop", Status.DB_FAILED);
        }
    }
}
