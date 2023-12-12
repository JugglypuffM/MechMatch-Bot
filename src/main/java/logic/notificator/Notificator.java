package logic.notificator;

import bots.Bot;
import bots.BotDriver;
import bots.platforms.Platform;
import database.models.Account;

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
        if (account.getTgid() != null)
            if (driver.getOnlineBots().containsKey(Platform.TELEGRAM))
                driver.send(driver.getOnlineBots().get(Platform.TELEGRAM), account.getTgid(),
                        account.getTgusermane(), "notification", notification);
            else driver.getLogger().warn("Notification failed: TELEGRAM bot is offline");
        else driver.getLogger().warn("Notification failed: user is not authorized on platform TELEGRAM");
        if (account.getDsid() != null)
            if (driver.getOnlineBots().containsKey(Platform.DISCORD))
                driver.send(driver.getOnlineBots().get(Platform.DISCORD), account.getDsid(),
                        account.getDsusername(), "notification", notification);
            else driver.getLogger().warn("Notification failed: DISCORD bot is offline");
        else driver.getLogger().warn("Notification failed: user is not authorized on platform DISCORD");
    }
}
