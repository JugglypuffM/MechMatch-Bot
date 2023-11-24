package mainBot.notificator;

import telegrammBot.BotRegistrar;

/**
 * Notification class to work with API's and send notifications, whenever it needed
 */
public class Notificator {
    /**
     * Notification method for telegramm bot
     * @param friendId id of user, which will receive notification
     * @param friendUsername username of user, which will receive notification
     * @param notification contents of notification
     */
    public void notifyFriend(String friendId, String friendUsername, String[] notification){
        if (BotRegistrar.isInit){
            BotRegistrar.bot.send(friendId, friendUsername, "notification", notification);
        }else {
            System.out.println("Бот не инициализирован.");
        }
    }
}
