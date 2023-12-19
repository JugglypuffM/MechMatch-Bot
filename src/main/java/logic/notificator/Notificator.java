package logic.notificator;

import bots.BotDriver;
import bots.platforms.Platform;
import database.entities.Account;

import java.util.ArrayList;
import java.util.List;

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
     *
     * @param friendId       id of user, which will receive notification
     * @param notification   contents of notification
     */
    public void notifyFriend(Integer friendId, String[] notification){
        if (this.driver == null){
            System.out.println("Notificator is in test mode, notification method has been invoked!");
            return;
        }
        Account account = driver.getDatabase().getAccount(friendId);
        List<Platform> platforms = new ArrayList<>();
        platforms.add(Platform.TELEGRAM);
        platforms.add(Platform.DISCORD);
        for (Platform platform: platforms){
            if (account.getPlatformId(platform) != null)
                if (driver.getOnlineBots().containsKey(platform))
                    driver.send(driver.getOnlineBots().get(platform), account.getPlatformId(platform) ,
                            account.getPlatformUsername(platform), "notification", notification);
                else driver.getLogger().warn("Notification failed:" + platform.stringRepresentation() + "bot is offline");
            else driver.getLogger().warn("Notification failed: user is not authorized on platform" + platform.stringRepresentation());
        }
    }
}
