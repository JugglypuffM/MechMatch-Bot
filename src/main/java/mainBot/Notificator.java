package mainBot;

import telegrammBot.BotRegistrar;

public class Notificator {
    public void notifyFriend(String id, String friendUsername, String[] notification){
        if (BotRegistrar.isInit){
            BotRegistrar.bot.send(id, friendUsername, "notification", notification);
        }else {
            System.out.println("Бот не инициализирован.");
        }
    }
}
