package logic.notificator;

import bots.BotDriver;
import bots.platforms.Platform;

/**
 * Notification class to work with API's and send notifications, whenever it needed
 */
public class Notificator {
    private final BotDriver driver;
    public Notificator(BotDriver m_driver){
        this.driver = m_driver;
    }
    /**
     * Notification method.
     * @param friendId id of user, which will receive notification
     * @param friendUsername username of user, which will receive notification
     * @param notification contents of notification
     */
    public void notifyFriend(Platform friendPlatform, String friendId, String friendUsername, String[] notification){
        if (this.driver == null){
            System.out.println("Notificator is in test mode, notification method has been invoked!");
            return;
        }
        if (driver.getOnlineBots().containsKey(friendPlatform)){
            driver.send(driver.getOnlineBots().get(friendPlatform), friendId,
                    friendUsername, "notification", notification);
        }else {
            driver.getLogger().warn("Notification failed: bot offline");
        }
    }
}
