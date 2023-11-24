package mainBot.commandHandlers;

import database.Database;
import database.models.Connection;
import database.models.User;
import mainBot.GlobalState;
import mainBot.Notificator;

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
    public synchronized void handleMessage(User sender, String[] reply, String message) {
        List<Integer> pending = database.getPendingOf(sender.getId());
        Connection connection = database.getConnection(pending.get(0));
        if (message.equalsIgnoreCase("да")){
            String[] notification = new String[24];
            connection.setIsLiked(true);
            database.updateConnection(connection);
            notification[0] = "Ура! Тебе ответили взаимностью, можно переходить к общению.";
            notification[1] = "Вот ссылка на профиль собеседника - @" + sender.getUsername();
            notificator.notifyFriend(connection.getFriendID(), database.getUser(connection.getFriendID()).getUsername(), notification);
            reply[0] = "Ура! Теперь вы можете перейти к общению.";
            reply[1] = "Вот ссылка на профиль собеседника - @" + database.getUser(connection.getFriendID()).getUsername();
        }
        else if (message.equalsIgnoreCase("нет")){
            connection.setIsLiked(false);
            database.updateConnection(connection);
            reply[0] = "Хорошо, больше ты этого человека не увидишь. Если только не решишь удалить его из списка не понравившихся профилей.";
        }
        else {
            reply[0] = "Введи да или нет.";
            return;
        }
        sender.setGlobalState(GlobalState.COMMAND);
    }
}
