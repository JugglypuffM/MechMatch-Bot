package logic.handlers;

import bots.platforms.Platform;
import database.entities.Profile;
import database.main.Database;
import database.entities.Account;
import database.entities.Connection;
import logic.notificator.Notificator;
import logic.states.GlobalState;

import java.util.List;

/**
 * Pending users watch handler.
 * Decides whether to send usernames to both of user or write suggested user in the black list.
 */
public class PendingHandler implements Handler{
    private final Database database;
    private final Notificator notificator;
    public PendingHandler(Database m_database, Notificator m_notificator){
        this.database = m_database;
        this.notificator = m_notificator;
    }
    public void handleMessage(Account user, Profile profile, String[] reply, String message, Platform platform) {
        List<Integer> pending = database.getPendingOf(user.getId());
        Connection connection = database.getConnection(pending.get(0));
        if (message.equalsIgnoreCase("да") || message.equals("❤️")){
            String[] notification = new String[24];
            connection.setIsLiked(true);
            database.updateConnection(connection);
            notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
            notification[1] = database.getUserUsernames(user.getId());
            notificator.notifyFriend(connection.getFriendID(), notification);
            reply[0] = "Ура! Теперь вы можете перейти к общению.";
            reply[1] = database.getUserUsernames(connection.getFriendID());
        }
        else if (message.equalsIgnoreCase("нет") || message.equals("\uD83D\uDC4E")){
            connection.setIsLiked(false);
            database.updateConnection(connection);
            reply[0] = "Хорошо, больше ты этого человека не увидишь. Если только не решишь удалить его из списка не понравившихся профилей.";
        }
        else {
            reply[0] = "Введи да или нет.";
            return;
        }
        user.setGlobalState(GlobalState.COMMAND);
    }
}
